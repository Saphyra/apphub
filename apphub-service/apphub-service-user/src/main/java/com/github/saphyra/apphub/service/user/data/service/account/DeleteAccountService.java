package com.github.saphyra.apphub.service.user.data.service.account;

import com.github.saphyra.apphub.api.platform.event_gateway.client.EventGatewayApiClient;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.encryption.impl.PasswordService;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteAccountService {
    private final EventGatewayApiClient eventGatewayApi;
    private final LocaleProvider localeProvider;
    private final PasswordService passwordService;
    private final UserDao userDao;

    public void deleteAccount(UUID userId, String password) {
        if (isNull(password)) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "password", "must not be null"), "Password must not be null.");
        }

        User user = userDao.findById(userId);
        if (!passwordService.authenticate(password, user.getPassword())) {
            throw new BadRequestException(ErrorCode.BAD_PASSWORD.name(), "Bad password.");
        }

        SendEventRequest<DeleteAccountEvent> event = SendEventRequest.<DeleteAccountEvent>builder()
            .eventName(DeleteAccountEvent.EVENT_NAME)
            .payload(new DeleteAccountEvent(userId))
            .build()
            .blockingRequest(true);

        eventGatewayApi.sendEvent(event, localeProvider.getLocaleValidated());
    }
}
