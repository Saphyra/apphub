package com.github.saphyra.apphub.service.community.group.service.group_member;

import com.github.saphyra.apphub.api.community.model.response.group.GroupMemberRoleRequest;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class GroupMemberRoleRequestValidatorTest {
    private final GroupMemberRoleRequestValidator underTest = new GroupMemberRoleRequestValidator();

    @Mock
    private GroupMemberRoleRequest request;

    @Before
    public void setUp() {
        given(request.getCanInvite()).willReturn(true);
        given(request.getCanKick()).willReturn(true);
        given(request.getCanModifyRoles()).willReturn(true);
    }

    @Test
    public void nullCanInvite() {
        given(request.getCanInvite()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "canInvite", "must not be null");
    }

    @Test
    public void nullCanKick() {
        given(request.getCanKick()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "canKick", "must not be null");
    }

    @Test
    public void nullCanModifyRoles() {
        given(request.getCanModifyRoles()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "canModifyRoles", "must not be null");
    }

    @Test
    public void valid() {
        underTest.validate(request);
    }
}