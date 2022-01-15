package com.yulin.controller.request;

import lombok.Data;

/**
 * @Auther:LinuxTYL
 * @Date:2022/1/3
 * @Description:
 */
@Data
public class ShortLinkUpdateRequest {

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

    /**
     * 标题
     */
    private String title;

    /**
     * 域名ID
     */
    private Long domainId;

    /**
     * 域名类型
     */
    private String domainType;

}
