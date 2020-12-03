package com.github.saphyra.apphub.lib.websocket_gateway.utils;

import com.github.saphyra.apphub.lib.websocket_gateway.WsForwardProperties;
import org.springframework.util.Assert;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * common util
 *
 * @author Jeffrey
 * @since 2017/12/04 15:06
 */
public class CommonUtils {

    private CommonUtils() {
        throw new AssertionError("No CommonUtils instances for you!");
    }

    /**
     * get loadbalance list index
     *
     * @param count counter
     * @param size  list size
     * @return index
     */
    public static int getLoadBlanceIndex(AtomicInteger count, int size) {
        Assert.notNull(count, "LoadBlance count can't be null!");
        Assert.isTrue(size > 0, "LoadBlance list size need greader than 0!");
        int index;
        while (true) {
            int current = count.get();
            int next = (current + 1) % size;
            if (count.compareAndSet(current, next)) {
                index = next;
                break;
            }
        }
        return index;
    }

    /**
     * get websocket pattern
     *
     * @param handler ForwardHandler
     * @return websocket server pattern
     */
    public static String getWsPattern(WsForwardProperties.ForwardHandler handler) {
        return handler.getPrefix() != null ? handler.getPrefix() + handler.getUri()
            : handler.getUri();
    }
}
