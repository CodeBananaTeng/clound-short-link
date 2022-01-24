package com.yulin.service;

import com.yulin.vo.ProductVO;

import java.util.List;

/**
 * @Auther:LinuxTYL
 * @Date:2022/1/23
 * @Description:
 */
public interface ProductService {
    List<ProductVO> list();

    ProductVO findDetailById(Long productId);

}
