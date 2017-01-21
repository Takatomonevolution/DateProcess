package com.xmevs.dateprocess.help;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by MSI on 2017/1/21.
 */

public class TimeslotHelp {

    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    //    private String startEndDate = "2016,02,28-2016,07,08";
    private int yearS;
    private int monthS;
    private int dayS;
    private int yearE;
    private int monthE;
    private int dayE;

    private String slotname;

    public void setDate(String date, String slotname) {
        String[] splitStartEndDate = date.split("-");
        String[][] splitStartEndDates = new String[splitStartEndDate.length][];
        for (int i = 0; i < splitStartEndDate.length; i++) {
            splitStartEndDates[i] = splitStartEndDate[i].split(",");
        }

        yearS = Integer.valueOf(splitStartEndDates[0][0]);
        monthS = Integer.valueOf(splitStartEndDates[0][1]);
        dayS = Integer.valueOf(splitStartEndDates[0][2]);
        yearE = Integer.valueOf(splitStartEndDates[1][0]);
        monthE = Integer.valueOf(splitStartEndDates[1][1]);
        dayE = Integer.valueOf(splitStartEndDates[1][2]);

        this.slotname = slotname;
    }

    public int getYearS() {
        return yearS;
    }

    public int getMonthS() {
        return monthS;
    }

    public int getDayS() {
        return dayS;
    }

    public int getYearE() {
        return yearE;
    }

    public int getMonthE() {
        return monthE;
    }

    public int getDayE() {
        return dayE;
    }

    public String getSlotname() {
        return slotname;
    }

    /**
     * 计算总天数
     * @return 总天数
     */
    public long totalDay() {
        Date start = dateParse(yearS, monthS, dayS);
        Date end = dateParse(yearE, monthE, dayE);
        long days = dayParse(end, start);
        return days;
    }

    /**
     *  计算已过天数
     * @return 已过天数
     */
    public long throughDay() {
        Date now = dateParseCalendar();
        Date end = dateParse(yearE, monthE, dayE);
        long days = dayParse(end, now);
        return days;
    }

    /**
     *  将year month day 转换成 date
     * @param year
     * @param month
     * @param day
     * @return
     */
    private Date dateParse(int year, int month, int day) {
        Date date = null;
        try {
            date = df.parse(String.format("%04d", year) + "-" + String.format("%02d", month) + "-" + String.format("%02d", day));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  date;
    }

    /**
     * 将现在的时间转成date类型
     * @return 现在的时间(date)
     */
    private Date dateParseCalendar () {
        Date date = null;
        Calendar c = Calendar.getInstance();
        String year = String.format("%04d", c.get(Calendar.YEAR));
        String month = String.format("%02d", c.get(Calendar.MONTH) + 1);
        String day = String.format("%02d", c.get(Calendar.DATE));
        try {
            date = df.parse(year + "-" + month + "-" + day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  date;
    }

    /**
     *  计算两个时间(date)的时间差
     * @param news 新的时间
     * @param old 老的时间（被减的时间）
     * @return 时间差
     */
    private long dayParse (Date news, Date old) {
        long l = (news.getTime() - old.getTime()) / 86400000;;
        return l;
    }
}
