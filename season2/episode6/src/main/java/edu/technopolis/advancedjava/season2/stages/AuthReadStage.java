package edu.technopolis.advancedjava.season2.stages;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Map;

import static edu.technopolis.advancedjava.season2.stages.Utils.AUTH_METHOD;
import static edu.technopolis.advancedjava.season2.stages.Utils.AUTH_REJECT;
import static edu.technopolis.advancedjava.season2.stages.Utils.SOCKS_VERSION;

public class AuthReadStage implements IStage {

    private ByteBuffer buffer;
    private SocketChannel socketChannel;

    public AuthReadStage(SocketChannel socketChannel, ByteBuffer byteBuffer) {
        this.buffer = byteBuffer;
        this.socketChannel = socketChannel;
    }

    private void reject(Map<SocketChannel, IStage> map) {
        buffer.put(SOCKS_VERSION)
                .put(AUTH_REJECT)
                .flip();
        map.put(socketChannel, new AuthWriteStage(socketChannel, buffer, false));
    }


    private void accept(Map<SocketChannel, IStage> map) {
        buffer.clear();
        buffer.put(SOCKS_VERSION)
                .put(AUTH_METHOD)
                .flip();
        map.put(socketChannel, new AuthWriteStage(socketChannel, buffer, true));
    }

    private boolean isAcceptableAuthMethod(int methodsNumber, ByteBuffer bb) {
        if (methodsNumber < 1) return false;
        for (int i = 0; i < methodsNumber; i++) {
            if (bb.get() == AUTH_METHOD) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void proceed(int ignore, Selector selector, Map<SocketChannel, IStage> map) {
        try {
            int bytes = socketChannel.read(buffer);

            if (bytes < 3) return;

            buffer.flip();

            if (buffer.get() == SOCKS_VERSION
                    && isAcceptableAuthMethod(buffer.get(), buffer)) {
                accept(map);
            } else {
                reject(map);
            }

            socketChannel.register(selector, SelectionKey.OP_WRITE);

        } catch (IOException ex) {
            System.err.println(ex);
            ex.printStackTrace();
        }
    }

}
