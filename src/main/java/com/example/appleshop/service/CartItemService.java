package com.example.appleshop.service;
import com.example.appleshop.entity.CartItemEntity;
import com.example.appleshop.entity.UserEntity;
import com.example.appleshop.repository.CartItemRepository;
import com.example.appleshop.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    public CartItemService(CartItemRepository cartItemRepository, UserRepository userRepository) {
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
    }

    public List<CartItemEntity> getItemsForUser(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return cartItemRepository.findByUser(user);
    }

    public CartItemEntity addOrUpdateItem(String username, Long productId, int qtyToAdd) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return cartItemRepository.findByUserAndProductId(user, productId)
                .map(item -> {
                    item.setQty(item.getQty() + qtyToAdd); // cộng thêm
                    return cartItemRepository.save(item);
                })
                .orElseGet(() -> {
                    CartItemEntity item = new CartItemEntity();
                    item.setUser(user);
                    item.setProductId(productId);
                    item.setQty(qtyToAdd);
                    return cartItemRepository.save(item);
                });
    }

    public void removeItem(Long id) {
        cartItemRepository.deleteById(id);
    }
}
