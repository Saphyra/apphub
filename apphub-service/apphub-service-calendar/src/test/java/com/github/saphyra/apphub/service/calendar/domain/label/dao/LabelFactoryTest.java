package com.github.saphyra.apphub.service.calendar.domain.label.dao;

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
class LabelFactoryTest {
    private static final UUID LABEL_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String LABEL = "label";

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private LabelFactory underTest;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(LABEL_ID);

        assertThat(underTest.create(USER_ID, LABEL))
            .returns(LABEL_ID, Label::getLabelId)
            .returns(USER_ID, Label::getUserId)
            .returns(LABEL, Label::getLabel);
    }
}