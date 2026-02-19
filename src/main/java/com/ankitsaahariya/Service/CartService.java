package com.ankitsaahariya.Service;

import com.ankitsaahariya.dto.request.AddToCartRequest;
import com.ankitsaahariya.dto.request.UpdateQuantityRequest;
import com.ankitsaahariya.dto.response.CartResponse;
import com.ankitsaahariya.dto.response.MessageResponse;

public interface CartService {

    CartResponse addToCart(AddToCartRequest request);

    CartResponse updateCartItemQuantity(Long cartItemId, UpdateQuantityRequest request);

    CartResponse removeCartItem(Long cartItemId);

    CartResponse getCart();

    MessageResponse clearCart();
}
