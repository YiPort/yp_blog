package com.yiport.provider;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

import java.util.Collection;

/**
 * 邮箱验证码认证凭证
 */
public class MailAuthenticationToken extends AbstractAuthenticationToken
{

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    private final Object principal;

    private String captcha;

    /**
     * 此构造函数用来初始化未授信凭据.
     */
    public MailAuthenticationToken(Object principal, String captcha)
    {
        super(null);
        this.principal = principal;
        this.captcha = captcha;
        setAuthenticated(false);
    }

    /**
     * 此构造函数用来初始化授信凭据.
     *
     * @param principal
     * @param captcha
     * @param authorities
     */
    public MailAuthenticationToken(Object principal, String captcha, Collection<? extends GrantedAuthority> authorities)
    {
        super(authorities);
        this.principal = principal;
        this.captcha = captcha;
        super.setAuthenticated(true);
    }

    public Object getCredentials()
    {
        return this.captcha;
    }

    public Object getPrincipal()
    {
        return this.principal;
    }

    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException
    {
        if (isAuthenticated)
        {
            throw new IllegalArgumentException(
                    "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }
        super.setAuthenticated(false);
    }

    @Override
    public void eraseCredentials()
    {
        super.eraseCredentials();
        captcha = null;
    }
}

