package com.github.saphyra.apphub.service.user.ban.dao;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface BanRepository extends CrudRepository<BanEntity, String> {
    void deleteByUserId(String userId);

    List<BanEntity> getByUserId(String userId);
}
