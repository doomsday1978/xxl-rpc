package com.xxl.rpc.remoting.net.impl.netty.server;

import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxl.rpc.remoting.net.params.XxlRpcRequest;
import com.xxl.rpc.remoting.net.params.XxlRpcResponse;
import com.xxl.rpc.remoting.provider.XxlRpcProviderFactory;
import com.xxl.rpc.util.ThrowableUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * netty server handler
 *
 * @author xuxueli 2015-10-29 20:07:37
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<XxlRpcRequest> {
	private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
	private XxlRpcProviderFactory xxlRpcProviderFactory;
	private ThreadPoolExecutor serverHandlerPool;

	public NettyServerHandler(final XxlRpcProviderFactory xxlRpcProviderFactory,
			final ThreadPoolExecutor serverHandlerPool) {
		this.xxlRpcProviderFactory = xxlRpcProviderFactory;
		this.serverHandlerPool = serverHandlerPool;
	}

	@Override
	public void channelRead0(final ChannelHandlerContext ctx, final XxlRpcRequest xxlRpcRequest) throws Exception {
		try {
			// do invoke
			serverHandlerPool.execute(new Runnable() {
				@Override
				public void run() {
					// invoke + response
					XxlRpcResponse xxlRpcResponse = xxlRpcProviderFactory.invokeService(xxlRpcRequest);
					ctx.writeAndFlush(xxlRpcResponse);
				}
			});
		} catch (Exception e) {
			// catch error
			XxlRpcResponse xxlRpcResponse = new XxlRpcResponse();
			xxlRpcResponse.setRequestId(xxlRpcRequest.getRequestId());
			xxlRpcResponse.setErrorMsg(ThrowableUtil.toString(e));
			ctx.writeAndFlush(xxlRpcResponse);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		logger.error(">>>>>>>>>>> xxl-rpc provider netty server caught exception", cause);
		ctx.close();
	}
}
