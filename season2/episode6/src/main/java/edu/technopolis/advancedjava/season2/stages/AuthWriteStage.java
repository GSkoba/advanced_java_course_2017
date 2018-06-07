package edu.technopolis.advancedjava.season2.stages;

import edu.technopolis.advancedjava.season2.LogUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Map;


public class AuthWriteStage implements IStage {

    private boolean accepted;
    private SocketChannel socketChannel;
    private ByteBuffer buffer;

    public AuthWriteStage(SocketChannel socketChannel, ByteBuffer buffer, boolean accepted) {
        this.socketChannel = socketChannel;
        this.buffer = buffer;
        this.accepted = accepted;
    }

    @Override
    public void proceed(int ignore, Selector selector, Map<SocketChannel, IStage> map) {
        try {
            if (!socketChannel.isOpen()) return;
            while (buffer.hasRemaining()) {
                socketChannel.write(buffer);
            }

            buffer.clear();

            if (accepted) {
                socketChannel.register(selector, SelectionKey.OP_READ);
                map.put(socketChannel, new ConnectionReadStage(socketChannel, buffer));
                System.out.println("Auth completed for " + socketChannel.getRemoteAddress());
            } else {
                socketChannel.close();
                System.out.println("Auth rejected for " + socketChannel.getRemoteAddress());
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
