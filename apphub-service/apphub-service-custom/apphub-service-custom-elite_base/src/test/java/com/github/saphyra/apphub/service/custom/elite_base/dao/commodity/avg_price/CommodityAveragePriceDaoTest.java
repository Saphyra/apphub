package com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.avg_price;

import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.avg_price.CommodityAveragePrice;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.avg_price.CommodityAveragePriceCache;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.avg_price.CommodityAveragePriceConverter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.avg_price.CommodityAveragePriceDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.avg_price.CommodityAveragePriceEntity;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.avg_price.CommodityAveragePriceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CommodityAveragePriceDaoTest {
    private static final String COMMODITY_NAME = "commodity-name";
    private static final Integer AVERAGE_PRICE = 34;
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();

    @Mock
    private CommodityAveragePriceConverter converter;

    @Mock
    private CommodityAveragePriceRepository repository;

    @Mock
    private CommodityAveragePriceCache cache;

    @InjectMocks
    private CommodityAveragePriceDao underTest;

    @Mock
    private CommodityAveragePrice newDomain;

    @Mock
    private CommodityAveragePrice storedDomain;

    @Mock
    private CommodityAveragePriceEntity entity;

    @Test
    void extractId() {
        given(newDomain.getCommodityName()).willReturn(COMMODITY_NAME);

        assertThat(underTest.extractId(newDomain)).isEqualTo(COMMODITY_NAME);
    }

    @Test
    void shouldSave_nullData() {
        given(newDomain.getAveragePrice()).willReturn(null);

        assertThat(underTest.shouldSave(newDomain)).isFalse();
    }

    @Test
    void shouldSave_noStored() {
        given(newDomain.getCommodityName()).willReturn(COMMODITY_NAME);
        given(newDomain.getAveragePrice()).willReturn(AVERAGE_PRICE);
        given(repository.findById(COMMODITY_NAME)).willReturn(Optional.empty());
        given(converter.convertEntity(Optional.empty())).willReturn(Optional.empty());

        assertThat(underTest.shouldSave(newDomain)).isTrue();
    }

    @Test
    void shouldSave_outdated() {
        given(newDomain.getCommodityName()).willReturn(COMMODITY_NAME);
        given(newDomain.getAveragePrice()).willReturn(AVERAGE_PRICE);
        given(repository.findById(COMMODITY_NAME)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.ofNullable(entity))).willReturn(Optional.of(storedDomain));
        given(newDomain.getLastUpdate()).willReturn(LAST_UPDATE.minusSeconds(1));
        given(storedDomain.getCommodityName()).willReturn(COMMODITY_NAME);
        given(storedDomain.getLastUpdate()).willReturn(LAST_UPDATE);

        assertThat(underTest.shouldSave(newDomain)).isFalse();
    }

    @Test
    void shouldSave_sameData() {
        given(newDomain.getCommodityName()).willReturn(COMMODITY_NAME);
        given(newDomain.getAveragePrice()).willReturn(AVERAGE_PRICE);
        given(repository.findById(COMMODITY_NAME)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.ofNullable(entity))).willReturn(Optional.of(storedDomain));
        given(newDomain.getLastUpdate()).willReturn(LAST_UPDATE);
        given(storedDomain.getLastUpdate()).willReturn(LAST_UPDATE);
        given(storedDomain.getAveragePrice()).willReturn(AVERAGE_PRICE);
        given(storedDomain.getCommodityName()).willReturn(COMMODITY_NAME);

        assertThat(underTest.shouldSave(newDomain)).isFalse();
    }

    @Test
    void shouldSave() {
        given(newDomain.getCommodityName()).willReturn(COMMODITY_NAME);
        given(newDomain.getAveragePrice()).willReturn(AVERAGE_PRICE);
        given(repository.findById(COMMODITY_NAME)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.ofNullable(entity))).willReturn(Optional.of(storedDomain));
        given(newDomain.getLastUpdate()).willReturn(LAST_UPDATE);
        given(storedDomain.getLastUpdate()).willReturn(LAST_UPDATE);
        given(storedDomain.getAveragePrice()).willReturn(AVERAGE_PRICE + 1);
        given(storedDomain.getCommodityName()).willReturn(COMMODITY_NAME);

        assertThat(underTest.shouldSave(newDomain)).isTrue();
    }
}