package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.body_data.body_material;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal.NamePercentPair;
import com.github.saphyra.apphub.test.common.CustomAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BodyMaterialFactoryTest {
    private static final UUID BODY_ID = UUID.randomUUID();
    private static final String MATERIAL = "material";
    private static final Double PERCENT = 34.23;
    private static final UUID ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private BodyMaterialFactory underTest;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(ID);

        CustomAssertions.singleListAssertThat(underTest.create(BODY_ID, new NamePercentPair[]{new NamePercentPair(MATERIAL, PERCENT)}))
            .returns(ID, BodyMaterial::getId)
            .returns(BODY_ID, BodyMaterial::getBodyId)
            .returns(MATERIAL, BodyMaterial::getMaterial)
            .returns(PERCENT, BodyMaterial::getPercent);

    }
}