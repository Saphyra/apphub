package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.request.CreateCategoryRequest;
import com.github.saphyra.apphub.api.notebook.model.response.CategoryTreeView;
import com.github.saphyra.apphub.api.notebook.model.response.ChildrenOfCategoryResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.notebook.controller.CategoryControllerImpl;
import com.github.saphyra.apphub.service.notebook.service.category.CategoryChildrenQueryService;
import com.github.saphyra.apphub.service.notebook.service.category.CategoryTreeQueryService;
import com.github.saphyra.apphub.service.notebook.service.category.creation.CategoryCreationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class CategoryControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID CATEGORY_ID = UUID.randomUUID();
    private static final String TYPE = "type";

    @Mock
    private CategoryChildrenQueryService categoryChildrenQueryService;

    @Mock
    private CategoryCreationService categoryCreationService;

    @Mock
    private CategoryTreeQueryService categoryTreeQueryService;

    @InjectMocks
    private CategoryControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private CreateCategoryRequest createCategoryRequest;

    @Mock
    private CategoryTreeView categoryTreeView;

    @Mock
    private ChildrenOfCategoryResponse childrenOfCategoryResponse;

    @Before
    public void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    public void createCategory() {
        given(categoryCreationService.createCategory(USER_ID, createCategoryRequest)).willReturn(CATEGORY_ID);

        OneParamResponse<UUID> response = underTest.createCategory(createCategoryRequest, accessTokenHeader);

        assertThat(response.getValue()).isEqualTo(CATEGORY_ID);
    }

    @Test
    public void getCategoryTree() {
        given(categoryTreeQueryService.getCategoryTree(USER_ID)).willReturn(Arrays.asList(categoryTreeView));

        List<CategoryTreeView> result = underTest.getCategoryTree(accessTokenHeader);

        assertThat(result).containsExactly(categoryTreeView);
    }

    @Test
    public void getChildrenOfCategory() {
        given(categoryChildrenQueryService.getChildrenOfCategory(USER_ID, CATEGORY_ID, TYPE)).willReturn(childrenOfCategoryResponse);

        ChildrenOfCategoryResponse result = underTest.getChildrenOfCategory(accessTokenHeader, CATEGORY_ID, TYPE);

        assertThat(result).isEqualTo(childrenOfCategoryResponse);
    }
}