package com.example.util;

public class RedisKeyUtil {
    private static final String SPLIT = ":";

    public static Object getBookInfosKey() {
        return "bookInfos";
    }
}
