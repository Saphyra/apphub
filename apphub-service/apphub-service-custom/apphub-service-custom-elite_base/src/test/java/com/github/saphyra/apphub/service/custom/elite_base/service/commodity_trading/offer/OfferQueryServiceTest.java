package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer;

import com.github.saphyra.apphub.api.custom.elite_base.model.commodity_trading.CommodityTradingRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.commodity_trading.OrderCommoditiesBy;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.custom.elite_base.common.EliteBaseProperties;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystemDao;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.dao.OfferDao;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.detail.OfferDetail;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.detail.OfferDetailFactory;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.filter.OfferFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OfferQueryServiceTest {
    private static final Integer SEARCH_PAGE_SIZE = 2;
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();

    @Mock
    private OfferDao offerDao;

    @Mock
    private OfferFilter offerFilter;

    @Mock
    private StarSystemDao starSystemDao;

    @Mock
    private OfferDetailFactory offerDetailFactory;

    @Mock
    private EliteBaseProperties eliteBaseProperties;

    private OfferQueryService underTest;

    @Mock
    private Offer filteredOffer;

    @Mock
    private Offer offer;

    @Mock
    private OfferDetail filteredOfferDetail;

    @Mock
    private OfferDetail offerDetail;

    @Mock
    private CommodityTradingRequest request;

    @Mock
    private StarSystem referenceSystem;

    @BeforeEach
    void setUp() {
        given(offerDao.getOrderBy()).willReturn(OrderCommoditiesBy.PRICE);

        underTest = OfferQueryService.builder()
            .offerDaos(List.of(offerDao))
            .offerFilters(List.of(offerFilter))
            .starSystemDao(starSystemDao)
            .offerDetailFactory(offerDetailFactory)
            .eliteBaseProperties(eliteBaseProperties)
            .build();
    }

    @Test
    void getOffers() {
        given(request.getReferenceStarId()).willReturn(STAR_SYSTEM_ID);
        given(eliteBaseProperties.getSearchPageSize()).willReturn(SEARCH_PAGE_SIZE);
        given(starSystemDao.findByIdValidated(STAR_SYSTEM_ID)).willReturn(referenceSystem);
        given(request.getOrderBy()).willReturn(OrderCommoditiesBy.PRICE);
        given(offerDao.getOffers(0, request, referenceSystem)).willReturn(List.of(filteredOffer, offer));
        given(offerDao.getOffers(SEARCH_PAGE_SIZE, request, referenceSystem)).willReturn(List.of(filteredOffer, offer));
        given(offerDetailFactory.create(List.of(filteredOffer, offer))).willReturn(List.of(filteredOfferDetail, offerDetail));
        given(offerFilter.filter(List.of(filteredOfferDetail, offerDetail), request)).willReturn(List.of(offerDetail));

        assertThat(underTest.getOffers(request))
            .returns(SEARCH_PAGE_SIZE * 2, BiWrapper::getEntity1)
            .returns(List.of(offerDetail, offerDetail), BiWrapper::getEntity2);
    }
}