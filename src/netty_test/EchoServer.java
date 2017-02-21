/**
 * Copyright(c) Guangzhou JiaxinCloud Science & Technology Ltd. 
 */
package netty_test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * <pre>
 * netty 服务端。
 * </pre>
 * @author 王文辉  wangwenhui@jiaxincloud.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class EchoServer {

    private final int port;
    

    public EchoServer(int port) { 
        this.port = port; 
    }

    public void run() throws Exception { 
        // Configure the server. 
        EventLoopGroup bossGroup = new NioEventLoopGroup(); 
        EventLoopGroup workerGroup = new NioEventLoopGroup(); 
        try { 
            ServerBootstrap b = new ServerBootstrap(); 
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 100) 
                   .handler(new LoggingHandler(LogLevel.INFO)).childHandler(new ChannelInitializer<SocketChannel>() { 
                       @Override 
                       public void initChannel(SocketChannel ch) throws Exception { 
                    	   ChannelPipeline pipeline = ch.pipeline();
                    	   // 以("\n")为结尾分割的 解码器
							pipeline.addLast("framer",
									new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
							// 字符串解码 和 编码
							pipeline.addLast("decoder", new StringDecoder());
							pipeline.addLast("encoder", new StringEncoder());
							//自定义逻辑的handler
							pipeline.addLast(
									// new LoggingHandler(LogLevel.INFO),
									new EchoServerHandler());
						}
                   });

            // Start the server. 
            ChannelFuture f = b.bind(port).sync();
            // Wait until the server socket is closed. 
            f.channel().closeFuture().sync(); 
        } finally { 
            // Shut down all event loops to terminate all threads. 
            bossGroup.shutdownGracefully(); 
            workerGroup.shutdownGracefully(); 
        } 
    }

     public static void  main(String[] args){
    	 //端口任意
    	 EchoServer server =new EchoServer(9000);
    	 try {
			server.run();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     }
}
