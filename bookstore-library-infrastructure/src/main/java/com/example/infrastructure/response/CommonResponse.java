package com.example.infrastructure.response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public abstract class CommonResponse {

    private static final Logger log = LoggerFactory.getLogger(CommonResponse.class);

    /**
     * 向客户端发送自定义操作信息
     */
    public static Response send(Response.Status status, String message) {
        Integer code = status.getFamily() == Response.Status.Family.SUCCESSFUL ? CodedMessage.CODE_SUCCESS : CodedMessage.CODE_DEFAULT_FAILURE;
        return Response.status(status).type(MediaType.APPLICATION_JSON).entity(new CodedMessage(code, message)).build();
    }

    /**
     * 向客户端发送操作失败的信息
     */
    public static Response failure(String message) {
        return send(Response.Status.INTERNAL_SERVER_ERROR, message);
    }

    /**
     * 向客户端发送操作成功的信息
     */
    public static Response success(String message) {
        return send(Response.Status.OK, message);
    }

    /**
     * 向客户端发送操作成功的信息
     */
    public static String success() {
        //return send(Response.Status.OK, "操作已成功");
        return "OK";
    }
}
