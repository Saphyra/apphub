package com.github.saphyra.apphub.service.feature.calendar.domain.share.service;

import com.github.saphyra.apphub.api.feature.calendar.model.request.ShareObjectRequest;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
//TODO unit test
class ShareObjectRequestValidator {
    void validate(UUID userId, ShareObjectRequest request) {
        ValidationUtil.notNull(request.getSharedWith(), "sharedWith");
        ValidationUtil.notNull(request.getOwner(), "owner");
        ValidationUtil.notNull(request.getObjectId(), "objectId");
        ValidationUtil.notNull(request.getType(), "type");
        ValidationUtil.doesNotContainNull(request.getGrants(), "operations");
        ValidationUtil.notNull(request.getParent(), "parent");

        //TODO validate object exists
        //TODo validate user has permission to share object
        //TODO validate not shared already
    }
}
