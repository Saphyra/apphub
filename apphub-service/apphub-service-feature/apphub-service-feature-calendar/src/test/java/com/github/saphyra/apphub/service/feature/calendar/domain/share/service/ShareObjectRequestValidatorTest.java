package com.github.saphyra.apphub.service.feature.calendar.domain.share.service;

import com.github.saphyra.apphub.api.etc.user.client.AccountClient;
import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.api.feature.calendar.model.request.ShareObjectRequest;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.Alm;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.PrincipalType;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.service.type.SharedObjectService;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.service.type.SharedObjectServiceProvider;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ShareObjectRequestValidatorTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID OWNER_ID = UUID.randomUUID();
    private static final UUID SHARED_WITH = UUID.randomUUID();
    private static final UUID OBJECT_ID = UUID.randomUUID();
    private static final UUID PARENT = UUID.randomUUID();
    private static final SharedObjectType TYPE = SharedObjectType.EVENT;

    @Mock
    private SharedObjectServiceProvider sharedObjectServiceProvider;

    @Mock
    private AlmDao almDao;

    @Mock
    private AccountClient accountClient;

    @InjectMocks
    private ShareObjectRequestValidator underTest;

    @Mock
    private SharedObjectService sharedObjectService;

    @Mock
    private Alm alm;

    @Test
    void valid() {
        ShareObjectRequest request = createValidRequest();

        given(sharedObjectServiceProvider.getForType(TYPE)).willReturn(sharedObjectService);
        given(sharedObjectService.exists(PARENT, OBJECT_ID)).willReturn(true);
        given(almDao.findForObject(SHARED_WITH, PrincipalType.USER, OBJECT_ID, TYPE)).willReturn(Optional.empty());
        given(accountClient.userExists(SHARED_WITH)).willReturn(true);

        underTest.validate(USER_ID, request);
    }

    @Test
    void nullSharedWith() {
        ShareObjectRequest request = createValidRequest();
        request.setSharedWith(null);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(USER_ID, request), "sharedWith", "must not be null");
    }

    @Test
    void nullOwner() {
        ShareObjectRequest request = createValidRequest();
        request.setOwner(null);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(USER_ID, request), "owner", "must not be null");
    }

    @Test
    void nullObjectId() {
        ShareObjectRequest request = createValidRequest();
        request.setObjectId(null);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(USER_ID, request), "objectId", "must not be null");
    }

    @Test
    void nullType() {
        ShareObjectRequest request = createValidRequest();
        request.setType(null);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(USER_ID, request), "type", "must not be null");
    }

    @Test
    void nullGrants() {
        ShareObjectRequest request = createValidRequest();
        request.setGrants(null);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(USER_ID, request), "grants", "must not be null");
    }

    @Test
    void grantsContainsNull() {
        ShareObjectRequest request = createValidRequest();
        request.setGrants(CollectionUtils.toList(Grant.DELETE, null, Grant.DELETE_CHILDREN));

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(USER_ID, request), "grants", "must not contain null values");
    }

    @Test
    void nullParent() {
        ShareObjectRequest request = createValidRequest();
        request.setParent(null);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(USER_ID, request), "parent", "must not be null");
    }

    @Test
    void notOwnRecord() {
        ShareObjectRequest request = createValidRequest();
        request.setOwner(UUID.randomUUID());

        ExceptionValidator.validateForbiddenOperation(() -> underTest.validate(USER_ID, request));
    }

    @Test
    void shareWithSelf() {
        ShareObjectRequest request = createValidRequest();
        request.setSharedWith(USER_ID);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(USER_ID, request), "sharedWith", "must not be self");
    }

    @Test
    void sharedWithDoesNotExist() {
        ShareObjectRequest request = createValidRequest();
        given(accountClient.userExists(SHARED_WITH)).willReturn(false);

        ExceptionValidator.validateNotFoundException(() -> underTest.validate(USER_ID, request));
    }

    @Test
    void objectDoesNotExist() {
        ShareObjectRequest request = createValidRequest();
        given(accountClient.userExists(SHARED_WITH)).willReturn(true);
        given(sharedObjectServiceProvider.getForType(TYPE)).willReturn(sharedObjectService);
        given(sharedObjectService.exists(PARENT, OBJECT_ID)).willReturn(false);

        ExceptionValidator.validateNotFoundException(() -> underTest.validate(USER_ID, request));
    }

    @Test
    void alreadyShared() {
        ShareObjectRequest request = createValidRequest();
        given(accountClient.userExists(SHARED_WITH)).willReturn(true);
        given(sharedObjectServiceProvider.getForType(TYPE)).willReturn(sharedObjectService);
        given(sharedObjectService.exists(PARENT, OBJECT_ID)).willReturn(true);
        given(almDao.findForObject(SHARED_WITH, PrincipalType.USER, OBJECT_ID, TYPE)).willReturn(Optional.of(alm));

        ExceptionValidator.validateNotLoggedException(() -> underTest.validate(USER_ID, request), HttpStatus.BAD_REQUEST, ErrorCode.ALREADY_EXISTS);
    }

    private ShareObjectRequest createValidRequest() {
        ShareObjectRequest request = new ShareObjectRequest();
        request.setSharedWith(SHARED_WITH);
        request.setOwner(USER_ID);
        request.setObjectId(OBJECT_ID);
        request.setType(TYPE);
        request.setParent(PARENT);
        request.setGrants(List.of(Grant.DELETE));
        return request;
    }
}