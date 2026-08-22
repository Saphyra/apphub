package com.github.saphyra.apphub.service.feature.calendar.domain.share.service;

import com.github.saphyra.apphub.api.etc.user.client.AccountClient;
import com.github.saphyra.apphub.api.feature.calendar.model.request.ShareObjectRequest;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.PrincipalType;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.service.type.SharedObjectServiceProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class ShareObjectRequestValidator {
    private final SharedObjectServiceProvider sharedObjectServiceProvider;
    private final AlmDao almDao;
    private final AccountClient accountClient;

    void validate(UUID userId, ShareObjectRequest request) {
        ValidationUtil.notNull(request.getSharedWith(), "sharedWith");
        ValidationUtil.notNull(request.getOwner(), "owner");
        ValidationUtil.notNull(request.getObjectId(), "objectId");
        ValidationUtil.notNull(request.getType(), "type");
        ValidationUtil.doesNotContainNull(request.getGrants(), "grants");
        ValidationUtil.notEmpty(request.getGrants(), "grants");
        ValidationUtil.notNull(request.getParent(), "parent");

        if (userId.equals(request.getSharedWith())) {
            throw ExceptionFactory.invalidParam("sharedWith", "must not be self");
        }

        if(!accountClient.userExists(request.getSharedWith())){
            throw ExceptionFactory.notFound("User not found by id " + request.getSharedWith());
        }

        if (!sharedObjectServiceProvider.getForType(request.getType()).exists(request.getParent(), request.getObjectId())) {
            throw ExceptionFactory.notFound(request.getType() + " not found with id " + request.getObjectId() + " for parent " + request.getParent());
        }

        if (almDao.findForObject(request.getSharedWith(), PrincipalType.USER, request.getObjectId(), request.getType()).isPresent()) {
            throw ExceptionFactory.notLoggedException(HttpStatus.BAD_REQUEST, ErrorCode.ALREADY_EXISTS);
        }
    }
}
