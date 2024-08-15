package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.StorageBoxModel;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.storage_box.StorageBox;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.storage_box.StorageBoxDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class StorageBoxFactoryTest {
    private static final UUID STORAGE_BOX_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String NAME = "name";

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private StorageBoxDao storageBoxDao;

    @InjectMocks
    private StorageBoxFactory underTest;

    @Test
    void existingStorageBoxId() {
        StorageBoxModel model = StorageBoxModel.builder()
            .storageBoxId(STORAGE_BOX_ID)
            .build();

        assertThat(underTest.getStorageBoxId(USER_ID, model)).isEqualTo(STORAGE_BOX_ID);
    }

    @Test
    void blankName() {
        StorageBoxModel model = StorageBoxModel.builder()

            .build();

        assertThat(underTest.getStorageBoxId(USER_ID, model)).isNull();
    }

    @Test
    void createNew() {
        StorageBoxModel model = StorageBoxModel.builder()
            .name(NAME)
            .build();

        given(idGenerator.randomUuid()).willReturn(STORAGE_BOX_ID);

        assertThat(underTest.getStorageBoxId(USER_ID, model)).isEqualTo(STORAGE_BOX_ID);

        ArgumentCaptor<StorageBox> argumentCaptor = ArgumentCaptor.forClass(StorageBox.class);
        then(storageBoxDao).should().save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue())
            .returns(STORAGE_BOX_ID, StorageBox::getStorageBoxId)
            .returns(USER_ID, StorageBox::getUserId)
            .returns(NAME, StorageBox::getName);
    }
}