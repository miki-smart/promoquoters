package com.backend.promoquoter.infrastructure.adapter.in.web;

import com.backend.promoquoter.infrastructure.adapter.in.web.dto.CartDtos;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {
    @PostMapping("/creatCart")
    public ResponseEntity<CartDtos> createCart(@RequestBody CartDtos cartDtos) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PostMapping("/calculateQuote")
    public ResponseEntity<CartDtos> calculateCartQuote(@RequestBody CartDtos cartDtos) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PostMapping("/confirmCart")
    public ResponseEntity<CartDtos> confirmCart(@RequestBody CartDtos cartDtos) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
