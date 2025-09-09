package com.github.saphyra.apphub.service.custom.elite_base.dao.commodity;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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
        given(commodityConverter.convertDomain(commodity)).willReturn(entity);

        underTest.add(commodityDomainId, commodity);

        underTest.doSynchronize(List.of(commodity));

        then(repository).should().save(entity);
    }
}