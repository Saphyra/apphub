package com.github.saphyra.apphub.service.community.group.service.group_member;

import com.github.saphyra.apphub.api.community.model.response.SearchResultItem;
import com.github.saphyra.apphub.api.user.model.response.AccountResponse;
import com.github.saphyra.apphub.service.community.common.AccountClientProxy;
import com.github.saphyra.apphub.service.community.common.AccountResponseToSearchResultItemConverter;
import com.github.saphyra.apphub.service.community.group.dao.group.Group;
import com.github.saphyra.apphub.service.community.group.dao.group.GroupDao;
import com.github.saphyra.apphub.service.community.group.dao.member.GroupMember;
import com.github.saphyra.apphub.service.community.group.dao.member.GroupMemberDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class GroupMemberCandidateQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID GROUP_ID = UUID.randomUUID();
    private static final String QUERY = "query";
    private static final UUID CANDIDATE_USER_ID = UUID.randomUUID();

    @Mock
    private GroupDao groupDao;

    @Mock
    private GroupMemberDao groupMemberDao;

    @Mock
    private GroupMemberCandidateCollector groupMemberCandidateCollector;

    @Mock
    private AccountClientProxy accountClientProxy;

    @Mock
    private AccountResponseToSearchResultItemConverter accountResponseToSearchResultItemConverter;

    @InjectMocks
    private GroupMemberCandidateQueryService underTest;

    @Mock
    private Group group;

    @Mock
    private GroupMember groupMember;

    @Mock
    private AccountResponse accountResponse1;

    @Mock
    private AccountResponse accountResponse2;

    @Mock
    private SearchResultItem searchResultItem;


    @Test
    public void forbiddenOperation() {
        given(groupMemberDao.findByGroupIdAndUserIdValidated(GROUP_ID, USER_ID)).willReturn(groupMember);
        given(groupMember.isCanInvite()).willReturn(false);

        Throwable ex = catchThrowable(() -> underTest.search(USER_ID, GROUP_ID, QUERY));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void search() {
        given(groupMemberDao.findByGroupIdAndUserIdValidated(GROUP_ID, USER_ID)).willReturn(groupMember);
        given(groupMember.isCanInvite()).willReturn(true);
        given(groupDao.findByIdValidated(GROUP_ID)).willReturn(group);
        given(groupMemberCandidateCollector.getCandidateUserIds(group)).willReturn(List.of(CANDIDATE_USER_ID));
        given(accountClientProxy.search(QUERY)).willReturn(List.of(accountResponse1, accountResponse2));
        given(accountResponse1.getUserId()).willReturn(CANDIDATE_USER_ID);
        given(accountResponse2.getUserId()).willReturn(UUID.randomUUID());
        given(accountResponseToSearchResultItemConverter.convert(accountResponse1)).willReturn(searchResultItem);

        List<SearchResultItem> result = underTest.search(USER_ID, GROUP_ID, QUERY);

        assertThat(result).containsExactly(searchResultItem);
    }
}