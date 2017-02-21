/**
 * Copyright(c) Guangzhou JiaxinCloud Science & Technology Ltd. 
 */
package netty_test;

import java.util.HashMap;
import java.util.Map;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * <pre>
 * channel 管理类。
 * </pre>
 * @author 王文辉  wangwenhui@jiaxincloud.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class MyChannelHandlerPool {

public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

//存储在线client 和 channelId 用于匹配单聊
public static Map<String,String>clientMap=new HashMap<String,String>();

}
