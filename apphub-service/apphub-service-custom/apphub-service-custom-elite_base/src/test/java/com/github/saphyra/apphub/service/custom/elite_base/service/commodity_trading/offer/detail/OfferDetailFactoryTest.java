package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.detail;

import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeenTestUtils;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.ItemLocationData;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body.Body;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body.BodyDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.Power;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.StarSystemData;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.Offer;
import com.github.saphyra.apphub.test.common.CustomAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class OfferDetailFactoryTest {
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final UUID BODY_ID = UUID.randomUUID();

    @Spy
    private ExecutorServiceBean executorServiceBean = ExecutorServiceBeenTestUtils.create(mock(ErrorReporterService.class));

    @Mock
    private LocationDataLoader locationDataLoader;

    @Mock
    private StarSystemDataLoader starSystemDataLoader;

    @Mock
    private BodyDao bodyDao;

    @InjectMocks
    private OfferDetailFactory underTest;

    @Mock
    private Offer offer;

    @Mock
    private ItemLocationData itemLocationData;

    @Mock
    private StarSystemData starSystemData;

    @Mock
    private Body body;

    @Test
    void create() {
        given(locationDataLoader.loadLocationData(List.of(offer))).willReturn(Map.of(EXTERNAL_REFERENCE, itemLocationData));
        given(starSystemDataLoader.loadStarSystemData(List.of(offer))).willReturn(Map.of(STAR_SYSTEM_ID, starSystemData));
        given(starSystemDataLoader.loadPowers(List.of(offer))).willReturn(Map.of(STAR_SYSTEM_ID, List.of(Power.ARCHON_DELAINE)));
        given(itemLocationData.getBodyId()).willReturn(BODY_ID);
        given(bodyDao.findAllById(List.of(BODY_ID))).willReturn(List.of(body));
        given(body.getId()).willReturn(BODY_ID);

        given(offer.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(offer.getStarSystemId()).willReturn(STAR_SYSTEM_ID);

        CustomAssertions.singleListAssertThat(underTest.create(List.of(offer)))
            .returns(offer, OfferDetail::getOffer)
            .returns(itemLocationData, OfferDetail::getLocationData)
            .returns(starSystemData, OfferDetail::getStarSystemData)
            .returns(Optional.of(List.of(Power.ARCHON_DELAINE)), OfferDetail::getPowers)
            .returns(body, OfferDetail::getBody);
    }
}