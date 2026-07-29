package net.colourlabs.bucketpatches.patches.coreprotect;

import java.lang.reflect.Proxy;
import java.sql.Connection;

public class ConnectionPool {
    private static Connection real;
    private static Connection proxy;

    public static synchronized Connection pool(Connection conn) {
        if (conn == null) return null;
        if (conn == proxy) return conn;
        if (conn == real) return proxy;

        real = conn;
        proxy = (Connection) Proxy.newProxyInstance(
            Connection.class.getClassLoader(),
            new Class[]{Connection.class},
            (target, method, args) -> {
                if (method.getName().equals("close")) return null;
                return method.invoke(conn, args);
            }
        );
        return proxy;
    }

    public static synchronized Connection borrow() {
        if (real == null || proxy == null) return null;
        try {
            if (!real.isClosed()) {
                try {
                    if (real.isValid(3)) return proxy;
                } catch (AbstractMethodError | Exception e) {
                    return proxy;
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    public static synchronized void shutdown() {
        try { if (real != null) real.close(); } catch (Exception ignored) {}
        real = null;
        proxy = null;
    }
}
