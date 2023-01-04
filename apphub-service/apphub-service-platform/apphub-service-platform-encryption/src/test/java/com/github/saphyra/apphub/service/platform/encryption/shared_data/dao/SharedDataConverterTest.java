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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class SharedDataConverterTest {
    private static final UUID SHARED_DATA_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_ID = UUID.randomUUID();
    private static final UUID SHARED_WITH = UUID.randomUUID();
    private static final String SHARED_DATA_ID_STRING = "shared-data-id";
    private static final String EXTERNAL_ID_STRING = "external-data";
    private static final String SHARED_WITH_STRING = "shared-with";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private SharedDataConverter underTest;

    @Test
    public void convertDomain() {
        SharedData sharedData = SharedData.builder()
            .sharedDataId(SHARED_DATA_ID)
            .externalId(EXTERNAL_ID)
            .dataType(DataType.TEST)
            .sharedWith(SHARED_WITH)
            .publicData(true)
            .accessMode(AccessMode.EDIT)
            .build();

        given(uuidConverter.convertDomain(SHARED_DATA_ID)).willReturn(SHARED_DATA_ID_STRING);
        given(uuidConverter.convertDomain(EXTERNAL_ID)).willReturn(EXTERNAL_ID_STRING);
        given(uuidConverter.convertDomain(SHARED_WITH)).willReturn(SHARED_WITH_STRING);

        SharedDataEntity result = underTest.convertDomain(sharedData);

        assertThat(result.getSharedDataId()).isEqualTo(SHARED_DATA_ID_STRING);
        assertThat(result.getExternalId()).isEqualTo(EXTERNAL_ID_STRING);
        assertThat(result.getDataType()).isEqualTo(DataType.TEST.name());
        assertThat(result.getSharedWith()).isEqualTo(SHARED_WITH_STRING);
        assertThat(result.isPublicData()).isTrue();
        assertThat(result.getAccessMode()).isEqualTo(AccessMode.EDIT.name());
    }

    @Test
    public void convertEntity() {
        SharedDataEntity entity = SharedDataEntity.builder()
            .sharedDataId(SHARED_DATA_ID_STRING)
            .externalId(EXTERNAL_ID_STRING)
            .dataType(DataType.TEST.name())
            .sharedWith(SHARED_WITH_STRING)
            .publicData(true)
            .accessMode(AccessMode.EDIT.name())
            .build();

        given(uuidConverter.convertEntity(SHARED_DATA_ID_STRING)).willReturn(SHARED_DATA_ID);
        given(uuidConverter.convertEntity(EXTERNAL_ID_STRING)).willReturn(EXTERNAL_ID);
        given(uuidConverter.convertEntity(SHARED_WITH_STRING)).willReturn(SHARED_WITH);

        SharedData result = underTest.convertEntity(entity);

        assertThat(result.getSharedDataId()).isEqualTo(SHARED_DATA_ID);
        assertThat(result.getExternalId()).isEqualTo(EXTERNAL_ID);
        assertThat(result.getDataType()).isEqualTo(DataType.TEST);
        assertThat(result.getSharedWith()).isEqualTo(SHARED_WITH);
        assertThat(result.getPublicData()).isTrue();
        assertThat(result.getAccessMode()).isEqualTo(AccessMode.EDIT);
    }
}