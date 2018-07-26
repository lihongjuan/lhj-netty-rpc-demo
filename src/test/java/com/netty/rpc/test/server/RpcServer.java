package com.netty.rpc.test.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *@Description 
 *@Author lihje@chanjet.com
 *@Date 2018/7/13
 */
public class RpcServer {

    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("server-spring.xml");
    }
}
