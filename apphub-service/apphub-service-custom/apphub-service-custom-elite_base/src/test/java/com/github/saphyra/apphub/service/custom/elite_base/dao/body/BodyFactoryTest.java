package com.github.saphyra.apphub.service.custom.elite_base.dao.body;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body.Body;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body.BodyFactory;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body.BodyType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BodyFactoryTest {
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final UUID ID = UUID.randomUUID();
    private static final Long BODY_ID = 324L;
    private static final String BODY_NAME = "body-name";
    private static final Double DISTANCE_FROM_STAR = 132.2;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private BodyFactory underTest;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(ID);

        assertThat(underTest.create(LAST_UPDATE, STAR_SYSTEM_ID, BodyType.PLANET, BODY_ID, BODY_NAME, DISTANCE_FROM_STAR))
            .returns(ID, Body::getId)
            .returns(LAST_UPDATE, Body::getLastUpdate)
            .returns(STAR_SYSTEM_ID, Body::getStarSystemId)
            .returns(BodyType.PLANET, Body::getType)
            .returns(BODY_ID, Body::getBodyId)
            .returns(BODY_NAME, Body::getBodyName)
            .returns(DISTANCE_FROM_STAR, Body::getDistanceFromStar);
    }
}