package com.github.saphyra.apphub.service.utils.log_formatter;

import com.github.saphyra.apphub.api.utils.model.log_formatter.SetLogParameterVisibilityRequest;
import com.github.saphyra.apphub.api.utils.model.log_formatter.LogParameterVisibilityResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.utils.log_formatter.repository.LogParameterVisibility;
import com.github.saphyra.apphub.service.utils.log_formatter.repository.LogParameterVisibilityDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LogFormatterControllerImplTest {
    private static final String PARAMETER = "parameter";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID ID = UUID.randomUUID();
    private static final LogParameterVisibility VISIBILITY = LogParameterVisibility.builder()
        .id(ID)
        .userId(USER_ID)
        .parameter(PARAMETER)
        .visible(false)
        .build();

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private LogParameterVisibilityDao dao;

    @InjectMocks
    private LogFormatterControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private LogParameterVisibility visibility;

    @BeforeEach
    public void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    public void getVisibility_exists() {
        given(dao.getByUserId(USER_ID)).willReturn(Arrays.asList(VISIBILITY));

        List<LogParameterVisibilityResponse> result = underTest.getVisibility(Arrays.asList(PARAMETER), accessTokenHeader);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(ID);
        assertThat(result.get(0).getParameter()).isEqualTo(PARAMETER);
        assertThat(result.get(0).isVisibility()).isFalse();
    }

    @Test
    public void getVisibility_doesNotExist() {
        given(dao.getByUserId(USER_ID)).willReturn(Collections.emptyList());
        given(idGenerator.randomUuid()).willReturn(ID);

        List<LogParameterVisibilityResponse> result = underTest.getVisibility(Arrays.asList(PARAMETER), accessTokenHeader);

        ArgumentCaptor<LogParameterVisibility> argumentCaptor = ArgumentCaptor.forClass(LogParameterVisibility.class);
        verify(dao).save(argumentCaptor.capture());

        LogParameterVisibility visibility = argumentCaptor.getValue();
        assertThat(visibility.getId()).isEqualTo(ID);
        assertThat(visibility.getUserId()).isEqualTo(USER_ID);
        assertThat(visibility.getParameter()).isEqualTo(PARAMETER);
        assertThat(visibility.isVisible()).isTrue();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(ID);
        assertThat(result.get(0).getParameter()).isEqualTo(PARAMETER);
        assertThat(result.get(0).isVisibility()).isTrue();
    }

    @Test
    public void setVisibility() {
        SetLogParameterVisibilityRequest request = SetLogParameterVisibilityRequest.builder()
            .id(ID)
            .visible(true)
            .build();
        given(dao.findById(ID)).willReturn(Optional.of(visibility));

        underTest.setVisibility(request, accessTokenHeader);

        verify(visibility).setVisible(true);
        verify(dao).save(visibility);
    }
}