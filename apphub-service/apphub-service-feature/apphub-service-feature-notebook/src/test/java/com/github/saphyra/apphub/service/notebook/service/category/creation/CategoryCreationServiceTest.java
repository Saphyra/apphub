package com.github.saphyra.apphub.service.notebook.service.category.creation;

import com.github.saphyra.apphub.api.feature.notebook.model.request.CreateCategoryRequest;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItem;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItemDao;
import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.service.notebook.service.DeprecatedListItemFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CategoryCreationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String TITLE = "title";
    private static final UUID PARENT_ID = UUID.randomUUID();
    private static final UUID CATEGORY_ID = UUID.randomUUID();

    @Mock
    private DeprecatedListItemFactory listItemFactory;

    @Mock
    private CreateCategoryRequestValidator createCategoryRequestValidator;

    @Mock
    private DeprecatedListItemDao listItemDao;

    @InjectMocks
    private CategoryCreationService underTest;

    @Mock
    private DeprecatedListItem listItem;

    @Test
    public void createCategory() {
        given(listItemFactory.create(USER_ID, TITLE, PARENT_ID, ListItemType.CATEGORY)).willReturn(listItem);
        given(listItem.getListItemId()).willReturn(CATEGORY_ID);

        CreateCategoryRequest request = CreateCategoryRequest.builder().title(TITLE).parent(PARENT_ID).build();

        UUID result = underTest.createCategory(USER_ID, request);

        verify(createCategoryRequestValidator).validate(request);
        verify(listItemDao).save(listItem);
        assertThat(result).isEqualTo(CATEGORY_ID);
    }
}