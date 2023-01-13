package com.github.saphyra.apphub.service.notebook.dao.image;

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
public class ImageConverterTest {
    private static final UUID IMAGE_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PARENT = UUID.randomUUID();
    private static final UUID FILE_ID = UUID.randomUUID();
    private static final String IMAGE_ID_STRING = "image-id";
    private static final String USER_ID_STRING = "user-id";
    private static final String PARENT_STRING = "parent";
    private static final String FILE_ID_STRING = "file-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private ImageConverter underTest;

    @Test
    public void convertDomain() {
        Image image = Image.builder()
            .imageId(IMAGE_ID)
            .userId(USER_ID)
            .parent(PARENT)
            .fileId(FILE_ID)
            .build();

        given(uuidConverter.convertDomain(IMAGE_ID)).willReturn(IMAGE_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(PARENT)).willReturn(PARENT_STRING);
        given(uuidConverter.convertDomain(FILE_ID)).willReturn(FILE_ID_STRING);

        ImageEntity result = underTest.convertDomain(image);

        assertThat(result.getImageId()).isEqualTo(IMAGE_ID_STRING);
        assertThat(result.getUserId()).isEqualTo(USER_ID_STRING);
        assertThat(result.getParent()).isEqualTo(PARENT_STRING);
        assertThat(result.getFileId()).isEqualTo(FILE_ID_STRING);
    }

    @Test
    public void convertEntity() {
        ImageEntity entity = ImageEntity.builder()
            .imageId(IMAGE_ID_STRING)
            .userId(USER_ID_STRING)
            .parent(PARENT_STRING)
            .fileId(FILE_ID_STRING)
            .build();

        given(uuidConverter.convertEntity(IMAGE_ID_STRING)).willReturn(IMAGE_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(uuidConverter.convertEntity(PARENT_STRING)).willReturn(PARENT);
        given(uuidConverter.convertEntity(FILE_ID_STRING)).willReturn(FILE_ID);

        Image result = underTest.convertEntity(entity);

        assertThat(result.getImageId()).isEqualTo(IMAGE_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getParent()).isEqualTo(PARENT);
        assertThat(result.getFileId()).isEqualTo(FILE_ID);
    }
}