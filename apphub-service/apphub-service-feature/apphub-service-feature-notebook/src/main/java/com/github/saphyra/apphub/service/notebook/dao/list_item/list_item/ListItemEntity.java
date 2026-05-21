package com.github.saphyra.apphub.service.notebook.dao.list_item.list_item;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
class ListItemEntity {
    private String listItemId;
    private String userId;
    private String parent;
    private String type;
    private String title;
    private String pinned;
    private String archived;
    private String data;
}
