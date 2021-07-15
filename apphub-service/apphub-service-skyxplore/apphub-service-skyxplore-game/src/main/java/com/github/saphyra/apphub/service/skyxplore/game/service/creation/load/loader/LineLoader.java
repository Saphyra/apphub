package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.LineModel;
import com.github.saphyra.apphub.lib.geometry.Line;
import com.github.saphyra.apphub.service.skyxplore.game.common.LineModelWrapper;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class LineLoader {
    private final GameItemLoader gameItemLoader;
    private final CoordinateLoader coordinateLoader;

    LineModelWrapper loadOne(UUID referenceId) {
        return loadAll(referenceId)
            .stream()
            .findFirst()
            .orElse(null);
    }

    List<LineModelWrapper> loadAll(UUID referenceId) {
        List<LineModel> lines = gameItemLoader.loadChildren(referenceId, GameItemType.LINE, LineModel[].class);
        return lines.stream()
            .map(this::convert)
            .collect(Collectors.toList());
    }

    private LineModelWrapper convert(LineModel model) {
        CoordinateModel a = coordinateLoader.loadById(model.getA());
        CoordinateModel b = coordinateLoader.loadById(model.getB());

        return LineModelWrapper.builder()
            .model(model)
            .line(new Line(a.getCoordinate(), b.getCoordinate()))
            .a(a)
            .b(b)
            .build();
    }
}
