package com.github.saphyra.apphub.service.skyxplore.game.common;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.geometry.Line;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class LineModelWrapperFactory {
    private final CoordinateModelFactory coordinateModelFactory;
    private final LineModelFactory lineModelFactory;
    private final IdGenerator idGenerator;

    public LineModelWrapper create(Line line, UUID gameId, UUID referenceId) {
        UUID lineId = idGenerator.randomUuid();
        CoordinateModel a = coordinateModelFactory.create(line.getA(), gameId, lineId);
        CoordinateModel b = coordinateModelFactory.create(line.getB(), gameId, lineId);

        return LineModelWrapper.builder()
            .line(line)
            .model(lineModelFactory.create(lineId, gameId, referenceId, a.getId(), b.getId()))
            .a(a)
            .b(b)
            .build();
    }
}
