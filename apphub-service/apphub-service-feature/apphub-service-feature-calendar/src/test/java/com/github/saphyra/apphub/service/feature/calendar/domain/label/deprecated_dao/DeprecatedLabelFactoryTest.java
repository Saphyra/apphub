package com.github.saphyra.apphub.service.feature.calendar.domain.label.deprecated_dao;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@Deprecated(forRemoval = true)
class DeprecatedLabelFactoryTest {
    private static final UUID LABEL_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String LABEL = "label";

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private DeprecatedLabelFactory underTest;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(LABEL_ID);

        assertThat(underTest.create(USER_ID, LABEL))
            .returns(LABEL_ID, DeprecatedLabel::getLabelId)
            .returns(USER_ID, DeprecatedLabel::getUserId)
            .returns(LABEL, DeprecatedLabel::getLabel);
    }
}