package com.project.concurrency.utils;

import org.apache.commons.codec.digest.DigestUtils;


public class MD5Util {

    public static String md5(String str) {
        return DigestUtils.md5Hex(str);
    }

    private static final String salt = "1a2b3c4d";

    /**
     * 第一次加密
     *
     * @param inputPass
     * @return java.lang.String
     * @author LC
     * @operation add
     * @date 4:49 下午 2022/3/1
     **/
    public static String inputPassToFromPass(String inputPass) {
        String str = ""+ salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    /**
     * 第二次加密
     * @author LC
     * @operation add
     * @date 4:52 下午 2022/3/1
     * @param formPass
     * @param salt
     * @return java.lang.String
     **/
    public static String formPassToDBPass(String formPass, String salt) {
        String str = ""+ salt.charAt(0) + salt.charAt(2) + formPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    public static String inputPassToDBPass(String inputPass, String salt) {
        String fromPass = inputPassToFromPass(inputPass);
        return formPassToDBPass(fromPass, salt);
    }

    public static void main(String[] args) {
        // d3b1294a61a07da9b49b6e22b2cbd7f9
        System.out.println(MD5Util.inputPassToFromPass("123456"));
        System.out.println(MD5Util.formPassToDBPass("d3b1294a61a07da9b49b6e22b2cbd7f9","1a2b3c4d"));
        System.out.println(MD5Util.inputPassToDBPass("123456","1a2b3c4d"));
    }

}