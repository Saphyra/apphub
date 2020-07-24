package com.github.saphyra.apphub.service.notebook.dao.content;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
@Slf4j
public class ContentRepositoryTest {
    private static final String TEXT_ID_1 = "text-id-1";
    private static final String TEXT_ID_2 = "text-id-2";
    private static final String PARENT_1 = "parent-1";
    private static final String PARENT_2 = "parent-2";

    @Autowired
    private ContentRepository underTest;

    @After
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    public void deleteByParent() {
        ContentEntity entity1 = ContentEntity.builder()
            .contentId(TEXT_ID_1)
            .parent(PARENT_1)
            .build();
        underTest.save(entity1);
        ContentEntity entity2 = ContentEntity.builder()
            .contentId(TEXT_ID_2)
            .parent(PARENT_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByParent(PARENT_2);

        assertThat(underTest.findAll()).containsExactly(entity1);
    }

    @Test
    public void findByParent() {
        ContentEntity entity1 = ContentEntity.builder()
            .contentId(TEXT_ID_1)
            .parent(PARENT_1)
            .build();
        underTest.save(entity1);
        ContentEntity entity2 = ContentEntity.builder()
            .contentId(TEXT_ID_2)
            .parent(PARENT_2)
            .build();
        underTest.save(entity2);

        Optional<ContentEntity> result = underTest.findByParent(PARENT_2);

        assertThat(result).contains(entity2);
    }
}