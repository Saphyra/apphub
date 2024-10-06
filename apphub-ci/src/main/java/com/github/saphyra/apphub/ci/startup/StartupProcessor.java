package com.github.saphyra.apphub.ci.startup;

import java.util.List;

public interface StartupProcessor {
    boolean canProcess(String arg);

    void process(List<String> args);
}
