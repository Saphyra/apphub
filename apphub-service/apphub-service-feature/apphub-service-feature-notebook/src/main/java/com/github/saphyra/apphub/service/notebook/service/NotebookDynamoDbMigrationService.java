package com.github.saphyra.apphub.service.notebook.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class NotebookDynamoDbMigrationService {
    @PostConstruct
    void migrate() {
        //TODO implement
    }
}
