package org.noear.socketd.transport.core.stream;

import org.noear.socketd.transport.core.*;

import java.util.concurrent.CompletableFuture;

/**
 * 请求流
 *
 * @author noear
 * @since 2.0
 */
public class StreamRequest extends StreamBase {
    private final CompletableFuture<Reply> future;

    public StreamRequest(String sid, long timeout, CompletableFuture<Reply> future) {
        super(sid, timeout);
        this.future = future;
    }

    /**
     * 是否单发接收
     */
    @Override
    public boolean isSingle() {
        return true;
    }

    /**
     * 是否结束接收
     */
    @Override
    public boolean isDone() {
        return future.isDone();
    }

    /**
     * 接收时
     */
    @Override
    public void onAccept(MessageInternal reply, Channel channel) {
        future.complete(reply);
    }
}