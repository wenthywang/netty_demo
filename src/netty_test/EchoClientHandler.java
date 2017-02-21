/**
 * Copyright(c) Guangzhou JiaxinCloud Science & Technology Ltd. 
 */
package netty_test;

import java.util.logging.Level;
import java.util.logging.Logger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * <pre>
 * client 消息处理以及channel初始化。
 * </pre>
 * 
 * @author 王文辉 wangwenhui@jiaxincloud.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 *          </pre>
 */
public class EchoClientHandler extends SimpleChannelInboundHandler<String> {

	private static final Logger logger = Logger.getLogger(EchoClientHandler.class.getName());

//	private final ByteBuf firstMessage;

	// /**
	// * Creates a client-side handler.
	// */
	// public EchoClientHandler(int firstMessageSize) {
	// if (firstMessageSize <= 0) {
	// throw new IllegalArgumentException("firstMessageSize: " +
	// firstMessageSize);
	// }
	// firstMessage = Unpooled.buffer(firstMessageSize);
	// firstMessage.writeBytes(msg.getBytes());
	// }
	// }
	//
	// @Override
	// public void channelActive(ChannelHandlerContext ctx) {
	// ctx.writeAndFlush(firstMessage);
	// System.out.print("active");
	// }
	//
	// @Override
	// public void channelRead(ChannelHandlerContext ctx, Object msg) throws
	// Exception {
	// ctx.write(msg);
	// System.out.print("read");
	// }
	//
	// @Override
	// public void channelReadComplete(ChannelHandlerContext ctx) throws
	// Exception {
	// ctx.flush();
	// System.out.print("readok");
	// }
	//
	 @Override
	 public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
	 // Close the connection when an exception is raised.
	 logger.log(Level.WARNING, "Unexpected exception from downstream.",
	 cause);
	 ctx.close();
	 }

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {

		System.out.println("Server say : " + msg);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println(" [Client-"+ctx.channel().id().toString()+"]-> active! ");
		super.channelActive(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println(" [Client-"+ctx.channel().id().toString()+"]-> close! ");
		super.channelInactive(ctx);
	}
}
