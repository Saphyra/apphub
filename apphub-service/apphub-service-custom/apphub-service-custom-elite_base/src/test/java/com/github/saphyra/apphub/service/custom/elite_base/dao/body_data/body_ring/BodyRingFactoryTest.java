package com.github.saphyra.apphub.service.custom.elite_base.dao.body_data.body_ring;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body_data.body_ring.BodyRing;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body_data.body_ring.BodyRingFactory;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body_data.body_ring.RingType;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal.Ring;
import com.github.saphyra.apphub.test.common.CustomAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BodyRingFactoryTest {
    private static final UUID BODY_ID = UUID.randomUUID();
    private static final String RING_NAME = "rirng-name";
    private static final Double INNER_RADIUS = 34.34;
    private static final Double OUTER_RADIUS = 245.54;
    private static final Double MASS = 345.6;
    private static final UUID ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private BodyRingFactory underTest;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(ID);

        CustomAssertions.singleListAssertThat(underTest.create(BODY_ID, new Ring[]{new Ring(RING_NAME, "eRingClass_Icy", INNER_RADIUS, OUTER_RADIUS, MASS)}))
            .returns(ID, BodyRing::getId)
            .returns(BODY_ID, BodyRing::getBodyId)
            .returns(RING_NAME, BodyRing::getName)
            .returns(RingType.ICY, BodyRing::getType)
            .returns(INNER_RADIUS, BodyRing::getInnerRadius)
            .returns(OUTER_RADIUS, BodyRing::getOuterRadius)
            .returns(MASS, BodyRing::getMass);
    }
}