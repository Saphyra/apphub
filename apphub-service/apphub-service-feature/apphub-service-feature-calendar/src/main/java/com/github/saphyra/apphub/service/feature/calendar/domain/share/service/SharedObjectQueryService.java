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
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.PrincipalType;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.service.type.SharedObject;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.service.type.SharedObjectService;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.service.type.SharedObjectServiceProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
//TODO unit test
public class SharedObjectQueryService {
    private final SharedObjectServiceProvider sharedObjectServiceProvider;
    private final AlmDao almDao;
    private final AccountClient accountClient;
    private final ExecutorServiceBean executorServiceBean;

    public SharedObjectResponse getSharedItem(UUID userId, SharedObjectType type, UUID objectId, UUID parent) {
        SharedObjectService sharedObjectService = sharedObjectServiceProvider.getForType(type);

        //Find Alm for object
        SharedObject sharedObject = almDao.findForObject(userId, PrincipalType.USER, objectId, type)
            //Search by alm if found
            .map(alm -> sharedObjectService.getSharedObject(alm.getOwner(), objectId, parent)) //TODO check permission
            //Search own if not found by alm
            .or(() -> Optional.of(sharedObjectService.getSharedObject(userId, objectId, parent)))
            .orElseThrow(() -> ExceptionFactory.notFound("SharedObject not found for userId %s and objectId %s for type %s".formatted(userId, objectId, type)));

        return SharedObjectResponse.builder()
            .objectId(sharedObject.objectId())
            .name(sharedObject.name())
            .owner(sharedObject.owner())
            .parent(sharedObject.parent())
            .sharedWith(getSharedWith(sharedObject.objectId(), type))
            .build();
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
