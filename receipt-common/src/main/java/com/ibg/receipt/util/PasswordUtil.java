package com.ibg.receipt.util;

import java.util.Random;

public class PasswordUtil {
    public static String getPassWord(int len){
        int i; //生成的随机数
        int count = 0; //生成的密码的长度
        // 密码字典
        char[] str = {
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                '~', '!', '@', '#', '$', '%', '^', '-', '+'
        };
        StringBuffer stringBuffer = new StringBuffer("");
        Random r = new Random();
        while(count < len){
        //生成 0 ~ 密码字典-1之间的随机数
            i = r.nextInt(str.length);
            stringBuffer.append(str[i]);
            count ++;
        }
        return stringBuffer.toString();
    }

    public static void main(String[] args) {
        System.out.println(getPassWord(10));
    }

}
