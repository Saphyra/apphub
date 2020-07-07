package com.github.saphyra.apphub.api.notebook.server;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;

public interface ListItemController {
    @RequestMapping(method = RequestMethod.DELETE, path = Endpoints.DELETE_NOTEBOOK_LIST_ITEM)
    void deleteListItem(@PathVariable("listItemId") UUID categoryId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
