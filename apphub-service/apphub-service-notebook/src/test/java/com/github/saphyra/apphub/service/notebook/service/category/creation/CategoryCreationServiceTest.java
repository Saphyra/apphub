package com.github.saphyra.apphub.service.notebook.service.category.creation;

import com.github.saphyra.apphub.api.notebook.model.request.CreateCategoryRequest;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
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
public class CategoryCreationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String TITLE = "title";
    private static final UUID PARENT_ID = UUID.randomUUID();
    private static final UUID CATEGORY_ID = UUID.randomUUID();

    @Mock
    private CategoryFactory categoryFactory;

    @Mock
    private CreateCategoryRequestValidator createCategoryRequestValidator;

    @Mock
    private ListItemDao listItemDao;

    @InjectMocks
    private CategoryCreationService underTest;

    @Mock
    private ListItem listItem;

    @Test
    public void createCategory() {
        given(categoryFactory.create(USER_ID, TITLE, PARENT_ID)).willReturn(listItem);
        given(listItem.getListItemId()).willReturn(CATEGORY_ID);

        CreateCategoryRequest request = CreateCategoryRequest.builder().title(TITLE).parent(PARENT_ID).build();

        UUID result = underTest.createCategory(USER_ID, request);

        verify(createCategoryRequestValidator).validate(request);
        verify(listItemDao).save(listItem);
        assertThat(result).isEqualTo(CATEGORY_ID);
    }
}