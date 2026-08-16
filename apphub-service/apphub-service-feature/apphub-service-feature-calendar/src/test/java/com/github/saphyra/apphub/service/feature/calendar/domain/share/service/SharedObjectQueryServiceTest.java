package com.github.saphyra.apphub.service.feature.calendar.domain.share.service;

import com.github.saphyra.apphub.api.etc.user.client.AccountClient;
import com.github.saphyra.apphub.api.etc.user.model.account.AccountResponse;
import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.api.feature.calendar.model.response.SharedObjectResponse;
import com.github.saphyra.apphub.api.feature.calendar.model.response.SharedWithResponse;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeenTestUtils;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.Alm;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.PrincipalType;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.service.type.SharedObject;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.service.type.SharedObjectService;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.service.type.SharedObjectServiceProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class SharedObjectQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final String TITLE = "title";
    private static final UUID SHARED_WITH = UUID.randomUUID();
    private static final String USERNAME = "username";
    private static final String EMAIL = "email";

    @Mock
    private SharedObjectServiceProvider sharedObjectServiceProvider;

    @Mock
    private AlmDao almDao;

    @Mock
    private AccountClient accountClient;

    @Spy
    private final ExecutorServiceBean executorServiceBean = ExecutorServiceBeenTestUtils.create(mock(ErrorReporterService.class));

    @InjectMocks
    private SharedObjectQueryService underTest;

    @Mock
    private SharedObjectService sharedObjectService;

    @Mock
    private Alm alm;

    @Mock
    private AccountResponse accountResponse;

    @Test
    void getSharedItem_ownRecord() {
        given(sharedObjectServiceProvider.getForType(SharedObjectType.EVENT)).willReturn(sharedObjectService);
        given(almDao.findForObject(USER_ID, PrincipalType.USER, EVENT_ID, SharedObjectType.EVENT)).willReturn(Optional.empty());

        SharedObject sharedObject = new SharedObject(EVENT_ID, USER_ID, USER_ID, TITLE);
        given(sharedObjectService.getSharedObject(USER_ID, EVENT_ID, USER_ID)).willReturn(sharedObject);

        given(almDao.getByObject(EVENT_ID, SharedObjectType.EVENT)).willReturn(List.of(alm));
        given(alm.getPrincipal()).willReturn(SHARED_WITH);
        given(accountClient.getAccountInternal(SHARED_WITH)).willReturn(accountResponse);
        given(accountResponse.getUserId()).willReturn(SHARED_WITH);
        given(accountResponse.getUsername()).willReturn(USERNAME);
        given(accountResponse.getEmail()).willReturn(EMAIL);
        given(alm.getGrants()).willReturn(List.of(Grant.DELETE));

        SharedObjectResponse result = underTest.getSharedItem(USER_ID, SharedObjectType.EVENT, EVENT_ID, USER_ID);

        assertThat(result)
            .returns(EVENT_ID, SharedObjectResponse::getObjectId)
            .returns(TITLE, SharedObjectResponse::getName)
            .returns(USER_ID, SharedObjectResponse::getOwner)
            .returns(USER_ID, SharedObjectResponse::getParent);

        assertThat(result.getSharedWith())
            .singleElement()
            .returns(SHARED_WITH, SharedWithResponse::getUserId)
            .returns(USERNAME, SharedWithResponse::getUsername)
            .returns(EMAIL, SharedWithResponse::getEmail)
            .returns(List.of(Grant.DELETE), SharedWithResponse::getGrants);
    }

    @Test
    void getSharedItem_sharedRecord() {
        given(sharedObjectServiceProvider.getForType(SharedObjectType.EVENT)).willReturn(sharedObjectService);
        given(almDao.findForObject(USER_ID, PrincipalType.USER, EVENT_ID, SharedObjectType.EVENT)).willReturn(Optional.of(alm));
        given(alm.getOwner()).willReturn(USER_ID);

        SharedObject sharedObject = new SharedObject(EVENT_ID, USER_ID, USER_ID, TITLE);
        given(sharedObjectService.getSharedObject(USER_ID, EVENT_ID, USER_ID)).willReturn(sharedObject);

        given(almDao.getByObject(EVENT_ID, SharedObjectType.EVENT)).willReturn(List.of(alm));
        given(alm.getPrincipal()).willReturn(SHARED_WITH);
        given(accountClient.getAccountInternal(SHARED_WITH)).willReturn(accountResponse);
        given(accountResponse.getUserId()).willReturn(SHARED_WITH);
        given(accountResponse.getUsername()).willReturn(USERNAME);
        given(accountResponse.getEmail()).willReturn(EMAIL);
        given(alm.getGrants()).willReturn(List.of(Grant.DELETE));

        SharedObjectResponse result = underTest.getSharedItem(USER_ID, SharedObjectType.EVENT, EVENT_ID, USER_ID);

        assertThat(result)
            .returns(EVENT_ID, SharedObjectResponse::getObjectId)
            .returns(TITLE, SharedObjectResponse::getName)
            .returns(USER_ID, SharedObjectResponse::getOwner)
            .returns(USER_ID, SharedObjectResponse::getParent);

        assertThat(result.getSharedWith())
            .singleElement()
            .returns(SHARED_WITH, SharedWithResponse::getUserId)
            .returns(USERNAME, SharedWithResponse::getUsername)
            .returns(EMAIL, SharedWithResponse::getEmail)
            .returns(List.of(Grant.DELETE), SharedWithResponse::getGrants);
    }
}