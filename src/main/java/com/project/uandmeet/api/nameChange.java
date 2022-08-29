package com.project.uandmeet.api;

public class nameChange {
    public static String nameChange(String ctpKorNm) {
        String subChar = null;
        if (ctpKorNm.contains("특별") || ctpKorNm.contains("광역"))
            subChar = ctpKorNm.substring(0, 2);
        else if (ctpKorNm.contains("도")) {
            if (ctpKorNm.length() < 4)
                subChar = ctpKorNm;
            else {
                subChar = ctpKorNm.substring(0, 1) + ctpKorNm.substring(2,3);
            }
        }
        return subChar;
    }
}
