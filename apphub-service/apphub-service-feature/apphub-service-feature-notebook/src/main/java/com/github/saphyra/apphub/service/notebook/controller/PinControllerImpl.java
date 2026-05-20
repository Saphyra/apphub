package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.feature.notebook.model.pin.PinGroupResponse;
import com.github.saphyra.apphub.api.feature.notebook.model.response.NotebookView;
import com.github.saphyra.apphub.api.feature.notebook.server.PinController;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.notebook.service.pin.PinService;
import com.github.saphyra.apphub.service.notebook.service.pin.group.PinGroupCreationService;
import com.github.saphyra.apphub.service.notebook.service.pin.group.PinGroupDeletionService;
import com.github.saphyra.apphub.service.notebook.service.pin.group.PinGroupItemService;
import com.github.saphyra.apphub.service.notebook.service.pin.group.PinGroupQueryService;
import com.github.saphyra.apphub.service.notebook.service.pin.group.PinGroupRenameService;
import com.github.saphyra.apphub.service.notebook.service.pin.group.PinGroupUpdateService;
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
    private final PinGroupUpdateService pinGroupUpdateService;

    @Override
    public void pinListItem(UUID listItemId, OneParamRequest<Boolean> pinned, AccessToken accessToken) {
        log.info("{} wants to change pin status of list item {}", accessToken.getUserId(), listItemId);
        pinService.pinListItem(accessToken.getUserId(), listItemId, pinned.getValue());
    }

    @Override
    public List<NotebookView> getPinnedItems(UUID pinGroupId, AccessToken accessToken) {
        log.info("{} wants wo query his pinned items", accessToken.getUserId());
        return pinService.getPinnedItems(accessToken.getUserId(), pinGroupId);
    }

    @Override
    public List<PinGroupResponse> createPinGroup(OneParamRequest<String> groupName, AccessToken accessToken) {
        log.info("{} wants to create a pin group.", accessToken.getUserId());

        pinGroupCreationService.create(accessToken.getUserId(), groupName.getValue());

        return getPinGroups(accessToken);
    }

    @Override
    public List<PinGroupResponse> renamePinGroup(OneParamRequest<String> groupName, UUID pinGroupId, AccessToken accessToken) {
        log.info("{} wants to rename pinGroup {}", accessToken.getUserId(), pinGroupId);

        pinGroupRenameService.rename(pinGroupId, groupName.getValue());

        return getPinGroups(accessToken);
    }

    @Override
    public List<PinGroupResponse> getPinGroups(AccessToken accessToken) {
        log.info("{} wants to know his pin groups.", accessToken.getUserId());

        return pinGroupQueryService.getPinGroups(accessToken.getUserId());
    }

    @Override
    public List<PinGroupResponse> deletePinGroup(UUID pinGroupId, AccessToken accessToken) {
        log.info("{} wants to delete pinGroup {}", accessToken.getUserId(), pinGroupId);

        pinGroupDeletionService.delete(pinGroupId);

        return getPinGroups(accessToken);
    }

    @Override
    public List<NotebookView> addItemToPinGroup(UUID pinGroupId, UUID listItemId, AccessToken accessToken) {
        log.info("{} wants to add listItem {} to pinGroup {}", accessToken.getUserId(), listItemId, pinGroupId);

        pinGroupItemService.addItem(accessToken.getUserId(), pinGroupId, listItemId);

        return getPinnedItems(pinGroupId, accessToken);
    }

    @Override
    public List<NotebookView> removeItemFromPinGroup(UUID pinGroupId, UUID listItemId, AccessToken accessToken) {
        log.info("{} wants to remove listItem {} from pinGroup {}", accessToken.getUserId(), listItemId, pinGroupId);

        pinGroupItemService.removeItem(accessToken.getUserId(), pinGroupId, listItemId);

        return getPinnedItems(pinGroupId, accessToken);
    }

    @Override
    public List<PinGroupResponse> pinGroupOpened(UUID pinGroupId, AccessToken accessToken) {
        log.info("{} opened pinGroup {}", accessToken.getUserId(), pinGroupId);

        pinGroupUpdateService.setLastOpened(pinGroupId);

        return getPinGroups(accessToken);
    }
}
