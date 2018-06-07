package edu.technopolis.advancedjava.season2;

import java.io.IOException;

/**
 * Логирование исключений
 */
public class LogUtils {
    static void logException(String s, IOException e) {
        System.err.println(s);
        e.printStackTrace();
    }
}
