package com.github.saphyra.apphub.service.custom.villany_atesz.index;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockItemResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.server.IndexController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.custom.villany_atesz.index.service.StockTotalValueQueryService;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item.StockItemQueryService;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service.ToolQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
class IndexControllerImpl implements IndexController {
    private final StockTotalValueQueryService stockTotalValueQueryService;
    private final StockItemQueryService stockItemQueryService;
    private final ToolQueryService toolQueryService;

    @Override
    public OneParamResponse<Integer> getTotalStockValue(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the total value of their stock", accessTokenHeader.getUserId());

        return new OneParamResponse<>(stockTotalValueQueryService.getTotalValue(accessTokenHeader.getUserId()));
    }

    @Override
    public OneParamResponse<Integer> getTotalToolboxValue(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the total value of their toolbox", accessTokenHeader.getUserId());

        return new OneParamResponse<>(toolQueryService.getTotalValue(accessTokenHeader.getUserId()));
    }

    @Override
    public List<StockItemResponse> getStockItemsMarkedForAcquisition(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know their stockItems marked for acquisition", accessTokenHeader.getUserId());

        return stockItemQueryService.getStockItemsMarkedForAcquisition(accessTokenHeader.getUserId());
    }
}
