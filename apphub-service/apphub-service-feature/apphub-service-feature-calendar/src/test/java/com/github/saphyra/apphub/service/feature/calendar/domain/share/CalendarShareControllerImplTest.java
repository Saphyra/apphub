package com.github.saphyra.apphub.service.feature.calendar.domain.share;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.api.feature.calendar.model.request.ShareObjectRequest;
import com.github.saphyra.apphub.api.feature.calendar.model.response.SharedObjectResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.service.EditSharedOperationsService;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.service.ShareObjectService;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.service.SharedObjectQueryService;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.service.UnshareObjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CalendarShareControllerImplTest {
    private static final UUID ID = UUID.randomUUID();
    private static final UUID PARENT = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID SHARED_WITH = UUID.randomUUID();

    @Mock
    private SharedObjectQueryService sharedObjectQueryService;

    @Mock
    private ShareObjectService shareObjectService;

    @Mock
    private EditSharedOperationsService editSharedOperationsService;

    @Mock
    private UnshareObjectService unshareObjectService;

    @InjectMocks
    private CalendarShareControllerImpl underTest;

    @Mock
    private AccessToken accessToken;

    @Mock
    private SharedObjectResponse response;

    @Mock
    private ShareObjectRequest request;

    @Test
    void getSharedItem() {
        given(accessToken.getUserId()).willReturn(USER_ID);
        given(sharedObjectQueryService.getSharedItem(USER_ID, SharedObjectType.EVENT, ID, PARENT)).willReturn(response);

        assertThat(underTest.getSharedItem(SharedObjectType.EVENT, ID, PARENT, accessToken)).isEqualTo(response);
    }

    @Test
    void getOperations() {
        assertThat(underTest.getOperations(SharedObjectType.OCCURRENCE)).containsExactlyInAnyOrder(Grant.SEE, Grant.VIEW, Grant.EDIT, Grant.DELETE, Grant.SHARE, Grant.EDIT_OPERATIONS);
    }

    @Test
    void shareObject() {
        given(accessToken.getUserId()).willReturn(USER_ID);

        underTest.shareObject(request, accessToken);

        then(shareObjectService).should().share(USER_ID, request);
    }

    @Test
    void editOperations() {
        given(accessToken.getUserId()).willReturn(USER_ID);

        underTest.editOperations(List.of(Grant.DELETE), SharedObjectType.OCCURRENCE, ID, SHARED_WITH, accessToken);

        then(editSharedOperationsService).should().editSharedOperations(USER_ID, SHARED_WITH, SharedObjectType.OCCURRENCE, ID, Set.of(Grant.DELETE));
    }

    @Test
    void unshare() {
        given(accessToken.getUserId()).willReturn(USER_ID);

        underTest.unshare(SharedObjectType.OCCURRENCE, ID, SHARED_WITH, accessToken);

        then(unshareObjectService).should().unshareObject(USER_ID, SHARED_WITH, SharedObjectType.OCCURRENCE, ID);
    }
}