package com.github.saphyra.apphub.service.custom.elite_base.dao.commodity;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.test.common.ReflectionUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_COMMODITY_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CommodityDaoTest {
    private static final Long MARKET_ID = 34L;
    private static final String COMMODITY_NAME = "commodity-name";
    private static final Integer MIN_STOCK = 3214;
    private static final Integer MIN_PRICE = 435;
    private static final Integer MAX_PRICE = 678;
    private static final Integer MIN_DEMAND = 3;

    @Mock
    private CommodityConverter converter;

    @Mock
    private CommodityRepository repository;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private CommodityDao underTest;

    @Mock
    private Commodity domain;

    @Mock
    private CommodityEntity entity;

    @Mock
    private ResultSet resultSet;

    @Test
    void getByExternalReferenceOrMarketId() {
        given(repository.getByMarketIdAndType(MARKET_ID, CommodityType.COMMODITY)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        assertThat(underTest.getByMarketIdAndType(MARKET_ID, CommodityType.COMMODITY)).containsExactly(domain);
    }

    @Test
    void getCommodityNames() throws SQLException, NoSuchFieldException, IllegalAccessException {
        given(jdbcTemplate.query(anyString(), any(ResultSetExtractor.class))).willAnswer(invocation -> invocation.getArgument(1, ResultSetExtractor.class).extractData(resultSet));
        given(resultSet.next())
            .willReturn(true)
            .willReturn(false);
        given(resultSet.getString(COLUMN_COMMODITY_NAME)).willReturn(COMMODITY_NAME);

        assertThat(underTest.getCommodityNames()).containsExactly(COMMODITY_NAME);

        Set<String> commodityNameCache = ReflectionUtils.getFieldValue(underTest, "commodityNameCache");
        assertThat(commodityNameCache).contains(COMMODITY_NAME);

        boolean loaded = ReflectionUtils.getFieldValue(underTest, "loaded");
        assertThat(loaded).isTrue();

        assertThat(underTest.getCommodityNames()).containsExactly(COMMODITY_NAME);

        then(jdbcTemplate).should().query(anyString(), any(ResultSetExtractor.class));
        then(jdbcTemplate).shouldHaveNoMoreInteractions();
    }

    @Test
    void findSuppliers() {
        given(repository.getSellOffers(COMMODITY_NAME, MIN_STOCK, MIN_PRICE, MAX_PRICE)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        assertThat(underTest.findSuppliers(COMMODITY_NAME, MIN_STOCK, MIN_PRICE, MAX_PRICE)).containsExactly(domain);
    }

    @Test
    void findConsumers() {
        given(repository.getBuyOffers(COMMODITY_NAME, MIN_DEMAND, MIN_PRICE, MAX_PRICE)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        assertThat(underTest.findConsumers(COMMODITY_NAME, MIN_DEMAND, MIN_PRICE, MAX_PRICE)).containsExactly(domain);
    }

    @Test
    void save() throws NoSuchFieldException, IllegalAccessException {
        given(domain.getCommodityName()).willReturn(COMMODITY_NAME);
        given(converter.convertDomain(domain)).willReturn(entity);

        underTest.save(domain);

        Set<String> commodityNameCache = ReflectionUtils.getFieldValue(underTest, "commodityNameCache");
        assertThat(commodityNameCache).contains(COMMODITY_NAME);

        then(repository).should().save(entity);
    }

    @Test
    void saveAll() throws NoSuchFieldException, IllegalAccessException {
        given(domain.getCommodityName()).willReturn(COMMODITY_NAME);
        given(converter.convertDomain(List.of(domain))).willReturn(List.of(entity));

        underTest.saveAll(List.of(domain));

        Set<String> commodityNameCache = ReflectionUtils.getFieldValue(underTest, "commodityNameCache");
        assertThat(commodityNameCache).contains(COMMODITY_NAME);

        then(repository).should().saveAll(List.of(entity));
    }

    @Test
    void deleteByExternalReferencesAndCommodityNames_empty(){
        underTest.deleteByExternalReferencesAndCommodityNames(List.of());

        then(jdbcTemplate).shouldHaveNoInteractions();
    }
}