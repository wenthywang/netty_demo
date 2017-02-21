/**
 * Copyright(c) Guangzhou JiaxinCloud Science & Technology Ltd. 
 */
package netty_test;

import java.net.InetAddress;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.alibaba.fastjson.JSONObject;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelMatcher;

/**
 * <pre>
 * 服务端channel handler。
 * </pre>
 * @author 王文辉  wangwenhui@jiaxincloud.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class EchoServerHandler extends SimpleChannelInboundHandler<String> {
	 private static final Logger logger = Logger.getLogger(EchoServerHandler.class.getName());
	 
//	 
//	    @Override 
//	    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception { 
//	    	 for (Channel channel :  MyChannelHandlerPool.channelGroup) {
//	             if (channel != ctx) {
//	                 channel.writeAndFlush("[" + ctx.remoteAddress() + "]" + msg + "\n");
//	             }
//	         }
//	    }
//
	    @Override 
	    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception { 
	        ctx.flush(); 
	    }
	    
	    @Override 
	    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { 
	        // Close the connection when an exception is raised. 
	        logger.log(Level.WARNING, "Unexpected exception from downstream.", cause); 
	        ctx.close(); 
	    }

		/**
		 * 服务端读取客户端消息
		 */
		@Override
		protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
			JSONObject obj=JSONObject.parseObject(msg);
			 String msgString=obj.getString("msg");
			//单聊
			if(msgString!=null&&!msgString.equals("")&&msgString.contains("->")){
				String toCllient=msgString.split("->")[1];
				if(MyChannelHandlerPool.clientMap.containsKey(toCllient)){
					final String channelId=MyChannelHandlerPool.clientMap.get(toCllient);
					MyChannelHandlerPool.channelGroup.writeAndFlush("[" + ctx.channel().remoteAddress()  + "]" + msgString + "\n",new ChannelMatcher() {
						@Override
						public boolean matches(Channel channel) {
							//找出不是当前的channel
							if(channel.id().toString().equals(channelId)){
								return true;
							}
							return false;
						}
					});
				}
			}else{
				//群聊
				final Channel currentChannel=ctx.channel();
				MyChannelHandlerPool.channelGroup.writeAndFlush("[" + ctx.channel().remoteAddress()  + "]" + msgString + "\n",new ChannelMatcher() {
					@Override
					public boolean matches(Channel channel) {
						//找出不是当前的channel
						if(channel!=currentChannel){
							return true;
						}
						return false;
					}
				});
			}
			
		
			     // 收到消息直接打印输出
		         System.out.println(ctx.channel().remoteAddress() + " Say : " + msgString);
			     // 返回客户端消息 - 我已经接收到了你的消息
			    ctx.writeAndFlush("Received your message !\n");
			
		} 
		
		//建立连接时 初始化
	   //这里channeActive的意思是当连接活跃(建立)的时候触发.输出消息源的远程地址。并返回欢迎消息。
		 @Override
		      public void channelActive(ChannelHandlerContext ctx) throws Exception {
		           System.out.println("RamoteAddress : " + ctx.channel().remoteAddress() + " active !");
		          
		           ctx.writeAndFlush( "Welcome to " + InetAddress.getLocalHost().getHostName() + " service!\n");
		            super.channelActive(ctx);
		            
		            //群发其他已经上线的client
//			         MyChannelHandlerPool.channelGroup.writeAndFlush("[" + ctx.channel().remoteAddress()  + "]" + "is online!" + "\n");
		          
		      
			         int index=MyChannelHandlerPool.channelGroup.size()+1;
			         String key="Client"+index;
			         MyChannelHandlerPool.channelGroup.writeAndFlush("[" + key + "]" + "is online!" + "\n");
			         
			         //放进channel 管理pool里
			         MyChannelHandlerPool.channelGroup.add(ctx.channel());
			         
			         MyChannelHandlerPool.clientMap.put(key, ctx.channel().id().toString());
			         
			         //添加channel管理组前群发消息上线
				     int connectCount= MyChannelHandlerPool.channelGroup.size();
			         System.out.println("当前连接数->"+connectCount);
		  }
		 
		 //处理客户端关闭连接
		    @Override
		    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		    	 System.out.println("RamoteAddress : " + ctx.channel().remoteAddress() + " close !");
		    	 //在服务端的map中删除客户端的连接数据
		    	 String offlineMsg="[" + ctx.channel().remoteAddress()  + "]" + "is offline!" + "\n";
		    	   //在channel管理中删除关闭的连接
		    	 MyChannelHandlerPool.channelGroup.remove(ctx.channel());
		    	 
		    	 if(MyChannelHandlerPool.clientMap.containsValue(ctx.channel().id().toString())){
		    		for(Entry<String,String>entry: MyChannelHandlerPool.clientMap.entrySet()){
		    			if(entry.getValue().equals(ctx.channel().id().toString())){
		    				  offlineMsg="[" +entry.getKey()  + "]" + "is offline!" + "\n";
		    				  MyChannelHandlerPool.clientMap.remove(entry.getKey());
		    			}
		    		}
		    	 }
		    	   //删除channel管理组前群发消息下线
				 MyChannelHandlerPool.channelGroup.writeAndFlush(offlineMsg);
		         ctx.fireChannelInactive();
		    }
}
