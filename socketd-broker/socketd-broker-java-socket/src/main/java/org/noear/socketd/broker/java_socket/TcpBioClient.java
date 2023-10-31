package org.noear.socketd.broker.java_socket;

import org.noear.socketd.client.*;
import org.noear.socketd.protocol.*;
import org.noear.socketd.protocol.impl.SessionDefault;

/**
 * Tcp-Bio 客户端实现
 *
 * @author noear
 * @since 2.0
 */
public class TcpBioClient extends ClientBase<TcpBioChannelAssistant> {
    public TcpBioClient(ClientConfig clientConfig) {
        super(clientConfig, new TcpBioChannelAssistant());
    }

    @Override
    public Session open() throws Exception {
        ClientConnector connector = new TcpBioClientConnector(this);
        Channel channel = new ClientChannel(connector.connect(), connector);
        return new SessionDefault(channel);
    }
}