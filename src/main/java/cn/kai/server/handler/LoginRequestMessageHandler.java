package cn.kai.server.handler;

import cn.kai.message.LoginRequestMessage;
import cn.kai.message.LoginResponseMessage;
import cn.kai.server.service.UserServiceFactory;
import cn.kai.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


@ChannelHandler.Sharable
public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
        String username = msg.getUsername();
        String password = msg.getPassword();

        boolean login = UserServiceFactory.getUserService().login(username, password);
        LoginResponseMessage respM;
        if (login) {
            //在自定义session中保存用户状态
            SessionFactory.getSession().bind(ctx.channel(), username);

            respM = new LoginResponseMessage(true, "登陆成功");
        } else {
            respM = new LoginResponseMessage(false, "用户名或密码错误");
        }
        ctx.writeAndFlush(respM);

    }


}
