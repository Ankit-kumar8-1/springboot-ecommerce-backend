package com.ankitsaahariya.dao;

import com.ankitsaahariya.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {

    @Query("""
        SELECT ci FROM CartItem ci 
        WHERE ci.cart.id = :cartId 
        AND ci.product.id = :productId 
        AND ci.size = :size
    """)
    Optional<CartItem> findByCartIdAndProductIdAndSize(
            @Param("cartId") Long cartId,
            @Param("productId") Long productId,
            @Param("size") String size
    );


    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.product.id = :productId")
    Optional<CartItem> findByCartIdAndProductId(
            @Param("cartId") Long cartId,
            @Param("productId") Long productId
    );

    void deleteByCartId(Long id);
}
