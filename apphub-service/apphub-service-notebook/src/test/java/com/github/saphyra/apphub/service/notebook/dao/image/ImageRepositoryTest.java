package com.github.saphyra.apphub.service.notebook.dao.image;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
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
public class ImageRepositoryTest {
    private static final String IMAGE_ID_1 = "image-id-1";
    private static final String IMAGE_ID_2 = "image-id-2";
    private static final String PARENT_1 = "parent-1";
    private static final String USER_ID_1 = "user-1";
    private static final String USER_ID_2 = "user-2";

    @Autowired
    private ImageRepository underTest;

    @After
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    public void findByParent() {
        ImageEntity entity = ImageEntity.builder()
            .imageId(IMAGE_ID_1)
            .parent(PARENT_1)
            .build();
        underTest.save(entity);

        Optional<ImageEntity> result = underTest.findByParent(PARENT_1);

        assertThat(result).contains(entity);
    }

    @Test
    @Transactional
    public void deleteByUserid() {
        ImageEntity entity1 = ImageEntity.builder()
            .imageId(IMAGE_ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);

        ImageEntity entity2 = ImageEntity.builder()
            .imageId(IMAGE_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByUserId(USER_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }
}