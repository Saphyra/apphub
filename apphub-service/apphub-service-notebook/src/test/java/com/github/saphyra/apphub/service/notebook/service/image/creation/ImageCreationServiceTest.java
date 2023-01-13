package com.github.saphyra.apphub.service.notebook.service.image.creation;

import com.github.saphyra.apphub.api.notebook.model.request.CreateImageRequest;
import com.github.saphyra.apphub.service.notebook.dao.image.Image;
import com.github.saphyra.apphub.service.notebook.dao.image.ImageDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemType;
import com.github.saphyra.apphub.service.notebook.service.ListItemFactory;
import com.github.saphyra.apphub.service.notebook.service.ListItemRequestValidator;
import com.github.saphyra.apphub.service.notebook.service.StorageProxy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ImageCreationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String TITLE = "title";
    private static final UUID PARENT = UUID.randomUUID();
    private static final String EXTENSION = "extension";
    private static final String FILE_NAME = String.format("file-name.%s", EXTENSION);
    private static final Long SIZE = 2345L;
    private static final UUID FILE_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private ListItemRequestValidator listItemRequestValidator;

    @Mock
    private ListItemFactory listItemFactory;

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private ImageFactory imageFactory;

    @Mock
    private StorageProxy storageProxy;

    @Mock
    private ImageDao imageDao;

    @InjectMocks
    private ImageCreationService underTest;

    @Mock
    private CreateImageRequest request;

    @Mock
    private ListItem listItem;

    @Mock
    private Image image;

    @Test
    public void createImage() {
        given(request.getTitle()).willReturn(TITLE);
        given(request.getParent()).willReturn(PARENT);
        given(request.getFileName()).willReturn(FILE_NAME);
        given(request.getSize()).willReturn(SIZE);

        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);

        given(storageProxy.createFile(FILE_NAME, EXTENSION, SIZE)).willReturn(FILE_ID);
        given(listItemFactory.create(USER_ID, TITLE, PARENT, ListItemType.IMAGE)).willReturn(listItem);
        given(imageFactory.create(USER_ID, LIST_ITEM_ID, FILE_ID)).willReturn(image);

        UUID result = underTest.createImage(USER_ID, request);

        verify(listItemRequestValidator).validate(TITLE, PARENT);
        verify(listItemDao).save(listItem);
        verify(imageDao).save(image);

        assertThat(result).isEqualTo(FILE_ID);
    }
}