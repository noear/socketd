package features.cases;

import org.junit.jupiter.api.Assertions;
import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.EntityMetas;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.entity.FileEntity;
import org.noear.socketd.transport.core.listener.SimpleListener;
import org.noear.socketd.transport.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * sendAndRequest() 超时
 *
 * @author noear
 * @since 2.0
 */
public class TestCase24_bigFile_1g extends BaseTestCase {
    private static Logger log = LoggerFactory.getLogger(TestCase24_bigFile_1g.class);

    public TestCase24_bigFile_1g(String schema, int port) {
        super(schema, port);
    }

    private Server server;
    private Session clientSession;

    private AtomicInteger messageCounter = new AtomicInteger();

    @Override
    public void start() throws Exception {
        log.trace("...");

        CountDownLatch countDownLatch = new CountDownLatch(1);

        super.start();
        //server
        server = SocketD.createServer(getSchema())
                .config(c -> c.port(getPort()))
                .listen(new SimpleListener() {
                    @Override
                    public void onMessage(Session session, Message message) throws IOException {
                        System.out.println("::" + message);
                        messageCounter.incrementAndGet();

                        String fileName = message.meta(EntityMetas.META_DATA_DISPOSITION_FILENAME);

                        if (fileName != null) {
                            System.out.println(fileName);
                            File fileNew = new File("/Users/noear/Downloads/socketd-big-upload.mov");
                            fileNew.delete();

                            fileNew.createNewFile();

                            try {
                                try (OutputStream outputStream = new FileOutputStream(fileNew)) {
                                    outputStream.write(message.dataAsBytes());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        countDownLatch.countDown();
                    }
                })
                .start();

        //休息下，启动可能要等会儿
        Thread.sleep(1000);


        //client
        String serverUrl = getSchema() + "://127.0.0.1:" + getPort() + "/path?u=a&p=2";
        clientSession = SocketD.createClient(serverUrl).open();

        clientSession.send("/user/upload", new FileEntity(new File("/Users/noear/Movies/[Socket.D 实战] 直播手写 FolkMQ (4).mov")));


        countDownLatch.await();

        Thread.sleep(10000);

        System.out.println("counter: " + messageCounter.get());
        Assertions.assertEquals(messageCounter.get(), 1, getSchema() + ":server 收的消息数量对不上");

        File file = new File("/Users/noear/Downloads/socketd-big-upload.mov");
        assert file.length() > 1024 * 1024 * 10;
    }

    @Override
    public void stop() throws Exception {
        if (clientSession != null) {
            clientSession.close();
        }

        if (server != null) {
            server.stop();
        }

        super.stop();
    }
}