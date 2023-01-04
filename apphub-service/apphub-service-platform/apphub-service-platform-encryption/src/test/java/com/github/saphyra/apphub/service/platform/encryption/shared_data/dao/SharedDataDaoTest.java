package com.github.saphyra.apphub.service.platform.encryption.shared_data.dao;

import com.github.saphyra.apphub.api.platform.encryption.model.AccessMode;
import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.api.platform.encryption.model.SharedData;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
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
public class SharedDataDaoTest {
    private static final String EXTERNAL_ID_STRING = "external-id";
    private static final UUID EXTERNAL_ID = UUID.randomUUID();
    private static final UUID SHARED_DATA_ID = UUID.randomUUID();
    private static final String SHARED_DATA_ID_STRING = "shared-data-id";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private SharedDataConverter converter;

    @Mock
    private SharedDataRepository repository;

    @InjectMocks
    private SharedDataDao underTest;

    @Mock
    private SharedData sharedData;

    @Mock
    private SharedDataEntity entity;

    @Test
    public void getByExternalIdAndDataTypeAndAccessMode() {
        given(repository.getByExternalIdAndDataTypeAndAccessMode(EXTERNAL_ID_STRING, DataType.TEST.name(), AccessMode.EDIT.name())).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(sharedData));
        given(uuidConverter.convertDomain(EXTERNAL_ID)).willReturn(EXTERNAL_ID_STRING);

        List<SharedData> result = underTest.getByExternalIdAndDataTypeAndAccessMode(EXTERNAL_ID, DataType.TEST, AccessMode.EDIT);

        assertThat(result).containsExactly(sharedData);
    }

    @Test
    public void getByExternalIdAndDataType() {
        given(repository.getByExternalIdAndDataType(EXTERNAL_ID_STRING, DataType.TEST.name())).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(sharedData));
        given(uuidConverter.convertDomain(EXTERNAL_ID)).willReturn(EXTERNAL_ID_STRING);

        List<SharedData> result = underTest.getByExternalIdAndDataType(EXTERNAL_ID, DataType.TEST);

        assertThat(result).containsExactly(sharedData);
    }

    @Test
    public void deleteById() {
        given(uuidConverter.convertDomain(SHARED_DATA_ID)).willReturn(SHARED_DATA_ID_STRING);
        given(repository.existsById(SHARED_DATA_ID_STRING)).willReturn(true);

        underTest.deleteById(SHARED_DATA_ID);

        verify(repository).deleteById(SHARED_DATA_ID_STRING);
    }

    @Test
    public void deleteByExternalIdAndDataType() {
        given(uuidConverter.convertDomain(EXTERNAL_ID)).willReturn(EXTERNAL_ID_STRING);

        underTest.deleteByExternalIdAndDataType(EXTERNAL_ID, DataType.TEST);

        verify(repository).deleteByExternalIdAndDataType(EXTERNAL_ID_STRING, DataType.TEST.name());
    }

    @Test
    public void deleteByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        underTest.deleteByUserId(USER_ID);

        verify(repository).deleteBySharedWith(USER_ID_STRING);
    }
}