package com.xmevs.dateprocess;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by MSI on 2016/7/4.
 */
public class DateProcessHelp {

    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//    private String startEndDate = "2016,02,28-2016,07,08";
    private int startY = 2016, startM = 7, startD = 11, endY = 2016, endM = 9, endD = 1;

    public int getStartY() {
        return startY;
    }

    public int getStartM() {
        return startM;
    }

    public int getStartD() {
        return startD;
    }

    public int getEndY() {
        return endY;
    }

    public int getEndM() {
        return endM;
    }

    public int getEndD() {
        return endD;
    }

    /*public String getStartEndDate() {
        return String.format("%04d", startY) + "," + String.format("%02d", startM) + "," + String.format("%02d", startD) + "-" +
                String.format("%04d", endY) + "," + String.format("%02d", endM) + "," + String.format("%02d", endD) ;
    }*/

      /**
     *  以六个整形形式设置开始日期、结束日期
     * @param startY
     * @param startM
     * @param startD
     * @param endY
     * @param endM
     * @param endD
     */
    public void setStartEndDate(int startY, int startM, int startD, int endY, int endM, int endD) {
        this.startY = startY;
        this.startM = startM;
        this.startD = startD;
        this.endY = endY;
        this.endM = endM;
        this.endD = endD;
    }

    /**
     *  以六个字符串形式设置开始、结束日期
     * @param startY
     * @param startM
     * @param startD
     * @param endY
     * @param endM
     * @param endD
     */
    public void setStartEndDate(String startY, String startM, String startD, String endY, String endM, String endD) {
        this.startY = Integer.valueOf(startY);
        this.startM = Integer.valueOf(startM);
        this.startD = Integer.valueOf(startD);
        this.endY = Integer.valueOf(endY);
        this.endM = Integer.valueOf(endM);
        this.endD = Integer.valueOf(endD);
    }

    /**
     *  以一串字符串的形式设置开始、结束日期
     * @param startEndDate
     */
    public void setStartEndDate(String startEndDate) {
        String[] splitStartEndDate = startEndDate.split("-");
        String[][] splitStartEndDates = new String[splitStartEndDate.length][];
            for (int i = 0; i < splitStartEndDate.length; i++) {
                splitStartEndDates[i] = splitStartEndDate[i].split(",");
            }

        startY = Integer.valueOf(splitStartEndDates[0][0]);
        startM = Integer.valueOf(splitStartEndDates[0][1]);
        startD = Integer.valueOf(splitStartEndDates[0][2]);
        endY = Integer.valueOf(splitStartEndDates[1][0]);
        endM = Integer.valueOf(splitStartEndDates[1][1]);
        endD = Integer.valueOf(splitStartEndDates[1][2]);
    }

    /**
     * 计算总天数
     * @return 总天数
     */
    long totalNumber() {
        Date start = null, end = null;
        try {
            start = df.parse(String.format("%04d", startY) + "-" + String.format("%02d", startM) + "-" + String.format("%02d", startD));
            end = df.parse(String.format("%04d",endY) + "-" + String.format("%02d", endM) + "-" + String.format("%02d", endD));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long diff = end.getTime() - start.getTime();
        long days = diff / 86400000;
        return days;
    }

    /**
     *  计算已过天数
     * @return 已过天数
     */
    long throughNumber() {
        Date news = null;
        Date end = null;
        try {
            Calendar c = Calendar.getInstance();
            String year = String.format("%04d", c.get(Calendar.YEAR));
            String month = String.format("%02d", c.get(Calendar.MONTH) + 1);
            String day = String.format("%02d", c.get(Calendar.DATE));
            news = df.parse(year + "-" + month + "-" + day);
            end = df.parse(String.format("%04d",endY) + "-" + String.format("%02d", endM) + "-" + String.format("%02d", endD));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long diff = end.getTime() - news.getTime();
        long days = diff / 86400000;
        return days;
    }

}
