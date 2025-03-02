package com.softdev.system.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {
    public static String formatTimestamp(long timestamp) {
        // 1. 创建Date对象
        Date date = new Date(timestamp);

        // 2. 定义日期格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 3. 设置时区（可选，默认使用系统时区）
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

        // 4. 格式化时间
        return sdf.format(date);
    }
}
