package com.github.saphyra.apphub.integration.frontend.model.modules;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ModuleLocation {
    ADMIN_PANEL("accounts", "admin-panel"),
    MANAGE_ACCOUNT("accounts", "account"),
    NOTEBOOK("office", "notebook");

    private final String categoryId;
    private final String moduleId;
}
