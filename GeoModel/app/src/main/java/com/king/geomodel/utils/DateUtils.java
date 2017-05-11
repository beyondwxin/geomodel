package com.king.geomodel.utils;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * 日期的处理工具类
 *
 * @author xuan
 */
public class DateUtils {
    private static final String TAG = DateUtils.class.getSimpleName();
    public static SimpleDateFormat sdf1 = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm");
    public static SimpleDateFormat sdf6 = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat sdf5 = new SimpleDateFormat(
            "yyyyMMddHHmmss");
    public static SimpleDateFormat sdf7 = new SimpleDateFormat(
            "MM-dd HH:mm");
    public static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    public static SimpleDateFormat sdf3 = new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat sdf4 = new SimpleDateFormat("HH-mm-ss");

    public static String formatDateYYYYMMDD(String dateStr) {
        try {
            Date date = new Date(Long.parseLong(dateStr));
            String str = sdf2.format(date);
            return str;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取当前时间并格式化成 yyyy-mm-dd hh-mm-ss格式
     *
     * @return
     */
    public static final String getCurrentFormateTime() {
        return sdf1.format(new Date());
    }

    /**
     * 获取当前时间并格式化成 yyyy-mm-dd 格式
     *
     * @return
     */
    public static final String getCurrentFormateTime2() {
        return sdf2.format(new Date());
    }

    /**
     * 获取当前时间并格式化成 yyyy-MM-dd HH:mm:ss 格式
     *
     * @return
     */
    public static final String getCurrentFormateTime6() {
        return sdf6.format(new Date());
    }

    /**
     * 获取三个月之前的日期 yyyy-MM-dd HH:mm:ss 格式
     *
     * @return
     */
    public static final String getThreeMonthAgoFormateTime6() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -3);
        Date date = new Date();
        date.setTime(calendar.getTimeInMillis());
        return sdf6.format(date);
    }


    /**
     * 获取当前时间并格式化成 yyyy-mm-dd 格式
     *
     * @return
     */
    public static final String getCurrentFormateTime2OfDate(String dateStr) {
        return sdf2.format(new Date(dateStr));
    }

    /**
     * yyyy-MM-dd HH:mm 格式化成 yyyy-mm-dd 格式
     *
     * @return
     */
    public static final String getFormateDataformAllTimeStrData(String dateAllStr) {
        try {
            Date data = sdf1.parse(dateAllStr);
            return sdf2.format(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateAllStr;
    }

    /**
     * yyyy-MM-dd HH:mm:ss 格式化成 MM-dd HH:mm:ss 格式
     *
     * @return
     */
    public static final String getFormateDataformAllTimeStrData2(String dateAllStr) {
        try {
            Date data = sdf6.parse(dateAllStr);
            return sdf7.format(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateAllStr;
    }

    /**
     * 获取当前时间并格式化成 HH:mm:ss 格式
     *
     * @return
     */
    public static final String getCurrentFormateTime3() {
        return sdf3.format(new Date());
    }

    /**
     * 格式化成 yyyy-mm-dd hh-mm格式
     *
     * @return
     */
    public static final String getFormateTime1(Date date) {
        return sdf1.format(date);
    }

    public static final Date parsDate2(String dateStr) {
        try {
            return sdf2.parse(dateStr.substring(0, 10));
        } catch (Exception e) {
            return null;
        }
    }

    public static Date parsDate1(String dateStr) {
        try {
            return sdf1.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 只限制年和月的日期选择框
     *
     * @param dateStr
     * @param title               标题
     * @param textchangeListenner
     * @param flag                是否第一次显示提示框 如果是第一次 则是开始时间，如果不是则是结束时间
     * @return
     */
    @SuppressLint("NewApi")
    public static DatePickerDialog getDataWithYYYYMM(Context context,
                                                     String dateStr, final String title,
                                                     final OnDateSelected textchangeListenner, boolean flag) {
        // TODO 这个时间是当前时间
        Calendar c = getCalendarWithDate(new Date());
        DatePickerDialog dialog = new DatePickerDialog(context,
                new OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        /** 选择的开始日期 */
                        StringBuffer date = new StringBuffer();
                        date.append(String.valueOf(year));
                        // date.append("-");
                        int month = monthOfYear + 1;
                        date.append(((month < 10) ? ("0" + month)
                                : (month + "")));
                        // date.append("-");
                        // date.append(((dayOfMonth < 10) ? ("0" + dayOfMonth)
                        // : (dayOfMonth + "")));
                        if (textchangeListenner != null) {
                            textchangeListenner.selectedListenner(date
                                    .toString());
                        }
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        );
        dialog.setTitle(title);
        DatePicker dp = dialog.getDatePicker();
        if (dp.getCalendarView() != null) {
            dp.getCalendarView().setVisibility(View.GONE);
        }
        ((ViewGroup) ((ViewGroup) dp.getChildAt(0)).getChildAt(0))
                .getChildAt(2).setVisibility(View.GONE);
        return dialog;
    }

    /**
     * 日期弹窗
     *
     * @param title               标题
     * @param textchangeListenner
     * @return
     */
    public static DatePickerDialog getDataDialog(Context context,
                                                 final String title, final OnDateSelected textchangeListenner) {
        // TODO 这个时间是当前时间
        Calendar c = getCalendarWithDate(new Date());
        DatePickerDialog dialog = new DatePickerDialog(context,
                new OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        /** 选择的开始日期 */
                        StringBuffer date = new StringBuffer();
                        date.append(String.valueOf(year));
                        date.append("-");
                        int month = monthOfYear + 1;
                        date.append(((month < 10) ? ("0" + month)
                                : (month + "")));
                        date.append("-");
                        date.append(((dayOfMonth < 10) ? ("0" + dayOfMonth)
                                : (dayOfMonth + "")));
                        if (textchangeListenner != null) {
                            textchangeListenner.selectedListenner(date
                                    .toString());
                        }
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        );
        dialog.setTitle(title);
        return dialog;
    }


    /**
     * 日期弹窗（不能早于今天）
     *
     * @param title               标题
     * @param textchangeListenner
     * @return
     */
    public static DatePickerDialog getDataDialogAfterToday(Context context,
                                                           final String title, final onDateSelected2 textchangeListenner) {
        // TODO 这个时间是当前时间
        Calendar c = getCalendarWithDate(new Date());
        DatePickerDialog dialog = new DatePickerDialog(context,
                new OnDateSetListener() {

                    @SuppressWarnings("deprecation")
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        /** 选择的开始日期 */
                        StringBuffer date = new StringBuffer();
                        date.append(String.valueOf(year));
                        date.append("-");
                        int month = monthOfYear + 1;
                        date.append(((month < 10) ? ("0" + month)
                                : (month + "")));
                        date.append("-");
                        date.append(((dayOfMonth < 10) ? ("0" + dayOfMonth)
                                : (dayOfMonth + "")));

                        if (textchangeListenner != null) {
                            textchangeListenner.selectedListener(new Date(year,
                                    monthOfYear, dayOfMonth));
                        }
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        );
        dialog.setTitle(title);
        return dialog;
    }

    /**
     * 时间弹窗
     *
     * @param dateStr
     * @param title               标题
     * @param textchangeListenner
     * @return
     * @author renxiaotian
     */
    public static TimePickerDialog getTimeDialog(Context context,
                                                 String dateStr, final String title,
                                                 final OnDateSelected textchangeListenner, boolean flag) {
        Calendar c = getCalendarWithDate(new Date());
        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                new OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        StringBuffer time = new StringBuffer();
                        time.append(((hourOfDay < 10) ? ("0" + hourOfDay)
                                : (hourOfDay + "")));
                        time.append(":");
                        time.append(((minute < 10) ? ("0" + minute)
                                : (minute + "")));
                        time.append(":00");
                        if (textchangeListenner != null) {
                            textchangeListenner.selectedListenner(time
                                    .toString());
                        }
                    }
                }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true
        );
        timePickerDialog.setTitle(title);
        return timePickerDialog;
    }

    /**
     * 初始化 Calendar 字符串 必须为 yyyy-MM-dd
     *
     * @return Calendar
     */
    public static Calendar getCalendarWithDate(String dateStr) {
        try {
            return getCalendarWithDate(sdf.parse(dateStr.substring(0, 10)));
        } catch (Exception e) {
            LogUtil.e(TAG, "checkDate1(dateStr,c) 格式转换错误");
            return Calendar.getInstance();
        }
    }

    /**
     * 初始化 Calendar
     *
     * @param date
     * @return
     */
    public static Calendar getCalendarWithDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date.getTime());
        return c;
    }

    public interface OnDateSelected {
        public void selectedListenner(String date);
    }

    public interface OnTimeSelected {
        public void selectedListenner(String date);
    }

    public interface onDateSelected2 {
        public void selectedListener(Date date);
    }

    public static StringBuffer formatDateToYYMMDD(String date) {
        String[] newDate = date.split("-");
        int year = StringUtil.getIntFromStr(newDate[0]);
        int monthOfYear = StringUtil.getIntFromStr(newDate[1]);
        int dayOfMonth = StringUtil.getIntFromStr(newDate[2]);
        StringBuffer dateBuffer = new StringBuffer();
        dateBuffer.append(String.valueOf(year));
        dateBuffer.append("-");
        dateBuffer.append(((monthOfYear < 10) ? ("0" + monthOfYear)
                : (monthOfYear + "")));
        dateBuffer.append("-");
        dateBuffer.append(((dayOfMonth < 10) ? ("0" + dayOfMonth)
                : (dayOfMonth + "")));
        return dateBuffer;
    }

    public static String createDateName() {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String timeStr = sf.format(new Date());
        return timeStr;
    }

    /**
     * 获取两个时间相差的秒数
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static long getTwoGapTimeSecond(String startTime, String endTime) {
        try {
            Date dateStart = sdf6.parse(startTime);
            Date dateEnd = sdf6.parse(endTime);
            long time = dateStart.getTime() - dateEnd.getTime();
            long totalS = time / 1000;
            return totalS;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int getTwoGapTimeSecond(Date startTime, Date endTime) {
        try {
            long time = startTime.getTime() - endTime.getTime();
            int totalS = new Long(time / 1000).intValue();
            return totalS;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 服务器时间和本地时间校准
     *
     * @param serverTime 服务器时间
     * @param localTime  本地时间
     * @return 校准后的时间
     */
    public static String checkWithLocal(String serverTime, String localTime) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dateStart = dateFormat.parse(serverTime);
            Date dateEnd = dateFormat.parse(localTime);
            long temp = dateStart.getTime() - dateEnd.getTime();
            long differenceTime = dateStart.getTime() - temp;
            return dateFormat.format(differenceTime);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 字符串转换成时间戳
     *
     * @param time
     * @return
     */
    public static String strToTimeStamp(String time) {
        String strTime = "";
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = sf.parse(time);
            long l = date.getTime();
            String str = String.valueOf(l);
            strTime = str.substring(0, 10);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return strTime;
    }

    /**
     * 验证日期是否比当前日期早
     *
     * @param time1
     * @param time2
     * @return true 则代表target1比target2晚或等于target2，否则比target2早
     */
    public static boolean compareDate(String time1, String time2) {
        boolean flag = false;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date startDate = sdf.parse(time1);
            Date endDate = sdf.parse(time2);
            if (startDate.compareTo(endDate) >= 0) {
                //结束日期早于开始日期,后者小
                flag = true;
            }
        } catch (ParseException e) {
            System.out.println("比较失败，原因：" + e.getMessage());
        }
        return flag;
    }

    /**
     * 起始时间早于(小于)终止时间
     * @param time1
     * @param time2
     * @return
     */
    public static boolean compareDate2(String time1, String time2) {
        boolean flag = false;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date startDate = sdf.parse(time1);
            Date endDate = sdf.parse(time2);
            if (startDate.compareTo(endDate) > 0) {
                //结束日期早于开始日期,后者小
                flag = true;
            }
        } catch (ParseException e) {
            System.out.println("比较失败，原因：" + e.getMessage());
        }
        return flag;
    }

    /**
     * 将日期字符串转成日期
     *
     * @param strDate 字符串日期
     * @return java.util.date日期类型
     */
    public static Date parseDate(String strDate) {
        DateFormat dateFormat = sdf1;
        Date returnDate = null;
        try {
            returnDate = dateFormat.parse(strDate);
        } catch (ParseException e) {
            Log.v(TAG, "parseDate failed !");
        }
        return returnDate;

    }
}
