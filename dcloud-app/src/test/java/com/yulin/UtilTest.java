package com.yulin;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.Manufacturer;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @Auther:LinuxTYL
 * @Date:2022/5/11
 * @Description:
 */

@Slf4j
public class UtilTest {

    @Test
    public void testUserAgentUtil(){
        String userAgentStr =  "Mozilla/5.0 (Linux;Android 10; LIO-AN00 Build/HUAWEILIO-AN00; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0Chrome/77.0.3865.120 MQQBrowser/6.2 TBS/045713Mobile Safari/537.36 MMWEBID/3189MicroMessenger/8.0.11.1980(0x28000B51) Process/toolsWeChat/arm64 Weixin NetType/WIFI Language/zh_CNABI/arm64";
        UserAgent userAgent = UserAgent.parseUserAgentString(userAgentStr);
        Browser browser = userAgent.getBrowser();
        OperatingSystem operatingSystem = userAgent.getOperatingSystem();

        //获取浏览器厂商
        String browserName = browser.getGroup().getName();
        String os = operatingSystem.getGroup().getName();
        String manufacturer = operatingSystem.getManufacturer().getName();
        String deviceName = operatingSystem.getDeviceType().getName();
        System.out.println("browserName="+browserName+",os="+os+",manuFacture="+manufacturer+",deviceType="+deviceName);
    }

}
