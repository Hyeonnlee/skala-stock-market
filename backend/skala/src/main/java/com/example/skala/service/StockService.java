package com.example.skala.service;

import com.example.skala.common.Response;
import com.example.skala.model.Stock;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

@Service
public class StockService {
    // STOCK_FILE_PATH는 사용되지 않음
    //private static final String STOCK_FILE_PATH = "src/main/resources/stocks.txt"; // resources에 위치한 파일
    private List<Stock> stocks = new ArrayList<>();

    public StockService() {
      loadStocksFromFile();
    }

    public Response getAllStocks() {
        //List<Stock> allStocks = readStocksFromFile();
        return new Response(true, "주식 목록 조회 성공", stocks);
    }

    private void loadStocksFromFile() {
      try {
        ClassPathResource resource = new ClassPathResource("stocks.txt"); // resources 폴더 내 stocks.txt
        InputStream inputStream = resource.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        String line;
        while ((line = reader.readLine()) != null) {
          String[] parts = line.split(",");
          if (parts.length == 3) {
            int id = Integer.parseInt(parts[0].trim());
            String name = parts[1].trim();
            int price = Integer.parseInt(parts[2].trim());
            stocks.add(new Stock(id, name, price));
          }
        }
        reader.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    private List<Stock> readStocksFromFile() {
        List<Stock> stocks = new ArrayList<>();
        try {
            ClassPathResource resource = new ClassPathResource("stocks.txt"); // resources 폴더 내 stocks.txt
            InputStream inputStream = resource.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    int id = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    int price = Integer.parseInt(parts[2].trim());
                    stocks.add(new Stock(id, name, price));
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stocks;
    }

    public Response createStock(Stock stock) {
        try {
            // 기존 파일에서 ID를 읽어 가장 마지막 ID + 1 부여
            List<Stock> stocks = readStocksFromFile();
            int newId = stocks.isEmpty() ? 1 : stocks.get(stocks.size() - 1).getId() + 1;
            stock.setId(newId); // 새 ID 설정

            String line = stock.getId() + "," + stock.getStockName() + "," + stock.getPrice() + "\n";
            
            // resources 폴더는 파일 쓰기를 지원하지 않으므로 실제 파일을 수정하려면 별도의 경로를 사용해야 합니다.
            // 아래는 파일을 다른 경로에 저장하는 예시입니다. (예: 프로젝트 내에 실제 파일 경로)
            Path path = Paths.get("src/main/resources/stocks.txt");
            Files.write(path, line.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
            
            return new Response(true, "주식 저장 성공", stock);
        } catch (IOException e) {
            e.printStackTrace();
            return new Response(false, "파일 저장 실패", null);
        }
    }

    // public Response updateStock(Stock stock) {
    //     // TODO: stocks.txt 내용을 수정하는 로직 작성
    //     return new Response("todo", "아직 구현되지 않음");
    // }

    // public Response deleteStock(Stock stock) {
    //     // TODO: stocks.txt에서 해당 항목 삭제하는 로직 작성
    //     return new Response("todo", "아직 구현되지 않음");
    // }
}
