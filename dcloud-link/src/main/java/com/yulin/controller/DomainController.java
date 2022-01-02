package com.yulin.controller;

import com.yulin.service.DomainService;
import com.yulin.utils.JsonData;
import com.yulin.vo.DomainVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Auther:LinuxTYL
 * @Date:2022/1/2
 * @Description:
 */
@RestController
@RequestMapping("/api/domain/v1")
public class DomainController {

    @Autowired
    private DomainService domainService;

    /**
     * 列举全部可用域名列表
     * @return
     */
    @GetMapping("list")
    public JsonData listAll(){
        List<DomainVO> list = domainService.listAll();
        return JsonData.buildSuccess(list);
    }

}
