package com.yulin.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectResult;
import com.yulin.config.OSSConfig;
import com.yulin.service.FileService;
import com.yulin.utils.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Auther:LinuxTYL
 * @Date:2021/12/18
 * @Description:
 */
@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Autowired
    private OSSConfig ossConfig;

    @Override
    public String uploadUserImg(MultipartFile file) {
        String bucketname = ossConfig.getBucketname();
        String endpoint = ossConfig.getEndpoint();
        String accessKeyId = ossConfig.getAccessKeyId();
        String accessKeySecret = ossConfig.getAccessKeySecret();

        //oss文件客户端构建
        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);

        //获取文件原始名称 对用户的文件进行归档 user/2021/12/12/sdasda
        String originalFilename = file.getOriginalFilename();
        //jdk获取日期格式
        LocalDateTime ldt = LocalDateTime.now();
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        String folder = pattern.format(ldt);
        String fileName = CommonUtil.generateUUID();
        //获取到文件扩展名
        String extendsion = originalFilename.substring(originalFilename.lastIndexOf("."));

        //在oss上的bucket创建文件夹
        String newFileName = "user/"+ folder + "/" + fileName + extendsion;
        try {
            PutObjectResult putObjectResult = ossClient.putObject(bucketname, newFileName, file.getInputStream());
            //拼装返回路径
            if (putObjectResult !=null){
                String imgurl = "https://"+bucketname+"."+endpoint+"/"+newFileName;
                return imgurl;
            }
        } catch (IOException e) {
            log.error("文件上传失败:{}",e.getMessage());
        }finally {
            ossClient.shutdown();
        }
        return null;
    }

}
