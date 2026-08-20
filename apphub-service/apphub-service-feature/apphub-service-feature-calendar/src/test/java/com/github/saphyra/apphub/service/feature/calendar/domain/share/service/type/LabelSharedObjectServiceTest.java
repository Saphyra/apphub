package com.github.saphyra.apphub.service.feature.calendar.domain.share.service.type;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.service.LabelObjectQueryService;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.Label;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.LabelDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
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

    @Mock
    private LabelDao labelDao;

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

        Optional<SharedObject> result = underTest.getSharedObject(OWNER_ID, OBJECT_ID, PARENT_ID);

        assertThat(result).isPresent();
        assertThat(result.get().objectId()).isEqualTo(LABEL_ID);
        assertThat(result.get().owner()).isEqualTo(USER_ID);
        assertThat(result.get().parent()).isEqualTo(USER_ID);
        assertThat(result.get().name()).isEqualTo(LABEL);
    }

    @Test
    void exists() {
        given(labelDao.findById(USER_ID, LABEL_ID)).willReturn(Optional.of(label));

        assertThat(underTest.exists(USER_ID, LABEL_ID)).isTrue();
    }
}