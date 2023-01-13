package com.github.saphyra.apphub.service.notebook.service.image.creation;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.notebook.dao.image.Image;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ImageFactoryTest {
    private static final UUID IMAGE_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PARENT = UUID.randomUUID();
    private static final UUID FILE_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private ImageFactory underTest;

    @Test
    public void create() {
        given(idGenerator.randomUuid()).willReturn(IMAGE_ID);

        Image result = underTest.create(USER_ID, PARENT, FILE_ID);

        assertThat(result.getImageId()).isEqualTo(IMAGE_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getParent()).isEqualTo(PARENT);
        assertThat(result.getFileId()).isEqualTo(FILE_ID);
    }
}