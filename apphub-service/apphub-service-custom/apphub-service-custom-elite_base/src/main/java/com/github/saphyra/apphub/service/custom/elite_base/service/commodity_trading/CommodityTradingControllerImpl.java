package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading;

import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopic;
import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingResponse;
import com.github.saphyra.apphub.api.elite_base.server.CommodityTradingController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.performance_reporting.PerformanceReporter;
import com.github.saphyra.apphub.service.custom.elite_base.common.PerformanceReportingKey;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.avg_price.CommodityAveragePriceDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
class CommodityTradingControllerImpl implements CommodityTradingController {
    private final CommodityTradingService commodityTradingService;
    private final CommodityDao commodityDao;
    private final PerformanceReporter performanceReporter;
    private final CommodityAveragePriceDao commodityAveragePriceDao;

    @Override
    public List<CommodityTradingResponse> bestTradeLocations(CommodityTradingRequest request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to find best trade locations based on {}", accessTokenHeader.getUserId(), request);

        return performanceReporter.wrap(
            () -> commodityTradingService.getTradeOffers(request),
            PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING,
            PerformanceReportingKey.API_BEST_TRADE_LOCATIONS.name()
        );
    }

    @Override
    public List<String> getCommodities(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the commodity list.", accessTokenHeader.getUserId());

        return commodityDao.getCommodityNames();
    }

    @Override
    public Integer getCommodityAveragePrice(String commodityName, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the average price of commodity {}", accessTokenHeader.getUserId(), commodityName);

        return commodityAveragePriceDao.findByIdValidated(commodityName)
            .getAveragePrice();
    }
}
