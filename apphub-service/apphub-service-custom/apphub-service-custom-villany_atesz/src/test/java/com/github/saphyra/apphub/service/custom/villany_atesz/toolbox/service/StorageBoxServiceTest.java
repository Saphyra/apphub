package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service;

import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.storage_box.StorageBox;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.storage_box.StorageBoxDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class StorageBoxServiceTest {
    private static final UUID STORAGE_BOX_ID = UUID.randomUUID();
    private static final String NAME = "name";
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private StorageBoxDao storageBoxDao;

    @InjectMocks
    private StorageBoxService underTest;

    @Mock
    private StorageBox storageBox;

    @Test
    void edit_blankBane() {
        ExceptionValidator.validateInvalidParam(() -> underTest.edit(STORAGE_BOX_ID, " "), "name", "must not be null or blank");
    }

    @Test
    void edit() {
        given(storageBoxDao.findByIdValidated(STORAGE_BOX_ID)).willReturn(storageBox);

        underTest.edit(STORAGE_BOX_ID, NAME);

        then(storageBox).should().setName(NAME);
        then(storageBoxDao).should().save(storageBox);
    }

    @Test
    void delete() {
        underTest.delete(USER_ID, STORAGE_BOX_ID);

        then(storageBoxDao).should().deleteByUserIdAndStorageBoxId(USER_ID, STORAGE_BOX_ID);
    }
}