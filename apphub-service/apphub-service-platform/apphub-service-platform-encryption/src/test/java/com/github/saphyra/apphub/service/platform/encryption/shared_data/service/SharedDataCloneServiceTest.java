package com.github.saphyra.apphub.service.platform.encryption.shared_data.service;

import com.github.saphyra.apphub.api.platform.encryption.model.AccessMode;
import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.api.platform.encryption.model.SharedData;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.platform.encryption.shared_data.dao.SharedDataDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SharedDataCloneServiceTest {
    private static final UUID EXTERNAL_ID = UUID.randomUUID();
    private static final UUID SHARED_DATA_ID = UUID.randomUUID();
    private static final UUID SHARED_WITH = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private SharedDataDao sharedDataDao;

    @Mock
    private SharedDataValidator sharedDataValidator;

    @InjectMocks
    private SharedDataCloneService underTest;

    @Mock
    private SharedData existingSharedData;

    @Test
    public void cloneSharedData() {
        SharedData inputSharedData = SharedData.builder()
            .externalId(EXTERNAL_ID)
            .dataType(DataType.TEST)
            .build();

        given(existingSharedData.getSharedWith()).willReturn(SHARED_WITH);
        given(existingSharedData.getPublicData()).willReturn(true);
        given(existingSharedData.getAccessMode()).willReturn(AccessMode.EDIT);

        given(sharedDataDao.getByExternalIdAndDataType(EXTERNAL_ID, DataType.TEST)).willReturn(List.of(existingSharedData));
        given(idGenerator.randomUuid()).willReturn(SHARED_DATA_ID);

        underTest.cloneSharedData(inputSharedData, DataType.TEST, EXTERNAL_ID);

        verify(sharedDataValidator).validateForCloning(inputSharedData);

        ArgumentCaptor<SharedData> argumentCaptor = ArgumentCaptor.forClass(SharedData.class);
        verify(sharedDataDao).save(argumentCaptor.capture());
        SharedData clonedSharedData = argumentCaptor.getValue();
        assertThat(clonedSharedData.getSharedDataId()).isEqualTo(SHARED_DATA_ID);
        assertThat(clonedSharedData.getExternalId()).isEqualTo(EXTERNAL_ID);
        assertThat(clonedSharedData.getDataType()).isEqualTo(DataType.TEST);
        assertThat(clonedSharedData.getSharedWith()).isEqualTo(SHARED_WITH);
        assertThat(clonedSharedData.getPublicData()).isTrue();
        assertThat(clonedSharedData.getAccessMode()).isEqualTo(AccessMode.EDIT);
    }
}