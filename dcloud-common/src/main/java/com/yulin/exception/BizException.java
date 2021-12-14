package com.yulin.exception;

import com.yulin.enums.BizCodeEnum;
import lombok.Data;

/**
 * @Auther:LinuxTYL
 * @Date:2021/12/14
 * @Description:
 */
@Data
public class BizException extends RuntimeException{
    private Integer code;
    private String msg;

    public BizException(Integer code, String message) {
        super(message);
        this.code = code;
        this.msg = message;
    }
    public BizException(BizCodeEnum bizCodeEnum) {
        super(bizCodeEnum.getMessage());
        this.code = bizCodeEnum.getCode();
        this.msg = bizCodeEnum.getMessage();
    }
}
