package com.github.saphyra.apphub.service.platform.storage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//TODO listen to user deletion
//TODO clean up StoredFiles without file uploaded
public class StorageApplication {
    public static void main(String[] args) {
        SpringApplication.run(StorageApplication.class, args);
    }
}
