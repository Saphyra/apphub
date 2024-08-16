package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool_type;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
class ToolTypeRepositoryTest {
    private static final String TOOL_TYPE_ID_1 = "tool-type-id-1";
    private static final String TOOL_TYPE_ID_2 = "tool-type-id-2";
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";

    @Autowired
    private ToolTypeRepository underTest;

    @AfterEach
    void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    void deleteByUserId() {
        ToolTypeEntity entity1 = ToolTypeEntity.builder()
            .toolTypeId(TOOL_TYPE_ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);

        ToolTypeEntity entity2 = ToolTypeEntity.builder()
            .toolTypeId(TOOL_TYPE_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByUserId(USER_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    void getByUserId() {
        ToolTypeEntity entity1 = ToolTypeEntity.builder()
            .toolTypeId(TOOL_TYPE_ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);

        ToolTypeEntity entity2 = ToolTypeEntity.builder()
            .toolTypeId(TOOL_TYPE_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.save(entity2);

        assertThat(underTest.getByUserId(USER_ID_1)).containsExactly(entity1);
    }
}