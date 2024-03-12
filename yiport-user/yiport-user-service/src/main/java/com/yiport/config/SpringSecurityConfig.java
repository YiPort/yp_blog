package com.yiport.config;

import com.yiport.config.properties.MySecurityProperties;
import com.yiport.provider.AccountAuthenticationProvider;
import com.yiport.provider.MailAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * SpringSecurity配置
 *
 * @author ultima
 * @version 2022/9/27 14:35
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SpringSecurityConfig
{
    private final com.yiport.filter.JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AccessDeniedHandler accessDeniedHandler;
    private final MailAuthenticationProvider mailAuthenticationProvider;
    private final AccountAuthenticationProvider accountAuthenticationProvider;
    private final MySecurityProperties securityProperties;

    /**
     * 注入 BCryptPasswordEncoder（加密方式配置）
     */
    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
    {
        http
                // 关闭csrf
                .csrf().disable()
                // 不通过Session获取SecurityContext
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                // 对于任何人都可见
                .antMatchers(securityProperties.getMatchers()).permitAll()
                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().authenticated();
        // 将 Token认证过滤器添加到过滤器链中，这里放在 UsernamePasswordAuthenticationFilter之前
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        // 配置异常处理器
        http.exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(authenticationEntryPoint);
        // 开启跨域
        http.cors();
        return http.build();
    }

    /**
     * 认证管理器配置
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationManagerBuilder auth,
                                                       AuthenticationConfiguration authenticationConfiguration) throws Exception
    {
        // 添加认证器
        // 账号密码验证器
        auth.authenticationProvider(accountAuthenticationProvider);
        // 邮箱验证码验证器
        auth.authenticationProvider(mailAuthenticationProvider);
        return authenticationConfiguration.getAuthenticationManager();
    }

}
