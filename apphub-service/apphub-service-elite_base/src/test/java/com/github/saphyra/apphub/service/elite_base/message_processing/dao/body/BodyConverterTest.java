package com.github.saphyra.apphub.service.elite_base.message_processing.dao.body;

import com.github.saphyra.apphub.lib.common_util.DateTimeConverter;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
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
class BodyConverterTest {
    private static final UUID ID = UUID.randomUUID();
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final Long BODY_ID = 456L;
    private static final String BODY_NAME = "body-name";
    private static final Double DISTANCE_FROM_STAR = 2345d;
    private static final String ID_STRING = "id";
    private static final String STAR_SYSTEM_ID_STRING = "star-system-id";
    private static final String LAST_UPDATE_STRING = "last-update";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private DateTimeConverter dateTimeConverter;

    @InjectMocks
    private BodyConverter underTest;

    @Test
    void convertDomain() {
        Body domain = Body.builder()
            .id(ID)
            .lastUpdate(LAST_UPDATE)
            .starSystemId(STAR_SYSTEM_ID)
            .type(BodyType.STAR)
            .bodyId(BODY_ID)
            .bodyName(BODY_NAME)
            .distanceFromStar(DISTANCE_FROM_STAR)
            .build();

        given(uuidConverter.convertDomain(ID)).willReturn(ID_STRING);
        given(uuidConverter.convertDomain(STAR_SYSTEM_ID)).willReturn(STAR_SYSTEM_ID_STRING);
        given(dateTimeConverter.convertDomain(LAST_UPDATE)).willReturn(LAST_UPDATE_STRING);

        assertThat(underTest.convertDomain(domain))
            .returns(ID_STRING, BodyEntity::getId)
            .returns(LAST_UPDATE_STRING, BodyEntity::getLastUpdate)
            .returns(STAR_SYSTEM_ID_STRING, BodyEntity::getStarSystemId)
            .returns(BodyType.STAR, BodyEntity::getType)
            .returns(BODY_ID, BodyEntity::getBodyId)
            .returns(BODY_NAME, BodyEntity::getBodyName)
            .returns(DISTANCE_FROM_STAR, BodyEntity::getDistanceFromStar);
    }

    @Test
    void convertEntity() {
        BodyEntity entity = BodyEntity.builder()
            .id(ID_STRING)
            .lastUpdate(LAST_UPDATE_STRING)
            .starSystemId(STAR_SYSTEM_ID_STRING)
            .type(BodyType.STAR)
            .bodyId(BODY_ID)
            .bodyName(BODY_NAME)
            .distanceFromStar(DISTANCE_FROM_STAR)
            .build();

        given(uuidConverter.convertEntity(ID_STRING)).willReturn(ID);
        given(uuidConverter.convertEntity(STAR_SYSTEM_ID_STRING)).willReturn(STAR_SYSTEM_ID);
        given(dateTimeConverter.convertToLocalDateTime(LAST_UPDATE_STRING)).willReturn(LAST_UPDATE);

        assertThat(underTest.convertEntity(entity))
            .returns(ID, Body::getId)
            .returns(LAST_UPDATE, Body::getLastUpdate)
            .returns(STAR_SYSTEM_ID, Body::getStarSystemId)
            .returns(BodyType.STAR, Body::getType)
            .returns(BODY_ID, Body::getBodyId)
            .returns(BODY_NAME, Body::getBodyName)
            .returns(DISTANCE_FROM_STAR, Body::getDistanceFromStar);
    }
}