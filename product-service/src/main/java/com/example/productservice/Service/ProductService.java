package com.example.productservice.Service;

import com.example.productservice.DTO.InventoryRequest;
import com.example.productservice.DTO.ProductRequest;
import com.example.productservice.DTO.ProductResponse;
import com.example.productservice.Entity.Product;
import com.example.productservice.Repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ProductService {


    private final RestTemplate restTemplate = new RestTemplate() ;
    private final String inventoryPath = "http://localhost:8084/inventories";

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public ProductResponse getProductByIdWithQuantity(Long id)
    {
        Product product = getProductById(id);
        ResponseEntity<InventoryRequest> inventoryRequest = restTemplate.getForEntity(inventoryPath + "/get/" + id ,InventoryRequest.class);

        return ProductResponse.builder()
                .id(id)
                .description(product.getDescription())
                .name(product.getName())
                .price(product.getPrice())
                .quantity(inventoryRequest.getBody().getQuantity())
                .build();

    }

    @Transactional
    public Product createProduct(ProductRequest productRequest)
    {
        Product product =
                Product.builder()
                        .name(productRequest.getName())
                        .description(productRequest.getDescription())
                        .price(productRequest.getPrice())
                        .build();

        Product product_transfer = productRepository.save(product);
        InventoryRequest inventoryRequest =
                InventoryRequest.builder()
                        .id(product_transfer.getProductId())
                        .quantity(productRequest.getQuantity())
                        .build();


        ResponseEntity<InventoryRequest> result
                =restTemplate.postForEntity(inventoryPath + "/create",inventoryRequest,InventoryRequest.class);

        return product_transfer;
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        Product existingProduct = getProductById(id);

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());

       // existingProduct.setQuantityInStock(updatedProduct.getQuantityInStock());

        return productRepository.save(existingProduct);
    }


    public void deleteProduct(Long id)
    {

        restTemplate.delete(inventoryPath + "/delete/" + id);
        productRepository.deleteById(id);
    }


    @Transactional
    public Product updateProductWithQuantity(Long id, ProductRequest updatedProduct) {

        Product product =
                Product.builder()
                        .productId(id)
                        .name(updatedProduct.getName())
                        .price(updatedProduct.getPrice())
                        .description(updatedProduct.getDescription())
                        .build();


        InventoryRequest inventoryRequest =
                InventoryRequest.builder()
                        .id(product.getProductId())
                        .quantity(updatedProduct.getQuantity())
                        .build();


      restTemplate.put(inventoryPath + "/update",inventoryRequest,InventoryRequest.class);

        return productRepository.save(product);
    }


}
