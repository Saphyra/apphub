package com.github.saphyra.apphub.lib.sql_builder.condition;

public class TrueCondition implements Condition {
    @Override
    public String get() {
        return "true";
    }
}
