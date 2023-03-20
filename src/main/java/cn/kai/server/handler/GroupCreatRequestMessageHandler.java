package cn.kai.server.handler;

import cn.kai.message.GroupCreateRequestMessage;
import cn.kai.message.GroupCreateResponseMessage;
import cn.kai.server.session.Group;
import cn.kai.server.session.GroupSession;
import cn.kai.server.session.GroupSessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;
import java.util.Set;
@ChannelHandler.Sharable
public class GroupCreatRequestMessageHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        Set<String> members = msg.getMembers();

        //群管理器
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.createGroup(groupName, members);
        if(group==null){
            //发送拉群消息
            List<Channel> membersChannel = groupSession.getMembersChannel(groupName);
            for(Channel channel :membersChannel){

                channel.writeAndFlush(new GroupCreateResponseMessage(true,"您已被拉入"+groupName+"群组"));

            }

            //发送成功消息
            ctx.writeAndFlush(new GroupCreateResponseMessage(true,groupName+"创建群成功"));
        }

        else{
            ctx.writeAndFlush(new GroupCreateResponseMessage(false,groupName+"群聊已经存在"));
        }




    }
}
