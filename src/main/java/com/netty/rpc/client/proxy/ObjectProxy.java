package com.netty.rpc.client.proxy;

import com.netty.rpc.client.RpcClientHandler;
import com.netty.rpc.protocol.RpcRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;

/**
 *@Description 
 *@Author lihje@chanjet.com
 *@Date 2018/7/17
 */
public class ObjectProxy<T> implements InvocationHandler {

    private Class<T> clazz;

    private String serverAddress;

    public ObjectProxy(Class<T> clazz, String serverAddress) {
        this.clazz = clazz;
        this.serverAddress = serverAddress;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        if (Object.class == method.getDeclaringClass()) {
            String name = method.getName();
            if ("equals".equals(name)) {
                return proxy == args[0];
            } else if ("hashCode".equals(name)) {
                return System.identityHashCode(proxy);
            } else if ("toString".equals(name)) {
                return proxy.getClass().getName() + "@" +
                        Integer.toHexString(System.identityHashCode(proxy)) +
                        ", with InvocationHandler " + this;
            } else {
                throw new IllegalStateException(String.valueOf(method));
            }
        }
        RpcRequest request = new RpcRequest();
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);

        EventLoopGroup group = new NioEventLoopGroup();
        RpcClientHandler clientHandler = new RpcClientHandler();
        try {
            String[] array = serverAddress.split(":");
            InetSocketAddress remotePeer = new InetSocketAddress(array[0], Integer.parseInt(array[1]));
            Bootstrap b = new Bootstrap();
            b.group(group);
            b.channel(NioSocketChannel.class);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline cp = ch.pipeline();
                    cp.addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0));
                    cp.addLast(new ObjectEncoder());
                    cp.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                    cp.addLast(clientHandler);
                }
            });
            ChannelFuture channelFuture = b.connect(remotePeer).sync();
            channelFuture.channel().writeAndFlush(request).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
        return clientHandler.getResponse();
    }
}
