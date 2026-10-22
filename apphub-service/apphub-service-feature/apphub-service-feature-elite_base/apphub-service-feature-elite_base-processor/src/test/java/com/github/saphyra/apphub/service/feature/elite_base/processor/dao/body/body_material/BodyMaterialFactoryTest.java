package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.body.body_material;

import com.github.saphyra.apphub.service.feature.elite_base.processor.message_processing.structure.journal.NamePercentPair;
import com.github.saphyra.apphub.test.common.CustomAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class BodyMaterialFactoryTest {
    private static final UUID BODY_ID = UUID.randomUUID();
    private static final String MATERIAL = "material";
    private static final Double PERCENT = 34.23;

    @InjectMocks
    private BodyMaterialFactory underTest;

    @Test
    void create() {
        CustomAssertions.singleListAssertThat(underTest.create(BODY_ID, new NamePercentPair[]{new NamePercentPair(MATERIAL, PERCENT)}))
            .returns(BODY_ID, BodyMaterial::getBodyId)
            .returns(MATERIAL, BodyMaterial::getMaterial)
            .returns(PERCENT, BodyMaterial::getPercent);

    }
}