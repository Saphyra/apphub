package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.request.CreateCategoryRequest;
import com.github.saphyra.apphub.api.notebook.model.response.CategoryTreeView;
import com.github.saphyra.apphub.api.notebook.model.response.ChildrenOfCategoryResponse;
import com.github.saphyra.apphub.api.notebook.server.CategoryController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.notebook.service.category.CategoryChildrenQueryService;
import com.github.saphyra.apphub.service.notebook.service.category.CategoryTreeQueryService;
import com.github.saphyra.apphub.service.notebook.service.category.creation.CategoryCreationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
class CategoryControllerImpl implements CategoryController {
    private final CategoryChildrenQueryService categoryChildrenQueryService;
    private final CategoryCreationService categoryCreationService;
    private final CategoryTreeQueryService categoryTreeQueryService;

    @Override
    public OneParamResponse<UUID> createCategory(CreateCategoryRequest request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to create a new category with parentId {}", accessTokenHeader.getUserId(), request.getParent());
        UUID categoryId = categoryCreationService.createCategory(accessTokenHeader.getUserId(), request);
        return new OneParamResponse<>(categoryId);
    }

    @Override
    public List<CategoryTreeView> getCategoryTree(AccessTokenHeader accessTokenHeader) {
        log.info("Querying category list for userId {}", accessTokenHeader.getUserId());
        return categoryTreeQueryService.getCategoryTree(accessTokenHeader.getUserId());
    }

    @Override
    public ChildrenOfCategoryResponse getChildrenOfCategory(AccessTokenHeader accessTokenHeader, UUID categoryId, String type, UUID exclude) {
        log.info("Querying children of category {} with type {} and exclusion {} for user {}", categoryId, type, exclude, accessTokenHeader.getUserId());
        return categoryChildrenQueryService.getChildrenOfCategory(accessTokenHeader.getUserId(), categoryId, type, exclude);
    }
}
