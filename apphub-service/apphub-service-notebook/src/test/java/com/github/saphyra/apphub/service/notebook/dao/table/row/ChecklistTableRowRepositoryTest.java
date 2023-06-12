package com.github.saphyra.apphub.service.notebook.dao.table.row;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
@Slf4j
public class ChecklistTableRowRepositoryTest {
    private static final String ROW_ID_1 = "row-id-1";
    private static final String ROW_ID_2 = "row-id-2";
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";
    private static final String PARENT_1 = "parent-1";
    private static final String PARENT_2 = "parent-2";
    private static final Integer ROW_INDEX = 435;

    @Autowired
    private ChecklistTableRowRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    public void deleteByUserId() {
        ChecklistTableRowEntity entity1 = ChecklistTableRowEntity.builder()
            .rowId(ROW_ID_1)
            .userId(USER_ID_1)
            .build();

        ChecklistTableRowEntity entity2 = ChecklistTableRowEntity.builder()
            .rowId(ROW_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        underTest.deleteByUserId(USER_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    public void findByParentAndRowIndex() {
        ChecklistTableRowEntity entity = ChecklistTableRowEntity.builder()
            .rowId(ROW_ID_1)
            .parent(PARENT_1)
            .rowIndex(ROW_INDEX)
            .build();
        underTest.save(entity);

        Optional<ChecklistTableRowEntity> result = underTest.findByParentAndRowIndex(PARENT_1, ROW_INDEX);

        assertThat(result).contains(entity);
    }

    @Test
    public void getByParent() {
        ChecklistTableRowEntity entity1 = ChecklistTableRowEntity.builder()
            .rowId(ROW_ID_1)
            .parent(PARENT_1)
            .build();

        ChecklistTableRowEntity entity2 = ChecklistTableRowEntity.builder()
            .rowId(ROW_ID_2)
            .parent(PARENT_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        List<ChecklistTableRowEntity> result = underTest.getByParent(PARENT_1);

        assertThat(result).containsExactly(entity1);
    }

    @Test
    @Transactional
    public void deleteByParent() {
        ChecklistTableRowEntity entity1 = ChecklistTableRowEntity.builder()
            .rowId(ROW_ID_1)
            .parent(PARENT_1)
            .build();

        ChecklistTableRowEntity entity2 = ChecklistTableRowEntity.builder()
            .rowId(ROW_ID_2)
            .parent(PARENT_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        underTest.deleteByParent(PARENT_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }
}