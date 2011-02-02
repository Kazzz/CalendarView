/*_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/
_/
_/　CopyRight(C) K.Tsunoda(AddinBox) 2001 All Rights Reserved.
_/　( http://www.h3.dion.ne.jp/~sakatsu/index.htm )
_/
_/　　この祝日マクロは『kt関数アドイン』で使用しているものです。
_/　　このロジックは、レスポンスを第一義として、可能な限り少ない
_/　【条件判定の実行】で結果を出せるように設計してあります。
_/　　この関数では、２００３年施行の改正祝日法までをサポートして
_/　います(９月の国民の休日を含む)。
_/
_/　(*1)このマクロを引用するに当たっては、必ずこのコメントも
_/　　　一緒に引用する事とします。
_/　(*2)他サイト上で本マクロを直接引用する事は、ご遠慮願います。
_/　　　【 http://www.h3.dion.ne.jp/~sakatsu/holiday_logic.htm 】
_/　　　へのリンクによる紹介で対応して下さい。
_/　(*3)[ktHolidayName]という関数名そのものは、各自の環境に
_/　　　おける命名規則に沿って変更しても構いません。
_/　
_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/*/


package org.kazzz.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 祭日判定のための機能を提供します
 * @since JDK1.5 Android Level 4
 *
 */
public class HolidayUtil {
    private HolidayUtil() {}
    
    private static final Calendar cstImplementTheLawOfHoliday
        = new GregorianCalendar( 1948, Calendar.JULY, 20 );   // 祝日法施行
    private static final  Calendar cstAkihitoKekkon
        = new GregorianCalendar( 1959, Calendar.APRIL, 10 );  // 明仁親王の結婚の儀
    private static final  Calendar cstShowaTaiso
        = new GregorianCalendar( 1989, Calendar.FEBRUARY, 24 );// 昭和天皇大喪の礼
    private static final  Calendar cstNorihitoKekkon
        = new GregorianCalendar( 1993, Calendar.JUNE, 9 );// 徳仁親王の結婚の儀
    private static final  Calendar cstSokuireiseiden
        = new GregorianCalendar( 1990, Calendar.NOVEMBER, 12 );// 即位礼正殿の儀
    private static final  Calendar cstImplementHoliday
        = new GregorianCalendar( 1973, Calendar.APRIL, 12 );// 振替休日施行

    
    /**
     * カレンダから祭日名を取得します
     * @param calendar カレンダをセット
     * @return String 祭日名が戻ります(祭日では無い場合、空文字が戻ります)
     */
    public static String getHolidayName(Calendar calendar)
    {
        String result;
        String holidayName = HolidayUtil.prvHolidayChk(calendar);
        if ( holidayName == "" ) {
            //ハッピーマンデー (政権交代で変えられてしまう可能性)
            if (calendar.get( Calendar.DAY_OF_WEEK ) == Calendar.MONDAY) {
                // 月曜以外は振替休日判定不要
                if (calendar.after( cstImplementHoliday) ||
                        calendar.equals( cstImplementHoliday )) {
                    Calendar yesterDay = (Calendar )calendar.clone();
                    yesterDay.add( Calendar.DATE, -1 );
                    holidayName = prvHolidayChk( yesterDay );
                    result = "";
                    if ( holidayName != "" ) {
                        result = "振替休日";
                    } else {
                        result = "";
                    }
                } else {
                    result = "";
                }
            } else {
                result = "";
            }
        } else {
            result = holidayName;
        }
        return result;
    }
    /**
     * Dateから祭日名を取得します
     * @param date Dateオブジェクトをセット
     */
    public static String getHolidayName(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String ret = HolidayUtil.getHolidayName(calendar);
        return ret != null ? ret : "";
    }

    /**
     * 日付文字列から祭日名を取得します
     * @param year 年をセット
     * @param month 月をセット
     * @param day 日をセット
     * @return String 祭日名が戻ります(祭日では無い場合、空文字が戻ります)
     */
    public static String getHolidayName(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month-1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return HolidayUtil.getHolidayName(calendar);
    }
    /**
     * 日曜日か否かを判定します
     * @param year 年をセット
     * @param month 月をセット
     * @param day 日をセット
     * @return true 休日だった場合は
     */
    public static boolean isSunday(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month-1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return HolidayUtil.isSunday(calendar);
    }
    /**
     * 日曜日か否かを判定します
     * @param date 対象の日付をセット
     * @return true 休日だった場合は
     */
    public static boolean isSunday(Calendar calendar) {
        if ( calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ) {
            //日曜日
            return true;
        } else {
            return false;
        }
    }
    /**
     * カレンダから祭日を取得します
     * @param calendar カレンダをセット
     * @return 祭日名が戻ります(空文字の場合、祭日ではありません)
     */
    private static String prvHolidayChk( Calendar calendar )
    {
        int nNumberOfWeek;
        String result;
        int year = calendar.get( Calendar.YEAR );
        int month = calendar.get( Calendar.MONTH ) + 1; // MyMonth:1～12
        int day = calendar.get( Calendar.DATE );
    
        if ( calendar.before( cstImplementTheLawOfHoliday ) ) {
            return ""; // 祝日法施行(1948/7/20 )以前
        } else;
    
        result = "";
        switch ( month ) {
            // １月 //
        case 1:
            if ( day == 1 ) {
                result = "元日";
            } else {
                if ( year >= 2000 ) {
                    nNumberOfWeek = ( (day - 1 ) / 7 ) + 1;
                    if ( ( nNumberOfWeek == 2 ) &&
                            ( calendar.get(Calendar.DAY_OF_WEEK ) 
                                    == Calendar.MONDAY ) ) {
                        result = "成人の日";
                    } else;
                } else {
                    if ( day == 15 ) {
                        result = "成人の日";
                    } else;
                }
            }
            break;
            // ２月 //
         case 2:
            if ( day == 11 ) {
                if ( year >= 1967 ) {
                    result = "建国記念の日";
                } else;
            } else {
                if ( calendar.equals( cstShowaTaiso ) ) {
                    result = "昭和天皇の大喪の礼";
                } else;
            }
            break;
            // ３月 //
         case 3:
            if ( day == prvDayOfSpringEquinox( year ) ) {  // 1948～2150以外は[99]
                result = "春分の日";                       // が返るので､必ず≠になる
            } else;
            break;
            // ４月 //
         case 4:
            if ( day == 29 ) {
                if ( year >= 1989 ) {
                    result = "みどりの日";
                } else {
                result = "天皇誕生日";
                }
            } else {
                if ( calendar.equals( cstAkihitoKekkon ) ) {
                    result = "皇太子明仁親王の結婚の儀";// ( =1959/4/10 )
                } else;
            }
            break;
            // ５月 //
         case 5:
            if ( day == 3 ) {
                result = "憲法記念日";
            } else {
                if ( day == 4 ) {
                    if ( calendar.get( Calendar.DAY_OF_WEEK ) 
                            > Calendar.MONDAY ) {
                        // 5/4が日曜日は『只の日曜』､月曜日は『憲法記念日の振替休日』
                        if ( year >= 1986 ) {
                            result = "国民の休日";
                        } else;
                    } else;
                } else {
                    if ( day == 5 ) {
                        result = "子供の日";
                    } else;
                }
            }
            break;
            // ６月 //
         case 6:
            if ( calendar.equals( cstNorihitoKekkon ) ) {
                result = "皇太子徳仁親王の結婚の儀";
            } else;
            break;
            // ７月 //
         case 7:
            if ( year >= 2003 ) {
                nNumberOfWeek = ( (day - 1 ) / 7 ) + 1;
                if ( ( nNumberOfWeek == 3 ) 
                        && ( calendar.get( 
                          Calendar.DAY_OF_WEEK ) == Calendar.MONDAY ) ) {
                    result = "海の日";
                } else;
            } else {
                if ( year >= 1996 ) {
                    if ( day == 20 ) {
                        result = "海の日";
                    } else;
                } else;
            }
            break;
            // ９月 //
         case 9:
            //第３月曜日( 15～21 )と秋分日(22～24 )が重なる事はない
            int MyAutumnEquinox = prvDayOfAutumnEquinox( year );
            if ( day == MyAutumnEquinox ) {    // 1948～2150以外は[99]
                result = "秋分の日";           // が返るので､必ず≠になる
            } else {
                if ( year >= 2003 ) {
                    nNumberOfWeek = ( (day - 1 ) / 7 ) + 1;
                    if ( (nNumberOfWeek == 3 ) && 
                            ( calendar.get(
                               Calendar.DAY_OF_WEEK ) == Calendar.MONDAY ) ) {
                        result = "敬老の日";
                    } else {
                        if ( calendar.get( 
                               Calendar.DAY_OF_WEEK ) == Calendar.TUESDAY ) {
                            if ( day == ( MyAutumnEquinox - 1 ) ) {
                                result = "国民の休日";
                            } else;
                        } else;
                    }
                } else {
                    if ( year >= 1966 ) {
                        if ( day == 15 ) {
                            result = "敬老の日";
                        } else;
                    } else;
                }
            }
            break;
            // １０月 //
         case 10:
            if ( year >= 2000 ) {
                nNumberOfWeek = ( ( day - 1 ) / 7 ) + 1;
                if ( (nNumberOfWeek == 2 ) 
                        && ( calendar.get( 
                         Calendar.DAY_OF_WEEK ) == Calendar.MONDAY ) ) {
                    result = "体育の日";
                } else;
            } else {
                if ( year >= 1966 ) {
                    if ( day == 10 ) {
                        result = "体育の日";
                    } else;
                } else;
            }
            break;
            // １１月 //
         case 11:
            if ( day == 3 ) {
                result = "文化の日";
            } else {
                if ( day == 23 ) {
                    result = "勤労感謝の日";
                } else {
                    if ( calendar.equals( cstSokuireiseiden ) ) {
                        result = "即位礼正殿の儀";
                    } else;
                }
            }
            break;
            // １２月 //
        case 12:
            if ( day == 23 ) {
                if ( year >= 1989 ) {
                    result = "天皇誕生日";
                } else;
            } else;
            break;
        }
    
        return result;
    }

    /**
     * 春分の日の判定
     * @param year 年をセット
     * @return 春分日が戻ります
     */
    private static int prvDayOfSpringEquinox( int year )
    {
        // 春分/秋分日の略算式は
        // 『海上保安庁水路部 暦計算研究会編 新こよみ便利帳』
        // で紹介されている式です。
        int springEquinox_ret;
        if ( year <= 1947 ) {
            springEquinox_ret = 99;    //祝日法施行前
        } else {
            if ( year <= 1979 ) {
                springEquinox_ret = (int)( 20.8357 + 
                ( 0.242194 * ( year - 1980 ) ) 
                - (int)( (year - 1983 ) / 4 ) );
            } else {
                if ( year <= 2099 ) {
                    springEquinox_ret = (int)( 20.8431 + 
                    ( 0.242194 * ( year - 1980 ) ) 
                    - (int)( (year - 1980 ) / 4 ) );
                } else {
                    if ( year <= 2150 ) {
                        springEquinox_ret = (int)( 21.851 + 
                        ( 0.242194 * ( year - 1980 ) ) 
                        - (int)( (year - 1980 ) / 4 ) );
                    } else {
                        springEquinox_ret = 99;   //2151年以降は略算式が無いので不明
                    }
                }
            }
        }
        return springEquinox_ret;
    }
    
    /**
     * 秋分日の判定
     * @param year 年をセット
     * @return 秋分日が戻ります
     */
    private static int prvDayOfAutumnEquinox( int MyYear )
    {
        // 春分/秋分日の略算式は
        // 『海上保安庁水路部 暦計算研究会編 新こよみ便利帳』
        // で紹介されている式です。
       int autumnEquinox_ret;
        if ( MyYear <= 1947 ) {
            autumnEquinox_ret = 99; //祝日法施行前
        } else {
            if ( MyYear <= 1979 ) {
                autumnEquinox_ret = (int)( 23.2588 + 
                ( 0.242194 * ( MyYear - 1980 ) ) 
                - (int)( (MyYear - 1983 ) / 4 ) );
            } else {
                if ( MyYear <= 2099 ) {
                    autumnEquinox_ret = (int)( 23.2488 + 
                    ( 0.242194 * ( MyYear - 1980 ) ) 
                    - (int)( (MyYear - 1980 ) / 4 ) );
                } else {
                    if ( MyYear <= 2150 ) {
                        autumnEquinox_ret = (int)( 24.2488 + 
                        ( 0.242194 * ( MyYear - 1980 ) ) 
                        - (int)( (MyYear - 1980 ) / 4 ) );
                    } else {
                        autumnEquinox_ret = 99; //2151年以降は略算式が無いので不明
                    }
                }
            }
        }
        return autumnEquinox_ret;
    }
    
}