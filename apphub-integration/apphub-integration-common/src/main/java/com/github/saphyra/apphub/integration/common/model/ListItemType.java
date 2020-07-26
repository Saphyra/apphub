package com.github.saphyra.apphub.integration.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ListItemType {
    CATEGORY("category"),
    TABLE("table"),
    TEXT("text"),
    CHECKLIST("checklist"),
    CHECKLIST_TABLE("checklist-table"),
    LINK("link");

    private final String cssClass;
}
