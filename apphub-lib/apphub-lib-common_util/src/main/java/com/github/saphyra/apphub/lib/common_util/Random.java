package com.github.saphyra.apphub.lib.common_util;

import com.github.saphyra.apphub.lib.common_domain.Range;

public class Random {
    public int randInt(int min, int max) {
        double rand = Math.floor(randDouble() * (max - min + 1) + min);
        return (int) rand;
    }

    public int randInt(double min, double max) {
        double rand = Math.floor(randDouble() * (max - min + 1) + min);
        return (int) rand;
    }

    public double randDouble(int min, int max) {
        return Random.this.randDouble() * (max - min + 1) + min;
    }

    public double randDouble() {
        return Math.random();
    }

    public double randDouble(double min, double max) {
        return randDouble() * (max - min + 1) + min;
    }

    public long randLong(long min, long max) {
        Double rand = Math.floor(randDouble() * (max - min + 1) + min);
        return rand.longValue();
    }

    public long randLong(int min, int max) {
        Double rand = Math.floor(randDouble() * (max - min + 1) + min);
        return rand.longValue();
    }

    public long randLong(double min, double max) {
        Double rand = Math.floor(randDouble() * (max - min + 1) + min);
        return rand.longValue();
    }

    public boolean randBoolean() {
        return randInt(0, 1) == 1;
    }

    public int randInt(Range<Integer> range) {
        return randInt(range.getMin(), range.getMax());
    }

    public double randDouble(Range<Double> range) {
        return randDouble(range.getMin(), range.getMax());
    }
}
