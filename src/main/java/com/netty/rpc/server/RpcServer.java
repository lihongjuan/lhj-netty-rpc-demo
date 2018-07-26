package com.netty.rpc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 *@Description 
 *@Author lihje@chanjet.com
 *@Date 2018/7/18
 */
public class RpcServer implements ApplicationContextAware, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);

    private String serverAddress;

    private Map<String, Object> handlerMap = new HashMap<>();

    private EventLoopGroup bossGroup = null;

    private EventLoopGroup workerGroup = null;

    public RpcServer(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RpcService.class);
        if (MapUtils.isNotEmpty(serviceBeanMap)) {
            for (Object serviceBean : serviceBeanMap.values()) {
                String interfaceName = serviceBean.getClass().getAnnotation(RpcService.class).value().getName();
                logger.info("Loading service: {}", interfaceName);
                handlerMap.put(interfaceName, serviceBean);
            }
        }
    }

    public RpcServer addService(String interfaceName, Object serviceBean) {
        if (!handlerMap.containsKey(interfaceName)) {
            logger.info("Loading service: {}", interfaceName);
            handlerMap.put(interfaceName, serviceBean);
        }

        return this;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        start();
    }

    public void start() throws Exception {
        try {
            if (bossGroup == null && workerGroup == null) {
                bossGroup = new NioEventLoopGroup();
                workerGroup = new NioEventLoopGroup();
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel channel) {
                                channel.pipeline().addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0));
                                channel.pipeline().addLast(new ObjectEncoder());
                                channel.pipeline().addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                                channel.pipeline().addLast(new RpcHandler(handlerMap));
                            }
                        })
                        .option(ChannelOption.SO_BACKLOG, 128)
                        .childOption(ChannelOption.SO_KEEPALIVE, true);

                String[] array = serverAddress.split(":");
                String host = array[0];
                int port = Integer.parseInt(array[1]);
                ChannelFuture future = bootstrap.bind(host, port).sync();
                logger.info("Server started on port {}", port);
                future.channel().closeFuture().sync();
            }
        } finally {
            if (bossGroup != null) {
                bossGroup.shutdownGracefully();
            }
            if (workerGroup != null) {
                workerGroup.shutdownGracefully();
            }
        }
    }
}
