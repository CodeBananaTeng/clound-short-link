package com.yulin.component;

import com.yulin.enums.ProductOrderPayTypeEnum;
import com.yulin.vo.PayInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther:LinuxTYL
 * @Date:2022/3/21
 * @Description:
 */
@Component
@Slf4j
public class PayFactory {

    @Autowired
    private AliPayStrategy aliPayStrategy;

    @Autowired
    private WechatPayStrategy wechatPayStrategy;

    /**
     * 创建支付，采用简单工厂模式
     * @param payInfoVO
     * @return
     */
    public String pay(PayInfoVO payInfoVO){
        String payType = payInfoVO.getPayType();
        if (ProductOrderPayTypeEnum.ALI_PAY.name().equals(payType)){
            //支付宝支付
            PayStrategyContext payStrategyContext = new PayStrategyContext(aliPayStrategy);
            return payStrategyContext.executeUnifiedOrder(payInfoVO);

        }else if (ProductOrderPayTypeEnum.WECHAT_PAY.name().equals(payType)){
            //微信支付
            PayStrategyContext payStrategyContext = new PayStrategyContext(wechatPayStrategy);
            return payStrategyContext.executeUnifiedOrder(payInfoVO);
        }
        return "";
    }

    /**
     * 关闭订单
     * @param payInfoVO
     * @return
     */
    public String closeOrder(PayInfoVO payInfoVO){
        String payType = payInfoVO.getPayType();
        if (ProductOrderPayTypeEnum.ALI_PAY.name().equals(payType)){
            //支付宝支付
            PayStrategyContext payStrategyContext = new PayStrategyContext(aliPayStrategy);
            return payStrategyContext.executeCloseOrder(payInfoVO);

        }else if (ProductOrderPayTypeEnum.WECHAT_PAY.name().equals(payType)){
            //微信支付
            PayStrategyContext payStrategyContext = new PayStrategyContext(wechatPayStrategy);
            return payStrategyContext.executeCloseOrder(payInfoVO);
        }
        return "";
    }

    /**
     * 查询支付状态
     * @param payInfoVO
     * @return
     */
    public String queryOrderStatus(PayInfoVO payInfoVO){
        String payType = payInfoVO.getPayType();
        if (ProductOrderPayTypeEnum.ALI_PAY.name().equals(payType)){
            //支付宝支付
            PayStrategyContext payStrategyContext = new PayStrategyContext(aliPayStrategy);
            return payStrategyContext.executeQueryPayStateOrder(payInfoVO);

        }else if (ProductOrderPayTypeEnum.WECHAT_PAY.name().equals(payType)){
            //微信支付
            PayStrategyContext payStrategyContext = new PayStrategyContext(wechatPayStrategy);
            return payStrategyContext.executeQueryPayStateOrder(payInfoVO);
        }
        return "";
    }

    /**
     * 退款接口
     * @param payInfoVO
     * @return
     */
    public String refund(PayInfoVO payInfoVO){
        String payType = payInfoVO.getPayType();
        if (ProductOrderPayTypeEnum.ALI_PAY.name().equals(payType)){
            //支付宝支付
            PayStrategyContext payStrategyContext = new PayStrategyContext(aliPayStrategy);
            return payStrategyContext.executeRefundOrder(payInfoVO);

        }else if (ProductOrderPayTypeEnum.WECHAT_PAY.name().equals(payType)){
            //微信支付
            PayStrategyContext payStrategyContext = new PayStrategyContext(wechatPayStrategy);
            return payStrategyContext.executeRefundOrder(payInfoVO);
        }
        return "";
    }

}
