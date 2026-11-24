package com.seckill.service;

import com.seckill.common.PageResult;
import com.seckill.domain.vo.ProductDetailVO;
import com.seckill.domain.vo.ProductVO;

/**
 * 商品浏览服务。
 */
public interface ProductService {

    PageResult<ProductVO> listProducts(int pageNum, int pageSize, String keyword);

    ProductDetailVO getDetail(Long id);
}
