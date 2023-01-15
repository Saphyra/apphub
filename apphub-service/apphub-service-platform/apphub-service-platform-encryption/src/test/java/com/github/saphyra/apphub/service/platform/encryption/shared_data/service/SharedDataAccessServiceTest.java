package com.github.saphyra.apphub.service.platform.encryption.shared_data.service;

import com.github.saphyra.apphub.api.platform.encryption.model.AccessMode;
import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.api.platform.encryption.model.SharedData;
import com.github.saphyra.apphub.service.platform.encryption.shared_data.dao.SharedDataDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SharedDataAccessServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_ID = UUID.randomUUID();

    @Mock
    private SharedDataDao sharedDataDao;

    @InjectMocks
    private SharedDataAccessService underTest;

    @Mock
    private SharedData sharedData;

    @BeforeEach
    public void setUp() {
        given(sharedDataDao.getByExternalIdAndDataTypeAndAccessMode(EXTERNAL_ID, DataType.TEST, AccessMode.EDIT)).willReturn(List.of(sharedData));
    }

    @Test
    public void publicSharedData() {
        given(sharedData.getPublicData()).willReturn(true);

        boolean result = underTest.hasAccess(USER_ID, AccessMode.EDIT, EXTERNAL_ID, DataType.TEST);

        assertThat(result).isTrue();
    }

    @Test
    public void hasAccess() {
        given(sharedData.getPublicData()).willReturn(false);
        given(sharedData.getSharedWith()).willReturn(USER_ID);

        boolean result = underTest.hasAccess(USER_ID, AccessMode.EDIT, EXTERNAL_ID, DataType.TEST);

        assertThat(result).isTrue();
    }

    @Test
    public void hasNoAccess() {
        given(sharedData.getPublicData()).willReturn(false);
        given(sharedData.getSharedWith()).willReturn(UUID.randomUUID());

        boolean result = underTest.hasAccess(USER_ID, AccessMode.EDIT, EXTERNAL_ID, DataType.TEST);

        assertThat(result).isFalse();
    }
}