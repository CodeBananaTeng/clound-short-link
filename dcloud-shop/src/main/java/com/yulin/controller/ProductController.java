package com.yulin.controller;

import com.yulin.service.ProductService;
import com.yulin.utils.JsonData;
import com.yulin.vo.ProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Auther:LinuxTYL
 * @Date:2022/1/23
 * @Description:
 */
@RestController
@RequestMapping("/api/product/v1")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 查看商品接口列表
     * @return
     */
    @GetMapping("list")
    public JsonData list(){
        List<ProductVO> list = productService.list();
        return JsonData.buildSuccess(list);
    }

    /**
     * 查看商品详情
     * @param productId
     * @return
     */
    @GetMapping("detail/{product_id}")
    public JsonData detail(@PathVariable("product_id") Long productId){
        ProductVO productVO = productService.findDetailById(productId);
        return JsonData.buildSuccess(productVO);
    }

}
