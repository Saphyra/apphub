package com.github.saphyra.apphub.service.notebook.dao.list_item.content;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
class ContentEntity {
    private String userId;
    private String listItemId;
    private Integer batchIndex;
    private String parentType;
    private String content;
}
