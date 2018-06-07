package edu.technopolis.advancedjava.season2.stages;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Map;

import static edu.technopolis.advancedjava.season2.stages.Utils.*;

public class ConnectionReadStage implements IStage {

    private SocketChannel clientSocketChannel, serverSocketChannel;
    private ByteBuffer buffer;

    public ConnectionReadStage(SocketChannel clientSocketChannel, ByteBuffer buffer) {
        this.clientSocketChannel = clientSocketChannel;
        this.buffer = buffer;
    }

    private void reject() {
        buffer.clear();
        buffer.put(SOCKS_VERSION)
                .put(RESERVED_BYTE)
                .put(ADRESS_TYPE)
                .put(new byte[4])
                .putShort((short) 0);
        buffer.flip();
    }

    private void accept(byte[] ip, short port) {
        buffer.put(SOCKS_VERSION)
                .put(CONNECTION_PROVIDED_CODE)
                .put(RESERVED_BYTE)
                .put(ADRESS_TYPE)
                .put(ip)
                .putShort(port);
        buffer.flip();
    }

    @Override
    public void proceed(int ignore, Selector selector, Map<SocketChannel, IStage> map) {
        try {

            if (!clientSocketChannel.isOpen()) return;

            int bytes = clientSocketChannel.read(buffer);

            buffer.flip();

            if (bytes == -1) {
                clientSocketChannel.close();
                System.out.println("Close: " + clientSocketChannel.toString());
                return;
            }

            if (buffer.position() > 10 || buffer.get() != SOCKS_VERSION
                    || buffer.get() != CMD_NUMBER
                    || buffer.get() != RESERVED_BYTE
                    || buffer.get() != ADRESS_TYPE) {
                reject();
                map.put(clientSocketChannel, new ConnectionWriteStage(clientSocketChannel, serverSocketChannel, buffer, null, false));
                clientSocketChannel.register(selector, SelectionKey.OP_WRITE);
            }

            byte[] ipv4 = new byte[4];
            buffer.get(ipv4);
            short port = buffer.getShort();

            serverSocketChannel = SocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.connect(new InetSocketAddress(InetAddress.getByAddress(ipv4), port));
            serverSocketChannel.finishConnect();

            buffer.clear();
            accept(ipv4, port);

            IStage stage = new ConnectionPenStage(clientSocketChannel, serverSocketChannel, buffer);
            map.put(clientSocketChannel, stage);
            map.put(serverSocketChannel, stage);

            serverSocketChannel.register(selector, SelectionKey.OP_CONNECT);
            clientSocketChannel.register(selector, SelectionKey.OP_READ);

        } catch (IOException ex) {
            System.err.println(ex);
            ex.printStackTrace();
        }
    }

}
