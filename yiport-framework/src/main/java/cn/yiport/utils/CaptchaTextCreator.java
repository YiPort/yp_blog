package cn.yiport.utils;

import com.google.code.kaptcha.text.impl.DefaultTextCreator;
import org.springframework.stereotype.Component;

import java.util.Random;

import static cn.yiport.constants.UserConstant.CAPTCHA_RESULT_SPLIT;


/**
 * 生成验证码
 *
 * @author YiPort
 */
@Component
public class CaptchaTextCreator extends DefaultTextCreator
{
    @Override
    public String getText()
    {
        Random random = new Random();
        int a = random.nextInt(70) + 15;
        int b = random.nextInt(16);
        String result;
        String captchaCode;
        if ((a & 1) == 1)
        {
            result = String.valueOf(a + b);
            captchaCode = a + " + " + b;
        }
        else
        {
            result = String.valueOf(a - b);
            captchaCode = a + " - " + b;
        }
        return captchaCode + CAPTCHA_RESULT_SPLIT + result;
    }
}
