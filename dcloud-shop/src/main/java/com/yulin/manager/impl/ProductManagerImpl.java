package com.yulin.manager.impl;

import com.yulin.manager.ProductManager;
import com.yulin.mapper.ProductMapper;
import groovy.util.logging.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Component;

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

}
