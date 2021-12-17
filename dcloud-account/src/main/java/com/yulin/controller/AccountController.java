package com.yulin.controller;


import com.yulin.enums.BizCodeEnum;
import com.yulin.service.FileService;
import com.yulin.utils.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
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

    /**
     * 文件上传最大1M
     * 文件格式扩展名判断
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public JsonData uploadUserImg(@RequestPart("file")MultipartFile file){
        String result = fileService.uploadUserImg(file);
        return result != null? JsonData.buildSuccess(result):JsonData.buildResult(BizCodeEnum.FILE_UPLOAD_USER_IMG_FAIL);
    }

}

