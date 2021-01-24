package com.github.saphyra.apphub.service.utils.log_formatter.repository;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface LogParameterVisibilityRepository extends CrudRepository<LogParameterVisibilityEntity, String> {
    //TODO unit test
    List<LogParameterVisibilityEntity> getByUserId(String userId);
}
