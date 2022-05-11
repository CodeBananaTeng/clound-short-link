package com.yulin.util;

import com.yulin.model.DevicesInfoDO;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.commons.lang3.StringUtils;

import java.security.MessageDigest;
import java.util.Map;

/**
 * @Auther:LinuxTYL
 * @Date:2022/5/10
 * @Description:
 */
public class DeviceUtil {

    /**
     * ⽣成web设备唯⼀ID
     * @param map
     * @return
     */
    public static String geneWebUniqueDeviceId(Map<String,String> map){
        String deviceId = MD5(map.toString());
        return deviceId;
    }
    /**
     * MD5加密
     *
     * @param data
     * @return
     */
    public static String MD5(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(data.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte item : array) {
                sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString().toUpperCase();
        } catch (Exception exception) {
        }
        return null;
    }

    /**
     * 获取浏览器对象
     * @param agent
     * @return
     */
    public static Browser getBrowser(String agent){
        UserAgent userAgent = UserAgent.parseUserAgentString(agent);
        return userAgent.getBrowser();
    }

    /**
     * 获取操作系统
     * @param agent
     * @return
     */
    public static OperatingSystem getOperatingSystem(String agent){
        UserAgent userAgent = UserAgent.parseUserAgentString(agent);
        return userAgent.getOperatingSystem();
    }

    /**
     * 获取浏览器名称
     * @param agent
     * @return fireFox Chrome
     */
    public static String getBrowserName(String agent){
        return getBrowser(agent).getGroup().getName();
    }

    /**
     * 获取设备类型
     * @param agent
     * @return
     */
    public static String getDeviceType(String agent){
        return getOperatingSystem(agent).getDeviceType().getName();
    }

    /**
     * 获取OS: WINDOWS,IOS,ANDROID
     * @param agent
     * @return
     */
    public static String getOs(String agent){
        return getOperatingSystem(agent).getGroup().getName();
    }

    /**
     * 获取设备厂家
     * @param agent
     * @return
     */
    public static String getDeviceManufacture(String agent){
        return getOperatingSystem(agent).getManufacturer().getName();
    }

    /**
     * 获取操作系统版本
     * @param userAgent
     * @return
     */
    public static String getOSVersion(String
                                              userAgent) {
        String osVersion = "";
        if(StringUtils.isBlank(userAgent)) {
            return osVersion;
        }
        String[] strArr = userAgent.substring(userAgent.indexOf("(")+1,userAgent.indexOf(")")).split(";");
        if(null == strArr || strArr.length == 0) {
            return osVersion;
        }
        osVersion = strArr[1];
        return osVersion;
    }

    /**
     * 解析对象
     * @param agent
     * @return
     */
    public static DevicesInfoDO getDeviceInfo(String agent){
        UserAgent userAgent = UserAgent.parseUserAgentString(agent);
        Browser browser = userAgent.getBrowser();
        OperatingSystem operatingSystem = userAgent.getOperatingSystem();

        String browserName = browser.getGroup().getName();
        String os = operatingSystem.getGroup().getName();
        String manufacturer = operatingSystem.getManufacturer().getName();
        String deviceType = operatingSystem.getDeviceType().getName();

        DevicesInfoDO devicesInfoDO = DevicesInfoDO.builder()
                .browserName(browserName)
                .deviceManufacturer(manufacturer)
                .deviceType(deviceType)
                .os(os)
                .osVersion(getOSVersion(agent))
                .build();
        return devicesInfoDO;
    }

}
