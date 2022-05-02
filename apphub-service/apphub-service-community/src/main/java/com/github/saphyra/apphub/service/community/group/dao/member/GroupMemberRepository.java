package com.github.saphyra.apphub.service.community.group.dao.member;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

interface GroupMemberRepository extends CrudRepository<GroupMemberEntity, String> {
    void deleteByUserId(String userId);

    void deleteByGroupId(String groupId);

    //TODO unit test
    List<GroupMemberEntity> getByUserId(String userId);

    //TODO unit test
    List<GroupMemberEntity> getByGroupId(String groupId);

    //TODO unit test
    Optional<GroupMemberEntity> findByGroupIdAndUserId(String groupId, String userId);
}
