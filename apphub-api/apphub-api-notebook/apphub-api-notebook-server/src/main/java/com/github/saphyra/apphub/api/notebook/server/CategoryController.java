package com.github.saphyra.apphub.api.notebook.server;

import com.github.saphyra.apphub.api.notebook.model.request.CreateCategoryRequest;
import com.github.saphyra.apphub.api.notebook.model.response.CategoryTreeView;
import com.github.saphyra.apphub.api.notebook.model.response.ChildrenOfCategoryResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.lib.config.common.endpoints.NotebookEndpoints;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

public interface CategoryController {
    @PutMapping(NotebookEndpoints.NOTEBOOK_CREATE_CATEGORY)
    OneParamResponse<UUID> createCategory(@RequestBody CreateCategoryRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @RequestMapping(method = RequestMethod.GET, path = NotebookEndpoints.NOTEBOOK_GET_CATEGORY_TREE)
    List<CategoryTreeView> getCategoryTree(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    /**
     * Returning all the direct children of the given category
     *
     * @param categoryId null returns content of root
     * @param type       Filtering for listItemType
     * @param exclude    Excluding listItems with the given listItemId. (Used by client, when editing listItems so the modified category cannot be moved to itself)
     */
    @RequestMapping(method = RequestMethod.GET, path = NotebookEndpoints.NOTEBOOK_GET_CHILDREN_OF_CATEGORY)
    ChildrenOfCategoryResponse getChildrenOfCategory(
        @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader,
        @RequestParam(name = "categoryId", required = false) UUID categoryId,
        @RequestParam(name = "type", required = false) String type,
        @RequestParam(name = "exclude", required = false) UUID exclude
    );
}
