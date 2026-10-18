package com.gabriel.SpringEcom.controller;

import com.gabriel.SpringEcom.dto.OrderDTO.OrderResponse;
import com.gabriel.SpringEcom.security.UserPrincipal;
import com.gabriel.SpringEcom.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("place")
    public ResponseEntity<OrderResponse> placeOrder(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        OrderResponse orderResponse = orderService.placeOrder(userPrincipal.getUser());
        return new ResponseEntity<>(orderResponse, HttpStatus.CREATED);
    }

    @GetMapping("all")
    public ResponseEntity<List<OrderResponse>> getAllOrderResponses(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<OrderResponse> responses = orderService.getAllOrderResponses(userPrincipal.getUser());
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

}