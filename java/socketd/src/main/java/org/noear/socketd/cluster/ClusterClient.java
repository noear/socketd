package org.noear.socketd.cluster;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.client.Client;
import org.noear.socketd.transport.client.ClientConfigHandler;
import org.noear.socketd.transport.client.ClientSession;
import org.noear.socketd.transport.core.*;
import org.noear.socketd.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 集群客户端
 *
 * @author noear
 */
public class ClusterClient implements Client {
    private final String[] serverUrls;

    private HeartbeatHandler heartbeatHandler;
    private ClientConfigHandler configHandler;
    private Listener listener;

    public ClusterClient(String... serverUrls) {
        this.serverUrls = serverUrls;
    }

    @Override
    public Client heartbeatHandler(HeartbeatHandler heartbeatHandler) {
        this.heartbeatHandler = heartbeatHandler;
        return this;
    }

    /**
     * 配置
     */
    @Override
    public Client config(ClientConfigHandler configHandler) {
        this.configHandler = configHandler;
        return this;
    }

    /**
     * 监听
     */
    @Override
    public Client listen(Listener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * 打开
     */
    @Override
    public ClientSession open() throws IOException {
        List<ClientSession> sessionList = new ArrayList<>();

        for (String urls : serverUrls) {
            for (String url : urls.split(",")) {
                url = url.trim();
                if (Utils.isEmpty(url)) {
                    continue;
                }

                Client client = SocketD.createClient(url);

                if (listener != null) {
                    client.listen(listener);
                }

                if (configHandler != null) {
                    client.config(configHandler);
                }

                if (heartbeatHandler != null) {
                    client.heartbeatHandler(heartbeatHandler);
                }

                sessionList.add(client.open());
            }
        }

        return new ClusterClientSession(sessionList);
    }
}