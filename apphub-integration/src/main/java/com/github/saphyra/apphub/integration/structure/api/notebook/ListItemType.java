package com.github.saphyra.apphub.integration.structure.api.notebook;

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
    LINK("link"),
    ONLY_TITLE("only-title");

    private final String selector;
}
