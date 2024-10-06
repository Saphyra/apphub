package com.github.saphyra.apphub.api.user.server;

import com.github.saphyra.apphub.api.user.model.ban.BanRequest;
import com.github.saphyra.apphub.api.user.model.ban.BanResponse;
import com.github.saphyra.apphub.api.user.model.ban.BanSearchResponse;
import com.github.saphyra.apphub.api.user.model.ban.BannedDetailsRequest;
import com.github.saphyra.apphub.api.user.model.ban.BannedDetailsResponse;
import com.github.saphyra.apphub.api.user.model.ban.MarkUserForDeletionRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.config.common.endpoints.UserEndpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

public interface BanController {
    /**
     * Removing a user's access to a specific role permanently, or for a specified time.
     * Checking the admin's password before any modification is saved
     */
    @PutMapping(UserEndpoints.ACCOUNT_BAN_USER)
    BanResponse banUser(@RequestBody BanRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    /**
     * Allowing the user to access a feature again
     * Checking the admin's password before any modification is saved
     */
    @DeleteMapping(UserEndpoints.ACCOUNT_REVOKE_BAN)
    BanResponse revokeBan(@RequestBody OneParamRequest<String> password, @PathVariable("banId") UUID banId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    /**
     * Actual bans of a specific user, including if the user account is marked for deletion
     */
    @GetMapping(UserEndpoints.ACCOUNT_GET_BANS)
    BanResponse getBans(@PathVariable("userId") UUID bannedUserId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    /**
     * Marking the user for deletion revokes its access to the system, and the account will be removed from the database after the specified time
     * Checking the admin's password before any modification is saved
     */
    @DeleteMapping(UserEndpoints.ACCOUNT_MARK_FOR_DELETION)
    BanResponse markUserForDeletion(@RequestBody MarkUserForDeletionRequest request, @PathVariable("userId") UUID deletedUserId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    /**
     * Cancelling the scheduled deletion. The user can access the system again.
     * Checking the admin's password before any modification is saved
     */
    @PostMapping(UserEndpoints.ACCOUNT_UNMARK_FOR_DELETION)
    BanResponse unmarkUserForDeletion(@PathVariable("userId") UUID deletedUserId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(UserEndpoints.ACCOUNT_BAN_SEARCH)
    List<BanSearchResponse> search(@RequestBody OneParamRequest<String> query, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(UserEndpoints.ACCOUNT_BAN_GET_DETAILS_FOR_ERROR_PAGE)
    BannedDetailsResponse getBannedDetails(@RequestBody BannedDetailsRequest request);

}
