package com.github.saphyra.apphub.lib.common_util;

public class Random {
    public int randInt(int min, int max) {
        Double rand = Math.floor(Math.random() * (max - min + 1) + min);
        return rand.intValue();
    }

    public int randInt(double min, double max) {
        Double rand = Math.floor(Math.random() * (max - min + 1) + min);
        return rand.intValue();
    }

    public double randDouble(int min, int max) {
        return Math.random() * (max - min + 1) + min;
    }

    public double randDouble(double min, double max) {
        return Math.random() * (max - min + 1) + min;
    }

    public long randLong(long min, long max) {
        Double rand = Math.floor(Math.random() * (max - min + 1) + min);
        return rand.longValue();
    }

    public long randLong(int min, int max) {
        Double rand = Math.floor(Math.random() * (max - min + 1) + min);
        return rand.longValue();
    }

    public long randLong(double min, double max) {
        Double rand = Math.floor(Math.random() * (max - min + 1) + min);
        return rand.longValue();
    }

    public boolean randBoolean() {
        return randInt(0, 1) == 1;
    }
}
