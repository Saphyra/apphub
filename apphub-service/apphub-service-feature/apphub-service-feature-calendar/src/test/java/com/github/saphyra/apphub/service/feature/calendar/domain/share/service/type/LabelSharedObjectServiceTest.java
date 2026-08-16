package com.github.saphyra.apphub.service.feature.calendar.domain.share.service.type;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.service.feature.calendar.domain.LabelObjectQueryService;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.Label;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LabelSharedObjectServiceTest {
    private static final UUID OWNER_ID = UUID.randomUUID();
    private static final UUID OBJECT_ID = UUID.randomUUID();
    private static final UUID PARENT_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LABEL_ID = UUID.randomUUID();
    private static final String LABEL = "label";

    @Mock
    private LabelObjectQueryService labelObjectQueryService;

    @InjectMocks
    private LabelSharedObjectService underTest;

    @Mock
    private Label label;

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(SharedObjectType.LABEL);
    }

    @Test
    void getSharedObject() {
        given(labelObjectQueryService.findLabel(OWNER_ID, OBJECT_ID, Grant.VIEW)).willReturn(java.util.Optional.of(label));
        given(label.getLabelId()).willReturn(LABEL_ID);
        given(label.getUserId()).willReturn(USER_ID);
        given(label.getLabel()).willReturn(LABEL);

        SharedObject result = underTest.getSharedObject(OWNER_ID, OBJECT_ID, PARENT_ID);

        assertThat(result.objectId()).isEqualTo(LABEL_ID);
        assertThat(result.owner()).isEqualTo(USER_ID);
        assertThat(result.parent()).isEqualTo(USER_ID);
        assertThat(result.name()).isEqualTo(LABEL);
    }
}