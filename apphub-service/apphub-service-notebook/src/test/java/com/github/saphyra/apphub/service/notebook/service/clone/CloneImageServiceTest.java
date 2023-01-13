package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.service.notebook.dao.image.Image;
import com.github.saphyra.apphub.service.notebook.dao.image.ImageDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.service.StorageProxy;
import com.github.saphyra.apphub.service.notebook.service.image.creation.ImageFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CloneImageServiceTest {
    private static final UUID ORIGINAL_PARENT = UUID.randomUUID();
    private static final UUID ORIGINAL_FILE_ID = UUID.randomUUID();
    private static final UUID CLONED_FILE_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID NEW_PARENT = UUID.randomUUID();

    @Mock
    private ImageDao imageDao;

    @Mock
    private StorageProxy storageProxy;

    @Mock
    private ImageFactory imageFactory;

    @InjectMocks
    private CloneImageService underTest;

    @Mock
    private ListItem toClone;

    @Mock
    private ListItem listItemClone;

    @Mock
    private Image image;

    @Mock
    private Image imageClone;

    @Test
    public void cloneImage() {
        given(toClone.getListItemId()).willReturn(ORIGINAL_PARENT);
        given(toClone.getUserId()).willReturn(USER_ID);

        given(listItemClone.getListItemId()).willReturn(NEW_PARENT);

        given(image.getFileId()).willReturn(ORIGINAL_FILE_ID);

        given(imageDao.findByParentValidated(ORIGINAL_PARENT)).willReturn(image);
        given(storageProxy.duplicateFile(ORIGINAL_FILE_ID)).willReturn(CLONED_FILE_ID);
        given(imageFactory.create(USER_ID, NEW_PARENT, CLONED_FILE_ID)).willReturn(imageClone);

        underTest.cloneImage(toClone, listItemClone);

        verify(imageDao).save(imageClone);
    }
}