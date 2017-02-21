/**
 * Copyright(c) Guangzhou JiaxinCloud Science & Technology Ltd. 
 */
package netty_test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.alibaba.fastjson.JSONObject;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * <pre>
 * 客户端。
 * </pre>
 * @author 王文辉  wangwenhui@jiaxincloud.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class EchoClient { 
    private final String host; 
    private final int port; 
    private final int firstMessageSize;//暂时不使用

    public EchoClient(String host, int port, int firstMessageSize) { 
        this.host = host; 
        this.port = port; 
        this.firstMessageSize = firstMessageSize; 
    }

    public void run() throws Exception { 
        // Configure the client. 
        EventLoopGroup group = new NioEventLoopGroup(); 
        try { 
            Bootstrap b = new Bootstrap(); 
           b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true).handler(new ChannelInitializer<SocketChannel>() { 
                @Override 
                public void initChannel(SocketChannel ch) throws Exception { 
                	 ChannelPipeline pipeline = ch.pipeline();
                	         /*
                	         * 这个地方的 必须和服务端对应上。否则无法正常解码和编码
                	         * 
                	       * 解码和编码 我将会在下一张为大家详细的讲解。再次暂时不做详细的描述
                	        * 
                	         * */
                	         pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                	        pipeline.addLast("decoder", new StringDecoder());
                	         pipeline.addLast("encoder", new StringEncoder());
                	        
                	       // 客户端的逻辑
                	     pipeline.addLast( 
                          // new LoggingHandler(LogLevel.INFO), 
                           new EchoClientHandler()); 
                } 
            });

            // Start the client. 
            ChannelFuture f = b.connect(host, port).sync();
            // 控制台输入
                       BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                      for (;;) {
                           String line = in.readLine();
                           if (line == null) {
                              continue;
                           }
                           /*
                              * 向服务端发送在控制台输入的文本 并用"\r\n"结尾
                             * 之所以用\r\n结尾 是因为我们在handler中添加了 DelimiterBasedFrameDecoder 帧解码。
                            * 这个解码器是一个根据\n符号位分隔符的解码器。所以每条消息的最后必须加上\n否则无法识别和解码
                             * */
                           JSONObject obj=new JSONObject();
                           obj.put("from", f.channel().id().toString());
                           obj.put("msg", line );
                           f.channel().writeAndFlush(obj.toJSONString()+ "\r\n");
                        }
            // Wait until the connection is closed. 
//            f.channel().closeFuture().sync(); 
        } finally { 
            // Shut down the event loop to terminate all threads. 
            group.shutdownGracefully(); 
        } 
    } 
    
    public static void  main(String[] args){
    	EchoClient client =new EchoClient("127.0.0.1",9000,10);
    	try {
    		client.run();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}