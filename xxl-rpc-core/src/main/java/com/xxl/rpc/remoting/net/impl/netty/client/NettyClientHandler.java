package com.xxl.rpc.remoting.net.impl.netty.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxl.rpc.remoting.invoker.XxlRpcInvokerFactory;
import com.xxl.rpc.remoting.net.params.XxlRpcResponse;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * rpc netty client handler
 *
 * @author xuxueli 2015-10-31 18:00:27
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<XxlRpcResponse> {
	private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);
	private XxlRpcInvokerFactory xxlRpcInvokerFactory;

	public NettyClientHandler(final XxlRpcInvokerFactory xxlRpcInvokerFactory) {
		this.xxlRpcInvokerFactory = xxlRpcInvokerFactory;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error(">>>>>>>>>>> xxl-rpc netty client caught exception", cause);
		ctx.close();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, XxlRpcResponse xxlRpcResponse) throws Exception {
		// notify response
		xxlRpcInvokerFactory.notifyInvokerFuture(xxlRpcResponse.getRequestId(), xxlRpcResponse);
	}
}
