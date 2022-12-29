package com.project.concurrency.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorUtil {
    private static final Pattern mobile_patten = Pattern.compile("^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$");
    public static boolean isMobile(String mobile){
        if(mobile.isEmpty()) return false;
        Matcher matcher = mobile_patten.matcher(mobile);
        return matcher.matches(); // 直接判断了是否match
    }
}
