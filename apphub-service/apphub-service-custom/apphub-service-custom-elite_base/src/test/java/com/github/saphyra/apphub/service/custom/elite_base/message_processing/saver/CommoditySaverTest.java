package com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver;

import com.github.saphyra.apphub.lib.performance_reporting.PerformanceReporter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.Commodity;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityLocation;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.last_update.LastUpdate;
import com.github.saphyra.apphub.service.custom.elite_base.dao.last_update.LastUpdateDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.last_update.LastUpdateFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doAnswer;

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

    @Mock
    private PerformanceReporter performanceReporter;

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

    @Mock
    private Commodity incorrectCommodity;

    @Mock
    private LastUpdate lastUpdate;

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
        given(commodityDao.getByMarketIdAndType(MARKET_ID, CommodityType.COMMODITY)).willReturn(List.of(incorrectCommodity, existingCommodity, deprecatedCommodity, modifiedCommodity));
        given(incorrectCommodity.getExternalReference()).willReturn(UUID.randomUUID());

        given(existingCommodity.getCommodityName()).willReturn(EXISTING_COMMODITY);
        given(deprecatedCommodity.getCommodityName()).willReturn(DEPRECATED_COMMODITY);
        given(modifiedCommodity.getCommodityName()).willReturn(MODIFIED_COMMODITY);

        given(existingCommodity.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(deprecatedCommodity.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(modifiedCommodity.getExternalReference()).willReturn(EXTERNAL_REFERENCE);

        given(commodityDataTransformer.transform(null, LAST_UPDATE, CommodityType.COMMODITY, CommodityLocation.STATION, EXTERNAL_REFERENCE, MARKET_ID, newCommodityData)).willReturn(Optional.of(newCommodity));
        given(commodityDataTransformer.transform(existingCommodity, LAST_UPDATE, CommodityType.COMMODITY, CommodityLocation.STATION, EXTERNAL_REFERENCE, MARKET_ID, existingCommodityData)).willReturn(Optional.empty());
        given(commodityDataTransformer.transform(modifiedCommodity, LAST_UPDATE, CommodityType.COMMODITY, CommodityLocation.STATION, EXTERNAL_REFERENCE, MARKET_ID, modifiedCommodityData)).willReturn(Optional.of(modifiedCommodity));

        given(existingCommodityData.getName()).willReturn(EXISTING_COMMODITY);
        given(modifiedCommodityData.getName()).willReturn(MODIFIED_COMMODITY);
        given(newCommodityData.getName()).willReturn(NEW_COMMODITY);
        given(lastUpdateFactory.create(EXTERNAL_REFERENCE, CommodityType.COMMODITY, LAST_UPDATE)).willReturn(lastUpdate);
        given(performanceReporter.wrap(any(Callable.class), any(), any())).willAnswer(invocation -> invocation.getArgument(0, Callable.class).call());
        doAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return null;
        }).when(performanceReporter).wrap(any(Runnable.class), any(), any());
        given(existingCommodityData.getDemand()).willReturn(1);
        given(newCommodityData.getDemand()).willReturn(1);
        given(modifiedCommodityData.getDemand()).willReturn(1);

        underTest.saveAll(LAST_UPDATE, CommodityType.COMMODITY, CommodityLocation.STATION, EXTERNAL_REFERENCE, MARKET_ID, List.of(existingCommodityData, newCommodityData, modifiedCommodityData));

        then(lastUpdateDao).should().save(lastUpdate);
        then(commodityDao).should().deleteAll(List.of(incorrectCommodity));
        then(commodityDao).should().deleteAll(List.of(deprecatedCommodity));
        then(commodityDao).should().saveAll(List.of(newCommodity, modifiedCommodity));
    }
}