package com.github.saphyra.apphub.service.custom.elite_base.dao.commodity;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.test.common.ReflectionUtils;
import com.google.common.cache.Cache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_COMMODITY_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class CommodityDaoTest {
    private static final Long MARKET_ID = 34L;
    private static final String COMMODITY_NAME = "commodity-name";
    private static final Integer MIN_STOCK = 3214;
    private static final Integer MIN_PRICE = 435;
    private static final Integer MAX_PRICE = 678;
    private static final Integer MIN_DEMAND = 3;
    private static final String EXTERNAL_REFERENCE_STRING = "external-reference";
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();

    @Mock
    private CommodityConverter converter;

    @Mock
    private CommodityRepository repository;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private Cache<CommodityCacheKey, List<Commodity>> commodityReadCache;

    @Mock
    private CommodityWriteBuffer writeBuffer;

    @Mock
    private CommodityDeleteBuffer deleteBuffer;

    @InjectMocks
    private CommodityDao underTest;

    @Mock
    private Commodity domain;

    @Mock
    private CommodityEntity entity;

    @Mock
    private ResultSet resultSet;

    @Test
    void getByMarketIdAndType() {
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

        boolean loaded = ReflectionUtils.getFieldValue(underTest, "commodityNamesLoaded");
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

        underTest.save(domain);

        Set<String> commodityNameCache = ReflectionUtils.getFieldValue(underTest, "commodityNameCache");
        assertThat(commodityNameCache).contains(COMMODITY_NAME);
    }

    @Test
    void saveAll() throws NoSuchFieldException, IllegalAccessException {
        given(domain.getCommodityName()).willReturn(COMMODITY_NAME);

        underTest.saveAll(List.of(domain));

        Set<String> commodityNameCache = ReflectionUtils.getFieldValue(underTest, "commodityNameCache");
        assertThat(commodityNameCache).contains(COMMODITY_NAME);
    }

    @Test
    void getCacheKey() {
        given(domain.getMarketId()).willReturn(MARKET_ID);
        given(domain.getType()).willReturn(CommodityType.COMMODITY);

        assertThat(underTest.getCacheKey(domain))
            .returns(MARKET_ID, CommodityCacheKey::getMarketId)
            .returns(CommodityType.COMMODITY, CommodityCacheKey::getCommodityType);
    }

    @Test
    void toDomainId() {
        CommodityEntityId entityId = CommodityEntityId.builder()
            .externalReference(EXTERNAL_REFERENCE_STRING)
            .commodityName(COMMODITY_NAME)
            .build();
        given(uuidConverter.convertEntity(EXTERNAL_REFERENCE_STRING)).willReturn(EXTERNAL_REFERENCE);

        assertThat(underTest.toDomainId(entityId))
            .returns(EXTERNAL_REFERENCE, CommodityDomainId::getExternalReference)
            .returns(COMMODITY_NAME, CommodityDomainId::getCommodityName);
    }

    @Test
    void getDomainId() {
        given(domain.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(domain.getCommodityName()).willReturn(COMMODITY_NAME);

        assertThat(underTest.getDomainId(domain))
            .returns(EXTERNAL_REFERENCE, CommodityDomainId::getExternalReference)
            .returns(COMMODITY_NAME, CommodityDomainId::getCommodityName);
    }

    @ParameterizedTest
    @MethodSource("matchesWithIdParameters")
    void matchesWithId(UUID externalReference, String commodityName, boolean expected) {
        CommodityDomainId domainId = CommodityDomainId.builder()
            .externalReference(externalReference)
            .commodityName(commodityName)
            .build();

        lenient().when(domain.getExternalReference()).thenReturn(EXTERNAL_REFERENCE);
        lenient().when(domain.getCommodityName()).thenReturn(COMMODITY_NAME);

        assertThat(underTest.matchesWithId(domainId, domain)).isEqualTo(expected);

    }

    static Stream<Arguments> matchesWithIdParameters() {
        return Stream.of(
            Arguments.of(EXTERNAL_REFERENCE, COMMODITY_NAME, true),
            Arguments.of(EXTERNAL_REFERENCE, "other-commodity-name", false),
            Arguments.of(UUID.randomUUID(), COMMODITY_NAME, false),
            Arguments.of(UUID.randomUUID(), "other-commodity-name", false)
        );
    }
}