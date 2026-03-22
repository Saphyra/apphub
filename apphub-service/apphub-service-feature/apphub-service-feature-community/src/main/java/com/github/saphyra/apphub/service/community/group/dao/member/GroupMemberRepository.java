package com.github.saphyra.apphub.service.community.group.dao.member;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

interface GroupMemberRepository extends CrudRepository<GroupMemberEntity, String> {
    void deleteByUserId(String userId);

    void deleteByGroupId(String groupId);

    List<GroupMemberEntity> getByUserId(String userId);

    List<GroupMemberEntity> getByGroupId(String groupId);

    Optional<GroupMemberEntity> findByGroupIdAndUserId(String groupId, String userId);
}
