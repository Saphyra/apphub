package com.github.saphyra.apphub.service.custom.elite_base.dao.body.body_material;

import com.github.saphyra.apphub.service.custom.elite_base.dao.body.body_material.BodyMaterial;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body.body_material.BodyMaterialDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body.body_material.BodyMaterialSyncService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class BodyMaterialSyncServiceTest {
    private static final UUID BODY_ID = UUID.randomUUID();

    @Mock
    private BodyMaterialDao bodyMaterialDao;

    @InjectMocks
    private BodyMaterialSyncService underTest;

    @Mock
    private BodyMaterial existingMaterial;

    @Mock
    private BodyMaterial newMaterial;

    @Mock
    private BodyMaterial deprecatedMaterial;

    @Test
    void sync() {
        given(bodyMaterialDao.getByBodyId(BODY_ID)).willReturn(List.of(existingMaterial, deprecatedMaterial));

        underTest.sync(BODY_ID, List.of(existingMaterial, newMaterial));

        then(bodyMaterialDao).should().deleteAll(List.of(deprecatedMaterial));
        then(bodyMaterialDao).should().saveAll(List.of(newMaterial));
    }
}