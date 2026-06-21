package com.github.saphyra.apphub.api.feature.task_manager.server;

import com.github.saphyra.apphub.api.feature.task_manager.model.TaskManagerEndpoints;
import com.github.saphyra.apphub.api.feature.task_manager.model.invitation.InvitationResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

//TODO API test
//TODO role protection test
public interface InvitationController {
    @GetMapping(TaskManagerEndpoints.TASK_MANAGER_GET_INVITATIONS)
    List<InvitationResponse> getInvitations(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessToken accessToken);

    @PostMapping(TaskManagerEndpoints.TASK_MANAGER_ACCEPT_INVITATION)
    void acceptInvitation(@PathVariable("organizationId") UUID organizationId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessToken accessToken);

    @DeleteMapping(TaskManagerEndpoints.TASK_MANAGER_REJECT_INVITATION)
    void rejectInvitation(@PathVariable("organizationId") UUID organizationId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessToken accessToken);
}
