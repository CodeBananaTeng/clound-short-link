package com.yulin.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yulin.manager.ProductManager;
import com.yulin.mapper.ProductMapper;
import com.yulin.model.ProductDO;
import groovy.util.logging.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Auther:LinuxTYL
 * @Date:2022/1/23
 * @Description:
 */
@Component
@Slf4j
public class ProductManagerImpl implements ProductManager {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<ProductDO> list() {
        return productMapper.selectList(null);
    }

    @Override
    public ProductDO findDetailById(Long productId) {
        return productMapper.selectOne(new QueryWrapper<ProductDO>().eq("id",productId));
    }
}
