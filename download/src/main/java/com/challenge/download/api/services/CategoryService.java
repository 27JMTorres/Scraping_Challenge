package com.challenge.download.api.services;

import com.challenge.download.scraping.walmart.ScrapingCategories;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    public String downloadCategories(){
        try{
            ScrapingCategories sc = new ScrapingCategories();
            String cats = sc.scrapCategories();
            return cats;
        }catch (Exception ex) {
            return "Bad Request";
        }
    }
}
