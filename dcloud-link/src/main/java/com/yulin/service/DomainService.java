package com.yulin.service;

import com.yulin.vo.DomainVO;

import java.util.List;

/**
 * @Auther:LinuxTYL
 * @Date:2022/1/3
 * @Description:
 */
public interface DomainService {

    /**
     * 列举全部可用域名
     * @return
     */
    List<DomainVO> listAll();

}
