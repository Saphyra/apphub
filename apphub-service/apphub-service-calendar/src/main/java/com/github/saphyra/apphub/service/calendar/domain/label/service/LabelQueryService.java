package com.github.saphyra.apphub.service.calendar.domain.label.service;

import com.github.saphyra.apphub.api.calendar.model.response.LabelResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class LabelQueryService {
    public List<LabelResponse> getLabels(UUID eventId) {
        //TODO implement
        return null;
    }
}
