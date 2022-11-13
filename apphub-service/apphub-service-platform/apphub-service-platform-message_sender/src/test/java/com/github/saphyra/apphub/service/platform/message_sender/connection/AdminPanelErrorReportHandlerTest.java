package com.github.saphyra.apphub.service.platform.message_sender.connection;

import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class AdminPanelErrorReportHandlerTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @InjectMocks
    private AdminPanelErrorReportHandler underTest;

    @Mock
    private WebSocketEvent event;

    @Test
    public void getGroup() {
        assertThat(underTest.getGroup()).isEqualTo(MessageGroup.ADMIN_PANEL_ERROR_REPORT);
    }

    @Test
    public void handleMessage_ping() {
        given(event.getEventName()).willReturn(WebSocketEventName.PING);

        underTest.handleMessage(USER_ID, event);

        //No exception
    }

    @Test
    public void handleMessage_unsupportedMessage() {
        given(event.getEventName()).willReturn(WebSocketEventName.ADMIN_PANEL_ERROR_REPORT);

        Throwable ex = catchThrowable(() -> underTest.handleMessage(USER_ID, event));

        ExceptionValidator.validateForbiddenOperation(ex);
    }
}