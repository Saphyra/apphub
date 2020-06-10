package com.github.saphyra.apphub.integration.frontend.model.modules;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ModuleLocation {
    MANAGE_ACCOUNT("accounts", "account");

    private final String categoryId;
    private final String moduleId;
}
