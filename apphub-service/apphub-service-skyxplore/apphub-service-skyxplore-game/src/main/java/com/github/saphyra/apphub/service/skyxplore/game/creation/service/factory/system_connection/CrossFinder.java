package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.system_connection;

import java.util.Collection;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.github.saphyra.apphub.lib.geometry.Cross;
import com.github.saphyra.apphub.lib.geometry.CrossCalculator;
import com.github.saphyra.apphub.lib.geometry.Line;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
class CrossFinder {
    private final CrossCalculator crossCalculator;

    Optional<Cross> findCross(Collection<Line> lines) {
        for (Line line1 : lines) {
            for (Line line2 : lines) {
                if (line1.equals(line2)) {
                    continue;
                }
                Optional<Cross> cross = crossCalculator.getCrossPointOfSections(line1, line2, false);
                if (cross.isPresent()) {
                    log.debug("Cross found: {}", cross.get());
                    return cross;
                }
            }
        }

        return Optional.empty();
    }
}
