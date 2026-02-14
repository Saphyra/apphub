package com.github.saphyra.apphub.service.custom.elite_base.dao.item.type;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.service.custom.elite_base.common.CacheProperties;
import com.github.saphyra.apphub.service.custom.elite_base.common.EliteBaseProperties;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemType;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ItemTypeDaoTest {
    private static final Duration CACHE_TIMEOUT = Duration.ofMillis(10);
    private static final String ITEM_NAME = "item-name";

    @Mock
    private ItemTypeConverter converter;

    @Mock
    private ItemTypeRepository repository;

    @Mock
    private ExecutorServiceBean executorServiceBean;

    @Mock
    private EliteBaseProperties eliteBaseProperties;

    @InjectMocks
    private ItemTypeDao underTest;

    @Mock
    private ItemTypeDto domain1;

    @Mock
    private ItemTypeDto domain2;

    @Mock
    private CacheProperties cacheProperties;

    @Mock
    private ItemTypeEntity entity;

    @Test
    void delete() {
        assertThat(catchThrowable(() -> underTest.delete(domain1))).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void deleteAll() {
        assertThat(catchThrowable(() -> underTest.deleteAll())).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void deleteAllWithList() {
        assertThat(catchThrowable(() -> underTest.deleteAll(List.of(domain1)))).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void deleteById() {
        assertThat(catchThrowable(() -> underTest.deleteById(ITEM_NAME))).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void findAll_cacheNotLoaded() {
        given(eliteBaseProperties.getCache()).willReturn(cacheProperties);
        given(cacheProperties.getCacheReadTimeout()).willReturn(CACHE_TIMEOUT);

        ExceptionValidator.validateLoggedException(() -> underTest.findAll(), HttpStatus.REQUEST_TIMEOUT, ErrorCode.TEMPORARILY_NOT_AVAILABLE);
    }

    @Test
    void findAll() {
        loadCache();

        assertThat(underTest.findAll()).containsExactly(domain1);
    }

    @Test
    void findAllById_cacheNotLoaded() {
        given(eliteBaseProperties.getCache()).willReturn(cacheProperties);
        given(cacheProperties.getCacheReadTimeout()).willReturn(CACHE_TIMEOUT);

        ExceptionValidator.validateLoggedException(() -> underTest.findAllById(List.of(ITEM_NAME)), HttpStatus.REQUEST_TIMEOUT, ErrorCode.TEMPORARILY_NOT_AVAILABLE);
    }

    @Test
    void findAllById() {
        loadCache();
        given(domain1.getItemName()).willReturn(ITEM_NAME);

        assertThat(underTest.findAllById(List.of(ITEM_NAME))).containsExactly(domain1);
    }

    @Test
    void findById_cacheNotLoaded() {
        given(eliteBaseProperties.getCache()).willReturn(cacheProperties);
        given(cacheProperties.getCacheReadTimeout()).willReturn(CACHE_TIMEOUT);

        ExceptionValidator.validateLoggedException(() -> underTest.findById(ITEM_NAME), HttpStatus.REQUEST_TIMEOUT, ErrorCode.TEMPORARILY_NOT_AVAILABLE);
    }

    @Test
    void findByIdValidated_notFound() {
        loadCache();

        ExceptionValidator.validateNotFoundException(() -> underTest.findByIdValidated(ITEM_NAME));
    }

    @Test
    void findByIdValidated() {
        loadCache();
        given(domain1.getItemName()).willReturn(ITEM_NAME);

        assertThat(underTest.findByIdValidated(ITEM_NAME)).isEqualTo(domain1);
    }

    @Test
    void save() {
        given(converter.convertDomain(domain1)).willReturn(entity);

        underTest.save(domain1);
        underTest.save(domain1);

        then(repository).should().save(entity);
    }

    @Test
    void saveAll() {
        loadCache();

        given(converter.convertDomain(List.of(domain2))).willReturn(List.of(entity));

        underTest.saveAll(List.of(domain1, domain2));

        then(repository).should().saveAll(List.of(entity));

        assertThat(underTest.findAll()).containsExactlyInAnyOrder(domain1, domain2);
    }

    @Test
    void getItemNames_cacheNotLoaded() {
        given(eliteBaseProperties.getCache()).willReturn(cacheProperties);
        given(cacheProperties.getCacheReadTimeout()).willReturn(CACHE_TIMEOUT);

        ExceptionValidator.validateLoggedException(() -> underTest.getItemNames(List.of(ItemType.EQUIPMENT)), HttpStatus.REQUEST_TIMEOUT, ErrorCode.TEMPORARILY_NOT_AVAILABLE);
    }

    @Test
    void getItemNames_differentType() {
        loadCache();
        given(domain1.getType()).willReturn(ItemType.EQUIPMENT);

        assertThat(underTest.getItemNames(List.of(ItemType.COMMODITY))).isEmpty();
    }

    @Test
    void getItemNames() {
        loadCache();
        given(domain1.getType()).willReturn(ItemType.EQUIPMENT);
        given(domain1.getItemName()).willReturn(ITEM_NAME);

        assertThat(underTest.getItemNames(List.of(ItemType.EQUIPMENT))).containsExactly(ITEM_NAME);
    }

    private void loadCache() {
        given(eliteBaseProperties.getCache()).willReturn(cacheProperties);
        given(cacheProperties.getCacheReadTimeout()).willReturn(CACHE_TIMEOUT);
        given(executorServiceBean.execute(any())).willAnswer(invocationOnMock -> {
            invocationOnMock.getArgument(0, Runnable.class).run();
            return null;
        });
        given(repository.findAll()).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain1));

        underTest.init(null);
    }
}