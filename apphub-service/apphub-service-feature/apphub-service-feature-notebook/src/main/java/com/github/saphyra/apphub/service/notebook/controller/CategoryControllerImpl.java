package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.feature.notebook.model.request.CreateCategoryRequest;
import com.github.saphyra.apphub.api.feature.notebook.model.response.CategoryTreeView;
import com.github.saphyra.apphub.api.feature.notebook.model.response.ChildrenOfCategoryResponse;
import com.github.saphyra.apphub.api.feature.notebook.server.CategoryController;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
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
    public OneParamResponse<UUID> createCategory(CreateCategoryRequest request, AccessToken accessToken) {
        log.info("{} wants to create a new category with parentId {}", accessToken.getUserId(), request.getParent());
        UUID categoryId = categoryCreationService.createCategory(accessToken.getUserId(), request);
        return new OneParamResponse<>(categoryId);
    }

    @Override
    public List<CategoryTreeView> getCategoryTree(AccessToken accessToken) {
        log.info("Querying category list for userId {}", accessToken.getUserId());
        return categoryTreeQueryService.getCategoryTree(accessToken.getUserId());
    }

    @Override
    public ChildrenOfCategoryResponse getChildrenOfCategory(AccessToken accessToken, UUID categoryId, String type, UUID exclude) {
        log.info("Querying children of category {} with type {} and exclusion {} for user {}", categoryId, type, exclude, accessToken.getUserId());
        return categoryChildrenQueryService.getChildrenOfCategory(accessToken.getUserId(), categoryId, type, exclude);
    }
}
