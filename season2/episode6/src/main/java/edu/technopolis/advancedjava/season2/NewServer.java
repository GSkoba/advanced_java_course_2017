package edu.technopolis.advancedjava.season2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.sun.istack.internal.NotNull;
import edu.technopolis.advancedjava.season2.stages.AuthReadStage;
import edu.technopolis.advancedjava.season2.stages.IStage;

import static edu.technopolis.advancedjava.season2.stages.Utils.BUFFER_SIZE;

public class NewServer {
    public static void main(String[] args) {
        Map<SocketChannel, IStage> map = new HashMap<>();
        try (ServerSocketChannel serverChannel = ServerSocketChannel.open();
             Selector selector = Selector.open()) {
            serverChannel.configureBlocking(false);
            serverChannel.bind(new InetSocketAddress(10001));
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                selector.select();
                @NotNull
                Set<SelectionKey> keys = selector.selectedKeys();
                if (keys.isEmpty()) {
                    continue;
                }
                keys.removeIf(key -> {
                    if (!key.isValid()) {
                        return true;
                    }
                    if (key.isAcceptable()) {
                        return accept(map, key);
                    }
                    if (key.isReadable()) {
                        map.get(key.channel()).proceed(SelectionKey.OP_READ, selector, map);
                        return true;
                    }
                    if(key.isConnectable()){
                        map.get(key.channel()).proceed(SelectionKey.OP_CONNECT, selector, map);
                        return true;
                    }
                    if (key.isWritable()) {
                        map.get(key.channel()).proceed(SelectionKey.OP_WRITE, selector, map);
                        return true;
                    }
                    return true;
                });
                map.keySet().removeIf(channel -> !channel.isOpen());
            }

        } catch (IOException e) {
            LogUtils.logException("Unexpected error on processing incoming connection", e);
        }
    }


    private static boolean accept(Map<SocketChannel, IStage> map, SelectionKey key) {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel channel = null;
        try {
            channel = serverChannel.accept();
            channel.configureBlocking(false);
            channel.register(key.selector(), SelectionKey.OP_READ);
            map.put(channel, new AuthReadStage(channel, ByteBuffer.allocate(BUFFER_SIZE)));
        } catch (IOException e) {
            LogUtils.logException("Failed to process channel " + channel, e);
            if (channel != null) {
                closeChannel(channel);
            }
        }
        return true;
    }

    private static void closeChannel(SocketChannel accept) {
        try {
            accept.close();
        } catch (IOException e) {
            System.err.println("Failed to close channel " + accept);
        }
    }

}
