package com.example.skala.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import com.example.skala.model.Stock;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findById(Long id);
    Optional<Stock> findByStockNameLike(String stockName);
}
