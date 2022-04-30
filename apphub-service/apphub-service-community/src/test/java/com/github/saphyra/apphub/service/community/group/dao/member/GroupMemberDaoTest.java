package com.github.saphyra.apphub.service.community.group.dao.member;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GroupMemberDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";
    private static final UUID GROUP_ID = UUID.randomUUID();
    private static final String GROUP_ID_STRING = "group-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private GroupMemberRepository repository;

    @Mock
    private GroupMemberConverter converter;

    @InjectMocks
    private GroupMemberDao underTest;

    @Mock
    private GroupMember groupMember;

    @Mock
    private GroupMemberEntity entity;

    @Test
    public void deleteByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        underTest.deleteByUserId(USER_ID);

        verify(repository).deleteByUserId(USER_ID_STRING);
    }

    @Test
    public void deleteByGroupId() {
        given(uuidConverter.convertDomain(GROUP_ID)).willReturn(GROUP_ID_STRING);

        underTest.deleteByGroupId(GROUP_ID);

        verify(repository).deleteByGroupId(GROUP_ID_STRING);
    }
}