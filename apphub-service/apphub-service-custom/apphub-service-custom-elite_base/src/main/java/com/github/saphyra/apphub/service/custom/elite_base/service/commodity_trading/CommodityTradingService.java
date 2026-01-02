package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading;

import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopic;
import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingResponse;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.performance_reporting.PerformanceReporter;
import com.github.saphyra.apphub.service.custom.elite_base.common.PerformanceReportingKey;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.Commodity;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystemDao;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer_filter.OfferFilterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommodityTradingService {
    private final CommodityTradingRequestValidator commodityTradingRequestValidator;
    private final CommodityDao commodityDao;
    private final StarSystemDao starSystemDao;
    private final OfferFilterService offerFilterService;
    private final OfferDetailsFetcher offerDetailsFetcher;
    private final PerformanceReporter performanceReporter;

    public List<CommodityTradingResponse> getTradeOffers(CommodityTradingRequest request) {
        commodityTradingRequestValidator.validate(request);
        TradeMode tradeMode = ValidationUtil.convertToEnumChecked(request.getTradeMode(), TradeMode::valueOf, "tradeMode");

        StarSystem referenceSystem = performanceReporter.wrap(
            ()-> starSystemDao.findByIdValidated(request.getReferenceStarId()),
            PerformanceReportingTopic.ELITE_BASE_QUERY,
            PerformanceReportingKey.COMMODITY_TRADING_REFERENCE_SYSTEM_RETRIEVAL.name()
        );

        List<Commodity> offers = performanceReporter.wrap(
            () -> tradeMode.getOfferProvider().apply(commodityDao, request),
            PerformanceReportingTopic.ELITE_BASE_QUERY,
            PerformanceReportingKey.COMMODITY_TRADING_OFFER_QUERY.name()
        );

        List<CommodityTradingResponse> responses = offerDetailsFetcher.assembleResponses(tradeMode, referenceSystem, offers, request.getIncludeFleetCarriers());

        return offerFilterService.filterOffers(responses, request);
    }
}
