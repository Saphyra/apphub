package com.github.saphyra.apphub.service.platform.encryption.shared_data.service;

import com.github.saphyra.apphub.api.platform.encryption.model.SharedData;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.platform.encryption.shared_data.dao.SharedDataDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SharedDataCreationServiceTest {
    private static final UUID SHARED_DATA_ID = UUID.randomUUID();

    @Mock
    private SharedDataValidator sharedDataValidator;

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private SharedDataDao sharedDataDao;

    @InjectMocks
    private SharedDataCreationService underTest;

    @Mock
    private SharedData sharedData;

    @Test
    public void createSharedData() {
        given(idGenerator.randomUuid()).willReturn(SHARED_DATA_ID);

        underTest.createSharedData(sharedData);

        verify(sharedData).setSharedDataId(SHARED_DATA_ID);
        verify(sharedDataDao).save(sharedData);
    }
}