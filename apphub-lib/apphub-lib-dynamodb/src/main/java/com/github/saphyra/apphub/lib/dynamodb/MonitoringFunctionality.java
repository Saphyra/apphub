package com.github.saphyra.apphub.lib.dynamodb;

public interface MonitoringFunctionality {
    MonitoringFunctionality UNMONITORED = _ -> null;

    String assemble(String tableName);
}
