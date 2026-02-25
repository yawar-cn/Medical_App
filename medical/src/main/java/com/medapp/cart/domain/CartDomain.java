package com.medapp.cart.domain;

import com.medapp.cart.entity.Cart;
import com.medapp.cart.entity.CartItem;
import com.medapp.cart.entity.CartStatus;
import com.medapp.cart.repository.CartItemRepository;
import com.medapp.cart.repository.CartRepository;
import com.medapp.common.exception.NotFoundException;
import com.medapp.user.entity.User;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class CartDomain {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public CartDomain(CartRepository cartRepository, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public Cart getOrCreateActiveCart(User user) {
        return cartRepository.findByUserIdAndStatus(user.getId(), CartStatus.ACTIVE)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    cart.setStatus(CartStatus.ACTIVE);
                    return cartRepository.save(cart);
                });
    }

    public Cart getActiveCartByUserId(UUID userId) {
        return cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Active cart not found"));
    }

    public List<CartItem> items(UUID cartId) {
        return cartItemRepository.findByCartId(cartId);
    }

    public CartItem saveItem(CartItem item) {
        return cartItemRepository.save(item);
    }

    public CartItem getItem(UUID cartId, UUID medicineId, UUID pharmacyId) {
        return cartItemRepository.findByCartIdAndMedicineIdAndPharmacyId(cartId, medicineId, pharmacyId)
                .orElseThrow(() -> new NotFoundException("Cart item not found"));
    }

    public void deleteItem(CartItem item) {
        cartItemRepository.delete(item);
    }

    public void clear(UUID cartId) {
        cartItemRepository.deleteByCartId(cartId);
    }
}
