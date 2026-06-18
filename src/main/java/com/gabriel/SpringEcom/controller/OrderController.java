package com.gabriel.SpringEcom.controller;

import com.gabriel.SpringEcom.model.dto.OrderRequest;
import com.gabriel.SpringEcom.model.dto.OrderResponse;
import com.gabriel.SpringEcom.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("http://localhost:5173/")
public class OrderController {
    
    @Autowired
    private OrderService orderService;

    @PostMapping("orders/place")
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest orderRequest) {
        OrderResponse orderResponse = orderService.placeOrder(orderRequest);
        return new ResponseEntity<>(orderResponse, HttpStatus.CREATED);
    }

    @GetMapping("orders")
    public ResponseEntity<List<OrderResponse>> getAllOrderResponses() {
        List<OrderResponse> responses = orderService.getAllOrderResponses();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

}