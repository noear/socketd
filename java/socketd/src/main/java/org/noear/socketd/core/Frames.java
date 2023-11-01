package org.noear.socketd.core;

import org.noear.socketd.core.entity.MetaEntity;
import org.noear.socketd.core.impl.MessageDefault;
import org.noear.socketd.utils.Utils;

/**
 * 帧工厂
 *
 * @author noear
 * @since 2.0
 * */
public class Frames {
    public static final Frame connectFrame(String uri) {
        return new Frame(Flag.Connect, new MessageDefault().key(Utils.guid()).topic(uri).entity(new MetaEntity(Constants.HEARDER_CONNECT)));
    }

    public static final Frame connackFrame(Message connect) {
        return new Frame(Flag.Connack, new MessageDefault().key(connect.getKey()).topic(connect.getTopic()).entity(new MetaEntity(Constants.HEARDER_CONNACK)));
    }

    public static final Frame pingFrame() {
        return new Frame(Flag.Ping, null);
    }

    public static final Frame pongFrame() {
        return new Frame(Flag.Pong, null);
    }

    /**
     * 一般用于 Udp（它没有断链的概念，需要通知）
     * */
    public static final Frame closeFrame() {
        return new Frame(Flag.Close, null);
    }
}
