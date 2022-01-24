package com.yulin.service.impl;

import com.yulin.manager.ProductManager;
import com.yulin.model.ProductDO;
import com.yulin.service.ProductService;
import com.yulin.vo.ProductVO;
import groovy.util.logging.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther:LinuxTYL
 * @Date:2022/1/23
 * @Description:
 */
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductManager productManager;

    @Override
    public List<ProductVO> list() {
        List<ProductDO> list = productManager.list();
        List<ProductVO> collect = list.stream().map(obj -> beanProcess(obj)).collect(Collectors.toList());
        return collect;
    }

    @Override
    public ProductVO findDetailById(Long productId) {
        ProductDO productDO = productManager.findDetailById(productId);
        ProductVO productVO = beanProcess(productDO);
        return productVO;
    }

    private ProductVO beanProcess(ProductDO productDO){
        ProductVO productVO = new ProductVO();
        BeanUtils.copyProperties(productDO,productVO);
        return productVO;
    }
}
