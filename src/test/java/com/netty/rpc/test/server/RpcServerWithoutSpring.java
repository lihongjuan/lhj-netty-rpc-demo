package com.netty.rpc.test.server;

import com.netty.rpc.server.RpcServer;
import com.netty.rpc.test.service.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *@Description 
 *@Author lihje@chanjet.com
 *@Date 2018/7/13
 */
public class RpcServerWithoutSpring {
    private static final Logger logger = LoggerFactory.getLogger(RpcServerWithoutSpring.class);

    public static void main(String[] args) {
        String serverAddress = "127.0.0.1:8866";
        RpcServer rpcServer = new RpcServer(serverAddress);
        HelloService helloService = new HelloServiceImpl();
        rpcServer.addService(HelloService.class.getName(), helloService);
        try {
            rpcServer.start();
        } catch (Exception ex) {
            logger.error("Exception: {}", ex);
        }
    }
}
