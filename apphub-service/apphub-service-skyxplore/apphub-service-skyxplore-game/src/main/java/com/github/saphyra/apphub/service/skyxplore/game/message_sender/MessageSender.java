package com.github.saphyra.apphub.service.skyxplore.game.message_sender;

import com.github.saphyra.apphub.lib.concurrency.FutureWrapper;

import java.util.List;

public interface MessageSender {
    List<FutureWrapper<Boolean>> sendMessages();
}
