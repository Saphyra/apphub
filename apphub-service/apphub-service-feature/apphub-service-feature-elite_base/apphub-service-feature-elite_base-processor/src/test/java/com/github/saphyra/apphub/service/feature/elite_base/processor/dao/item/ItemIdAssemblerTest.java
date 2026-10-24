package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ItemIdAssemblerTest {
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String ITEM_NAME = "item-name";

    @Spy
    private final UuidConverter uuidConverter = new UuidConverter();

    @InjectMocks
    private ItemIdAssembler underTest;

    @Mock
    private Item item;

    @Test
    void station() {
        given(item.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(item.getLocationType()).willReturn(ItemLocationType.STATION);
        given(item.getItemName()).willReturn(ITEM_NAME);

        assertThat(underTest.assembleId(item)).isEqualTo(ITEM_NAME + ":" + EXTERNAL_REFERENCE + ":");
    }

    @Test
    void fleetCarrier() {
        given(item.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(item.getLocationType()).willReturn(ItemLocationType.FLEET_CARRIER);
        given(item.getItemName()).willReturn(ITEM_NAME);

        assertThat(underTest.assembleId(item)).isEqualTo(ITEM_NAME + "::" + EXTERNAL_REFERENCE);
    }
}