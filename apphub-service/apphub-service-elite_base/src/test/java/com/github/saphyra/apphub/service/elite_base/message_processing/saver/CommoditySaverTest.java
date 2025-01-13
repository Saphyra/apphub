package com.github.saphyra.apphub.service.elite_base.message_processing.saver;

import com.github.saphyra.apphub.service.elite_base.message_processing.dao.commodity.Commodity;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.commodity.CommodityDao;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.commodity.CommodityLocation;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.commodity.CommodityType;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.last_update.LastUpdateDao;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.last_update.LastUpdateFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CommoditySaverTest {
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final Long MARKET_ID = 23423L;
    private static final String EXISTING_COMMODITY = "existing-commodity";
    private static final String MODIFIED_COMMODITY = "modified-commodity";
    private static final String DEPRECATED_COMMODITY = "deprecated-commodity";
    private static final String NEW_COMMODITY = "new-commodity";

    @Mock
    private CommodityDao commodityDao;

    @Mock
    private CommodityDataTransformer commodityDataTransformer;

    @Mock
    private LastUpdateDao lastUpdateDao;

    @Mock
    private LastUpdateFactory lastUpdateFactory;

    @InjectMocks
    private CommoditySaver underTest;

    @Mock
    private CommoditySaver.CommodityData modifiedCommodityData;

    @Mock
    private CommoditySaver.CommodityData existingCommodityData;

    @Mock
    private CommoditySaver.CommodityData newCommodityData;

    @Mock
    private Commodity existingCommodity;

    @Mock
    private Commodity deprecatedCommodity;

    @Mock
    private Commodity newCommodity;

    @Mock
    private Commodity modifiedCommodity;

    @Test
    void nullMarketIdAndCommodityLocation() {
        assertThat(catchThrowable(() -> underTest.saveAll(LAST_UPDATE, CommodityType.COMMODITY, null, EXTERNAL_REFERENCE, null, List.of()))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nullMarketIdAndExternalReference() {
        assertThat(catchThrowable(() -> underTest.saveAll(LAST_UPDATE, CommodityType.COMMODITY, CommodityLocation.STATION, null, null, List.of()))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void saveAll() {
        given(commodityDao.getByExternalReferenceOrMarketId(EXTERNAL_REFERENCE, MARKET_ID)).willReturn(List.of(existingCommodity, deprecatedCommodity, modifiedCommodity));

        given(existingCommodity.getCommodityName()).willReturn(EXISTING_COMMODITY);
        given(deprecatedCommodity.getCommodityName()).willReturn(DEPRECATED_COMMODITY);
        given(modifiedCommodity.getCommodityName()).willReturn(MODIFIED_COMMODITY);

        given(commodityDataTransformer.transform(null, LAST_UPDATE, CommodityType.COMMODITY, CommodityLocation.STATION, EXTERNAL_REFERENCE, MARKET_ID, newCommodityData)).willReturn(Optional.of(newCommodity));
        given(commodityDataTransformer.transform(existingCommodity, LAST_UPDATE, CommodityType.COMMODITY, CommodityLocation.STATION, EXTERNAL_REFERENCE, MARKET_ID, existingCommodityData)).willReturn(Optional.empty());
        given(commodityDataTransformer.transform(modifiedCommodity, LAST_UPDATE, CommodityType.COMMODITY, CommodityLocation.STATION, EXTERNAL_REFERENCE, MARKET_ID, modifiedCommodityData)).willReturn(Optional.of(modifiedCommodity));

        given(existingCommodityData.getName()).willReturn(EXISTING_COMMODITY);
        given(modifiedCommodityData.getName()).willReturn(MODIFIED_COMMODITY);
        given(newCommodityData.getName()).willReturn(NEW_COMMODITY);

        underTest.saveAll(LAST_UPDATE, CommodityType.COMMODITY, CommodityLocation.STATION, EXTERNAL_REFERENCE, MARKET_ID, List.of(existingCommodityData, newCommodityData, modifiedCommodityData));

        then(lastUpdateFactory).should().create(EXTERNAL_REFERENCE, CommodityType.COMMODITY, LAST_UPDATE);
        then(commodityDao).should().deleteAll(List.of(deprecatedCommodity));
        then(commodityDao).should().saveAll(List.of(newCommodity, modifiedCommodity));
    }
}