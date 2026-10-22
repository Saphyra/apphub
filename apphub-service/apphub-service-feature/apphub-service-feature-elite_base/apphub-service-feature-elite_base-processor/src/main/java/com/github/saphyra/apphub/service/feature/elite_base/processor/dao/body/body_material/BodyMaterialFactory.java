package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.body.body_material;

import com.github.saphyra.apphub.service.feature.elite_base.processor.message_processing.structure.journal.NamePercentPair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BodyMaterialFactory {
    public List<BodyMaterial> create(UUID bodyId, NamePercentPair[] materials) {
        return Arrays.stream(materials)
            .map(namePercentPair -> create(bodyId, namePercentPair))
            .toList();
    }

    private BodyMaterial create(UUID bodyId, NamePercentPair namePercentPair) {
        return BodyMaterial.builder()
            .bodyId(bodyId)
            .material(namePercentPair.getName())
            .percent(namePercentPair.getPercent())
            .build();
    }
}
