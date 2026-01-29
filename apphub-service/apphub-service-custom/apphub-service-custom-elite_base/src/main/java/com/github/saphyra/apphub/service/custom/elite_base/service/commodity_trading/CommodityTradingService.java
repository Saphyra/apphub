package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading;

import com.github.saphyra.apphub.api.custom.elite_base.model.commodity_trading.CommodityTradingRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.commodity_trading.CommodityTradingResponse;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.OfferDetail;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.OfferQueryService;
import com.github.saphyra.apphub.service.custom.elite_base.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class CommodityTradingService {
    private final CommodityTradingRequestValidator commodityTradingRequestValidator;
    private final OfferQueryService offerQueryService;
    private final OfferConverter offerConverter;

    CommodityTradingResponse getTradeOffers(CommodityTradingRequest request) {
        commodityTradingRequestValidator.validate(request);

        BiWrapper<Integer, List<OfferDetail>> offers = Utils.measuredOperation(
            () -> offerQueryService.getOffers(request),
            "OfferDetails query took {} ms"
        );

        return Utils.measuredOperation(
            () -> convert(offers),
            "Response assembly took {} ms"
        );
    }

    private CommodityTradingResponse convert(BiWrapper<Integer, List<OfferDetail>> offers) {
        return CommodityTradingResponse.builder()
            .offset(offers.getEntity1())
            .items(offerConverter.convert(offers.getEntity2()))
            .build();
    }
}
