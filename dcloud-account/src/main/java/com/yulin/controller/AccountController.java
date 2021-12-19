package com.yulin.controller;


import com.yulin.controller.request.AccountLoginRequest;
import com.yulin.controller.request.AccountRegisterRequest;
import com.yulin.enums.BizCodeEnum;
import com.yulin.service.AccountService;
import com.yulin.service.FileService;
import com.yulin.utils.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ⼆当家⼩D
 * @since 2021-12-15
 */
@RestController
@RequestMapping("/api/v1/account")
public class AccountController {

    @Autowired
    private FileService fileService;

    @Autowired
    private AccountService accountService;

    /**
     * 文件上传最大1M
     * 文件格式扩展名判断
     * @param file
     * @return
     */
    @PostMapping("upload")
    public JsonData uploadUserImg(@RequestPart("file")MultipartFile file){
        String result = fileService.uploadUserImg(file);
        return result != null? JsonData.buildSuccess(result):JsonData.buildResult(BizCodeEnum.FILE_UPLOAD_USER_IMG_FAIL);
    }

    /**
     * 用户注册
     * @param registerRequest
     * @return
     */
    @PostMapping("register")
    public JsonData register(@RequestBody AccountRegisterRequest registerRequest){
        JsonData jsonData = accountService.register(registerRequest);
        return jsonData;
    }

    @PostMapping("login")
    public JsonData login(@RequestBody AccountLoginRequest request){
        JsonData jsonData = accountService.login(request);
        return jsonData;
    }

}

