package com.github.saphyra.apphub.api.notebook.server;

import com.github.saphyra.apphub.api.notebook.model.request.CreateCategoryRequest;
import com.github.saphyra.apphub.api.notebook.model.response.CategoryListView;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient("notebook")
public interface CategoryController {
    @RequestMapping(method = RequestMethod.PUT, path = Endpoints.CREATE_NOTEBOOK_CATEGORY)
    void createCategory(@RequestBody CreateCategoryRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @RequestMapping(method = RequestMethod.GET, path = Endpoints.GET_NOTEBOOK_CATEGORY_VIEWS)
    List<CategoryListView> getCategoryViews(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
