package com.yulin.controller;


import com.yulin.controller.request.ShortLinkAddRequest;
import com.yulin.controller.request.ShortLinkDelRequest;
import com.yulin.controller.request.ShortLinkPageRequest;
import com.yulin.controller.request.ShortLinkUpdateRequest;
import com.yulin.service.ShortLinkService;
import com.yulin.utils.JsonData;
import com.yulin.vo.ShortLinkVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

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

    @Value("${rpc.token}")
    private String rpcToken;

    @GetMapping("check")
    JsonData check(@RequestParam("shortLinkCode")String shortLinkCode, HttpServletRequest request){

        String token = request.getHeader("rpc-token");
        if (rpcToken.equalsIgnoreCase(token)){
            ShortLinkVO shortLinkVO = shortLinkService.parseShortLinkCode(shortLinkCode);
            return shortLinkVO == null ? JsonData.buildError("短链码不存在"):JsonData.buildSuccess();
        }else {
            return JsonData.buildError("非法访问");
        }
    }

    /**
     * 新增短链
     * @param request
     * @return
     */
    @PostMapping("add")
    public JsonData createShortLink(@RequestBody ShortLinkAddRequest request){
        JsonData jsonData = shortLinkService.createShortLink(request);
        return jsonData;
    }

    /**
     * 分页查找短链
     */
    @RequestMapping("page")
    public JsonData pageByGroupId(@RequestBody ShortLinkPageRequest request){
        Map<String ,Object> result = shortLinkService.pageByGroupId(request);
        return JsonData.buildSuccess(result);
    }

    /**
     * 删除短链码
     * @param request
     * @return
     */
    @PostMapping("del")
    public JsonData del(@RequestBody ShortLinkDelRequest request){
        JsonData jsonData = shortLinkService.del(request);
        return jsonData;
    }

    /**
     * 更新短链
     * @param request
     * @return
     */
    @PostMapping("update")
    public JsonData update(@RequestBody ShortLinkUpdateRequest request){
        JsonData jsonData = shortLinkService.update(request);
        return jsonData;
    }

}

