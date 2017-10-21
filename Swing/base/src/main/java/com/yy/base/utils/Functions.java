package com.yy.base.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

/**
 * <br>author: wzg<br/>
 * date:   2017/4/26 18:57 <br/>
 */

public class Functions {

    /**
     * 验证emailAddr是否为有效地址
     */
    public static boolean isValidEmail(String emailAddr) {
        Matcher matcher = Regex.EMAIL_ADDRESS_PATTERN.matcher(emailAddr);

        return matcher.matches();
    }

    public static boolean areSameDay(Date dateA, Date dateB) {
        Calendar calDateA = Calendar.getInstance();
        calDateA.setTime(dateA);

        Calendar calDateB = Calendar.getInstance();
        calDateB.setTime(dateB);

        return calDateA.get(Calendar.YEAR) == calDateB.get(Calendar.YEAR)
                && calDateA.get(Calendar.MONTH) == calDateB.get(Calendar.MONTH)
                &&  calDateA.get(Calendar.DAY_OF_MONTH) == calDateB.get(Calendar.DAY_OF_MONTH);
    }

    public static boolean containTheDay(List<Date> dateList, Date theDate){
        if(dateList == null || dateList.size() <= 0){
            return false;
        }

        boolean isContain = false;

        for (int i = dateList.size()-1; i  >= 0; i--) {
            if (areSameDay(dateList.get(i), theDate)) {
                isContain = true;
                break;
            }
        }

        return isContain;

    }
}