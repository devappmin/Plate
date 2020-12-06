package com.petabyte.plate.data;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum FoodStyle {
    WITH_LOVER("연인과 함께"),
    SWEET("달달한"),
    COST_EFFECTIVE("가성비"),
    SALTY("짭잘한"),
    GOOD_MOOD("분위기 좋은"),
    SOLO_EATING("혼밥하기 좋은"),
    KOREAN("한식"),
    JAPANESE("일식"),
    CHINESE("중식"),
    WESTERN("양식"),
    ASIAN("아시안식"),
    BUSINESS("사업할 때"),
    FAMILY("가족들과"),
    FRIENDS("친구들과"),
    MIND_EFFECTIVE("가심비"),
    BEEF("소고기"),
    SEAFOOD("해산물"),
    PORK("돼지고기"),
    SHEEP("양고기");


    public final String label;

    FoodStyle(String label) {
        this.label = label;
    }

    public static FoodStyle getFoodStyle(String name) {
        for (FoodStyle style : FoodStyle.values()) {
            if (style.label.equals(name))
                return style;
        }

        return null;
    }

    private static final List<FoodStyle> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    public static FoodStyle randomLetter()  {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }
}
