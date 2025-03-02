package com.softdev.system.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * REST API 响应工具类
 */
public class ResponseUtil {

    /**
     * 成功状态码
     */
    public enum StatusCode {
        SUCCESS(200, "操作成功"),
        BAD_REQUEST(400, "请求参数错误"),
        UNAUTHORIZED(401, "未授权访问"),
        FORBIDDEN(403, "禁止访问"),
        NOT_FOUND(404, "资源不存在"),
        INTERNAL_ERROR(500, "服务器内部错误");

        private final int code;
        private final String defaultMsg;

        StatusCode(int code, String defaultMsg) {
            this.code = code;
            this.defaultMsg = defaultMsg;
        }
    }

    /**
     * 基础响应结构
     */
    private static class ResponseResult<T> {
        private final int code;
        private final String msg;
        private final T data;

        public ResponseResult(int code, String msg, T data) {
            this.code = code;
            this.msg = msg;
            this.data = data;
        }

        // Getter 方法
        public int getCode() { return code; }
        public String getMsg() { return msg; }
        public T getData() { return data; }
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> ResponseEntity<ResponseResult<T>> success(T data) {
        return success(data, StatusCode.SUCCESS.defaultMsg);
    }

    /**
     * 成功响应（自定义消息）
     */
    public static <T> ResponseEntity<ResponseResult<T>> success(T data, String message) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseResult<>(
                        StatusCode.SUCCESS.code,
                        message,
                        data
                ));
    }

    /**
     * 成功响应（无数据）
     */
    public static ResponseEntity<ResponseResult<Void>> success() {
        return success(null);
    }

    /**
     * 失败响应
     */
    public static ResponseEntity<ResponseResult<Void>> fail(StatusCode statusCode) {
        return fail(statusCode, statusCode.defaultMsg);
    }

    /**
     * 失败响应（自定义消息）
     */
    public static ResponseEntity<ResponseResult<Void>> fail(StatusCode statusCode, String message) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ResponseResult<>(
                        statusCode.code,
                        message,
                        null
                ));
    }

    /**
     * 权限拒绝响应
     */
    public static ResponseEntity<ResponseResult<Void>> accessDenied() {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ResponseResult<>(
                        StatusCode.FORBIDDEN.code,
                        StatusCode.FORBIDDEN.defaultMsg,
                        null
                ));
    }

    /**
     * 未认证响应
     */
    public static ResponseEntity<ResponseResult<Void>> unauthorized() {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ResponseResult<>(
                        StatusCode.UNAUTHORIZED.code,
                        StatusCode.UNAUTHORIZED.defaultMsg,
                        null
                ));
    }

    /**
     * 异常响应（自动提取异常信息）
     */
    public static ResponseEntity<ResponseResult<Void>> error(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseResult<>(
                        StatusCode.INTERNAL_ERROR.code,
                        e.getMessage(),
                        null
                ));
    }
}