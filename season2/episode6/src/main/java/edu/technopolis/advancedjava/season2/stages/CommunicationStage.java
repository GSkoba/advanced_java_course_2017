package edu.technopolis.advancedjava.season2.stages;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Map;

public class CommunicationStage implements IStage {

    private SocketChannel clientSocketChannel, serverSocketChannel;
    private ByteBuffer clientBuffer, serverBuffer;

    public CommunicationStage(SocketChannel clientSocketChannel, SocketChannel serverSocketChannel, ByteBuffer clientBuffer, ByteBuffer serverBuffer) {
        this.clientSocketChannel = clientSocketChannel;
        this.serverSocketChannel = serverSocketChannel;
        this.clientBuffer = clientBuffer;
        this.serverBuffer = serverBuffer;
    }

    @Override
    public void proceed(int operation, Selector selector, Map<SocketChannel, IStage> map) {
        try {
            if (!clientSocketChannel.isOpen()) {
                if (serverSocketChannel.isOpen()) serverSocketChannel.close();
                return;
            }

            if (!serverSocketChannel.isOpen()) {
                if (clientSocketChannel.isOpen()) clientSocketChannel.close();
                return;
            }

            if (operation == SelectionKey.OP_READ) {
                if (clientBuffer.hasRemaining()) {
                    return;
                }
                clientBuffer.clear();
                int bytes = clientSocketChannel.read(clientBuffer);
                clientBuffer.flip();
                if (bytes > 0) {
                    serverSocketChannel.register(selector, SelectionKey.OP_WRITE);
                } else {
                    clientSocketChannel.close();
                    serverSocketChannel.close();
                }
            } else if (operation == SelectionKey.OP_WRITE) {
                while (serverBuffer.hasRemaining()) {
                    clientSocketChannel.write(serverBuffer);
                }
                clientSocketChannel.register(selector, SelectionKey.OP_READ);
            }

        } catch (IOException ex) {
            System.err.println(ex);
            ex.printStackTrace();
        }
    }
}
