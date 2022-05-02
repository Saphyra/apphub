package com.github.saphyra.apphub.service.community.group.service;

import com.github.saphyra.apphub.api.community.model.response.group.GroupListResponse;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.community.group.dao.group.Group;
import com.github.saphyra.apphub.service.community.group.dao.group.GroupDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class RenameGroupService {
    private final GroupNameValidator groupNameValidator;
    private final GroupDao groupDao;
    private final GroupToResponseConverter groupToResponseConverter;

    public GroupListResponse rename(UUID userId, UUID groupId, String groupName) {
        groupNameValidator.validate(groupName);

        Group group = groupDao.findByIdValidated(groupId);

        if (!group.getOwnerId().equals(userId)) {
            throw ExceptionFactory.forbiddenOperation(userId + " must not rename group " + groupId);
        }

        group.setName(groupName);
        groupDao.save(group);

        return groupToResponseConverter.convert(group);
    }
}
