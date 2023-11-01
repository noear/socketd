package org.noear.socketd.core.impl;

import org.noear.socketd.core.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * 通道默认实现（每个连接都会建立一个通道）
 *
 * @author noear
 * @since 2.0
 */
public class ChannelDefault<S> extends ChannelBase implements Channel {
    private final S source;

    //接收器注册
    private final Map<String, Acceptor> acceptorMap;
    //助理
    private final ChannelAssistant<S> assistant;
    //最大请求数（根据请求、响应加减计数）
    private final int maxRequests;
    //会话（懒加载）
    private Session session;

    public ChannelDefault(S source, int maxRequests,  ChannelAssistant<S> assistant) {
        super();
        this.source = source;
        this.assistant = assistant;
        this.acceptorMap = new HashMap<>();
        this.maxRequests = maxRequests;
    }

    @Override
    public boolean isValid() {
        return assistant.isValid(source);
    }

    @Override
    public int getRequestMax() {
        return maxRequests;
    }

    @Override
    public InetSocketAddress getRemoteAddress() throws IOException {
        return assistant.getRemoteAddress(source);
    }

    @Override
    public InetSocketAddress getLocalAddress() throws IOException {
        return assistant.getLocalAddress(source);
    }

    /**
     * 发送
     */
    @Override
    public void send(Frame frame, Acceptor acceptor) throws IOException {
        if (acceptor != null) {
            acceptorMap.put(frame.getMessage().getKey(), acceptor);
        }

        assistant.write(source, frame);
    }

    /**
     * 收回（收回答复）
     * */
    @Override
    public void retrieve(Frame frame) throws IOException {
        Acceptor acceptor = acceptorMap.get(frame.getMessage().getKey());

        if (acceptor != null) {
            if (acceptor.isSingle() || frame.getFlag() == Flag.ReplyEnd) {
                acceptorMap.remove(frame.getMessage().getKey());
            }

            acceptor.accept(frame.getMessage());
        }
    }

    /**
     * 获取会话
     */
    @Override
    public Session getSession() {
        if (session == null) {
            session = new SessionDefault(this);
        }

        return session;
    }

    /**
     * 关闭
     */
    @Override
    public void close() throws IOException {
        assistant.close(source);
        acceptorMap.clear();
    }
}