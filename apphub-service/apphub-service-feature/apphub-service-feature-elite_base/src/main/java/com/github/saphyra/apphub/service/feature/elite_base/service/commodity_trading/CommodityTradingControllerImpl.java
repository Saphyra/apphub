package com.github.saphyra.apphub.service.feature.elite_base.service.commodity_trading;

import com.github.saphyra.apphub.api.feature.elite_base.model.commodity_trading.CommodityTradingRequest;
import com.github.saphyra.apphub.api.feature.elite_base.model.commodity_trading.CommodityTradingResponse;
import com.github.saphyra.apphub.api.feature.elite_base.server.CommodityTradingController;
import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.monitoring.instrument.MonitoringInstruments;
import com.github.saphyra.apphub.service.feature.elite_base.common.PerformanceReportingKey;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.ItemType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.trading.commodity.avg_price.CommodityAveragePriceDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.type.ItemTypeDao;
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
    private final MonitoringInstruments monitoringInstruments;
    private final CommodityAveragePriceDao commodityAveragePriceDao;

    @Override
    public CommodityTradingResponse bestTradeLocations(CommodityTradingRequest request, AccessToken accessToken) {
        log.info("{} wants to find best trade locations based on {}", accessToken.getUserId(), request);

        return monitoringInstruments.wrap(
            () -> commodityTradingService.getTradeOffers(request),
            Feature.ELITE_BASE_QUERY,
            PerformanceReportingKey.API_BEST_TRADE_LOCATIONS.name()
        );
    }

    @Override
    public Collection<String> getCommodities(AccessToken accessToken) {
        log.info("{} wants to know the trading item names.", accessToken.getUserId());

        return itemTypeDao.getItemNames(ItemType.TRADING_TYPES);
    }

    @Override
    public Integer getCommodityAveragePrice(String commodityName, AccessToken accessToken) {
        log.info("{} wants to know the average price of commodity {}", accessToken.getUserId(), commodityName);

        return commodityAveragePriceDao.findByIdValidated(commodityName)
            .getAveragePrice();
    }
}
