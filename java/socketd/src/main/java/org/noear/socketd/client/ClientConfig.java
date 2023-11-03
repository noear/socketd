package org.noear.socketd.client;

import org.noear.socketd.core.*;
import org.noear.socketd.core.impl.KeyGeneratorGuid;
import org.noear.socketd.core.impl.RangesHandlerDefault;

import javax.net.ssl.SSLContext;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 客记端配置（单位：毫秒）
 *
 * @author noear
 * @since 2.0
 */
public class ClientConfig implements Config {
    private final String schema;
    private Charset charset;

    private final String url;
    private final URI uri;

    private Codec<ByteBuffer> codec;
    private KeyGenerator keyGenerator;
    private RangesHandler rangesHandler;
    private SSLContext sslContext;

    private long heartbeatInterval;

    private long connectTimeout;

    private int readBufferSize;
    private int writeBufferSize;

    private boolean autoReconnect;

    private int maxRequests;
    private int maxUdpSize;
    private int rangeSize;

    public ClientConfig(String url) {
        this.url = url;
        this.uri = URI.create(url);
        this.schema = uri.getScheme();
        this.charset = StandardCharsets.UTF_8;

        this.codec = new CodecByteBuffer(this);
        this.keyGenerator = new KeyGeneratorGuid();
        this.rangesHandler = new RangesHandlerDefault();

        this.connectTimeout = 3000;
        this.heartbeatInterval = 20 * 1000;

        this.autoReconnect = true;

        this.maxRequests = 10;
        this.maxUdpSize = 2048; //2k //与 netty 保持一致 //实际可用 1464
        this.rangeSize = 1024 * 1024 * 16; //16m
    }

    /**
     * 是否客户端模式
     */
    @Override
    public boolean clientMode() {
        return true;
    }

    /**
     * 获取协议架构
     */
    @Override
    public String getSchema() {
        return schema;
    }

    @Override
    public Charset getCharset() {
        return charset;
    }

    public ClientConfig charset(Charset charset) {
        this.charset = charset;
        return this;
    }

    /**
     * 获取编解码器
     */
    @Override
    public Codec<ByteBuffer> getCodec() {
        return codec;
    }

    public ClientConfig codec(Codec<ByteBuffer> codec) {
        this.codec = codec;
        return this;
    }

    /**
     * 获取标识生成器
     */
    @Override
    public KeyGenerator getKeyGenerator() {
        return keyGenerator;
    }

    @Override
    public RangesHandler getRangesHandler() {
        return rangesHandler;
    }

    public ClientConfig keyGenerator(KeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
        return this;
    }

    /**
     * 获取连接地址
     */
    public String getUrl() {
        return url;
    }

    /**
     * 获取连接地址
     */
    public URI getUri() {
        return uri;
    }

    /**
     * 获取连接主机
     */
    public String getHost() {
        return uri.getHost();
    }

    /**
     * 获取连接端口
     */
    public int getPort() {
        return uri.getPort();
    }

    /**
     * 获取 ssl 上下文
     */
    @Override
    public SSLContext getSslContext() {
        return sslContext;
    }

    /**
     * 配置 ssl 上下文
     */
    public ClientConfig sslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
        return this;
    }

    /**
     * 获取心跳间隔
     */
    public long getHeartbeatInterval() {
        return heartbeatInterval;
    }

    /**
     * 配置心跳间隔
     */
    public ClientConfig heartbeatInterval(long heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
        return this;
    }

    /**
     * 获取连接超时
     */
    public long getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * 配置连接超时
     */
    public ClientConfig connectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    /**
     * 获取读缓冲大小
     */
    public int getReadBufferSize() {
        return readBufferSize;
    }

    /**
     * 配置读缓冲大小
     */
    public ClientConfig readBufferSize(int readBufferSize) {
        this.readBufferSize = readBufferSize;
        return this;
    }

    /**
     * 获取写缓冲大小
     */
    public int getWriteBufferSize() {
        return writeBufferSize;
    }

    /**
     * 配置写缓冲大小
     */
    public ClientConfig writeBufferSize(int writeBufferSize) {
        this.writeBufferSize = writeBufferSize;
        return this;
    }

    /**
     * 是否自动重链
     */
    public boolean isAutoReconnect() {
        return autoReconnect;
    }

    public ClientConfig autoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
        return this;
    }

    /**
     * 允许最大请求数
     */
    @Override
    public int getMaxRequests() {
        return maxRequests;
    }

    public ClientConfig maxRequests(int maxRequests) {
        this.maxRequests = maxRequests;
        return this;
    }

    /**
     * 允许最大UDP包大小
     */
    @Override
    public int getMaxUdpSize() {
        return maxUdpSize;
    }

    public ClientConfig maxUdpSize(int maxUdpSize) {
        this.maxUdpSize = maxUdpSize;
        return this;
    }

    /**
     * 获取分片大小
     */
    @Override
    public int getRangeSize() {
        return rangeSize;
    }

    public ClientConfig rangeSize(int rangeSize) {
        this.rangeSize = rangeSize;
        return this;
    }

    @Override
    public String toString() {
        return "ClientConfig{" +
                "schema='" + schema + '\'' +
                ", charset=" + charset +
                ", url='" + url + '\'' +
                ", heartbeatInterval=" + heartbeatInterval +
                ", connectTimeout=" + connectTimeout +
                ", readBufferSize=" + readBufferSize +
                ", writeBufferSize=" + writeBufferSize +
                ", autoReconnect=" + autoReconnect +
                ", maxRequests=" + maxRequests +
                ", maxUdpSize=" + maxUdpSize +
                '}';
    }
}