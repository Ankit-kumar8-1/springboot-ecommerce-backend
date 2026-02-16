package com.ankitsaahariya.ServiceImp;

import com.ankitsaahariya.Service.SellerProductService;
import com.ankitsaahariya.dao.CategoryRepository;
import com.ankitsaahariya.dao.ProductRepository;
import com.ankitsaahariya.dao.SellerProfileRepository;
import com.ankitsaahariya.dao.UserRepository;
import com.ankitsaahariya.domain.SellerVerificationStatus;
import com.ankitsaahariya.dto.request.ProductRequest;
import com.ankitsaahariya.dto.response.MessageResponse;
import com.ankitsaahariya.dto.response.ProductResponse;
import com.ankitsaahariya.entities.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SellerProductServiceImpl  implements SellerProductService {

    private final UserRepository userRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Transactional
    @Override
    public ProductResponse createProduct(ProductRequest request) {
        SellerProfile seller = getCurrentSeller();
        validateSellerEligibility(seller);

        Category category = categoryRepository.findByIdAndActiveTrue(request.getCategoryId())
                .orElseThrow(()-> new RuntimeException("Category not found or inactive !"));

        if(request.getSellingPrice() >= request.getMrpPrice()){
            throw new RuntimeException("Selling price must be less than MRP price");
        }

        int discount = calculateDiscountPercentage(request.getMrpPrice(),request.getSellingPrice());

        Product product = new Product();
        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setMrpPrice(request.getMrpPrice());
        product.setSellingPrice(request.getSellingPrice());
        product.setDiscountPercent(discount);
        product.setQuantity(request.getQuantity());
        product.setColor(request.getColor());
        product.setImages(request.getImages());
        product.setSizes(request.getSizes());
        product.setCategory(category);
        product.setSeller(seller);
        product.setCreatedAt(LocalDateTime.now());
        product.setNumRatings(0);

        Product savedProduct = productRepository.save(product);
        seller.setTotalProducts(seller.getTotalProducts()+1);
        sellerProfileRepository.save(seller);

        return mapToResponse(savedProduct);
    }

    @Transactional
    @Override
    public ProductResponse updateProduct(Long productId, ProductRequest request) {
        SellerProfile seller = getCurrentSeller();
        validateSellerEligibility(seller);

        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new RuntimeException("Product not found with id : " + productId));

        if(!product.getSeller().getId().equals(seller.getId())){
            throw new RuntimeException("You can update only your own product !");
        }

        Category category = categoryRepository.findByIdAndActiveTrue(request.getCategoryId())
                .orElseThrow(()-> new RuntimeException("Category not found or Inactive !"));

        if(request.getSellingPrice() >= request.getMrpPrice()){
            throw new RuntimeException("Selling price must be less than MRP price");
        }

        int discount = calculateDiscountPercentage(request.getMrpPrice(),request.getSellingPrice());

        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setMrpPrice(request.getMrpPrice());
        product.setSellingPrice(request.getSellingPrice());
        product.setDiscountPercent(discount);
        product.setQuantity(request.getQuantity());
        product.setColor(request.getColor());
        product.setImages(request.getImages());
        product.setSizes(request.getSizes());
        product.setCategory(category);

        Product updatedProduct = productRepository.save(product);
        return mapToResponse(updatedProduct);
    }

    @Override
    public MessageResponse deleteProduct(Long productId) {
        SellerProfile seller = getCurrentSeller();
        validateSellerEligibility(seller);

        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new RuntimeException("Product Not found !"));

        if(!product.getSeller().getId().equals(seller.getId())){
            throw new RuntimeException("you can delete only  your own products !");
        }
        boolean hasOrders = productRepository.existsInOrders(productId);
        if (hasOrders){
            throw new RuntimeException("Cannot delete product with existing orders");
        }

        productRepository.delete(product);
        seller.setTotalProducts(Math.max(0,seller.getTotalProducts() -1));
        sellerProfileRepository.save(seller);

        return new MessageResponse("Product deleted Successfully !");
    }

    @Override
    public Page<ProductResponse> getSellerProducts(Pageable pageable, String search) {
        SellerProfile seller = getCurrentSeller();

        Page<Product> productPage;

        if(search!= null && !search.isBlank()){
            String searchPattern = "%" + search.toLowerCase() + "%";
            productPage = productRepository.findAll((root, query, cb) -> cb.and(
                            cb.equal(root.get("seller").get("id"), seller.getId()),
                            cb.or(
                                    cb.like(cb.lower(root.get("title")), searchPattern),
                                    cb.like(cb.lower(root.get("description")), searchPattern))),
                    pageable);
        } else {
            productPage = productRepository.findBySellerId(seller.getId(), pageable);
        }

        return productPage.map(this::mapToResponse);
    }

    @Override
    public ProductResponse getProductById(Long productId) {
        SellerProfile seller = getCurrentSeller();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        // Check ownership
        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new RuntimeException("You can only view your own products");
        }

        return mapToDetailResponse(product);
    }

    private ProductResponse mapToDetailResponse(Product product) {
        ProductResponse response = mapToResponse(product);

        // Add recent reviews (last 5)
        if (product.getReviews() != null && !product.getReviews().isEmpty()) {
            List<ProductResponse.ReviewSummary> reviewSummaries = product.getReviews()
                    .stream()
                    .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()))
                    .limit(5)
                    .map(this::mapReviewToSummary)
                    .toList();

            response.setRecentReviews(reviewSummaries);
        }

        return response;
    }

    private ProductResponse.ReviewSummary mapReviewToSummary(Review review) {
        ProductResponse.ReviewSummary summary = new ProductResponse.ReviewSummary();
        summary.setId(review.getId());
        summary.setReviewText(review.getReviewText());
        summary.setRating(review.getRating());
        summary.setCreatedAt(review.getCreatedAt());

        if (review.getUser() != null) {
            summary.setUserName(review.getUser().getFullName());
        }

        return summary;
    }

    private ProductResponse mapToResponse(Product product) {

        ProductResponse response = new ProductResponse();

        response.setId(product.getId());
        response.setTitle(product.getTitle());
        response.setDescription(product.getDescription());
        response.setMrpPrice(product.getMrpPrice());
        response.setSellingPrice(product.getSellingPrice());
        response.setDiscountPercent(product.getDiscountPercent());
        response.setQuantity(product.getQuantity());
        response.setColor(product.getColor());
        response.setImages(product.getImages());
        response.setSizes(product.getSizes());
        response.setNumRatings(product.getNumRatings());
        response.setCreatedAt(product.getCreatedAt());

        if(product.getCategory() != null){
            response.setCategoryId(product.getCategory().getId());
            response.setCategoryName(product.getCategory().getName());
            response.setCategorySlug(product.getCategory().getSlug());
        }

        if(product.getSeller() != null){
            response.setSellerId(product.getSeller().getId());
            response.setSellerBusinessName(product.getSeller().getBusinessName());
            response.setSellerRating(product.getSeller().getSellerRating());
        }

        return response;

    }

    private int calculateDiscountPercentage(int mrpPrice, int sellingPrice) {
        if (mrpPrice <= 0) return 0;
        return (int) Math.round(((mrpPrice - sellingPrice) * 100.0) / mrpPrice);
    }

    private void validateSellerEligibility(SellerProfile seller) {
        if (seller == null){
            throw new RuntimeException("Seller profile not found");
        }

        if (seller.getVerificationStatus() != SellerVerificationStatus.APPROVED){
            throw  new RuntimeException("Seller not approved. Current status: " + seller.getVerificationStatus() +
                    ". Please wait for admin approval.");
        }

        if(!seller.getIsActive()){
            throw new RuntimeException("Seller account is inactive . please contact support");
        }
    }

    private SellerProfile getCurrentSeller() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("User not found with this Email : "+ email));

        return  sellerProfileRepository.findByUserId(user.getId())
                .orElseThrow(()-> new RuntimeException("Seller profile not found. Please apply as seller first."));
    }
}
