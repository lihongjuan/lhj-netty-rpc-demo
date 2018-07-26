package com.netty.rpc.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *@Description 
 *@Author lihje@chanjet.com
 *@Date 2018/7/18
 */
public class RpcClientHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger logger = LoggerFactory.getLogger(RpcClientHandler.class);

    private Object response;

    public Object getResponse() {
        return response;
    }
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg){
        this.response=msg;
        logger.info("client received server message:" + response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        logger.error("client caught exception", cause);
        ctx.close();
    }
}
