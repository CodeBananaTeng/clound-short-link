package com.yulin.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Auther:LinuxTYL
 * @Date:2022/4/3
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrafficPageRequest {

    private int page;

    private int size;

}
