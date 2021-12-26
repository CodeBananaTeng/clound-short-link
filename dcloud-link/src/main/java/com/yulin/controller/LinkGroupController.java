package com.yulin.controller;


import com.yulin.controller.request.LinkGroupAddRequest;
import com.yulin.enums.BizCodeEnum;
import com.yulin.service.LinkGroupService;
import com.yulin.utils.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yulin
 * @since 2021-12-26
 */
@RestController
@RequestMapping("/api/group/v1")
public class LinkGroupController {

    @Autowired
    private LinkGroupService linkGroupService;

    /**
     * 创建分组
     * @param addRequest
     * @return
     */
    @PostMapping("/add")
    public JsonData add(@RequestBody LinkGroupAddRequest addRequest){

        int rows = linkGroupService.add(addRequest);
        return rows == 1 ? JsonData.buildSuccess():JsonData.buildResult(BizCodeEnum.GROUP_ADD_FAIL);

    }

    /**
     * 测试接口
     * @return
     */
    @GetMapping("test")
    public JsonData test(){
        return JsonData.buildSuccess();
    }

    /**
     * 根据Id删除接口
     * @return
     */
    @DeleteMapping("/del/{group_id}")
    public JsonData del(@PathVariable("group_id") Long groupId){
        int rows = linkGroupService.del(groupId);
        return rows == 1 ? JsonData.buildSuccess():JsonData.buildResult(BizCodeEnum.GROUP_NOT_EXIST);

    }

}

