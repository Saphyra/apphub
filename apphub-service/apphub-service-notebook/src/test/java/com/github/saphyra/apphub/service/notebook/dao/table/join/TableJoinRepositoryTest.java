package com.github.saphyra.apphub.service.notebook.dao.table.join;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
@Slf4j
public class TableJoinRepositoryTest {
    private static final String TABLE_JOIN_ID_1 = "table-join-id-1";
    private static final String TABLE_JOIN_ID_2 = "table-join-id-2";
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";
    private static final String PARENT_1 = "parent-1";
    private static final String PARENT_2 = "parent-2";

    @Autowired
    private TableJoinRepository underTest;

    @After
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    public void deleteByUserId() {
        TableJoinEntity entity1 = TableJoinEntity.builder()
            .tableJoinId(TABLE_JOIN_ID_1)
            .userId(USER_ID_1)
            .build();
        TableJoinEntity entity2 = TableJoinEntity.builder()
            .tableJoinId(TABLE_JOIN_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        underTest.deleteByUserId(USER_ID_2);

        assertThat(underTest.findAll()).containsExactly(entity1);
    }

    @Test
    public void getByParent() {
        TableJoinEntity entity1 = TableJoinEntity.builder()
            .tableJoinId(TABLE_JOIN_ID_1)
            .parent(PARENT_1)
            .build();
        TableJoinEntity entity2 = TableJoinEntity.builder()
            .tableJoinId(TABLE_JOIN_ID_2)
            .parent(PARENT_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        List<TableJoinEntity> result = underTest.getByParent(PARENT_1);

        assertThat(result).containsExactly(entity1);
    }

}