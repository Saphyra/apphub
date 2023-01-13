package com.github.saphyra.apphub.service.notebook.service.image.deletion;

import com.github.saphyra.apphub.service.notebook.dao.image.Image;
import com.github.saphyra.apphub.service.notebook.dao.image.ImageDao;
import com.github.saphyra.apphub.service.notebook.service.StorageProxy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ImageDeletionServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID FILE_ID = UUID.randomUUID();

    @Mock
    private ImageDao imageDao;

    @Mock
    private StorageProxy storageProxy;

    @InjectMocks
    private ImageDeletionService underTest;

    @Mock
    private Image image;

    @Test
    public void deleteImage() {
        given(imageDao.findByParentValidated(LIST_ITEM_ID)).willReturn(image);

        given(image.getFileId()).willReturn(FILE_ID);

        underTest.deleteImage(LIST_ITEM_ID);

        verify(storageProxy).deleteFile(FILE_ID);
        verify(imageDao).delete(image);
    }
}