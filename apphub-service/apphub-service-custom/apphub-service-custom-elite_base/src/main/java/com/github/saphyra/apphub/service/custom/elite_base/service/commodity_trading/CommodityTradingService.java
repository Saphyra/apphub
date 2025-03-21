package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingResponse;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
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

    public List<CommodityTradingResponse> getTradeOffers(CommodityTradingRequest request) {
        commodityTradingRequestValidator.validate(request);
        TradeMode tradeMode = ValidationUtil.convertToEnumChecked(request.getTradeMode(), TradeMode::valueOf, "tradeMode");

        StarSystem referenceSystem = starSystemDao.findByIdValidated(request.getReferenceStarId());

        List<Commodity> offers = tradeMode.getOfferProvider().apply(commodityDao, request);

        List<CommodityTradingResponse> responses = offerDetailsFetcher.assembleResponses(tradeMode, referenceSystem, offers, request.getIncludeFleetCarriers());

        return offerFilterService.filterOffers(responses, request);
    }
}
