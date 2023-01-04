package com.github.saphyra.apphub.service.platform.encryption.shared_data;

import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.api.platform.encryption.model.SharedData;
import com.github.saphyra.apphub.service.platform.encryption.shared_data.dao.SharedDataDao;
import com.github.saphyra.apphub.service.platform.encryption.shared_data.service.SharedDataCloneService;
import com.github.saphyra.apphub.service.platform.encryption.shared_data.service.SharedDataCreationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SharedDataControllerImplTest {
    private static final UUID EXTERNAL_ID = UUID.randomUUID();
    private static final UUID SHARED_DATA_ID = UUID.randomUUID();

    @Mock
    private SharedDataCreationService sharedDataCreationService;

    @Mock
    private SharedDataCloneService sharedDataCloneService;

    @Mock
    private SharedDataDao sharedDataDao;

    @InjectMocks
    private SharedDataControllerImpl underTest;

    @Mock
    private SharedData sharedData;

    @Test
    public void getSharedData() {
        given(sharedDataDao.getByExternalIdAndDataType(EXTERNAL_ID, DataType.TEST)).willReturn(List.of(sharedData));

        List<SharedData> result = underTest.getSharedData(DataType.TEST, EXTERNAL_ID);

        assertThat(result).containsExactly(sharedData);
    }

    @Test
    public void createSharedData() {
        underTest.createSharedData(sharedData);

        verify(sharedDataCreationService).createSharedData(sharedData);
    }

    @Test
    public void cloneSharedData() {
        underTest.cloneSharedData(sharedData, DataType.TEST, EXTERNAL_ID);

        verify(sharedDataCloneService).cloneSharedData(sharedData, DataType.TEST, EXTERNAL_ID);
    }

    @Test
    public void deleteSharedDataEntity() {
        underTest.deleteSharedDataEntity(SHARED_DATA_ID);

        verify(sharedDataDao).deleteById(SHARED_DATA_ID);
    }

    @Test
    public void deleteSharedData() {
        underTest.deleteSharedData(DataType.TEST, EXTERNAL_ID);

        verify(sharedDataDao).deleteByExternalIdAndDataType(EXTERNAL_ID, DataType.TEST);
    }
}