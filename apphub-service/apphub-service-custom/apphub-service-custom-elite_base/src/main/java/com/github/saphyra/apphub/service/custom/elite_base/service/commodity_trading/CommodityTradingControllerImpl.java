package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading;

import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopic;
import com.github.saphyra.apphub.api.custom.elite_base.model.commodity_trading.CommodityTradingRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.commodity_trading.CommodityTradingResponse;
import com.github.saphyra.apphub.api.elite_base.server.CommodityTradingController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.performance_reporting.PerformanceReporter;
import com.github.saphyra.apphub.service.custom.elite_base.common.PerformanceReportingKey;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.commodity.avg_price.CommodityAveragePriceDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.type.ItemTypeDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@Slf4j
class CommodityTradingControllerImpl implements CommodityTradingController {
    private final CommodityTradingService commodityTradingService;
    private final ItemTypeDao itemTypeDao;
    private final PerformanceReporter performanceReporter;
    private final CommodityAveragePriceDao commodityAveragePriceDao;

    @Override
    public CommodityTradingResponse bestTradeLocations(CommodityTradingRequest request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to find best trade locations based on {}", accessTokenHeader.getUserId(), request);

        return performanceReporter.wrap(
            () -> commodityTradingService.getTradeOffers(request),
            PerformanceReportingTopic.ELITE_BASE_QUERY,
            PerformanceReportingKey.API_BEST_TRADE_LOCATIONS.name()
        );
    }

    @Override
    public Collection<String> getCommodities(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the trading item names.", accessTokenHeader.getUserId());

        return itemTypeDao.getItemNames(ItemType.TRADING_TYPES);
    }

    @Override
    public Integer getCommodityAveragePrice(String commodityName, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the average price of commodity {}", accessTokenHeader.getUserId(), commodityName);

        return commodityAveragePriceDao.findByIdValidated(commodityName)
            .getAveragePrice();
    }
}
