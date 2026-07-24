package com.github.saphyra.apphub.api.feature.calendar.server;

import com.github.saphyra.apphub.api.feature.calendar.model.CalendarEndpoints;
import com.github.saphyra.apphub.api.feature.calendar.model.Operation;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.api.feature.calendar.model.request.ShareObjectRequest;
import com.github.saphyra.apphub.api.feature.calendar.model.response.SharedObjectResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

//TODO role protection test
//TODO API test
public interface CalendarShareController {
    @GetMapping(CalendarEndpoints.CALENDAR_GET_SHARED_ITEM)
    SharedObjectResponse getSharedItem(
        @PathVariable("type") SharedObjectType type,
        @PathVariable("id") UUID id,
        @RequestParam(name = "parent", required = false) UUID parent,
        @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessToken accessToken
    );

    @GetMapping(CalendarEndpoints.CALENDAR_GET_OPERATIONS)
    List<Operation> getOperations(@PathVariable("type") SharedObjectType type);

    @PutMapping(CalendarEndpoints.CALENDAR_SHARE_OBJECT)
    void shareObject(@RequestBody ShareObjectRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessToken accessToken);

    @PostMapping(CalendarEndpoints.CALENDAR_SHARE_EDIT_OPERATIONS)
    void editOperations(
        @RequestBody List<Operation> operations,
        @PathVariable("type") SharedObjectType type,
        @PathVariable("id") UUID id,
        @PathVariable("sharedWith") UUID sharedWith,
        @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessToken accessToken
    );

    @DeleteMapping(CalendarEndpoints.CALENDAR_UNSHARE)
    void unshare(
        @PathVariable("type") SharedObjectType type,
        @PathVariable("id") UUID id,
        @PathVariable("sharedWith") UUID sharedWith,
        @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessToken accessToken
    );
}
