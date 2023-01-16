package com.github.saphyra.apphub.service.community.group.service.group_member;

import com.github.saphyra.apphub.api.community.model.response.group.GroupMemberRoleRequest;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class GroupMemberRoleRequestValidatorTest {
    private final GroupMemberRoleRequestValidator underTest = new GroupMemberRoleRequestValidator();

    @Mock
    private GroupMemberRoleRequest request;

    @BeforeEach
    public void setUp() {

    }

    @Test
    public void nullCanInvite() {
        given(request.getCanInvite()).willReturn(true);

        given(request.getCanInvite()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "canInvite", "must not be null");
    }

    @Test
    public void nullCanKick() {
        given(request.getCanInvite()).willReturn(true);
        given(request.getCanKick()).willReturn(true);

        given(request.getCanKick()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "canKick", "must not be null");
    }

    @Test
    public void nullCanModifyRoles() {
        given(request.getCanInvite()).willReturn(true);
        given(request.getCanKick()).willReturn(true);
        given(request.getCanModifyRoles()).willReturn(true);

        given(request.getCanModifyRoles()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "canModifyRoles", "must not be null");
    }

    @Test
    public void valid() {
        given(request.getCanInvite()).willReturn(true);
        given(request.getCanKick()).willReturn(true);
        given(request.getCanModifyRoles()).willReturn(true);

        underTest.validate(request);
    }
}