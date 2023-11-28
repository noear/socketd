package org.noear.socketd.transport.java_tcp;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.Channel;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.core.internal.ChannelDefault;
import org.noear.socketd.transport.server.Server;
import org.noear.socketd.transport.server.ServerBase;
import org.noear.socketd.transport.server.ServerConfig;
import org.noear.socketd.utils.RunUtils;
import org.noear.socketd.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * Tcp-Bio 服务端实现（支持 ssl, host）
 *
 * @author noear
 * @since 2.0
 */
public class TcpBioServer extends ServerBase<TcpBioChannelAssistant> {
    private static final Logger log = LoggerFactory.getLogger(TcpBioServer.class);

    private ServerSocket server;
    private ExecutorService serverExecutor;

    public TcpBioServer(ServerConfig config) {
        super(config, new TcpBioChannelAssistant(config));
    }

    /**
     * 创建 server（支持 ssl, host）
     */
    private ServerSocket createServer() throws IOException {
        if (config().getSslContext() == null) {
            if (Utils.isEmpty(config().getHost())) {
                return new ServerSocket(config().getPort());
            } else {
                return new ServerSocket(config().getPort(), 50, InetAddress.getByName(config().getHost()));
            }
        } else {
            if (Utils.isEmpty(config().getHost())) {
                return config().getSslContext().getServerSocketFactory().createServerSocket(config().getPort());
            } else {
                return config().getSslContext().getServerSocketFactory().createServerSocket(config().getPort(), 50, InetAddress.getByName(config().getHost()));
            }
        }
    }

    @Override
    public String title() {
        return "tcp/bio/java-tcp/" + SocketD.version();
    }

    @Override
    public Server start() throws IOException {
        if (isStarted) {
            throw new IllegalStateException("Socket.D server started");
        } else {
            isStarted = true;
        }

        serverExecutor = Executors.newFixedThreadPool(config().getMaxThreads());
        server = createServer();

        serverExecutor.submit(this::accept);

        log.info("Socket.D server started: {server=" + config().getLocalUrl() + "}");

        return this;
    }

    /**
     * 接受请求
     */
    private void accept() {
        while (true) {
            Socket socketTmp = null;
            try {
                Socket socket = socketTmp = server.accept();

                //闲置超时
                if (config().getIdleTimeout() > 0L) {
                    //单位：毫秒
                    socket.setSoTimeout((int) config().getIdleTimeout());
                }

                //读缓冲
                if (config().getReadBufferSize() > 0) {
                    socket.setReceiveBufferSize(config().getReadBufferSize());
                }

                //写缓冲
                if (config().getWriteBufferSize() > 0) {
                    socket.setSendBufferSize(config().getWriteBufferSize());
                }

                serverExecutor.submit(() -> {
                    try {
                        Channel channel = new ChannelDefault<>(socket, config(), assistant());
                        receive(channel, socket);
                    } catch (Throwable e) {
                        if (log.isWarnEnabled()) {
                            log.warn("Server receive error", e);
                        }
                        close(socket);
                    }
                });
            } catch (RejectedExecutionException e) {
                if (socketTmp != null) {
                    log.warn("Server thread pool is full", e);
                    RunUtils.runAndTry(socketTmp::close);
                }
            } catch (Throwable e) {
                if (server.isClosed()) {
                    //说明被手动关掉了
                    return;
                }

                log.warn("Server accept error", e);
            }
        }
    }

    /**
     * 接收数据
     */
    private void receive(Channel channel, Socket socket) {
        while (true) {
            try {
                try {
                    if (socket.isClosed()) {
                        processor().onClose(channel);
                        break;
                    }

                    Frame frame = assistant().read(socket);
                    if (frame != null) {
                        processor().onReceive(channel, frame);
                    }
                } catch (SocketTimeoutException e) {
                    //说明 idleTimeout
                    if (log.isDebugEnabled()) {
                        log.debug("Server channel idle timeout, remoteIp={}", socket.getRemoteSocketAddress());
                    }
                    //注意：socket 客户端无法感知关闭，需要发消息通知
                    channel.sendClose();
                    throw e;
                }
            } catch (IOException e) {
                //如果是 SocketTimeoutException，说明 idleTimeout
                processor().onError(channel, e);
                processor().onClose(channel);
                close(socket);
                break;
            } catch (Throwable e) {
                processor().onError(channel, e);
            }
        }
    }


    private void close(Socket socket) {
        try {
            socket.close();
        } catch (Throwable e) {
            log.debug("Server socket close error", e);
        }
    }

    @Override
    public void stop() {
        if (isStarted) {
            isStarted = false;
        } else {
            return;
        }

        try {
            if (server != null) {
                server.close();
            }

            if (serverExecutor != null) {
                serverExecutor.shutdown();
            }
        } catch (Exception e) {
            log.debug("Server stop error", e);
        }
    }
}