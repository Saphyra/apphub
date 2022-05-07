package com.github.saphyra.apphub.service.community.group.dao.member;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GroupMemberDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";
    private static final UUID GROUP_ID = UUID.randomUUID();
    private static final String GROUP_ID_STRING = "group-id";
    private static final UUID GROUP_MEMBER_ID = UUID.randomUUID();
    private static final String GROUP_MEMBER_ID_STRING = "group-member-id";

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

    @Test
    public void getByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.getByUserId(USER_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(groupMember));

        List<GroupMember> result = underTest.getByUserId(USER_ID);

        assertThat(result).containsExactly(groupMember);
    }

    @Test
    public void getByGroupId() {
        given(uuidConverter.convertDomain(GROUP_ID)).willReturn(GROUP_ID_STRING);
        given(repository.getByGroupId(GROUP_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(groupMember));

        List<GroupMember> result = underTest.getByGroupId(GROUP_ID);

        assertThat(result).containsExactly(groupMember);
    }

    @Test
    public void findByGroupIdAndUserIdValidated_notFound() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(GROUP_ID)).willReturn(GROUP_ID_STRING);
        given(repository.findByGroupIdAndUserId(GROUP_ID_STRING, USER_ID_STRING)).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.findByGroupIdAndUserIdValidated(GROUP_ID, USER_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    public void findByGroupIdAndUserIdValidated() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(GROUP_ID)).willReturn(GROUP_ID_STRING);
        given(repository.findByGroupIdAndUserId(GROUP_ID_STRING, USER_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(groupMember));

        GroupMember result = underTest.findByGroupIdAndUserIdValidated(GROUP_ID, USER_ID);

        assertThat(result).isEqualTo(groupMember);
    }

    @Test
    public void findByIdValidated() {
        given(uuidConverter.convertDomain(GROUP_MEMBER_ID)).willReturn(GROUP_MEMBER_ID_STRING);
        given(repository.findById(GROUP_MEMBER_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(groupMember));

        GroupMember result = underTest.findByIdValidated(GROUP_MEMBER_ID);

        assertThat(result).isEqualTo(groupMember);
    }

    @Test
    public void findByIdValidated_notFound() {
        given(uuidConverter.convertDomain(GROUP_MEMBER_ID)).willReturn(GROUP_MEMBER_ID_STRING);
        given(repository.findById(GROUP_MEMBER_ID_STRING)).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.findByIdValidated(GROUP_MEMBER_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    public void findById() {
        given(uuidConverter.convertDomain(GROUP_MEMBER_ID)).willReturn(GROUP_MEMBER_ID_STRING);
        given(repository.findById(GROUP_MEMBER_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(groupMember));

        Optional<GroupMember> result = underTest.findById(GROUP_MEMBER_ID);

        assertThat(result).contains(groupMember);
    }
}