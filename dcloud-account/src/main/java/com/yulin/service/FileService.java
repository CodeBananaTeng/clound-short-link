package com.yulin.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @Auther:LinuxTYL
 * @Date:2021/12/18
 * @Description:
 */
public interface FileService {

    /**
     * 文件上传
     * @param file
     * @return
     */
    String uploadUserImg(MultipartFile file);

}
