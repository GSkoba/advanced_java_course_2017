package edu.technopolis.advancedjava.season2.stages;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Map;

public class ConnectionWriteStage implements IStage {

    private SocketChannel clientSocketChannel, serverSocketChannel;
    private ByteBuffer clientBuffer, serverBuffer;
    private boolean accepted;


    public ConnectionWriteStage(SocketChannel clientSocketChannel, SocketChannel serverSocketChannel, ByteBuffer clientBuffer, ByteBuffer serverBuffer, boolean accepted) {
        this.clientSocketChannel = clientSocketChannel;
        this.serverSocketChannel = serverSocketChannel;
        this.clientBuffer = clientBuffer;
        this.serverBuffer = serverBuffer;
        this.accepted = accepted;
    }

    @Override
    public void proceed(int ignore, Selector selector, Map<SocketChannel, IStage> map) {
        try {
            if (!clientSocketChannel.isOpen()) {
                if (serverSocketChannel.isOpen()) selector.close();
                return;
            }

            while (clientBuffer.hasRemaining()) {
                clientSocketChannel.write(clientBuffer);
            }

            if (accepted) {
                serverBuffer.flip();
                map.put(clientSocketChannel, new CommunicationStage(clientSocketChannel, serverSocketChannel, clientBuffer, serverBuffer));
                map.put(serverSocketChannel, new CommunicationStage(serverSocketChannel, clientSocketChannel, serverBuffer, clientBuffer));
                clientSocketChannel.register(selector, SelectionKey.OP_READ);
                serverSocketChannel.register(selector, SelectionKey.OP_READ);
                System.out.println("Connected to " + serverSocketChannel.getRemoteAddress());
            } else {
                System.out.println("Connection rejected " + serverSocketChannel.getRemoteAddress());
            }
        } catch (IOException ex) {
            System.err.println(ex);
            ex.printStackTrace();
        }
    }
}
