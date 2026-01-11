package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading;

import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopic;
import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingResponse;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.performance_reporting.PerformanceReporter;
import com.github.saphyra.apphub.service.custom.elite_base.common.PerformanceReportingKey;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.Tradeable;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.TradingDaoSupport;
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
//TODO unit test
public class CommodityTradingService {
    private final CommodityTradingRequestValidator commodityTradingRequestValidator;
    private final StarSystemDao starSystemDao;
    private final OfferFilterService offerFilterService;
    private final OfferDetailsFetcher offerDetailsFetcher;
    private final PerformanceReporter performanceReporter;
    private final TradingDaoSupport tradingDaoSupport;
    private final OfferDetailConverter offerDetailConverter;

    public List<CommodityTradingResponse> getTradeOffers(CommodityTradingRequest request) {
        commodityTradingRequestValidator.validate(request);
        TradeMode tradeMode = ValidationUtil.convertToEnumChecked(request.getTradeMode(), TradeMode::valueOf, "tradeMode");

        StarSystem referenceSystem = performanceReporter.wrap(
            ()-> starSystemDao.findByIdValidated(request.getReferenceStarId()),
            PerformanceReportingTopic.ELITE_BASE_QUERY,
            PerformanceReportingKey.COMMODITY_TRADING_REFERENCE_SYSTEM_RETRIEVAL.name()
        );
        log.info("ReferenceSystem found: {}", referenceSystem);

        List<Tradeable> offers = performanceReporter.wrap(
            () -> tradingDaoSupport.getOffers(tradeMode, request.getItemName(), request.getMinTradeAmount(), request.getMinPrice(), request.getMaxPrice()),
            PerformanceReportingTopic.ELITE_BASE_QUERY,
            PerformanceReportingKey.COMMODITY_TRADING_OFFER_QUERY.name()
        );
        log.info("Found {} offers", offers.size());

        List<OfferDetail> offerDetails = offerDetailsFetcher.assembleOffers(tradeMode, referenceSystem, offers, request.getIncludeFleetCarriers());
        log.info("CommodityTradingResponses assembled");

        List<OfferDetail> filteredOffers = offerFilterService.filterOffers(offerDetails, request);
        log.info("{} offers are returned after filtering", filteredOffers.size());
        return offerDetailConverter.convert(filteredOffers);
    }
}
