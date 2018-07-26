package com.netty.rpc.client;

import com.netty.rpc.client.proxy.ObjectProxy;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.lang.reflect.Proxy;

/**
 *@Description 
 *@Author lihje@chanjet.com
 *@Date 2018/7/16
 */
@Component
public class RpcClient {

    public static <T> T create(Class<T> interfaceClass,String serverAddress) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new ObjectProxy<T>(interfaceClass,serverAddress)
        );
    }
}

