package com.yulin.exception;

import com.yulin.utils.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Auther:LinuxTYL
 * @Date:2021/12/14
 * @Description:
 */
@ControllerAdvice//使用这个注解的时候就需要加上@ResponseBody注解，使用下面的时候就不需要加上这个注解
//@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public JsonData handler(Exception e){
        if (e instanceof BizException){
            BizException bizException = (BizException) e;
            log.error("[业务异常]{}",e);
            return JsonData.buildCodeAndMsg(bizException.getCode(), bizException.getMsg());
        }else {
            log.error("系统异常{}",e);
            return JsonData.buildError("系统异常");
        }
    }

}
