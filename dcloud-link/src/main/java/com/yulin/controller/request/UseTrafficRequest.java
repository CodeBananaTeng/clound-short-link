package com.yulin.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Auther:LinuxTYL
 * @Date:2022/4/3
 * @Description:
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UseTrafficRequest {

    private Long accountNo;

    /**
     * 业务id，短链码，或者其他的id
     */
    private String bizId;




}
