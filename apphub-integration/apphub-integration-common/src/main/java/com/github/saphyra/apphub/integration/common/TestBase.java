package com.github.saphyra.apphub.integration.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saphyra.apphub.integration.common.framework.DatabaseUtil;
import com.github.saphyra.util.ObjectMapperWrapper;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.sql.SQLException;
import java.util.Objects;

import static java.util.Objects.isNull;

@Slf4j
public class TestBase {
    public static final ObjectMapperWrapper OBJECT_MAPPER_WRAPPER = new ObjectMapperWrapper(new ObjectMapper());
    
    public static int SERVER_PORT;
    public static int DATABASE_PORT;

    @BeforeSuite(alwaysRun = true)
    public void setUpSuite() {
        SERVER_PORT = Integer.parseInt(Objects.requireNonNull(System.getProperty("serverPort"), "serverPort is null"));
        log.info("ServerPort: {}", SERVER_PORT);

        DATABASE_PORT = Integer.parseInt(Objects.requireNonNull(System.getProperty("databasePort"), "serverPort is null"));
        log.info("DatabasePort: {}", DATABASE_PORT);
    }

    @AfterSuite(alwaysRun = true)
    public void tearDownSuite() throws SQLException {
        if(!isNull(DatabaseUtil.CONNECTION)){
            DatabaseUtil.CONNECTION.close();
        }
    }
}
