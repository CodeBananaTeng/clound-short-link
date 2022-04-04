package com.yulin.feign;

import com.yulin.utils.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Auther:LinuxTYL
 * @Date:2022/4/4
 * @Description:
 */
@FeignClient(name = "dcloud-shop-service")
public interface ProductFeignService {

    /**
     * 获取流量包商品详情
     * @param productId
     * @return
     */
    @GetMapping("/api/product/v1/detail/{product_id}")
    JsonData detail(@PathVariable("product_id")long productId);

}
