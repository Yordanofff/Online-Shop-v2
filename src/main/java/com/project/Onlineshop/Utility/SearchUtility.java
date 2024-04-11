package com.project.Onlineshop.Utility;

public class SearchUtility {

    // Currently this will follow a very basic logic. Could become a bit better with a list - so "yagoda" becomes both "ягода" and "Йагода" to cover other cases...
    // Another example would be "q" to be both "я" and "к" ... will have to generate a list of all possible combinations and return all results that match any of the words.
    public static String convertToCyrillic(String latinWord) {
        String cyrillicWord = latinWord
                .replace("zh", "ж")
                .replace("ch", "ч")
                .replace("sh", "ш")
                .replace("sch", "щ")
                .replace("ju", "ю")
                .replace("yu", "ю")
                .replace("ja", "я")
                .replace("ya", "я")
                .replace("a", "а")
                .replace("b", "б")
                .replace("v", "в")
                .replace("w", "в")
                .replace("g", "г")
                .replace("d", "д")
                .replace("e", "е")
                .replace("z", "з")
                .replace("i", "и")
                .replace("y", "й")
                .replace("k", "к")
                .replace("q", "я")
                .replace("l", "л")
                .replace("m", "м")
                .replace("n", "н")
                .replace("o", "о")
                .replace("p", "п")
                .replace("r", "р")
                .replace("s", "с")
                .replace("t", "т")
                .replace("u", "у")
                .replace("f", "ф")
                .replace("h", "х")
                .replace("c", "ц")
                .replace("x", "кс");

        return cyrillicWord;
    }

    public static String convertToLatin(String cyrillicWord) {
        String latinWord = cyrillicWord
                .replace("кс", "x")
                .replace("а", "a")
                .replace("б", "b")
                .replace("в", "v")
                .replace("г", "g")
                .replace("д", "d")
                .replace("е", "e")
                .replace("ж", "j")
                .replace("з", "z")
                .replace("и", "i")
                .replace("й", "y")
                .replace("к", "k")
                .replace("л", "l")
                .replace("м", "m")
                .replace("н", "n")
                .replace("о", "o")
                .replace("п", "p")
                .replace("р", "r")
                .replace("с", "s")
                .replace("т", "t")
                .replace("у", "u")
                .replace("ф", "f")
                .replace("х", "h")
                .replace("ц", "c")
                .replace("ч", "ch")
                .replace("ш", "sh")
                .replace("щ", "sht")
                .replace("ю", "u")
                .replace("я", "q");

        return latinWord;
    }


    public static boolean containsCyrillic(String word) {
        for (char c : word.toCharArray()) {
            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CYRILLIC) {
                return true;
            }
        }
        return false;
    }
}
