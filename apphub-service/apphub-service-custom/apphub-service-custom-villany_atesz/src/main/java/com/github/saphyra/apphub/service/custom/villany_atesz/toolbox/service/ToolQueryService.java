package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolResponse;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.Tool;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.ToolDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ToolQueryService {
    private final ToolDao toolDao;

    public List<ToolResponse> getTools(UUID userId) {
        return toolDao.getByUserId(userId)
            .stream()
            .map(tool -> ToolResponse.builder()
                .toolId(tool.getToolId())
                .brand(tool.getBrand())
                .name(tool.getName())
                .cost(tool.getCost())
                .acquiredAt(tool.getAcquiredAt())
                .warrantyExpiresAt(tool.getWarrantyExpiresAt())
                .status(tool.getStatus())
                .scrappedAt(tool.getScrappedAt())
                .build())
            .collect(Collectors.toList());
    }

    public int getTotalValue(UUID userId) {
        return toolDao.getByUserId(userId)
            .stream()
            .mapToInt(Tool::getCost)
            .sum();
    }
}
