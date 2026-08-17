package com.github.saphyra.apphub.service.feature.calendar.domain.share;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.api.feature.calendar.model.request.ShareObjectRequest;
import com.github.saphyra.apphub.api.feature.calendar.model.response.SharedObjectResponse;
import com.github.saphyra.apphub.api.feature.calendar.server.CalendarShareController;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.service.EditSharedOperationsService;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.service.ShareObjectService;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.service.SharedObjectQueryService;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.service.UnshareObjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
class CalendarShareControllerImpl implements CalendarShareController {
    private final SharedObjectQueryService sharedObjectQueryService;
    private final ShareObjectService shareObjectService;
    private final EditSharedOperationsService editSharedOperationsService;
    private final UnshareObjectService unshareObjectService;

    @Override
    public SharedObjectResponse getSharedItem(SharedObjectType type, UUID id, UUID parent, AccessToken accessToken) {
        log.info("{} wants to query SharedItem {} of type {}", accessToken.getUserId(), id, type);
        return sharedObjectQueryService.getSharedItem(accessToken.getUserId(), type, id, parent);
    }

    @Override
    public List<Grant> getOperations(SharedObjectType type) {
        return Arrays.stream(Grant.values())
            .filter(operation -> operation.getObjectTypes().contains(type))
            .toList();
    }

    @Override
    public void shareObject(ShareObjectRequest request, AccessToken accessToken) {
        log.info("{} wants to share {} {} with {}", accessToken.getUserId(), request.getType(), request.getObjectId(), request.getSharedWith());

        shareObjectService.share(accessToken.getUserId(), request);
    }

    @Override
    public void editOperations(List<Grant> grants, SharedObjectType type, UUID id, UUID sharedWith, AccessToken accessToken) {
        log.info("{} wants to edit operations of user {} to {} {}", accessToken.getUserId(), sharedWith, type, id);

        editSharedOperationsService.editSharedOperations(accessToken.getUserId(), sharedWith, type, id, grants);
    }

    @Override
    public void unshare(SharedObjectType type, UUID id, UUID sharedWith, AccessToken accessToken) {
        log.info("{} wants to unshare {} {} with {}", accessToken.getUserId(), type, id, sharedWith);

        unshareObjectService.unshareObject(accessToken.getUserId(), sharedWith, type, id);
    }
}
