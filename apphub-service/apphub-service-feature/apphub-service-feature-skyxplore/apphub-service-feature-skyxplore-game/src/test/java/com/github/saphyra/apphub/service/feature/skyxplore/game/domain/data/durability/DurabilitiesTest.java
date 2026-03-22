package com.github.saphyra.apphub.service.feature.skyxplore.game.domain.data.durability;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DurabilitiesTest {
    @InjectMocks
    private Durabilities underTest;

    @Mock
    private Durability durability;

    @Test
    void setExisting() {
        underTest.add(durability);

        underTest.setExisting();

        then(durability).should().setExisting(true);
    }
}