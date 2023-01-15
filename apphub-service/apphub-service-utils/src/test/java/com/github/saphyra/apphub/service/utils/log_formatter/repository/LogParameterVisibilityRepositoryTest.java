package com.github.saphyra.apphub.service.utils.log_formatter.repository;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import org.junit.After;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
public class LogParameterVisibilityRepositoryTest {
    private static final String ID_1 = "id-1";
    private static final String ID_2 = "id-2";
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";

    @Autowired
    private LogParameterVisibilityRepository underTest;

    @After
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    public void getByUserId() {
        LogParameterVisibilityEntity entity1 = LogParameterVisibilityEntity.builder()
            .id(ID_1)
            .userId(USER_ID_1)
            .build();
        LogParameterVisibilityEntity entity2 = LogParameterVisibilityEntity.builder()
            .id(ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        List<LogParameterVisibilityEntity> result = underTest.getByUserId(USER_ID_1);

        assertThat(result).containsExactly(entity1);
    }
}
