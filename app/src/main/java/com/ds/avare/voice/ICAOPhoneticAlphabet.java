package com.ds.avare.voice;

/**
 * Created by Michal on 12/17/2016.
 */

public class ICAOPhoneticAlphabet {

    //static private String[] letters = { "Alfa", "Bravo", "Charlie", "Delta", "Echo", "Foxtrot", "Golf", "Hotel", "India", "Juliett", "Kilo", "Lima", "Mike", "November", "Oscar", "Papa", "Quebec", "Romeo", "Sierra", "Tango", "Uniform", "Victor", "Whiskey", "X-ray", "Yankee", "Zulu" };
    static private String[] letters = { "Alfah", "'Brahvoh", "Charlee", "Deltah", "Eckoh", "Fokstrot", "Golf", "Hohtel", "Indeeah", "Juliett", "Keyloh", "Leemah", "Mike", "November", "Osscah", "Pahpah", "Kehbeck", "Rowmeoh", "Seeairrah", "Tango", "Yooneeform", "Victah", "Wisskey", "Ecks-ray", "Yangkee", "Zooloo" };
    static private String[] digits = {"zeero", "wun", "too", "tree", "four" /*'fower sounds weird...*/, "fife", "six", "seven", "ait", "niner"};

    static private String convertOneLetter(char c) {
        return (c>='A' && c<='Z') ? letters[c-'A']
                : (c>='0' && c<='9') ? digits[c-'0']
                : c == '.' ? "point"
                : c == ',' ? "comma"
                : c == '&' ? "and"
                : Character.toString(c);
    }

    static  public String convert(String s) {
        StringBuilder sb = new StringBuilder();
        for(char c: s.toCharArray()) {
            sb.append(convertOneLetter(c));
            sb.append(" ");
        }
        return sb.toString();
    }

}
