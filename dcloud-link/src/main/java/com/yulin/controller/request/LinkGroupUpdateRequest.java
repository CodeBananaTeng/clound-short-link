package com.yulin.controller.request;

import lombok.Data;

/**
 * @Auther:LinuxTYL
 * @Date:2021/12/26
 * @Description:
 */
@Data
public class LinkGroupUpdateRequest {

    /**
     * 组id
     */
    private Long id;

    /**
     * 组名
     */
    private String title;

}
