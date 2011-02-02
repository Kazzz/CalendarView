/*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.kazzz.view.calendar;

import java.util.Date;

import org.kazzz.util.HolidayUtil;
import org.kazzz.util.StrUtil;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * カレンダビュー内部で使用する日付情報を提供します
 * 
 * @author Kazzz.
 * @since JDK1.5 Android Level 4
 *
 */
public class DateInfo implements Parcelable {
    public static final Parcelable.Creator<DateInfo> CREATOR = 
        new Parcelable.Creator<DateInfo>() {
        public DateInfo createFromParcel(Parcel in) {
            return new DateInfo(in);
        }
        
        public DateInfo[] newArray(int size) {
            return new DateInfo[size];
        }
    };
    protected int year, month, day, hour, minute;
    protected boolean isHoliday;
    protected String holidayName;
    protected Object object;
    protected Date reportDate;
    /**
     * コンストラクタ
     * @param date Dateオブジェクトをセット
     */
    public DateInfo(int year, int month, int day) {
        //this();
        this.year = year;
        this.month = month;
        this.day = day;
        this.holidayName = 
            HolidayUtil.getHolidayName(this.year, this.month,  this.day);
        this.isHoliday = 
            HolidayUtil.isSunday(this.year, this.month,  this.day) 
            || StrUtil.isNotEmpty(this.holidayName);
    }
    /**
     * コンストラクタ
     * @param in 入力となるパーセルオブジェクトをセット
     */
    public DateInfo(Parcel in) {
        //this();
        this.readFromParcel(in);
    }
    /* (non-Javadoc)
     * @see android.os.Parcelable#describeContents()
     */
    @Override
    public int describeContents() {
        return 0;
    }
    /* (non-Javadoc)
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.year);
        dest.writeInt(this.month);
        dest.writeInt(this.day);
        dest.writeInt(this.hour);
        dest.writeInt(this.minute);
        dest.writeInt(this.isHoliday ? 1:0);
        dest.writeString(this.holidayName);
    }
    /**
     * Parcel内からインスタンスを構成します 
     * @param source パーセルオブジェクトをセット
     */
    public void readFromParcel(Parcel source) {
        this.year = source.readInt();
        this.month = source.readInt();
        this.day = source.readInt();
        this.hour = source.readInt();
        this.minute = source.readInt();
        this.isHoliday = source.readInt() == 1 ? true :false;
        this.holidayName = source.readString();
    }
    
    /**
     * yearを取得します
     * @return int yearが戻ります
     */
    public int getYear() {
        return this.year;
    }
    /**
     * yearを設定します
     * @param year yearをセットします
     */
    public void setYear(int year) {
        if ( this.year !=  year ) {
            this.year = year;
            this.createDate();
        }
    }
    /**
     * monthを取得します
     * @return int monthが戻ります
     */
    public int getMonth() {
        return this.month;
    }
    /**
     * monthを設定します
     * @param month monthをセットします
     */
    public void setMonth(int month) {
        if ( this.month !=  month ) {
            this.month = month;
            this.createDate();
        }
    }
    /**
     * dayを取得します
     * @return int dayが戻ります
     */
    public int getDay() {
        return this.day;
    }
    /**
     * dayを設定します
     * @param day dayをセットします
     */
    public void setDay(int day) {
        if ( this.day !=  day ) {
            this.day = day;
            this.createDate();
        }
    }
    
    /**
     * hourを取得します
     * @return int hourが戻ります
     */
    public int getHour() {
        return this.hour;
    }
    /**
     * hourを設定します
     * @param hour hourをセットします
     */
    public void setHour(int hour) {
        if ( this.hour !=  hour ) {
            this.hour = hour;
            this.createDate();
        }
    }
    /**
     * minuteを取得します
     * @return int minuteが戻ります
     */
    public int getMinute() {
        return this.minute;
    }
    /**
     * minuteを設定します
     * @param minute minuteをセットします
     */
    public void setMinute(int minute) {
        if ( this.minute !=  minute ) {
            this.minute = minute;
            this.createDate();
        }
    }
    /**
     * 年月日を設定します
     * @param year 年をセット
     * @param month 月をセット
     * @param day 日をセット
     */
    public void setYMD(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = 0;
        this.minute = 0;
        this.createDate();
    }
    /**
     * 年月日時分を設定します
     * @param year 年をセット
     * @param month 月をセット
     * @param day 日をセット
     * @param hour 日をセット
     * @param minute 日をセット
     */
    public void setYMDHM(int year, int month, int day, int hour, int minute) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.createDate();
    }
    /**
     * isHolidayを取得します
     * @return boolean isHolidayが戻ります
     */
    public boolean isHoliday() {
        return this.isHoliday;
    }
    /**
     * objectを取得します
     * @return Object objectが戻ります
     */
    public Object getObject() {
        return this.object;
    }
    /**
     * objectを設定します
     * @param object objectをセットします
     */
    public void setObject(Object object) {
        this.object = object;
    }
    /**
     * holidayNameを取得します
     * @return String holidayNameが戻ります
     */
    public String getHolidayName() {
        return this.holidayName;
    }
    
    /**
     * 内部の日付情報からDateオブジェクト生成します
     * @return Date 生成した日付が戻ります
     */
    public Date createDate() {
        this.reportDate = 
            new Date(this.year + "/" + this.month + "/" + this.day 
                    + " " + this.hour + ":" + this.minute);
        return this.reportDate;
    }
    /**
     * 内部の日付をYYMMDD形式で取得します
     * @return String YYMMDD形式の日付が戻ります
     */
    public String getYMD() {
        return Integer.toString(this.year) 
        + toTwoDigits(this.month) + toTwoDigits(this.day);
    }
    /**
     * 内部の日付をYYMMDD HH:MM形式で取得します
     * @return String YYMMDD HH:MM形式の日付が戻ります
     */
    public String getYMDHM() {
        return Integer.toString(this.year) 
        + toTwoDigits(this.month) + toTwoDigits(this.day)
        + toTwoDigits(this.hour) + toTwoDigits(this.minute);
    }
    /**
     * 数値を2桁に整形します
     * @param n 数値をセット
     * @return String 2桁の数値が戻ります
     */
    private String toTwoDigits(int n) {
        if (n >= 10) {
            return Integer.toString(n);
        } else {
            return "0" + n;
        }
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if ( o == null ) return false;
        if ( o instanceof DateInfo) {
            DateInfo other = (DateInfo)o;
            return ( this.year == other.year 
                    && this.month == other.month 
                    && this.day == other.day
                    && this.hour == other.hour
                    && this.minute == other.minute);
        }
        return false;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DateInfo [");
        builder.append(" Date[year/month/day hour:minute] = [");
        builder.append(this.createDate().toLocaleString());
        builder.append(", isHoliday = " + this.isHoliday);
        builder.append(StrUtil.isNotEmpty(this.holidayName)
                ? ", " + this.holidayName
                : "");
        builder.append( StrUtil.isNotEmpty(this.holidayName)
                ? ", " + this.holidayName
                : "");
        builder.append("]");
        return builder.toString();
    }
    
 }