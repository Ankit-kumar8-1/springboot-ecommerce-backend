package com.ankitsaahariya.ServiceImp;

import com.ankitsaahariya.Service.AdminProductService;
import com.ankitsaahariya.dao.CategoryRepository;
import com.ankitsaahariya.dao.ProductRepository;
import com.ankitsaahariya.dto.response.ProductResponse;
import com.ankitsaahariya.entities.Product;
import com.ankitsaahariya.entities.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminProductServiceImpl implements AdminProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public Page<ProductResponse> getAllProducts(Pageable pageable, Long categoryId, Long sellerId, String search) {
        Specification<Product>  spec = Specification.where(null);

        if(categoryId != null){
            spec = spec.and((root,query,cb)->
                    cb.equal(root.get("category").get("id"),categoryId));
        }

        if (sellerId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("seller").get("id"), sellerId));
        }

        if (search != null && !search.isBlank()) {
            String searchPattern = "%" + search.toLowerCase() + "%";
            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("title")), searchPattern),
                            cb.like(cb.lower(root.get("description")), searchPattern)
                    ));
        }

        Page<Product> productPage = productRepository.findAll(spec, pageable);

        return productPage.map(this::mapToResponse);
    }

    @Override
    public ProductResponse getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

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
}
