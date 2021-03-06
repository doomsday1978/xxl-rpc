package com.xxl.rpc.remoting.net.impl.mina.client;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxl.rpc.remoting.invoker.XxlRpcInvokerFactory;
import com.xxl.rpc.remoting.net.params.XxlRpcResponse;

/**
 * rpc mina handler
 * 
 * @author xuxueli 2015-11-14 18:55:19
 */
public class MinaClientHandler extends IoHandlerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(MinaClientHandler.class);
	private XxlRpcInvokerFactory xxlRpcInvokerFactory;

	public MinaClientHandler(final XxlRpcInvokerFactory xxlRpcInvokerFactory) {
		this.xxlRpcInvokerFactory = xxlRpcInvokerFactory;
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		// super.exceptionCaught(session, cause);
		logger.error(">>>>>>>>>>> xxl-rpc mina client caught exception:", cause);
		session.closeOnFlush();
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		XxlRpcResponse xxlRpcResponse = (XxlRpcResponse) message;
		// notify response
		xxlRpcInvokerFactory.notifyInvokerFuture(xxlRpcResponse.getRequestId(), xxlRpcResponse);
	}
}
