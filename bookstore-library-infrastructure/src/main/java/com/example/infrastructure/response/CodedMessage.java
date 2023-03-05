package com.example.infrastructure.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CodedMessage {
    /**
     * 约定的成功标志
     */
    public static final Integer CODE_SUCCESS = 0;
    /**
     * 默认的失败标志，其他失败含义可以自定义
     */
    public static final Integer CODE_DEFAULT_FAILURE = 1;

    private Integer code;
    private String message;
    private Object data;

    public CodedMessage(Integer code, String message) {
        setCode(code);
        setMessage(message);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
