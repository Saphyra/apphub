package com.github.saphyra.apphub.service.custom.elite_base.dao.body_data;

import com.github.saphyra.apphub.lib.common_util.DateTimeConverter;
import com.github.saphyra.apphub.lib.common_util.LazyLoadedField;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body_data.BodyData;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body_data.BodyDataConverter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body_data.BodyDataEntity;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body_data.ReserveLevel;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body_data.body_material.BodyMaterial;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body_data.body_material.BodyMaterialDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body_data.body_material.BodyMaterialSyncService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body_data.body_ring.BodyRing;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body_data.body_ring.BodyRingDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body_data.body_ring.BodyRingSyncService;
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
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class BodyDataConverterTest {
    private static final UUID BODY_ID = UUID.randomUUID();
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();
    private static final Double SURFACE_GRAVITY = 34.3214;
    private static final String BODY_ID_STRING = "body-id";
    private static final String LAST_UPDATE_STRING = "last-update";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private DateTimeConverter dateTimeConverter;

    @Mock
    private BodyMaterialDao bodyMaterialDao;

    @Mock
    private BodyRingDao bodyRingDao;

    @Mock
    private BodyMaterialSyncService bodyMaterialSyncService;

    @Mock
    private BodyRingSyncService bodyRingSyncService;

    @InjectMocks
    private BodyDataConverter underTest;

    @Mock
    private BodyMaterial material;

    @Mock
    private BodyRing ring;

    @ParameterizedTest
    @MethodSource("falseish")
    void convertDomain_falseish(Boolean value) {
        BodyData domain = BodyData.builder()
            .bodyId(BODY_ID)
            .lastUpdate(LAST_UPDATE)
            .landable(value)
            .surfaceGravity(SURFACE_GRAVITY)
            .reserveLevel(ReserveLevel.LOW)
            .hasRing(value)
            .build();

        given(uuidConverter.convertDomain(BODY_ID)).willReturn(BODY_ID_STRING);
        given(dateTimeConverter.convertDomain(LAST_UPDATE)).willReturn(LAST_UPDATE_STRING);

        assertThat(underTest.convertDomain(domain))
            .returns(BODY_ID_STRING, BodyDataEntity::getBodyId)
            .returns(LAST_UPDATE_STRING, BodyDataEntity::getLastUpdate)
            .returns(value, BodyDataEntity::getLandable)
            .returns(SURFACE_GRAVITY, BodyDataEntity::getSurfaceGravity)
            .returns(ReserveLevel.LOW, BodyDataEntity::getReserveLevel)
            .returns(value, BodyDataEntity::getHasRing);

        then(bodyMaterialSyncService).shouldHaveNoInteractions();
        then(bodyRingSyncService).shouldHaveNoInteractions();
    }

    @Test
    void convertDomain() {
        BodyData domain = BodyData.builder()
            .bodyId(BODY_ID)
            .lastUpdate(LAST_UPDATE)
            .landable(true)
            .surfaceGravity(SURFACE_GRAVITY)
            .reserveLevel(ReserveLevel.LOW)
            .hasRing(true)
            .materials(LazyLoadedField.loaded(List.of(material)))
            .rings(LazyLoadedField.loaded(List.of(ring)))
            .build();

        given(uuidConverter.convertDomain(BODY_ID)).willReturn(BODY_ID_STRING);
        given(dateTimeConverter.convertDomain(LAST_UPDATE)).willReturn(LAST_UPDATE_STRING);

        assertThat(underTest.convertDomain(domain))
            .returns(BODY_ID_STRING, BodyDataEntity::getBodyId)
            .returns(LAST_UPDATE_STRING, BodyDataEntity::getLastUpdate)
            .returns(true, BodyDataEntity::getLandable)
            .returns(SURFACE_GRAVITY, BodyDataEntity::getSurfaceGravity)
            .returns(ReserveLevel.LOW, BodyDataEntity::getReserveLevel)
            .returns(true, BodyDataEntity::getHasRing);

        then(bodyMaterialSyncService).should().sync(BODY_ID, List.of(material));
        then(bodyRingSyncService).should().sync(BODY_ID, List.of(ring));
    }

    @ParameterizedTest
    @MethodSource("falseish")
    void convertEntity_falseish(Boolean value) {
        BodyDataEntity domain = BodyDataEntity.builder()
            .bodyId(BODY_ID_STRING)
            .lastUpdate(LAST_UPDATE_STRING)
            .landable(value)
            .surfaceGravity(SURFACE_GRAVITY)
            .reserveLevel(ReserveLevel.LOW)
            .hasRing(value)
            .build();

        given(uuidConverter.convertEntity(BODY_ID_STRING)).willReturn(BODY_ID);
        given(dateTimeConverter.convertToLocalDateTime(LAST_UPDATE_STRING)).willReturn(LAST_UPDATE);

        assertThat(underTest.convertEntity(domain))
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
    void convertEntity() {
        BodyDataEntity domain = BodyDataEntity.builder()
            .bodyId(BODY_ID_STRING)
            .lastUpdate(LAST_UPDATE_STRING)
            .landable(true)
            .surfaceGravity(SURFACE_GRAVITY)
            .reserveLevel(ReserveLevel.LOW)
            .hasRing(true)
            .build();

        given(uuidConverter.convertEntity(BODY_ID_STRING)).willReturn(BODY_ID);
        given(dateTimeConverter.convertToLocalDateTime(LAST_UPDATE_STRING)).willReturn(LAST_UPDATE);
        given(bodyMaterialDao.getByBodyId(BODY_ID)).willReturn(List.of(material));
        given(bodyRingDao.getByBodyId(BODY_ID)).willReturn(List.of(ring));

        assertThat(underTest.convertEntity(domain))
            .returns(BODY_ID, BodyData::getBodyId)
            .returns(LAST_UPDATE, BodyData::getLastUpdate)
            .returns(true, BodyData::getLandable)
            .returns(SURFACE_GRAVITY, BodyData::getSurfaceGravity)
            .returns(ReserveLevel.LOW, BodyData::getReserveLevel)
            .returns(true, BodyData::getHasRing)
            .returns(List.of(material), BodyData::getMaterials)
            .returns(List.of(ring), BodyData::getRings);
    }

    private static Stream<Arguments> falseish() {
        return Stream.of(
            Arguments.of(new Object[]{null}),
            Arguments.of(false)
        );
    }
}