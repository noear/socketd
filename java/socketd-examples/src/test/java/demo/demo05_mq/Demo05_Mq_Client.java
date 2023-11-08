package demo.demo05_mq;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.listener.BuilderListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Demo05_Mq_Client {
    public static void main(String[] args) throws Exception {
        MqClient client = new MqClient("127.0.0.1", 8602);
        client.connect();

        client.subscribe("user.created", (message) -> {
            System.out.println(message);
        });

        client.subscribe("user.updated", (message) -> {
            System.out.println(message);
        });

        client.publish("user.created", "test", 0);
    }

    public static class MqClient {
        private Map<String, Consumer<String>> listenerMap = new HashMap<>();
        private String server;
        private int port;
        private Session session;

        public MqClient(String server, int port) {
            this.server = server;
            this.port = port;
        }

        /**
         * 链接
         */
        public void connect() throws Exception {
            session = SocketD.createClient("sd:udp://" + server + ":" + port)
                    .config(c -> c.heartbeatInterval(5)) //心跳频率调高，确保不断连
                    .listen(new BuilderListener()
                            .on("mq.broadcast", (s, m) -> {
                                String topic = m.getMeta("topic");
                                Consumer<String> listener = listenerMap.get(topic);
                                if (listener != null) {
                                    listener.accept(m.getDataAsString());
                                }
                            }))
                    .open();
        }

        /**
         * 订阅消息
         */
        public void subscribe(String topic, Consumer<String> listener) throws IOException {
            listenerMap.put(topic, listener);
            //Qos1，确保订阅成功了
            session.sendAndRequest("mq.sub", new StringEntity("").meta("topic", topic));
        }

        /**
         * 发布消息
         */
        public void publish(String topic, String message, int qos) throws IOException {
            if (qos > 0) {
                session.sendAndRequest("mq.push", new StringEntity(message).meta("topic", topic));
            } else {
                session.send("mq.push", new StringEntity(message).meta("topic", topic));
            }
        }
    }
}