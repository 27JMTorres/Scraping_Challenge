package com.challenge.download.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.challenge.download.api.services.CategoryService;
import com.challenge.download.api.services.ProductService;

@RestController
public class DownloadControler {

    @Autowired
    CategoryService cs;

    @Autowired
    ProductService ps;

    @RequestMapping("category/")
    public String downloadCategories(){
        String result = cs.downloadCategories();
        return result;
    }

    @RequestMapping("product")
    public String downloadProducts(@RequestParam(required = false) String category){
        String result = ps.downloadProducts(category);
        return result;
    }
}