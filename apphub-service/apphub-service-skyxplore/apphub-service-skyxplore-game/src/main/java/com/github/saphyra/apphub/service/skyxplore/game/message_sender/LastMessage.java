package com.github.saphyra.apphub.service.skyxplore.game.message_sender;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Data
public class LastMessage<T> {
    private final T payload;
    private final LocalDateTime sentAt;
}
