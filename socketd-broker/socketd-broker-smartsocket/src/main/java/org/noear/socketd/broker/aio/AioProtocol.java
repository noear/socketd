package org.noear.socketd.broker.aio;

import org.noear.socketd.protocol.CodecByteBuffer;
import org.noear.socketd.protocol.Frame;
import org.smartboot.socket.Protocol;
import org.smartboot.socket.transport.AioSession;

import java.nio.ByteBuffer;

/**
 * @author noear
 * @since 2.0
 */
public class AioProtocol implements Protocol<Frame> {
    CodecByteBuffer codec = new CodecByteBuffer();

    @Override
    public Frame decode(ByteBuffer buffer, AioSession aioSession) {
        FixedLengthFrameDecoder decoder = AioAttachment.getDecoder(aioSession);

        if (decoder == null) {
            if (buffer.remaining() < Integer.BYTES) {
                return null;
            } else {
                buffer.mark();
                decoder = new FixedLengthFrameDecoder(buffer.getInt());
                buffer.reset();
                AioAttachment.setDecoder(aioSession, decoder);
            }
        }

        if (decoder.read(buffer) == false) {
            return null;
        } else {
            AioAttachment.setDecoder(aioSession, null);
            buffer = decoder.getBuffer();
            buffer.flip();
        }

        return codec.decode(buffer);
    }
}
