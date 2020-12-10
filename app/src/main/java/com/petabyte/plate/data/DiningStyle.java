package com.petabyte.plate.data;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum DiningStyle {
    KOREAN_DINING("한식 다이닝"),
    JAPANESE_DINING("일식 다이닝"),
    CHINESE_DINING("중식 다이닝"),
    WESTERN_DINING("양식 다이닝"),
    FUSION_DINING("퓨전 다이닝"),
    SUSHI_OMAKASE("스시 오마카세"),
    BEEF_OMAKASE("소고기 오마카세"),
    BUFFET("뷔페"),
    CAFE("카페");


    public final String label;

    DiningStyle(String label) {
        this.label = label;
    }

    private static final List<DiningStyle> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    public static DiningStyle randomDining()  {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }
}
