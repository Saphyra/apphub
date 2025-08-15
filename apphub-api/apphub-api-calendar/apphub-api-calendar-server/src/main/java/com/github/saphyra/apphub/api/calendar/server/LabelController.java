package com.github.saphyra.apphub.api.calendar.server;

import com.github.saphyra.apphub.api.calendar.model.response.LabelResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.lib.config.common.endpoints.CalendarEndpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

//TODO Role protection test
//TODO API test
public interface LabelController {
    @PutMapping(CalendarEndpoints.CALENDAR_CREATE_LABEL)
    OneParamResponse<UUID> createLabel(@RequestBody OneParamRequest<String> label, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(CalendarEndpoints.CALENDAR_GET_LABELS)
    List<LabelResponse> getLabels(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(CalendarEndpoints.CALENDAR_GET_LABEL)
    LabelResponse getLabel(@PathVariable("labelId") UUID labelId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(CalendarEndpoints.CALENDAR_DELETE_LABEL)
    List<LabelResponse> deleteLabel(@PathVariable("labelId") UUID labelId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(CalendarEndpoints.CALENDAR_EDIT_LABEL)
    List<LabelResponse> editLabel(@RequestBody OneParamRequest<String> label, @PathVariable("labelId") UUID labelId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
