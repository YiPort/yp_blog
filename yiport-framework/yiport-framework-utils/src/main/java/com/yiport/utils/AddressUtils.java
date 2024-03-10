package com.yiport.utils;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.net.NetUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HtmlUtil;
import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.yiport.constants.SystemConstants.*;

/**
 * 获取地址类
 */
@Slf4j
@NoArgsConstructor
@Component
public class AddressUtils
{

    @Autowired
    private RedisCache redisCache;

    // IP地址查询
    public static final String IP_URL = "http://whois.pconline.com.cn/ipJson.jsp";

    private static final ObjectMapper OBJECT_MAPPER = SpringUtil.getBean(ObjectMapper.class);

    public String getRealAddressByIP(String ip)
    {
        String address = UNKNOWN;
        if (StringUtils.isBlank(ip))
        {
            return address;
        }
        // 内网不查询
        ip = "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : HtmlUtil.cleanHtmlTag(ip);
        if (NetUtil.isInnerIP(ip))
        {
            return INTRANET_IP;
        }
        try
        {
            String rspStr = HttpUtil.createGet(IP_URL)
                    .body("ip=" + ip + "&json=true", "GBK")
                    .timeout(5000)
                    .execute()
                    .body();
            if (StringUtils.isEmpty(rspStr))
            {
                log.error("获取地理位置异常 {}", ip);
                return UNKNOWN;
            }
            Dict obj = parseMap(rspStr);
            String pro = obj.getStr("pro");
            String city = obj.getStr("city");
            String err = obj.getStr("err");
            // 国外ip
            if (err.equals("noprovince"))
            {
                String addr = obj.getStr("addr");
                redisCache.incrHash(FOREIGN_IP, addr);
                return String.format("%s", addr);
            }
            // 不能获取city的ip
            if (err.equals("nocity"))
            {
                redisCache.incrHash(IP_ADDRESS, pro);
                return String.format("%s", pro);
            }
            // 正常获取的ip
            redisCache.incrHash(IP_ADDRESS, pro);
            return String.format("%s %s", pro, city);
        }
        catch (Exception e)
        {
            log.error("获取地理位置异常 {}", ip);
        }
        return UNKNOWN;
    }

    public static Dict parseMap(String text)
    {
        if (StringUtils.isBlank(text))
        {
            return null;
        }
        try
        {
            return OBJECT_MAPPER.readValue(text, OBJECT_MAPPER.getTypeFactory().constructType(Dict.class));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
