package com.netty.rpc.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 *@Description 
 *@Author lihje@chanjet.com
 *@Date 2018/7/18
 */
@Data
public class RpcRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String className;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] parameters;
}