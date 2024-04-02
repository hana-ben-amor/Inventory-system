package com.example.productservice.Controller;

import com.example.productservice.DTO.ProductRequest;
import com.example.productservice.DTO.ProductResponse;
import com.example.productservice.Entity.Product;
import com.example.productservice.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @GetMapping("/with_quantity/{id}")
    public ProductResponse getProductByIdWithQuantity(@PathVariable Long id) {
        return productService.getProductByIdWithQuantity(id);
    }



    @PostMapping("/create")
    public Product createProduct(@RequestBody ProductRequest productRequest) {

        return productService.createProduct(productRequest);
    }

    @PutMapping("update_no_quantity/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product updatedProduct)
    {
        return productService.updateProduct(id, updatedProduct);
    }

    @PutMapping("update/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody ProductRequest updatedProduct)
    {
        return productService.updateProductWithQuantity(id, updatedProduct);
    }


    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
