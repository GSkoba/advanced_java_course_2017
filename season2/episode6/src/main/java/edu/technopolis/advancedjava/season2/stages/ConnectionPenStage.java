package edu.technopolis.advancedjava.season2.stages;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Map;

import static edu.technopolis.advancedjava.season2.stages.Utils.*;

public class ConnectionPenStage implements IStage {

    private SocketChannel clientSocketChannel, serverSocketChannel;
    private ByteBuffer clientBuffer;


    public ConnectionPenStage(SocketChannel clientSocketChannel, SocketChannel serverSocketChannel, ByteBuffer clientBuffer) {
        this.clientSocketChannel = clientSocketChannel;
        this.serverSocketChannel = serverSocketChannel;
        this.clientBuffer = clientBuffer;
    }

    private void reject() {
        clientBuffer.put(SOCKS_VERSION)
                .put(RESERVED_BYTE)
                .put(ADRESS_TYPE)
                .put(new byte[4])
                .putShort((short) 0);
        clientBuffer.flip();
    }

    @Override
    public void proceed(int operation, Selector selector, Map<SocketChannel, IStage> map) {
        try {

            if (!clientSocketChannel.isOpen()) {
                if (serverSocketChannel.isOpen()) serverSocketChannel.close();
                return;
            }

            if (serverSocketChannel.finishConnect()) {
                IStage stage = new ConnectionWriteStage(clientSocketChannel, serverSocketChannel, clientBuffer, ByteBuffer.allocate(BUFFER_SIZE), true);
                map.put(clientSocketChannel, stage);
                map.put(serverSocketChannel, stage);
                serverSocketChannel.register(selector, SelectionKey.OP_READ);
                clientSocketChannel.register(selector, SelectionKey.OP_WRITE);
            } else {
                clientBuffer.clear();
                reject();
                clientSocketChannel.register(selector, SelectionKey.OP_WRITE);
                map.put(clientSocketChannel, new ConnectionWriteStage(clientSocketChannel, serverSocketChannel, clientBuffer, ByteBuffer.allocate(BUFFER_SIZE), false));
                serverSocketChannel.close();
            }

        } catch (IOException ex) {
            System.err.println(ex);
            ex.printStackTrace();
        }
    }
}
