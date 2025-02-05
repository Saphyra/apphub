package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.body_data.body_material;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BodyMaterialConverterTest {
    private static final UUID ID = UUID.randomUUID();
    private static final UUID BODY_ID = UUID.randomUUID();
    private static final String MATERIAL = "material";
    private static final Double PERCENT = 23.3;
    private static final String ID_STRING = "id";
    private static final String BODY_ID_STRING = "body-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private BodyMaterialConverter underTest;

    @Test
    void convertDomain() {
        BodyMaterial domain = BodyMaterial.builder()
            .id(ID)
            .bodyId(BODY_ID)
            .material(MATERIAL)
            .percent(PERCENT)
            .build();

        given(uuidConverter.convertDomain(ID)).willReturn(ID_STRING);
        given(uuidConverter.convertDomain(BODY_ID)).willReturn(BODY_ID_STRING);

        assertThat(underTest.convertDomain(domain))
            .returns(ID_STRING, BodyMaterialEntity::getId)
            .returns(BODY_ID_STRING, BodyMaterialEntity::getBodyId)
            .returns(MATERIAL, BodyMaterialEntity::getMaterial)
            .returns(PERCENT, BodyMaterialEntity::getPercent);
    }

    @Test
    void convertEntity() {
        BodyMaterialEntity entity = BodyMaterialEntity.builder()
            .id(ID_STRING)
            .bodyId(BODY_ID_STRING)
            .material(MATERIAL)
            .percent(PERCENT)
            .build();

        given(uuidConverter.convertEntity(ID_STRING)).willReturn(ID);
        given(uuidConverter.convertEntity(BODY_ID_STRING)).willReturn(BODY_ID);

        assertThat(underTest.convertEntity(entity))
            .returns(ID, BodyMaterial::getId)
            .returns(BODY_ID, BodyMaterial::getBodyId)
            .returns(MATERIAL, BodyMaterial::getMaterial)
            .returns(PERCENT, BodyMaterial::getPercent);
    }
}