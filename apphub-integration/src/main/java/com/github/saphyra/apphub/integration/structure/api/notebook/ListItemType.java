package com.github.saphyra.apphub.integration.structure.api.notebook;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ListItemType {
    CATEGORY("category", "category"),
    TABLE("table", "table"),
    TEXT("text", "text"),
    CHECKLIST("checklist", "checklist"),
    CHECKLIST_TABLE("checklist-table", "checklist_table"),
    LINK("link", "link"),
    ONLY_TITLE("only-title", "only_title");

    private final String selector;
    private final String clazz;
}
