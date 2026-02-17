package com.ankitsaahariya.ServiceImp;

import com.ankitsaahariya.Service.PublicProductService;
import com.ankitsaahariya.dao.CategoryRepository;
import com.ankitsaahariya.dao.ProductRepository;
import com.ankitsaahariya.dao.SellerProfileRepository;
import com.ankitsaahariya.dto.response.ProductResponse;
import com.ankitsaahariya.entities.Category;
import com.ankitsaahariya.entities.Product;
import com.ankitsaahariya.entities.Review;
import com.ankitsaahariya.entities.SellerProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PublicProductServiceImpl implements PublicProductService {
    private final SellerProfileRepository sellerProfileRepository;
    private  final ProductRepository  productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public Page<ProductResponse> getAllProducts(
            Pageable pageable,
            Long categoryId,
            Long sellerId,
            Integer minPrice,
            Integer maxPrice,
            String color,
            String sizes,
            Integer minRating,
            String search,
            String sort) {


        Specification<Product> spec = Specification.where(hasPositiveQuantity());

        if (categoryId != null){
            spec = spec.and(hasCategory(categoryId));
        }

        if (sellerId != null){
            SellerProfile seller = sellerProfileRepository.findById(sellerId)
                    .orElseThrow(()-> new RuntimeException("Seller not found with this id : " + sellerId));

            if(!seller.getIsActive()){
                throw new RuntimeException("Seller is inactive");
            }

            spec = spec.and(hasSeller(sellerId));
        }
        if (minPrice != null){
            spec = spec.and(hasMinPrice(minPrice));
        }
        if(maxPrice != null){
            spec = spec.and(hasMaxPrice(maxPrice));
        }

        if(color != null && !color.isBlank()){
            spec=spec.and(hasColor(color));
        }

        if (sizes!= null && !sizes.isBlank()){
            spec = spec.and(hasSizes(sizes));
        }

        if (minRating!=null){
            spec =spec.and(hasMinRating(minRating));
        }

        if(search!= null && !search.isBlank()){
            spec = spec.and(matchSearch(search));
        }
        Pageable sortedPageable = applySorting(pageable, sort);

        Page<Product> productPage = productRepository.findAll(spec, sortedPageable);

        return productPage.map(this::mapToResponse);
    }

    @Override
    public ProductResponse getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new RuntimeException("Product not found with this Product Id : "+ productId));

        if (product.getQuantity() <= 0){
            throw  new RuntimeException("Product currently Out of stock !");
        }

        return mapToDetailResponse(product);
    }

    @Override
    public Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable) {
        // Validate category
        Category category = categoryRepository.findByIdAndActiveTrue(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found or inactive"));

        Specification<Product> spec = Specification
                .where(hasPositiveQuantity())
                .and(hasCategory(categoryId));

        Page<Product> productPage = productRepository.findAll(spec, pageable);

        return productPage.map(this::mapToResponse);
    }

    @Override
    public Page<ProductResponse> getProductsBySeller(Long sellerId, Pageable pageable) {
        SellerProfile seller = sellerProfileRepository.findById(sellerId).
                orElseThrow(()-> new RuntimeException("Seller not found With this Id : "+ sellerId));

        if (!seller.getIsActive()){
            throw new RuntimeException("Seller not active !");
        }
        Specification<Product> spec = Specification
                .where(hasPositiveQuantity())
                .and(hasSeller(sellerId));

        Page<Product> productPage = productRepository.findAll(spec, pageable);

        return productPage.map(this::mapToResponse);
    }

    @Override
    public List<ProductResponse> searchProducts(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new RuntimeException("Search keyword is required");
        }

        String searchPattern = "%" + keyword.toLowerCase() + "%";

        Specification<Product> spec = Specification
                .where(hasPositiveQuantity())
                .and(matchSearch(keyword));

        List<Product> products = productRepository.findAll(spec, PageRequest.of(0, 20)).getContent();

        return products.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ProductResponse> getRelatedProducts(Long productId, int limit) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Specification<Product> spec = Specification
                .where(hasPositiveQuantity())
                .and(hasCategory(product.getCategory().getId()))
                .and((root, query, cb) -> cb.notEqual(root.get("id"), productId));

        List<Product> relatedProducts = productRepository.findAll(
                spec,
                PageRequest.of(0, limit)).getContent();

        return relatedProducts.stream()
                .map(this::mapToResponse)
                .toList();
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

    private Pageable applySorting(Pageable pageable, String sort) {
        if (sort == null || sort.isBlank()) {
            return pageable;
        }

        Sort sorting = switch (sort.toLowerCase()) {
            case "price_low" -> Sort.by(Sort.Direction.ASC, "sellingPrice");
            case "price_high" -> Sort.by(Sort.Direction.DESC, "sellingPrice");
            case "newest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "rating" -> Sort.by(Sort.Direction.DESC, "numRatings");
            case "discount" -> Sort.by(Sort.Direction.DESC, "discountPercent");
            default -> pageable.getSort();
        };

        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sorting
        );
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

        // Category info
        if (product.getCategory() != null) {
            response.setCategoryId(product.getCategory().getId());
            response.setCategoryName(product.getCategory().getName());
            response.setCategorySlug(product.getCategory().getSlug());
        }

        // Seller info
        if (product.getSeller() != null) {
            response.setSellerId(product.getSeller().getId());
            response.setSellerBusinessName(product.getSeller().getBusinessName());
            response.setSellerRating(product.getSeller().getSellerRating());
        }

        return response;
    }

    private Specification<Product> matchSearch(String keyword) {
        return (root, query, cb) -> {
            String searchPattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("title")), searchPattern),
                    cb.like(cb.lower(root.get("description")), searchPattern),
                    cb.like(cb.lower(root.get("color")), searchPattern)
            );
        };
    }

    private Specification<Product> hasMinRating(Integer minRating) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("numRatings"),minRating));
    }

    private Specification<Product> hasSizes(String sizes) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("sizes")), "%" + sizes.toLowerCase() + "%");
    }

    private Specification<Product> hasColor(String color) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("color")), "%" + color.toLowerCase() + "%");
    }

    private Specification<Product> hasMaxPrice(Integer maxPrice) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("sellingPrice"),maxPrice));
    }

    private Specification<Product> hasMinPrice(Integer minPrice) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("sellingPrice"),minPrice));
    }

    private Specification<Product> hasSeller(Long sellerId) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("seller").get("id"),sellerId));
    }

    private Specification<Product> hasCategory(Long categoryId) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("category").get("id"),categoryId));
    }

    private Specification<Product> hasPositiveQuantity() {
        return  ((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan(root.get("quantify"),0));
    }
}
