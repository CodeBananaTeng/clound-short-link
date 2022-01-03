package com.yulin.enums;

import lombok.Getter;

/**
 * @Auther:LinuxTYL
 * @Date:2021/12/14
 * @Description:
 */
public enum BizCodeEnum {
    /**
     * 短链分组
     */
    GROUP_REPEAT(23001,"分组名重复"),
    GROUP_OPER_FAIL(23503,"分组名操作失败"),
    GROUP_NOT_EXIST(23404,"分组不存在"),
    GROUP_ADD_FAIL(23405,"分组添加失败"),

    /**
     *验证码
     */
    CODE_TO_ERROR(240001,"接收号码不合规"),
    CODE_LIMITED(240002,"验证码发送过快"),
    CODE_ERROR(240003,"验证码错误"),
    CODE_CAPTCHA_ERROR(240101,"图形验证码错误"),
    /**
     * 账号
     */
    ACCOUNT_REPEAT(250001,"账号已经存在"),
    ACCOUNT_UNREGISTER(250002,"账号不存在"),
    ACCOUNT_PWD_ERROR(250003,"账号或者密码错误"),
    ACCOUNT_UNLOGIN(250004,"账号未登录"),
    /**
     * 短链
     */
    SHORT_LINK_NOT_EXIST(260404,"短链不存在"),
    /**
     * 订单
     */
    ORDER_CONFIRM_PRICE_FAIL(280002,"创建订单-验价失败"),
    ORDER_CONFIRM_REPEAT(280008,"订单恶意-᯿复提交"),
    ORDER_CONFIRM_TOKEN_EQUAL_FAIL(280009,"订单令牌缺少"),
    ORDER_CONFIRM_NOT_EXIST(280010,"订单不存在"),
    /**
     * ⽀付
     */
    PAY_ORDER_FAIL(300001,"创建⽀付订单失败"),
    PAY_ORDER_CALLBACK_SIGN_FAIL(300002,"⽀付订单回调验证签失败"),
    PAY_ORDER_CALLBACK_NOT_SUCCESS(300003,"⽀付宝回调更新订单失败"),
    PAY_ORDER_NOT_EXIST(300005,"订单不存在"),
    PAY_ORDER_STATE_ERROR(300006,"订单状态不正常"),
    PAY_ORDER_PAY_TIMEOUT(300007,"订单⽀付超时"),
    /**
     * 流控操作
     */
    CONTROL_FLOW(500101,"限流控制"),
    CONTROL_DEGRADE(500201,"降级控制"),
    CONTROL_AUTH(500301,"认证控制"),
    /**
     * 流ᰁ包操作
     */
    TRAFFIC_FREE_NOT_EXIST(600101,"免费流ᰁ包不存在，联系客服"),
    TRAFFIC_REDUCE_FAIL(600102,"流ᰁ不⾜，扣减失败"),
    TRAFFIC_EXCEPTION(600103,"流ᰁ包数据异常,⽤户⽆流ᰁ包"),
    /**
     * 通⽤操作码
     */
    OPS_REPEAT(110001,"᯿复操作"),
    OPS_NETWORK_ADDRESS_ERROR(110002,"⽹络地址错误"),
    /**
     * ⽂件相关
     */
    FILE_UPLOAD_USER_IMG_FAIL(700101,"⽤户头像⽂件上传失败"),

    /**
     * 数据库路由信息
     */
    DB_ROUTE_NOT_FOUND(800101,"数据库未找到"),

    /**
     * MQ消费异常
     */
    MQ_CONSUME_EXCEPTION(900101,"消费者消费异常");

    @Getter
    private String message;
    @Getter
    private int code;
    private BizCodeEnum(int code, String message){
        this.code = code;
        this.message = message;
    }
}
