package com.github.saphyra.apphub.service.elite_base.message_processing.dao.body_data.body_ring;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.Ring;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class BodyRingFactory {
    private final IdGenerator idGenerator;

    public List<BodyRing> create(UUID bodyId, Ring[] rings) {
        return Arrays.stream(rings)
            .map(ring -> create(bodyId, ring))
            .toList();
    }

    private BodyRing create(UUID bodyId, Ring ring) {
        return BodyRing.builder()
            .id(idGenerator.randomUuid())
            .bodyId(bodyId)
            .name(ring.getRingName())
            .type(RingType.parse(ring.getRingType()))
            .innerRadius(ring.getInnerRadius())
            .outerRadius(ring.getOuterRadius())
            .mass(ring.getMass())
            .build();
    }
}
