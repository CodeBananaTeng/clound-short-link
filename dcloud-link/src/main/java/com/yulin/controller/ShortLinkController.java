package com.yulin.controller;


import com.yulin.controller.request.ShortLinkAddRequest;
import com.yulin.service.ShortLinkService;
import com.yulin.utils.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yulin
 * @since 2021-12-26
 */
@RestController
@RequestMapping("/api/link/v1")
public class ShortLinkController {

    @Autowired
    private ShortLinkService shortLinkService;

    @PostMapping("add")
    public JsonData createShortLink(@RequestBody ShortLinkAddRequest request){
        JsonData jsonData = shortLinkService.createShortLink(request);
        return jsonData;
    }

}

