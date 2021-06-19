package com.github.saphyra.apphub.service.user.disabled_role;

import com.github.saphyra.apphub.api.user.model.response.DisabledRoleResponse;
import com.github.saphyra.apphub.api.user.server.DisabledRoleController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.user.common.CheckPasswordService;
import com.github.saphyra.apphub.service.user.disabled_role.dao.DisabledRoleEntity;
import com.github.saphyra.apphub.service.user.disabled_role.dao.DisabledRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.apache.commons.lang3.StringUtils.isBlank;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DisabledRoleControllerImpl implements DisabledRoleController {
    private final DisabledRoleRepository repository;
    private final DisabledRoleProperties properties;
    private final CheckPasswordService checkPasswordService;

    @Override
    public void disableRole(OneParamRequest<String> password, String role, AccessTokenHeader accessTokenHeader) {
        if (!properties.getRolesCanBeDisabled().contains(role)) {
            throw ExceptionFactory.invalidParam("role", "unknown or cannot be disabled");
        }

        checkPasswordService.checkPassword(accessTokenHeader.getUserId(), password.getValue());

        repository.save(new DisabledRoleEntity(role));
    }

    @Override
    public void enableRole(OneParamRequest<String> password, String role, AccessTokenHeader accessTokenHeader) {
        if (isBlank(role)) {
            throw ExceptionFactory.invalidParam("role", "must not be null or blank");
        }

        checkPasswordService.checkPassword(accessTokenHeader.getUserId(), password.getValue());

        if (repository.existsById(role)) {
            repository.deleteById(role);
        }
    }

    @Override
    public List<DisabledRoleResponse> getDisabledRoles() {
        List<String> disabledRoles = StreamSupport.stream(repository.findAll().spliterator(), false)
            .map(DisabledRoleEntity::getRole)
            .collect(Collectors.toList());

        return properties.getRolesCanBeDisabled()
            .stream()
            .map(role -> DisabledRoleResponse.builder()
                .role(role)
                .disabled(disabledRoles.contains(role))
                .build()
            ).collect(Collectors.toList());
    }
}
