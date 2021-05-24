package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.system_connection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.github.saphyra.apphub.lib.geometry.Cross;
import com.github.saphyra.apphub.lib.geometry.DistanceCalculator;
import com.github.saphyra.apphub.lib.geometry.Line;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
class CrossRemovalService {
    private final DistanceCalculator distanceCalculator;
    private final CrossFinder crossFinder;

    List<Line> removeCrosses(List<Line> lines) {
        int crossAmount = 0;
        List<Line> result = new ArrayList<>(lines);

        for (Optional<Cross> optionalCross = crossFinder.findCross(result); optionalCross.isPresent(); optionalCross = crossFinder.findCross(result)) {
            Cross cross = optionalCross.get();

            Line line1 = cross.getLine1();
            Line line2 = cross.getLine2();
            Line lineToRemove = Stream.of(line1, line2)
                .max(Comparator.comparing(o -> o.getLength(distanceCalculator)))
                .get();
            log.debug("Removing connection {}  due to cross with {}", lineToRemove, cross.getOther(lineToRemove));

            result.remove(lineToRemove);
            crossAmount++;
        }
        log.info("{} number of crosses were removed.", crossAmount);
        return result;
    }
}
