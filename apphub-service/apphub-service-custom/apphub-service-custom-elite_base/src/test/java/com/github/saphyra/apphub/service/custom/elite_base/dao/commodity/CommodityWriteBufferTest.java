package com.github.saphyra.apphub.service.custom.elite_base.dao.commodity;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CommodityWriteBufferTest {
    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private CommodityRepository repository;

    @Mock
    private CommodityConverter commodityConverter;

    @InjectMocks
    private CommodityWriteBuffer underTest;

    @Mock
    private CommodityDomainId commodityDomainId;

    @Mock
    private Commodity commodity;

    @Mock
    private CommodityEntity entity;

    @Test
    void doSynchronize() {
        given(commodityConverter.convertDomain(any(Collection.class))).willReturn(List.of(entity));

        underTest.add(commodityDomainId, commodity);

        underTest.doSynchronize();

        then(repository).should().saveAll(List.of(entity));
    }
}