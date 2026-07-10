package com.github.saphyra.apphub.service.feature.calendar.domain.share.service.type;

import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
//TODO unit test
public class SharedObjectServiceProvider {
    private final List<SharedObjectService> sharedObjectServices;

    public SharedObjectService getForType(SharedObjectType type) {
        return sharedObjectServices.stream()
            .filter(service -> service.getType() == type)
            .findAny()
            .orElseThrow(() -> ExceptionFactory.reportedException(HttpStatus.NOT_IMPLEMENTED, ErrorCode.GENERAL_ERROR, "SharedObjectService not found for type " + type));
    }
}
