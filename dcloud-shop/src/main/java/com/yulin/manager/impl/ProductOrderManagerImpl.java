package com.yulin.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yulin.manager.ProductOrderManager;
import com.yulin.mapper.ProductOrderMapper;
import com.yulin.model.ProductOrderDO;
import com.yulin.vo.ProductOrderVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Auther:LinuxTYL
 * @Date:2022/2/1
 * @Description:
 */
@Component
public class ProductOrderManagerImpl implements ProductOrderManager {

    @Autowired
    private ProductOrderMapper productOrderMapper;

    @Override
    public int add(ProductOrderDO productOrderDO) {
        int rows = productOrderMapper.insert(productOrderDO);
        return rows;
    }

    @Override
    public ProductOrderDO findByOutTradeNoAndAccountNo(String outTradeNo, Long accountNo) {
        ProductOrderDO productOrderDO = productOrderMapper.selectOne(new QueryWrapper<ProductOrderDO>()
                .eq("out_trade_no", outTradeNo)
                .eq("account_no", accountNo)
                .eq("del",0));

        return productOrderDO;
    }

    @Override
    public int updateOrderPayStatus(String outTradeNo, Long accountNo, String newState, String oldState) {

        int rows = productOrderMapper.update(null, new UpdateWrapper<ProductOrderDO>()
                .eq("out_trade_no", outTradeNo)
                .eq("account_no", accountNo)
                .eq("state", oldState)
                .set("state", newState));
        return rows;
    }

    @Override
    public Map<String, Object> page(int page, int size, Long accountNo, String state) {
        Page<ProductOrderDO> pageInfo = new Page<>(page,size);
        IPage<ProductOrderDO> orderDOIPage;
        //是否按照状态查询
        if (StringUtils.isBlank(state)){
            orderDOIPage = productOrderMapper.selectPage(pageInfo, new QueryWrapper<ProductOrderDO>()
                    .eq("account_no", accountNo)
                    .eq("del",0));
        }else {
            orderDOIPage = productOrderMapper.selectPage(pageInfo, new QueryWrapper<ProductOrderDO>()
                    .eq("account_no", accountNo)
                    .eq("state",state)
                    .eq("del",0));
        }
        List<ProductOrderDO> orderDOIPageRecords = orderDOIPage.getRecords();
        List<ProductOrderVO> productOrderVOList = orderDOIPageRecords.stream().map(obj -> {
            ProductOrderVO productOrderVO = new ProductOrderVO();
            BeanUtils.copyProperties(obj, productOrderVO);
            return productOrderVO;
        }).collect(Collectors.toList());
        Map<String,Object> pageMap = new HashMap<>();
        pageMap.put("total_record",orderDOIPage.getTotal());
        pageMap.put("total_page",orderDOIPage.getPages());
        pageMap.put("current_data",productOrderVOList);
        return pageMap;
    }

    @Override
    public int del(Long productOrderId, Long accountNo) {
        int rows = productOrderMapper.update(null, new UpdateWrapper<ProductOrderDO>()
                .eq("id", productOrderId)
                .eq("account_no", accountNo)
                .set("del", 1));
        return rows;
    }
}
