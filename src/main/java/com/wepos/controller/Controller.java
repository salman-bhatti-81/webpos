package com.wepos.controller;

import com.wepos.catalog.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/webpos")
public class Controller {
    private final CatalogService catalogService;

    @Autowired
    public Controller(CatalogService giftlovApiService) {
        this.catalogService = giftlovApiService;
    }

    @GetMapping("/catalogs")
    public boolean getExampleData() {
         catalogService.getCatalog();
         return true;
    }
}
