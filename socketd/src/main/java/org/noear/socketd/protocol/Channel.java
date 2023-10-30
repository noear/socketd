package org.noear.socketd.protocol;

import org.noear.socketd.exception.SocktedConnectionException;

import java.io.Closeable;
import java.io.IOException;

/**
 * 通道
 *
 * @author noear
 * @since 2.0
 */
public interface Channel extends Closeable {
    /**
     * 设置握手信息
     * */
    void setHandshaker(Handshaker handshaker);

    /**
     * 获取握手信息
     */
    Handshaker getHandshaker();

    /**
     * 设置心跳时间
     * */
    void setHeartbeatTime();

    /**
     * 获取心跳时间
     * */
    long getHeartbeatTime();


    /**
     * 发送连接（握手）
     */
    void sendConnect(String uri) throws IOException;

    /**
     * 发送连接确认（握手）
     */
    void sendConnack(Payload connect) throws IOException;

    /**
     * 发送 Ping（心跳）
     */
    void sendPing() throws IOException;

    /**
     * 发送 Pong（心跳）
     */
    void sendPong() throws IOException;

    /**
     * 发送
     */
    void send(Frame frame, Acceptor acceptor) throws IOException, SocktedConnectionException;

    /**
     * 收回
     * */
    void retrieve(Frame frame) throws IOException;

    /**
     * 获取会话
     */
    Session getSession();
}