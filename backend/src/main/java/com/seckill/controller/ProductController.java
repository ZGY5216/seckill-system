package com.seckill.controller;

import com.seckill.common.PageResult;
import com.seckill.common.Result;
import com.seckill.domain.vo.ProductDetailVO;
import com.seckill.domain.vo.ProductVO;
import com.seckill.service.ProductService;
import org.springframework.web.bind.annotation.*;

/**
 * 商品浏览接口（公开）。
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public Result<PageResult<ProductVO>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "12") int pageSize,
            @RequestParam(required = false) String keyword) {
        return Result.ok(productService.listProducts(pageNum, pageSize, keyword));
    }

    @GetMapping("/{id}")
    public Result<ProductDetailVO> detail(@PathVariable Long id) {
        return Result.ok(productService.getDetail(id));
    }
}
