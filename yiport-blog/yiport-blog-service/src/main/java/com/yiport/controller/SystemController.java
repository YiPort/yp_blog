package com.yiport.controller;

import com.yiport.annotation.SystemLog;
import com.yiport.domain.ResponseResult;
import com.yiport.handler.exception.SystemException;
import com.yiport.utils.JwtUtil;
import com.yiport.utils.RedisCache;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static com.yiport.constants.BusinessConstants.BLOG_ADMIN;
import static com.yiport.constants.SystemConstants.IP_ADDRESS;
import static com.yiport.enums.AppHttpCodeEnum.NEED_LOGIN;
import static com.yiport.enums.AppHttpCodeEnum.NO_OPERATOR_AUTH;

/**
 * 系统控制层
 *
 * @author wzy
 * @version 2022/6/29 16:06
 */
@RestController
@RequestMapping("/system")
public class SystemController
{
    @Autowired
    private RedisCache redisCache;

    @Autowired
    private HttpServletRequest httpServletRequest;

    /**
     * 获取ip所在地数据列表
     *
     * @return
     */
    @SystemLog(businessName = "获取ip所在地数据列表")
    @GetMapping("/ip")
    public ResponseResult getIpAddress()
    {
        checkRole();
        Map<String, Object> ipAddress = redisCache.getEntriesCacheMapValue(IP_ADDRESS);
        ArrayList<Map<String, Object>> data = new ArrayList<>();
        ipAddress.forEach((key, value) -> {
            Map<String, Object> map = new HashMap<>(2);
            map.put("name", key.replace('省', ' ').replace('市', ' ').trim());
            map.put("value", value);
            data.add(map);
        });
        return ResponseResult.okResult(data);
    }

    /**
     * 获取缓存监控详细信息
     *
     * @return
     */
    @SystemLog(businessName = "获取缓存监控详细信息")
    @GetMapping("/cache")
    public ResponseResult getCacheInfo()
    {
        checkRole();
        Properties info = (Properties) redisCache.execute((RedisCallback<Object>) RedisServerCommands::info);
        Properties commandStats = (Properties) redisCache.execute((RedisCallback<Object>) connection -> connection.info("commandstats"));
        Object dbSize = redisCache.execute((RedisCallback<Object>) RedisServerCommands::dbSize);

        Map<String, Object> result = new HashMap<>(3);
        result.put("info", info);
        result.put("dbSize", dbSize);

        List<Map<String, String>> pieList = new ArrayList<>();
        if (commandStats != null)
        {
            commandStats.stringPropertyNames().forEach(key -> {
                Map<String, String> data = new HashMap<>(2);
                String property = commandStats.getProperty(key);
                data.put("name", StringUtils.removeStart(key, "cmdstat_"));
                data.put("value", StringUtils.substringBetween(property, "calls=", ",usec"));
                pieList.add(data);
            });
        }
        result.put("commandStats", pieList);
        return ResponseResult.okResult(result);
    }

    /**
     * 权限检查
     */
    private void checkRole()
    {
        // token校验
        String token = httpServletRequest.getHeader("token");
        if (StringUtils.isAnyBlank(token))
        {
            throw new SystemException(NEED_LOGIN, "未登录，请登录后重试");
        }
        Claims claims;
        try
        {
            claims = JwtUtil.parseJWT(token);
        }
        catch (Exception e)
        {
            throw new SystemException(NEED_LOGIN, "未登录，请登录后重试");
        }
        Object admin = redisCache.getCacheObject(BLOG_ADMIN + claims.getId());
        if (Objects.isNull(admin))
        {
            throw new SystemException(NO_OPERATOR_AUTH);
        }
    }
}
