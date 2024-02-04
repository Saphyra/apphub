package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.pin.PinGroupResponse;
import com.github.saphyra.apphub.api.notebook.model.response.NotebookView;
import com.github.saphyra.apphub.api.notebook.server.PinController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.notebook.service.pin.PinService;
import com.github.saphyra.apphub.service.notebook.service.pin.group.PinGroupCreationService;
import com.github.saphyra.apphub.service.notebook.service.pin.group.PinGroupDeletionService;
import com.github.saphyra.apphub.service.notebook.service.pin.group.PinGroupItemService;
import com.github.saphyra.apphub.service.notebook.service.pin.group.PinGroupQueryService;
import com.github.saphyra.apphub.service.notebook.service.pin.group.PinGroupRenameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PinControllerImpl implements PinController {
    private final PinService pinService;
    private final PinGroupQueryService pinGroupQueryService;
    private final PinGroupCreationService pinGroupCreationService;
    private final PinGroupRenameService pinGroupRenameService;
    private final PinGroupDeletionService pinGroupDeletionService;
    private final PinGroupItemService pinGroupItemService;

    @Override
    public void pinListItem(UUID listItemId, OneParamRequest<Boolean> pinned, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to change pin status of list item {}", accessTokenHeader.getUserId(), listItemId);
        pinService.pinListItem(listItemId, pinned.getValue());
    }

    @Override
    public List<NotebookView> getPinnedItems(UUID pinGroupId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants wo query his pinned items", accessTokenHeader.getUserId());
        return pinService.getPinnedItems(accessTokenHeader.getUserId(), pinGroupId);
    }

    @Override
    public List<PinGroupResponse> createPinGroup(OneParamRequest<String> groupName, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to create a pin group.", accessTokenHeader.getUserId());

        pinGroupCreationService.create(accessTokenHeader.getUserId(), groupName.getValue());

        return getPinGroups(accessTokenHeader);
    }

    @Override
    public List<PinGroupResponse> renamePinGroup(OneParamRequest<String> groupName, UUID pinGroupId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to rename pinGroup {}", accessTokenHeader.getUserId(), pinGroupId);

        pinGroupRenameService.rename(pinGroupId, groupName.getValue());

        return getPinGroups(accessTokenHeader);
    }

    @Override
    public List<PinGroupResponse> getPinGroups(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know his pin groups.", accessTokenHeader.getUserId());

        return pinGroupQueryService.getPinGroups(accessTokenHeader.getUserId());
    }

    @Override
    public List<PinGroupResponse> deletePinGroup(UUID pinGroupId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete pinGroup {}", accessTokenHeader.getUserId(), pinGroupId);

        pinGroupDeletionService.delete(pinGroupId);

        return getPinGroups(accessTokenHeader);
    }

    @Override
    public List<NotebookView> addItemToPinGroup(UUID pinGroupId, UUID listItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to add listItem {} to pinGroup {}", accessTokenHeader.getUserId(), listItemId, pinGroupId);

        pinGroupItemService.addItem(accessTokenHeader.getUserId(), pinGroupId, listItemId);

        return getPinnedItems(pinGroupId, accessTokenHeader);
    }

    @Override
    public List<NotebookView> removeItemFromPinGroup(UUID pinGroupId, UUID listItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to remove listItem {} from pinGroup {}", accessTokenHeader.getUserId(), listItemId, pinGroupId);

        pinGroupItemService.removeItem(accessTokenHeader.getUserId(), pinGroupId, listItemId);

        return getPinnedItems(pinGroupId, accessTokenHeader);
    }
}
