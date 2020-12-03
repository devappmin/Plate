package com.petabyte.plate.utils;

public class KoreanUtil {
    /**
     * input에 따라 firstGrammar와 secondGrammar 중 제대로된 입력값을 넣는다.
     * @param input 한국어 단어
     * @param firstGrammar 마지막이 받침으로 끝날 때 입력하고자 하는 값
     * @param secondGrammar 마지막이 받침이 없이 끝날 때 입력하고자 하는 값
     * @return 두 값을 더한 값
     */
    public static final String includeGrammarCheck(String input, String firstGrammar, String secondGrammar) {
        // 입력된 한국어 단어 중에 마지막 글자를 입력
        char lastChar = input.charAt(input.length() - 1);

        // 만약에 그 값이 한국어가 아니라면 firstGrammar와 합쳐서 리턴
        if (lastChar < 0xAC00 || lastChar > 0xD7A3)
            return input + firstGrammar;

        // 받침이 있는지 없는지 확인하고 결정
        String chosen = (lastChar - 0xAC00) % 28 > 0 ? firstGrammar : secondGrammar;

        // 결정된 값을 리턴
        return input + chosen;
    }
}
