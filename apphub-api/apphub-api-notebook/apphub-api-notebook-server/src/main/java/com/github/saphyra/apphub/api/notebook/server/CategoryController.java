package com.github.saphyra.apphub.api.notebook.server;

import com.github.saphyra.apphub.api.notebook.model.request.CreateCategoryRequest;
import com.github.saphyra.apphub.api.notebook.model.response.CategoryTreeView;
import com.github.saphyra.apphub.api.notebook.model.response.ChildrenOfCategoryResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

public interface CategoryController {
    @RequestMapping(method = RequestMethod.PUT, path = Endpoints.CREATE_NOTEBOOK_CATEGORY)
    OneParamResponse<UUID> createCategory(@RequestBody CreateCategoryRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @RequestMapping(method = RequestMethod.GET, path = Endpoints.GET_NOTEBOOK_CATEGORY_TREE)
    List<CategoryTreeView> getCategoryTree(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @RequestMapping(method = RequestMethod.GET, path = Endpoints.GET_CHILDREN_OF_NOTEBOOK_CATEGORY)
    ChildrenOfCategoryResponse getChildrenOfCategory(
        @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader,
        @RequestParam(name = "categoryId", required = false) UUID categoryId,
        @RequestParam(name = "type", required = false) String type,
        @RequestParam(name = "exclude", required = false) UUID exclude
    );
}
