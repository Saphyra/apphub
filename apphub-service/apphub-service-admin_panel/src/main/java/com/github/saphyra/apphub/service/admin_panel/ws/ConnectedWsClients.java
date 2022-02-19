package com.github.saphyra.apphub.service.admin_panel.ws;

import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.Vector;

@Component
public class ConnectedWsClients extends Vector<UUID> {
    public synchronized void addIfAbsent(UUID userId) {
        if (!contains(userId)) {
            add(userId);
        }
    }
}
