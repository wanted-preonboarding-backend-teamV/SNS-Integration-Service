package com.wanted.teamV.type;

public enum OrderByType {

    CREATED_AT_ASC,
    CREATED_AT_DESC,
    UPDATED_AT_ASC,
    UPDATED_AT_DESC,
    LIKE_COUNT_ASC,
    LIKE_COUNT_DESC,
    SHARE_COUNT_ASC,
    SHARE_COUNT_DESC,
    VIEW_COUNT_ASC,
    VIEW_COUNT_DESC
    ;

    public static OrderByType parse(String value) {
        for (OrderByType type : OrderByType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return CREATED_AT_DESC;
    }

}
