package com.github.saphyra.apphub.service.custom.elite_base.dao.commodity;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.custom.elite_base.common.CacheProperties;
import com.github.saphyra.apphub.service.custom.elite_base.common.EliteBaseProperties;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CommodityNameCacheTest {
    private static final String SAVED_COMMODITY_NAME = "saved-commodity-name";
    private static final String EXTERNAL_REFERENCE = "external-reference";
    private static final String NEW_COMMODITY_NAME = "new-commodity-name";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Mock
    private EliteBaseProperties eliteBaseProperties;

    @Autowired
    private SleepService sleepService;

    @Autowired
    private ErrorReporterService errorReporterService;

    @Autowired
    private ExecutorServiceBean executorServiceBean;

    private CommodityNameCache underTest;

    @Mock
    private CacheProperties cacheProperties;

    @Autowired
    private CommodityRepository commodityRepository;

    @BeforeEach
    void setUp() {
        given(eliteBaseProperties.getCache()).willReturn(cacheProperties);
        given(cacheProperties.getCacheReadTimeout()).willReturn(Duration.ofSeconds(1));

        underTest = CommodityNameCache.builder()
            .jdbcTemplate(jdbcTemplate)
            .eliteBaseProperties(eliteBaseProperties)
            .sleepService(sleepService)
            .errorReporterService(errorReporterService)
            .executorServiceBean(executorServiceBean)
            .build();
    }

    @AfterEach
    void clear() {
        commodityRepository.deleteAll();
    }

    @Test
    void getCommodityNames_timeout() {
        ExceptionValidator.validateLoggedException(() -> underTest.getCommodityNames(), HttpStatus.REQUEST_TIMEOUT, ErrorCode.TEMPORARILY_NOT_AVAILABLE);
    }

    @Test
    void getCommodityNames() {
        given(cacheProperties.getCacheReadTimeout()).willReturn(Duration.ofSeconds(10));
        mockLoad();

        assertThat(underTest.getCommodityNames()).containsExactly(SAVED_COMMODITY_NAME);
    }

    @Test
    void add_timeout() {
        underTest.add(NEW_COMMODITY_NAME);

        underTest.init(null);

        assertThat(underTest.getCommodityNames()).isEmpty();
    }

    @Test
    void add() {
        given(cacheProperties.getCacheReadTimeout()).willReturn(Duration.ofSeconds(10));
        mockLoad();

        underTest.add(NEW_COMMODITY_NAME);

        assertThat(underTest.getCommodityNames()).containsExactlyInAnyOrder(SAVED_COMMODITY_NAME, NEW_COMMODITY_NAME);
    }

    @Test
    void addAll_timeout() {
        underTest.addAll(List.of(NEW_COMMODITY_NAME));

        underTest.init(null);

        assertThat(underTest.getCommodityNames()).isEmpty();
    }

    @Test
    void addAll() {
        given(cacheProperties.getCacheReadTimeout()).willReturn(Duration.ofSeconds(10));
        mockLoad();

        underTest.addAll(List.of(NEW_COMMODITY_NAME));

        assertThat(underTest.getCommodityNames()).containsExactlyInAnyOrder(SAVED_COMMODITY_NAME, NEW_COMMODITY_NAME);
    }

    private void mockLoad() {
        CommodityEntity entity = CommodityEntity.builder()
            .id(CommodityEntityId.builder()
                .commodityName(SAVED_COMMODITY_NAME)
                .externalReference(EXTERNAL_REFERENCE)
                .build())
            .build();
        commodityRepository.save(entity);

        underTest.init(null);
    }
}