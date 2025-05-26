package com.example.skala.controller;

import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Parameter;

import com.example.skala.service.StockService;
import com.example.skala.common.Response;
import com.example.skala.model.Stock;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {
    private final StockService stockService;

    // 주식 목록 조회
    @GetMapping("/list")
    public Response getAllStocks() {
		return stockService.getAllStocks();
	}

    @PostMapping
    public Response createStock(@RequestBody @Parameter(description = "저장할 주식") Stock stock) {
        System.out.println("Received stock: " + stock);
		return stockService.createStock(stock);
	}

    // @PutMapping
    // public Response updateStock(@RequestBody @Parameter(description = "수정할 주식") Stock stock) {
	// 	return stockService.updateStock(stock);
	// }

    // @DeleteMapping
    // public Response deleteStock(@RequestBody @Parameter(description = "삭제할 주식") Stock stock) {
	// 	return stockService.deleteStock(stock);
	// }
}
