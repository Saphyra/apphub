package com.github.saphyra.apphub.ci.ui.utils;

import lombok.experimental.UtilityClass;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

@UtilityClass
public class ScreenSizeUtils {
    public int getScreenHeight() {
        GraphicsDevice screen = GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getDefaultScreenDevice();

        return screen.getDisplayMode().getHeight();
    }
}
