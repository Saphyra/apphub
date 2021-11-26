package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.durability_item;

import com.github.saphyra.apphub.api.skyxplore.model.game.DurabilityItemModel;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemValidator;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DurabilityItemValidatorTest {
    private static final UUID PARENT = UUID.randomUUID();
    private static final Integer MAX_DURABILITY = 435;
    private static final Integer CURRENT_DURABILITY = 12;

    @Mock
    private GameItemValidator gameItemValidator;

    @InjectMocks
    private DurabilityItemValidator underTest;

    @Mock
    private DurabilityItemModel model;

    @After
    public void validate() {
        verify(gameItemValidator).validate(model);
    }

    @Test
    public void nullParent() {
        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "parent", "must not be null");
    }

    @Test
    public void nullMaxDurability() {
        given(model.getParent()).willReturn(PARENT);
        given(model.getMaxDurability()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "maxDurability", "must not be null");
    }

    @Test
    public void nullCurrentDurability() {
        given(model.getParent()).willReturn(PARENT);
        given(model.getMaxDurability()).willReturn(MAX_DURABILITY);
        given(model.getCurrentDurability()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "currentDurability", "must not be null");
    }

    @Test
    public void valid() {
        given(model.getParent()).willReturn(PARENT);
        given(model.getMaxDurability()).willReturn(MAX_DURABILITY);
        given(model.getCurrentDurability()).willReturn(CURRENT_DURABILITY);

        underTest.validate(model);
    }
}