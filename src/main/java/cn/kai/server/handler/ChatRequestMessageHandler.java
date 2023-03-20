package cn.kai.server.handler;

import cn.kai.message.ChatRequestMessage;
import cn.kai.message.ChatResponseMessage;
import cn.kai.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
@ChannelHandler.Sharable
public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
        String to = msg.getTo();
        Channel channel = SessionFactory.getSession().getChannel(to);
        //在线
        if((channel!=null)){
            channel.writeAndFlush(new ChatResponseMessage(msg.getFrom(),msg.getContent()));
        }
        //不在线
        else{
            ctx.writeAndFlush(new ChatResponseMessage(false,"对方不在线或者不存在"));
        }


    }
}
