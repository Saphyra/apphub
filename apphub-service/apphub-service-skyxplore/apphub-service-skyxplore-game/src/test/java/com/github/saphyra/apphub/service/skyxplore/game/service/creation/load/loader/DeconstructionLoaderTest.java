package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.DeconstructionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DeconstructionLoaderTest {
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID DECONSTRUCTION_ID = UUID.randomUUID();
    private static final Integer CURRENT_WORK_POINTS = 245;
    private static final Integer PRIORITY = 456;

    @Mock
    private GameItemLoader gameItemLoader;

    @InjectMocks
    private DeconstructionLoader underTest;

    @Mock
    private DeconstructionModel deconstructionModel;

    @Test
    void load() {
        given(gameItemLoader.loadChildren(EXTERNAL_REFERENCE, GameItemType.DECONSTRUCTION, DeconstructionModel[].class)).willReturn(List.of(deconstructionModel));

        given(deconstructionModel.getId()).willReturn(DECONSTRUCTION_ID);
        given(deconstructionModel.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(deconstructionModel.getCurrentWorkPoints()).willReturn(CURRENT_WORK_POINTS);
        given(deconstructionModel.getPriority()).willReturn(PRIORITY);

        Deconstruction result = underTest.load(EXTERNAL_REFERENCE);

        assertThat(result.getDeconstructionId()).isEqualTo(DECONSTRUCTION_ID);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(result.getCurrentWorkPoints()).isEqualTo(CURRENT_WORK_POINTS);
        assertThat(result.getPriority()).isEqualTo(PRIORITY);
    }
}