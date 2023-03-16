package com.example.warehouse.controller;

import com.example.domain.security.Role;
import com.example.warehouse.service.ProductService;
import com.example.domain.warehouse.Product;
import com.example.dto.ProductInfo;
import com.example.infrastructure.response.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.List;


/**
 * 产品
 */
@RestController
@RequestMapping("/restful")
public class ProductController {

    @Autowired
    ProductService service;

    /**
     * 获取仓库中所有的货物信息
     */
    @GetMapping("/products")
    public List<ProductInfo> getAllProducts() {
        return service.getAllProducts();
    }

    /**
     * 获取仓库中指定的货物信息
     */
    @GetMapping("/products/{id}")
    public ProductInfo getProduct(@PathVariable("id") Integer id) {
        return service.getProduct(id);
    }

    /**
     * 更新产品信息
     */
    @PutMapping("/products")
    @RolesAllowed(Role.ADMIN)
    @PreAuthorize("#oauth2.hasAnyScope('BROWSER')")
    public String updateProduct(@Valid @RequestBody Product product) {
        service.updateProduct(product);
        return CommonResponse.success();
    }

    /**
     * 创建新的产品
     */
    @PostMapping("/products")
    @RolesAllowed(Role.ADMIN)
    @PreAuthorize("#oauth2.hasAnyScope('BROWSER')")
    public ProductInfo createProduct(@Valid @RequestBody ProductInfo product) {
        return service.saveProduct(product);
    }

    /**
     * 删除产品
     */
    @DeleteMapping("/products/{id}")
    @RolesAllowed(Role.ADMIN)
    @PreAuthorize("#oauth2.hasAnyScope('BROWSER')")
    public String removeProduct(@PathVariable("id") Integer id) {
        service.removeProduct(id);
        return CommonResponse.success();
    }
}
