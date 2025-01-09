package com.github.saphyra.apphub.service.elite_base.message_processing.dao.body_data;

import com.github.saphyra.apphub.service.elite_base.message_processing.dao.body_data.body_material.BodyMaterial;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.body_data.body_material.BodyMaterialFactory;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.body_data.body_ring.BodyRing;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.body_data.body_ring.BodyRingFactory;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.NamePercentPair;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.Ring;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BodyDataFactoryTest {
    private static final UUID BODY_ID = UUID.randomUUID();
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();
    private static final Double SURFACE_GRAVITY = 34.32;

    @Mock
    private BodyMaterialFactory bodyMaterialFactory;

    @Mock
    private BodyRingFactory bodyRingFactory;

    @InjectMocks
    private BodyDataFactory underTest;

    @Mock
    private NamePercentPair material;

    @Mock
    private Ring ring;

    @Mock
    private BodyMaterial bodyMaterial;

    @Mock
    private BodyRing bodyRing;

    @ParameterizedTest
    @MethodSource("falseish")
    void create_falseish(Boolean value) {
        assertThat(underTest.create(BODY_ID, LAST_UPDATE, value, SURFACE_GRAVITY, ReserveLevel.LOW, value, null, null))
            .returns(BODY_ID, BodyData::getBodyId)
            .returns(LAST_UPDATE, BodyData::getLastUpdate)
            .returns(value, BodyData::getLandable)
            .returns(SURFACE_GRAVITY, BodyData::getSurfaceGravity)
            .returns(ReserveLevel.LOW, BodyData::getReserveLevel)
            .returns(value, BodyData::getHasRing)
            .returns(Collections.emptyList(), BodyData::getMaterials)
            .returns(Collections.emptyList(), BodyData::getRings);
    }

    @Test
    void create() {
        NamePercentPair[] materials = {material};
        Ring[] rings = {ring};

        given(bodyMaterialFactory.create(BODY_ID, materials)).willReturn(List.of(bodyMaterial));
        given(bodyRingFactory.create(BODY_ID, rings)).willReturn(List.of(bodyRing));

        assertThat(underTest.create(BODY_ID, LAST_UPDATE, true, SURFACE_GRAVITY, ReserveLevel.LOW, true, materials, rings))
            .returns(BODY_ID, BodyData::getBodyId)
            .returns(LAST_UPDATE, BodyData::getLastUpdate)
            .returns(true, BodyData::getLandable)
            .returns(SURFACE_GRAVITY, BodyData::getSurfaceGravity)
            .returns(ReserveLevel.LOW, BodyData::getReserveLevel)
            .returns(true, BodyData::getHasRing)
            .returns(List.of(bodyMaterial), BodyData::getMaterials)
            .returns(List.of(bodyRing), BodyData::getRings);
    }

    private static Stream<Arguments> falseish() {
        return Stream.of(
            Arguments.of(new Object[]{null}),
            Arguments.of(false)
        );
    }
}