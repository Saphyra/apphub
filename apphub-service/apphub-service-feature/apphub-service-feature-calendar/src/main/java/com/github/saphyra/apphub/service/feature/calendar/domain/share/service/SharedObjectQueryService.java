package com.github.saphyra.apphub.service.feature.calendar.domain.share.service;

import com.github.saphyra.apphub.api.etc.user.client.AccountClient;
import com.github.saphyra.apphub.api.etc.user.model.account.AccountResponse;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.api.feature.calendar.model.response.SharedObjectResponse;
import com.github.saphyra.apphub.api.feature.calendar.model.response.SharedWithResponse;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.Alm;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.service.type.SharedObjectServiceProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SharedObjectQueryService {
    private final SharedObjectServiceProvider sharedObjectServiceProvider;
    private final AlmDao almDao;
    private final AccountClient accountClient;
    private final ExecutorServiceBean executorServiceBean;

    public SharedObjectResponse getSharedItem(UUID userId, SharedObjectType type, UUID objectId, UUID parent) {
        return sharedObjectServiceProvider.getForType(type)
            .getSharedObject(userId, objectId, parent)
            .map(sharedObject -> SharedObjectResponse.builder()
                .objectId(sharedObject.objectId())
                .name(sharedObject.name())
                .owner(sharedObject.owner())
                .parent(sharedObject.parent())
                .sharedWith(getSharedWith(sharedObject.objectId(), type))
                .build())
            .orElseThrow(() -> ExceptionFactory.notFound(userId + " has no access to " + type + " with id " + objectId + " or it does not exist"));
    }

    private List<SharedWithResponse> getSharedWith(UUID id, SharedObjectType objectType) {
        return executorServiceBean.processCollectionWithWait(almDao.getByObject(id, objectType), this::mapAlm);
    }

    private SharedWithResponse mapAlm(Alm alm) {
        AccountResponse account = accountClient.getAccountInternal(alm.getPrincipal());

        return SharedWithResponse.builder()
            .userId(account.getUserId())
            .username(account.getUsername())
            .email(account.getEmail())
            .grants(alm.getGrants())
            .build();
    }
}
