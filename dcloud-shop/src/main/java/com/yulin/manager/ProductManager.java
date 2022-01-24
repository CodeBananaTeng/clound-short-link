package com.yulin.manager;

import com.yulin.model.ProductDO;

import java.util.List;

/**
 * @Auther:LinuxTYL
 * @Date:2022/1/23
 * @Description:
 */
public interface ProductManager {
    List<ProductDO> list();

    ProductDO findDetailById(Long productId);

}
