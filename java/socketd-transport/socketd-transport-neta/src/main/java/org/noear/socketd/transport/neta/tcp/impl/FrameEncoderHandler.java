package org.noear.socketd.transport.neta.tcp.impl;

import net.hasor.neta.bytebuf.ByteBuf;
import net.hasor.neta.channel.PipeContext;
import net.hasor.neta.handler.PipeHandler;
import net.hasor.neta.handler.PipeRcvQueue;
import net.hasor.neta.handler.PipeSndQueue;
import net.hasor.neta.handler.PipeStatus;
import org.noear.socketd.transport.core.Config;
import org.noear.socketd.transport.core.Frame;

import java.io.IOException;

/**
 * @author noear
 * @since 2.3
 */
public class FrameEncoderHandler implements PipeHandler<Frame, ByteBuf> {
    private Config config;

    public FrameEncoderHandler(Config config) {
        this.config = config;
    }

    @Override
    public PipeStatus doHandler(PipeContext context, PipeRcvQueue<Frame> src, PipeSndQueue<ByteBuf> dst) throws IOException {
        boolean hasAny;
        for (hasAny = false; src.hasMore(); hasAny = true) {
            Frame frame = src.takeMessage();
            ByteBufCodecWriter writer = config.getCodec().write(frame, (n) -> new ByteBufCodecWriter(n));

            dst.offerMessage(writer.buffer());
        }

        return hasAny ? PipeStatus.Next : PipeStatus.Exit;
    }
}