package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool_type;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface ToolTypeRepository extends CrudRepository<ToolTypeEntity, String> {
    void deleteByUserId(String userId);

    List<ToolTypeEntity> getByUserId(String userId);

    void deleteByUserIdAndToolTypeId(String userId, String toolTypeId);
}
