package com.yulin.controller.request;

import lombok.Data;

/**
 * @Auther:LinuxTYL
 * @Date:2022/1/3
 * @Description:
 */
@Data
public class ShortLinkDelRequest {

    /**
     * 组
     */
    private Long groupId;

    /**
     * 映射ID
     */
    private Long mappingId;

    /**
     * 短链码
     */
    private String code;

}
