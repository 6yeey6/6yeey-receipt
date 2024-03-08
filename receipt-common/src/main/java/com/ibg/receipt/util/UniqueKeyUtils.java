package com.ibg.receipt.util;

import java.util.UUID;

public class UniqueKeyUtils {

    public static String uniqueKey() {
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }

    public static String lowerUniqueKey(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static void main(String[] args) {
        System.out.println(UniqueKeyUtils.uniqueKey());
    }
}
