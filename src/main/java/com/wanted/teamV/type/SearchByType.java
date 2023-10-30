package com.wanted.teamV.type;

public enum SearchByType {

    TITLE,
    CONTENT,
    BOTH
    ;

    public static SearchByType parse(String value) {
        for (SearchByType type : SearchByType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return BOTH;
    }

}
