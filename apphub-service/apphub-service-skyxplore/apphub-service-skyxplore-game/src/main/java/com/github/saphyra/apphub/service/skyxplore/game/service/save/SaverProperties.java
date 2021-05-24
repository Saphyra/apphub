package com.github.saphyra.apphub.service.skyxplore.game.service.save;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class SaverProperties {
    @Value("${game.saver.maxChunkSize}")
    private int maxChunkSize;
}
