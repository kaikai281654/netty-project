package cn.RPC.handler;

import ch.qos.logback.classic.util.LogbackMDCAdapter;
import cn.RPC.Message.RpcResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/*
泛型通配符 Promise<?> 只能 从泛型容器里获取值，不能从泛型容器中设置值
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {


    public static final Map<Integer, Promise<Object>> PROMISES = new ConcurrentHashMap<>();


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage msg) throws Exception {
        Promise<Object> objectPromise = PROMISES.remove(msg.getSequenceId());

        log.debug("{}", msg);
        if (objectPromise!=null) {
            Object returnValue = msg.getReturnValue();
            Exception exceptionValue = msg.getExceptionValue();

            if(exceptionValue!=null) {
            objectPromise.setFailure(exceptionValue);
            }
            if(returnValue!=null){
                objectPromise.setSuccess(returnValue);
            }

        }


    }
}
