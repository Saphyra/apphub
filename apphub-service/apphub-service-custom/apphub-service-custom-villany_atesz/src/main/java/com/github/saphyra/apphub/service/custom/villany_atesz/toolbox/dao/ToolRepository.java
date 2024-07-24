package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface ToolRepository extends CrudRepository<ToolEntity, String> {
    void deleteByUserId(String userId);

    List<ToolEntity> getByUserId(String userId);

    void deleteByUserIdAndToolId(String userId, String toolId);
}
