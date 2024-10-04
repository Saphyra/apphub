package com.github.saphyra.apphub.api.custom.villany_atesz.server;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockItemResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.lib.config.common.endpoints.VillanyAteszEndpoints;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

public interface IndexController {
    @GetMapping(VillanyAteszEndpoints.VILLANY_ATESZ_INDEX_TOTAL_STOCK_VALUE)
    OneParamResponse<Integer> getTotalStockValue(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(VillanyAteszEndpoints.VILLANY_ATESZ_INDEX_TOTAL_TOOLBOX_VALUE)
    OneParamResponse<Integer> getTotalToolboxValue(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(VillanyAteszEndpoints.VILLANY_ATESZ_INDEX_GET_STOCK_ITEMS_MARKED_FOR_ACQUISITION)
    List<StockItemResponse> getStockItemsMarkedForAcquisition(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
