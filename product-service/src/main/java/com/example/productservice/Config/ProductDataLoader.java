package com.example.productservice.Config;

import com.example.productservice.Entity.Product;
import com.example.productservice.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductDataLoader implements CommandLineRunner {

    @Autowired
    private ProductService productService;

    @Override
    public void run(String... args) throws Exception {
        // Add 5 sample products at the start of the application
//        List<Product> products = new ArrayList<>();
//        products.add(new Product(null, "Product 1", "Description 1", 10.99, 100));
//        products.add(new Product(null, "Product 2", "Description 2", 15.99, 150));
//        products.add(new Product(null, "Product 3", "Description 3", 20.99, 200));
//        products.add(new Product(null, "Product 4", "Description 4", 25.99, 250));
//        products.add(new Product(null, "Product 5", "Description 5", 30.99, 300));
//
//        for (Product product : products) {
//            productService.createProduct(product);
//        }
    }
}
