package com.github.saphyra.apphub.service.notebook.dao.pin_group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Data
@Builder
class PinGroupEntity {
    private String userId;
    private String pinGroupId;
    private String pinGroupName; //Encrypted
    private Long lastOpened;
    private String listItemIds; //JSON Set
}
