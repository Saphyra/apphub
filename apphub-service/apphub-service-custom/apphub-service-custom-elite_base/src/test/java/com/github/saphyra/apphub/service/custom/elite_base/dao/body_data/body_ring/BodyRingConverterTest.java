package com.github.saphyra.apphub.service.custom.elite_base.dao.body_data.body_ring;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body_data.body_ring.BodyRing;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body_data.body_ring.BodyRingConverter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body_data.body_ring.BodyRingEntity;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body_data.body_ring.RingType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BodyRingConverterTest {
    private static final UUID ID = UUID.randomUUID();
    private static final UUID BODY_ID = UUID.randomUUID();
    private static final String NAME = "name";
    private static final Double INNER_RADIUS = 324.3;
    private static final Double OUTER_RADIUS = 2345.4;
    private static final Double MASS = 53.3;
    private static final String ID_STRING = "id";
    private static final String BODY_ID_STRING = "body-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private BodyRingConverter underTest;

    @Test
    void convertDomain() {
        BodyRing domain = BodyRing.builder()
            .id(ID)
            .bodyId(BODY_ID)
            .name(NAME)
            .type(RingType.ICY)
            .innerRadius(INNER_RADIUS)
            .outerRadius(OUTER_RADIUS)
            .mass(MASS)
            .build();

        given(uuidConverter.convertDomain(ID)).willReturn(ID_STRING);
        given(uuidConverter.convertDomain(BODY_ID)).willReturn(BODY_ID_STRING);

        assertThat(underTest.convertDomain(domain))
            .returns(ID_STRING, BodyRingEntity::getId)
            .returns(BODY_ID_STRING, BodyRingEntity::getBodyId)
            .returns(NAME, BodyRingEntity::getName)
            .returns(RingType.ICY, BodyRingEntity::getType)
            .returns(INNER_RADIUS, BodyRingEntity::getInnerRadius)
            .returns(OUTER_RADIUS, BodyRingEntity::getOuterRadius)
            .returns(MASS, BodyRingEntity::getMass);
    }

    @Test
    void convertEntity() {
        BodyRingEntity domain = BodyRingEntity.builder()
            .id(ID_STRING)
            .bodyId(BODY_ID_STRING)
            .name(NAME)
            .type(RingType.ICY)
            .innerRadius(INNER_RADIUS)
            .outerRadius(OUTER_RADIUS)
            .mass(MASS)
            .build();

        given(uuidConverter.convertEntity(ID_STRING)).willReturn(ID);
        given(uuidConverter.convertEntity(BODY_ID_STRING)).willReturn(BODY_ID);

        assertThat(underTest.convertEntity(domain))
            .returns(ID, BodyRing::getId)
            .returns(BODY_ID, BodyRing::getBodyId)
            .returns(NAME, BodyRing::getName)
            .returns(RingType.ICY, BodyRing::getType)
            .returns(INNER_RADIUS, BodyRing::getInnerRadius)
            .returns(OUTER_RADIUS, BodyRing::getOuterRadius)
            .returns(MASS, BodyRing::getMass);
    }
}