package com.github.saphyra.apphub.service.skyxplore.game.message_sender;

import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;

import java.util.List;
import java.util.concurrent.Future;

public interface MessageSender {
    List<Future<ExecutionResult<Boolean>>> sendMessages();
}
