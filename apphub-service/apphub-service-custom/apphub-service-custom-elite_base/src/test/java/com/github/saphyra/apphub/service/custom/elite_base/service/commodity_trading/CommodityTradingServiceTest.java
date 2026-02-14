package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading;

import com.github.saphyra.apphub.api.custom.elite_base.model.commodity_trading.CommodityTradingRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.commodity_trading.CommodityTradingResponse;
import com.github.saphyra.apphub.api.custom.elite_base.model.commodity_trading.CommodityTradingResponseItem;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.detail.OfferDetail;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.OfferQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CommodityTradingServiceTest {
    private static final Integer OFFSET = 243;

    @Mock
    private CommodityTradingRequestValidator commodityTradingRequestValidator;

    @Mock
    private OfferQueryService offerQueryService;

    @Mock
    private OfferConverter offerConverter;

    @InjectMocks
    private CommodityTradingService underTest;

    @Mock
    private CommodityTradingRequest request;

    @Mock
    private CommodityTradingResponseItem item;

    @Mock
    private OfferDetail offerDetail;

    @Test
    void getTradeOffers() {
        given(offerQueryService.getOffers(request)).willReturn(new BiWrapper<>(OFFSET, List.of(offerDetail)));
        given(offerConverter.convert(List.of(offerDetail))).willReturn(List.of(item));

        assertThat(underTest.getTradeOffers(request))
            .returns(OFFSET, CommodityTradingResponse::getOffset)
            .returns(List.of(item), CommodityTradingResponse::getItems);

        then(commodityTradingRequestValidator).should().validate(request);
    }
}