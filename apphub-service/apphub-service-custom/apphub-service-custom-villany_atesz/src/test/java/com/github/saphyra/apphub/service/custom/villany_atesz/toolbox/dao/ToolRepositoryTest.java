package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao;

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
class ToolRepositoryTest {
    private static final String TOOL_ID_1 = "tool-id-1";
    private static final String TOOL_ID_2 = "tool-id-2";
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";

    @Autowired
    private ToolRepository underTest;

    @AfterEach
    void clear() {
        underTest.deleteAll();
    }

    @Transactional
    @Test
    void deleteByUserId() {
        ToolEntity entity1 = ToolEntity.builder()
            .toolId(TOOL_ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);

        ToolEntity entity2 = ToolEntity.builder()
            .toolId(TOOL_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByUserId(USER_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    void getByUserId() {
        ToolEntity entity1 = ToolEntity.builder()
            .toolId(TOOL_ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);

        ToolEntity entity2 = ToolEntity.builder()
            .toolId(TOOL_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.save(entity2);

        assertThat(underTest.getByUserId(USER_ID_1)).containsExactly(entity1);
    }

    @Transactional
    @Test
    void deleteByUserIdAndToolId() {
        ToolEntity entity1 = ToolEntity.builder()
            .toolId(TOOL_ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);

        ToolEntity entity2 = ToolEntity.builder()
            .toolId(TOOL_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByUserIdAndToolId(USER_ID_1, TOOL_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }
}