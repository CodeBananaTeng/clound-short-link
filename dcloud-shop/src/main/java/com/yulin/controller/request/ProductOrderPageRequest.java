package com.yulin.controller.request;

import lombok.Data;

/**
 * @Auther:LinuxTYL
 * @Date:2022/1/3
 * @Description:
 */
@Data
public class ProductOrderPageRequest {

    /**
     * 状态
     */
    private String state;

    /**
     * 第几页
     */
    private int page;

    /**
     * 每页多少条
     */
    private int size;

}
