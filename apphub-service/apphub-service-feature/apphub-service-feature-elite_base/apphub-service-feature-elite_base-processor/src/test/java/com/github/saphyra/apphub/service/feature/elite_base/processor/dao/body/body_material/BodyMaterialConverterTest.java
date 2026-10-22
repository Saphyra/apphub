package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.body.body_material;

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
    private static final UUID BODY_ID = UUID.randomUUID();
    private static final String MATERIAL = "material";
    private static final Double PERCENT = 23.3;
    private static final String BODY_ID_STRING = "body-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private BodyMaterialConverter underTest;

    @Test
    void convertDomain() {
        BodyMaterial domain = BodyMaterial.builder()
            .bodyId(BODY_ID)
            .material(MATERIAL)
            .percent(PERCENT)
            .build();

        given(uuidConverter.convertDomain(BODY_ID)).willReturn(BODY_ID_STRING);

        assertThat(underTest.convertDomain(domain))
            .returns(BODY_ID_STRING, e -> e.getId().getBodyId())
            .returns(MATERIAL, e -> e.getId().getMaterial())
            .returns(PERCENT, BodyMaterialEntity::getPercent);
    }

    @Test
    void convertEntity() {
        BodyMaterialEntity entity = BodyMaterialEntity.builder()
            .id(new BodyMaterialEntityId(BODY_ID_STRING, MATERIAL))
            .percent(PERCENT)
            .build();

        given(uuidConverter.convertEntity(BODY_ID_STRING)).willReturn(BODY_ID);

        assertThat(underTest.convertEntity(entity))
            .returns(BODY_ID, BodyMaterial::getBodyId)
            .returns(MATERIAL, BodyMaterial::getMaterial)
            .returns(PERCENT, BodyMaterial::getPercent);
    }
}