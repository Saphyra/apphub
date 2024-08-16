package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.StorageBoxModel;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.storage_box.StorageBoxDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StorageBoxValidatorTest {
    private static final UUID STORAGE_BOX_ID = UUID.randomUUID();

    @Mock
    private StorageBoxDao storageBoxDao;

    @InjectMocks
    private StorageBoxValidator underTest;

    @Test
    void nullStorageBoxIdAndName() {
        ExceptionValidator.validateInvalidParam(() -> underTest.validate(StorageBoxModel.builder().build()), "storageBox.name", "must not be null");
    }

    @Test
    void storageBoxDoesNotExist() {
        given(storageBoxDao.exists(STORAGE_BOX_ID)).willReturn(false);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(StorageBoxModel.builder().storageBoxId(STORAGE_BOX_ID).build()), "storageBoxId", "not found");
    }

    @Test
    void onlyNameFilled() {
        underTest.validate(StorageBoxModel.builder().name("asd").build());
    }

    @Test
    void storageBoxFound() {
        given(storageBoxDao.exists(STORAGE_BOX_ID)).willReturn(true);

        underTest.validate(StorageBoxModel.builder().storageBoxId(STORAGE_BOX_ID).build());
    }
}