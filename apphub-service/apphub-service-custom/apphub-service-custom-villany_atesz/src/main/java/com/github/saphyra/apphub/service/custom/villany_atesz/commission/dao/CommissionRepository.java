package com.github.saphyra.apphub.service.custom.villany_atesz.commission.dao;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface CommissionRepository extends CrudRepository<CommissionEntity, String> {
    List<CommissionEntity> getByUserId(String userId);

    void deleteByUserId(String userId);
}
