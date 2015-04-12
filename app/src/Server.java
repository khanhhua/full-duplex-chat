import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Server extends Thread implements UserInput {
    public void listen(int port) throws IOException {
        channel = ServerSocketChannel.open();

        channel.configureBlocking(false);
        channel.socket().bind(new InetSocketAddress(8888));

        selector = Selector.open();
        channel.register(selector, SelectionKey.OP_ACCEPT);
    }

    @Override
    public void run() {
        clients = new ArrayList<SocketChannel>();

        Iterator<SelectionKey> it;
        while (channel.isOpen()) {
            try {
                if (selector.select() != 0) {
                    it = selector.selectedKeys().iterator();
                    while (it.hasNext()) {
                        SelectionKey key = it.next();
                        handleKey(key);
                        it.remove();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleKey(SelectionKey key) throws IOException {
        // System.out.println("Server.handleKey");

        if (key.isAcceptable()) {
            SocketChannel channelClient = channel.accept();
            channelClient.configureBlocking(false);
            channelClient.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);

            System.out.println("Client is accepted");

            clients.add(channelClient);
        } else if (key.isReadable()) {
            SocketChannel channelClient = (SocketChannel) key.channel();
            if (!channelClient.isOpen()) {
                System.out.println("Channel terminated by client");
            }
            ByteBuffer buffer = ByteBuffer.allocate(80);
            buffer.clear();
            channelClient.read(buffer);
            if (buffer.get(0) == 0) {
                System.out.println("Nothing to read.");
                channelClient.close();

                clients.remove(channelClient);
                return;
            }

            System.out.printf("Client says: %s", new String(buffer.array()));
        } else if (key.isWritable()) {

        }
    }

    List<SocketChannel> clients;
    ServerSocketChannel channel;

    Selector selector;

    @Override
    public void write(String input) throws IOException {
        for(SocketChannel channelClient:clients) {
            channelClient.write(ByteBuffer.wrap(input.getBytes()));
        }
    }
}
