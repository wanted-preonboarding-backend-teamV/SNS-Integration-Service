package com.wanted.teamV.common;

import java.text.ParseException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class H2CustomAlias {

    public static String date_format(Date date, String mysqlFormatPattern) throws ParseException {
        if (date == null) return null;
        String dateFormatPattern = mysqlFormatPattern
                .replace("%Y", "yyyy")
                .replace("%m", "MM")
                .replace("%d", "dd")
                .replace("%h", "hh");
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate().format(DateTimeFormatter.ofPattern(dateFormatPattern));
    }

}
