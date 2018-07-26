package com.netty.rpc.test.server;

import com.netty.rpc.server.RpcService;
import com.netty.rpc.test.service.HelloService;

/**
 *@Description 
 *@Author lihje@chanjet.com
 *@Date 2018/7/23
 */
@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(String name) {
        return "Hello! " + name;
    }
}
