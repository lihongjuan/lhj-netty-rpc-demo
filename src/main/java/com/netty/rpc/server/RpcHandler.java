package com.netty.rpc.server;

import com.netty.rpc.protocol.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

/**
 *@Description 
 *@Author lihje@chanjet.com
 *@Date 2018/7/18
 */
public class RpcHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger logger = LoggerFactory.getLogger(RpcHandler.class);

    private final Map<String, Object> handlerMap;

    public RpcHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        RpcRequest rpcRequest = (RpcRequest) msg;
        Object clazz = handlerMap.get(rpcRequest.getClassName());
        Method method = clazz.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
        Object result = method.invoke(clazz, rpcRequest.getParameters());
        ctx.write(result);
        ctx.flush();
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("server caught exception", cause);
        ctx.close();
    }
}
