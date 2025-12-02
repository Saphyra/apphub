package com.github.saphyra.apphub.lib.sql_builder;

public class TrueCondition implements Condition{
    @Override
    public String get() {
        return "true";
    }
}
