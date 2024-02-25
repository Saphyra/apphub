package com.github.saphyra.apphub.service.notebook.service.pin.group;

import com.github.saphyra.apphub.api.notebook.model.pin.PinGroupResponse;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.notebook.dao.pin.group.PinGroupDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class PinGroupQueryService {
    private final PinGroupDao pinGroupDao;
    private final DateTimeUtil dateTimeUtil;

    public List<PinGroupResponse> getPinGroups(UUID userId) {
        return pinGroupDao.getByUserId(userId)
            .stream()
            .map(pinGroup -> PinGroupResponse.builder()
                .pinGroupId(pinGroup.getPinGroupId())
                .pinGroupName(pinGroup.getPinGroupName())
                .lastOpened(dateTimeUtil.format(pinGroup.getLastOpened()))
                .build())
            .collect(Collectors.toList());
    }
}
