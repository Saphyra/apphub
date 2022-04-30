package com.github.saphyra.apphub.service.community.group.dao.member;

import org.springframework.data.repository.CrudRepository;

interface GroupMemberRepository extends CrudRepository<GroupMemberEntity, String> {
    void deleteByUserId(String userId);

    void deleteByGroupId(String groupId);
}
