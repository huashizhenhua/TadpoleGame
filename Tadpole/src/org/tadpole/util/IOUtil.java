package org.tadpole.util;

import java.io.Closeable;
import java.io.IOException;

public class IOUtil {
    public static void close(Closeable obj) {
        if (obj != null) {
            try {
                obj.close();
                obj = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
