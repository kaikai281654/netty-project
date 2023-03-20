package cn.dakaizi.ChatRoom.entity;

import lombok.Data;
import lombok.ToString;

/**
 * 消息的 === 基类
 */
@Data
@ToString(callSuper = true)
public class LoginRequestMessage extends Message {
    private String username;
    private String password;
//    private String nickname;

    public LoginRequestMessage() {
    }

    public LoginRequestMessage(String username, String password) {
        this.username = username;
        this.password = password;
//        this.nickname = nickname;
    }

    @Override
    public int getMessageType() {
        return LoginRequestMessage;
    }

}
