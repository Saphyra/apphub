package com.github.saphyra.apphub.service.calendar.domain.label;

import com.github.saphyra.apphub.api.calendar.model.response.LabelResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.calendar.domain.label.service.LabelQueryService;
import com.github.saphyra.apphub.service.calendar.domain.label.service.LabelService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class LabelControllerImplTest {
    private static final String LABEL = "label";
    private static final UUID LABEL_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private LabelQueryService labelQueryService;

    @Mock
    private LabelService labelService;

    @InjectMocks
    private LabelControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private LabelResponse labelResponse;

    @Test
    void createLabel() {
        given(labelService.createLabel(USER_ID, LABEL)).willReturn(LABEL_ID);
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);

        assertThat(underTest.createLabel(new OneParamRequest<>(LABEL), accessTokenHeader))
            .returns(LABEL_ID, OneParamResponse::getValue);
    }

    @Test
    void getLabels() {
        given(labelQueryService.getByUserId(USER_ID)).willReturn(List.of(labelResponse));
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);

        assertThat(underTest.getLabels(accessTokenHeader)).containsExactly(labelResponse);
    }

    @Test
    void getLabel() {
        given(labelQueryService.getLabel(LABEL_ID)).willReturn(labelResponse);

        assertThat(underTest.getLabel(LABEL_ID, accessTokenHeader)).isEqualTo(labelResponse);
    }

    @Test
    void deleteLabel() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(labelQueryService.getByUserId(USER_ID)).willReturn(List.of(labelResponse));

        assertThat(underTest.deleteLabel(LABEL_ID, accessTokenHeader)).containsExactly(labelResponse);

        then(labelService).should().deleteLabel(USER_ID, LABEL_ID);
    }

    @Test
    void editLabel() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(labelQueryService.getByUserId(USER_ID)).willReturn(List.of(labelResponse));

        assertThat(underTest.editLabel(new OneParamRequest<>(LABEL), LABEL_ID, accessTokenHeader)).containsExactly(labelResponse);

        then(labelService).should().editLabel(USER_ID, LABEL_ID, LABEL);
    }
}