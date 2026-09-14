package com.github.saphyra.apphub.service.feature.calendar.domain.share.service;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.api.feature.calendar.model.request.ShareObjectRequest;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.Alm;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmFactory;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.PrincipalType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ShareObjectServiceTest {
    private static final UUID SHARED_WITH = UUID.randomUUID();
    private static final UUID OBJECT_ID = UUID.randomUUID();
    private static final UUID OWNER = UUID.randomUUID();
    private static final UUID PARENT = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private ShareObjectRequestValidator shareObjectRequestValidator;

    @Mock
    private AlmFactory almFactory;

    @Mock
    private AlmDao almDao;

    @InjectMocks
    private ShareObjectService underTest;

    @Mock
    private Alm alm;

    @Test
    void share() {
        ShareObjectRequest request = ShareObjectRequest.builder()
            .sharedWith(SHARED_WITH)
            .objectId(OBJECT_ID)
            .type(SharedObjectType.EVENT)
            .owner(OWNER)
            .parent(PARENT)
            .grants(Set.of(Grant.DELETE))
            .build();
        given(almFactory.create(SHARED_WITH, PrincipalType.USER, OBJECT_ID, SharedObjectType.EVENT, OWNER, PARENT, Set.of(Grant.DELETE))).willReturn(alm);

        underTest.share(USER_ID, request);

        then(shareObjectRequestValidator).should().validate(USER_ID, request);
        then(almDao).should().save(alm);
    }
}