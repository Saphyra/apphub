package com.github.saphyra.apphub.service.platform.encryption.shared_data.dao;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import org.junit.After;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
public class SharedDataRepositoryTest {
    private static final String SHARED_DATA_ID_1 = "shared-data-id-1";
    private static final String SHARED_DATA_ID_2 = "shared-data-id-2";
    private static final String EXTERNAL_ID_1 = "external-id-1";
    private static final String EXTERNAL_ID_2 = "external-id-2";
    private static final String DATA_TYPE_1 = "data-type-1";
    private static final String DATA_TYPE_2 = "data-type-2";
    private static final String ACCESS_MODE_1 = "access-mode-1";
    private static final String ACCESS_MODE_2 = "access-mode-2";
    private static final String SHARED_WITH_1 = "shared-with-1";
    private static final String SHARED_WITH_2 = "shared-with-2";

    @Autowired
    private SharedDataRepository underTest;

    @After
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    public void getByExternalIdAndDataTypeAndAccessMode() {
        SharedDataEntity sharedData1 = SharedDataEntity.builder()
            .sharedDataId(SHARED_DATA_ID_1)
            .externalId(EXTERNAL_ID_1)
            .dataType(DATA_TYPE_1)
            .accessMode(ACCESS_MODE_1)
            .build();
        underTest.save(sharedData1);
        SharedDataEntity sharedData2 = SharedDataEntity.builder()
            .sharedDataId(SHARED_DATA_ID_2)
            .externalId(EXTERNAL_ID_2)
            .dataType(DATA_TYPE_2)
            .accessMode(ACCESS_MODE_2)
            .build();
        underTest.save(sharedData2);

        List<SharedDataEntity> result = underTest.getByExternalIdAndDataTypeAndAccessMode(EXTERNAL_ID_1, DATA_TYPE_1, ACCESS_MODE_1);

        assertThat(result).containsExactly(sharedData1);
    }

    @Test
    public void getByExternalIdAndDataType() {
        SharedDataEntity sharedData1 = SharedDataEntity.builder()
            .sharedDataId(SHARED_DATA_ID_1)
            .externalId(EXTERNAL_ID_1)
            .dataType(DATA_TYPE_1)
            .build();
        underTest.save(sharedData1);
        SharedDataEntity sharedData2 = SharedDataEntity.builder()
            .sharedDataId(SHARED_DATA_ID_2)
            .externalId(EXTERNAL_ID_2)
            .dataType(DATA_TYPE_2)
            .build();
        underTest.save(sharedData2);

        List<SharedDataEntity> result = underTest.getByExternalIdAndDataType(EXTERNAL_ID_1, DATA_TYPE_1);

        assertThat(result).containsExactly(sharedData1);
    }

    @Test
    @Transactional
    public void deleteByExternalIdAndDataType() {
        SharedDataEntity sharedData1 = SharedDataEntity.builder()
            .sharedDataId(SHARED_DATA_ID_1)
            .externalId(EXTERNAL_ID_1)
            .dataType(DATA_TYPE_1)
            .build();
        underTest.save(sharedData1);
        SharedDataEntity sharedData2 = SharedDataEntity.builder()
            .sharedDataId(SHARED_DATA_ID_2)
            .externalId(EXTERNAL_ID_2)
            .dataType(DATA_TYPE_2)
            .build();
        underTest.save(sharedData2);

        underTest.deleteByExternalIdAndDataType(EXTERNAL_ID_1, DATA_TYPE_1);

        assertThat(underTest.findAll()).containsExactly(sharedData2);
    }

    @Test
    @Transactional
    public void deleteBySharedWith() {
        SharedDataEntity sharedData1 = SharedDataEntity.builder()
            .sharedDataId(SHARED_DATA_ID_1)
            .sharedWith(SHARED_WITH_1)
            .build();
        underTest.save(sharedData1);
        SharedDataEntity sharedData2 = SharedDataEntity.builder()
            .sharedDataId(SHARED_DATA_ID_2)
            .sharedWith(SHARED_WITH_2)
            .build();
        underTest.save(sharedData2);

        underTest.deleteBySharedWith(SHARED_WITH_1);

        assertThat(underTest.findAll()).containsExactly(sharedData2);
    }
}