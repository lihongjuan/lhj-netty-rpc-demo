package com.netty.rpc.test.client;

import com.netty.rpc.client.RpcClient;
import com.netty.rpc.client.proxy.ObjectProxy;
import com.netty.rpc.test.service.HelloService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:client-spring.xml")
public class RpcClientTest {

    @Autowired
    private RpcClient rpcClient;

    @Value("${server.address}")
    private String serverAddress;

    @Test
    public void helloTest() {
        HelloService helloService = rpcClient.create(HelloService.class,serverAddress);
        String result = helloService.hello("Hongjuan");
        Assert.assertEquals("Hello! Hongjuan", result);
    }

    @Test
    public void dymicTest() throws Exception{
        InvocationHandler handler = new ObjectProxy<>(HelloService.class,null);
        Class<?> proxyClass = Proxy.getProxyClass(HelloService.class.getClassLoader(), HelloService.class);
        HelloService helloService = (HelloService) proxyClass.getConstructor(InvocationHandler.class).newInstance(handler);
    }

}
