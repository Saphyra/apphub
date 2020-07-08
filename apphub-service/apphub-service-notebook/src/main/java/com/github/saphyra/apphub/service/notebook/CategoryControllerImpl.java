package com.github.saphyra.apphub.service.notebook;

import com.github.saphyra.apphub.api.notebook.model.request.CreateCategoryRequest;
import com.github.saphyra.apphub.api.notebook.model.response.CategoryTreeView;
import com.github.saphyra.apphub.api.notebook.model.response.ChildrenOfCategoryResponse;
import com.github.saphyra.apphub.api.notebook.server.CategoryController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.notebook.service.category.CategoryChildrenQueryService;
import com.github.saphyra.apphub.service.notebook.service.category.CategoryViewQueryService;
import com.github.saphyra.apphub.service.notebook.service.category.creation.CategoryCreationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CategoryControllerImpl implements CategoryController {
    private final CategoryChildrenQueryService categoryChildrenQueryService;
    private final CategoryCreationService categoryCreationService;
    private final CategoryViewQueryService categoryViewQueryService;

    @Override
    //TODO int test
    //TODO API test
    public void createCategory(CreateCategoryRequest request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to create a new category with parentId {}", accessTokenHeader.getUserId(), request.getParent());
        categoryCreationService.createCategory(accessTokenHeader.getUserId(), request);
    }

    @Override
    //TODO int test
    //TODO API test
    public List<CategoryTreeView> getCategoryTree(AccessTokenHeader accessTokenHeader) {
        log.info("Querying category list for userId {}", accessTokenHeader.getUserId());
        return categoryViewQueryService.getCategoryTree(accessTokenHeader.getUserId());
    }

    @Override
    //TODO int test
    //TODO API test
    public ChildrenOfCategoryResponse getChildrenOfCategory(AccessTokenHeader accessTokenHeader, UUID categoryId, String type) {
        log.info("Querying children of category {} with type {} for user {}", categoryId, type, accessTokenHeader.getUserId());
        return categoryChildrenQueryService.getChildrenOfCategory(accessTokenHeader.getUserId(), categoryId, type);
    }
}
