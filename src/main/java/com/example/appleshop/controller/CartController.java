package com.example.appleshop.controller;
import com.example.appleshop.entity.CartItemEntity;
import com.example.appleshop.service.CartItemService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartItemService cartItemService;

    public CartController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @GetMapping("/items")
    public List<CartItemEntity> getItems() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return cartItemService.getItemsForUser(username);
    }

    @PostMapping("/items")
    public CartItemEntity addItem(@RequestBody Map<String,Object> body) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Long productId = Long.valueOf(body.get("productId").toString());
        int qty = Integer.parseInt(body.get("qty").toString());
        return cartItemService.addOrUpdateItem(username, productId, qty);
    }


    @DeleteMapping("/items/{id}")
    public void deleteItem(@PathVariable Long id) {
        cartItemService.removeItem(id);
    }
}
