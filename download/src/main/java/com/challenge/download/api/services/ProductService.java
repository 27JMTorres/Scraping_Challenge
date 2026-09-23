package com.challenge.download.api.services;

import com.challenge.download.scraping.walmart.ScrapingProducts;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    public String downloadProducts(String category){
        try{
            ScrapingProducts sp = new ScrapingProducts();
            String result = sp.scrapProducts(category);
            return result;
        }catch (Exception ex) {
            return "Bad Request";
        }
    }
}
