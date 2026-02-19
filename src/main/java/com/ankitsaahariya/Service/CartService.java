package com.ankitsaahariya.Service;

import com.ankitsaahariya.dto.request.AddToCartRequest;
import com.ankitsaahariya.dto.request.UpdateQuantityRequest;
import com.ankitsaahariya.dto.response.CartResponse;

public interface CartService {

    CartResponse addToCart(AddToCartRequest request);

    CartResponse updateCartItemQuantity(Long cartItemId, UpdateQuantityRequest request);

    CartResponse removeCartItem(Long cartItemId);
}
