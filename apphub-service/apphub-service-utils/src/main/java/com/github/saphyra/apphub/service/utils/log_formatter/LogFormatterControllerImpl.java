package com.github.saphyra.apphub.service.utils.log_formatter;

import com.github.saphyra.apphub.api.utils.model.request.SetLogParameterVisibilityRequest;
import com.github.saphyra.apphub.api.utils.model.response.LogParameterVisibilityResponse;
import com.github.saphyra.apphub.api.utils.server.LogFormatterController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.utils.log_formatter.repository.LogParameterVisibility;
import com.github.saphyra.apphub.service.utils.log_formatter.repository.LogParameterVisibilityDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LogFormatterControllerImpl implements LogFormatterController {
    private final IdGenerator idGenerator;
    private final LogParameterVisibilityDao dao;

    @Override
    public List<LogParameterVisibilityResponse> getVisibility(List<String> parameters, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the visibility of {} number of parameters", accessTokenHeader.getUserId(), parameters.size());
        Map<String, LogParameterVisibility> visibilities = dao.getByUserId(accessTokenHeader.getUserId())
            .stream()
            .collect(Collectors.toMap(LogParameterVisibility::getParameter, Function.identity()));

        return parameters.stream()
            .map(parameter -> getOrCreate(parameter, visibilities, accessTokenHeader.getUserId()))
            .map(this::convert)
            .collect(Collectors.toList());
    }

    private LogParameterVisibility getOrCreate(String parameter, Map<String, LogParameterVisibility> visibilities, UUID userId) {
        return Optional.ofNullable(visibilities.get(parameter))
            .orElseGet(() -> {
                LogParameterVisibility visibility = LogParameterVisibility.builder()
                    .id(idGenerator.randomUUID())
                    .userId(userId)
                    .parameter(parameter)
                    .visible(true)
                    .build();
                dao.save(visibility);
                return visibility;
            });
    }

    private LogParameterVisibilityResponse convert(LogParameterVisibility logParameterVisibility) {
        return LogParameterVisibilityResponse.builder()
            .id(logParameterVisibility.getId())
            .parameter(logParameterVisibility.getParameter())
            .visibility(logParameterVisibility.isVisible())
            .build();
    }

    @Override
    public void setVisibility(SetLogParameterVisibilityRequest request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to update visibility of id {}", accessTokenHeader.getUserId(), request.getId());

        dao.findById(request.getId())
            .ifPresent(logParameterVisibility -> {
                logParameterVisibility.setVisible(request.isVisible());
                dao.save(logParameterVisibility);
                log.info("Visibility updated for id {}.", logParameterVisibility.getId());
            });
    }
}
