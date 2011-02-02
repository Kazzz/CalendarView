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
package org.kazzz.util;

import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.StringTokenizer;
/**
 *   文字列系ヘルパユーティリティクラス
 *
 * @author Kazz.
 * @since JDK1.5 Android Level 4
 *
 */
public final class StrUtil {
    private static final String hanKana
    = "ｱｲｳｴｵｧｨｩｪｫｶｷｸｹｺｻｼｽｾｿﾀﾁﾂｯﾃﾄﾅﾆﾇﾈﾉﾊﾋﾌﾍﾎﾏﾐﾑﾒﾓﾔﾕﾖｬｭｮﾗﾘﾙﾚﾛﾜｦﾝｰﾞﾟ､｡";
    private static final String zenKana
    = "アイウエオァィゥェォカキクケコサシスセソタチツッテトナニヌネノハヒフヘホマミムメモヤユヨャュョラリルレロワヲンー゛゜、。";
    public static final String[] EMPTY_STRINGS = new String[0];

    /**
     * 現在の日付、時間を文字列に変換する
     *
     * @param formated フォーマットするのであればtrueを指定する
     * @return String 今日の日付と時間を戻す
     */
    public static final String nowtoStr(boolean formated) {
        return timetoStr(System.currentTimeMillis(), formated);
    }

    /**
     * 現在の日付、時間を文字列に変換する
     *
     *@param time 時間を表すlong値(msec)をセット
     *@param formated フォーマットするのであればtrueを指定する
     *@return String 時間を日付と時間に変換して戻す
     */
    public static final String timetoStr(long time, boolean formated) {
        Calendar calendar = Calendar.getInstance();
        Date currentTime = new Date(time);
        calendar.setTime(currentTime);
        StringBuilder buff = new StringBuilder();
        if ( formated ) {
            buff.append(calendar.get(Calendar.YEAR)).append("/").append(calendar.get(Calendar.MONTH)+1).
                append("/").append(calendar.get(Calendar.DATE)).append(" ").append(calendar.get(Calendar.HOUR_OF_DAY)).
                append(":").append(calendar.get(Calendar.MINUTE)).append(":").append(calendar.get(Calendar.SECOND));
        } else {
            buff.append(calendar.get(Calendar.YEAR)).append(calendar.get(Calendar.MONTH)+1).
                append(calendar.get(Calendar.DATE)).append(calendar.get(Calendar.HOUR_OF_DAY)).
                append(calendar.get(Calendar.MINUTE)).append(calendar.get(Calendar.SECOND));
        }
        return buff.toString();
    }

    /**
     * 文字列中のエスケープ文字を文字列として変換する
     *
     * @param str 変換対象の文字列
     * @return String エスケープを文字に変換した結果が戻る
     */
    public static final String translateEscape(String str) {
        StringBuilder sb = new StringBuilder();
        int lens = str.length();
        for (int i = 0 ;i < lens; i++) {
            int ch = str.charAt(i);
            switch(ch) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    sb.append((char) ch);
            }
        }
        return sb.toString();
    }

    /**
     * 文字列中のHTML特殊文字を実体参照にフィルタリングする <br>
     * <pre>
     *  このメソッドはHTMLFormなどから入力された特殊文字中、HTMLのマークアップと見なされる文字を
     *  実体参照に置き換える。
     *  クロスサイトスクリプティングを避ける為、システムからHTMLに動的に文字列変数を出力する場合、
     *  このメソッドを必ず通すこと。
     *  <pre>
     * @param str 変換対象の文字列
     * @return String HTMLマークアップ対象を実体参照にフィルタした文字が戻る
     */
    public static final String untaintHTMLEntity(String str) {
        StringBuilder buff = null;
        if ( str != null && str.length() > 0 ) {
            buff = new StringBuilder(str.length());
            char c;
            int count = str.length();
            for ( int i=0; i< count; i++){
                c = str.charAt(i);
                if ( c == '<' ) {
                    buff.append("&lt;");
                } else
                if ( c == '>' ) {
                    buff.append("&gt;");
                } else 
                if ( c == '\'' ) {
                    buff.append("&#39;"); //&apos;
                } else 
                if ( c == '"' ) {
                    buff.append("&quot;");
                } else 
                if ( c == '&' ) {
                    buff.append("&amp;");
                } else {
                    buff.append(c);
                }
            }
        } else {
            buff = new StringBuilder();
        }
        return buff.toString();
    }

    /**
     * 文字列配列中のHTML特殊文字を実体参照にフィルタリングする <br>
     * <pre>
     *  このメソッドはHTMLFormなどから入力された特殊文字中、HTMLのマークアップと見なされる文字を
     *  実体参照に置き換える。
     *  クロスサイトスクリプティングを避ける為、システムからHTMLに動的に文字列変数を出力する場合、
     *  このメソッドを必ず通すこと。
     *  <pre>
     * @param strArray 変換対象の文字列配列
     * @return String[] HTMLマークアップ対象を実体参照にフィルタした文字列配列が戻る
     */
    public static final String[] untaintHTMLEntity(String[] strArray) {
        String[] newArray = new String[strArray.length];
        int lengs = strArray.length;
        for ( int i=0; i < lengs; i++){
            newArray[i] = untaintHTMLEntity(strArray[i]);
        }
        return newArray;
    }

    /**
     * 複数の名前を連結してプロパティの名前を作る
     *
     * @param String strArray 連結対象の文字列の配列
     * @param char delim 連結する際の区切り文字
     * @return String 全てを区切り文字で結合した名前が戻る
     */
    public static final String bindWordwithDelim(String[] strArray, char delim) {
        StringBuilder buff = new StringBuilder();
        int leach = strArray.length;
        for (int i = 0; i < leach; i++) {
            buff.append(strArray[i].trim());
            if ( i < strArray.length-1) { buff.append(delim); }
        }
        return buff.toString();
    }

    /**
     * 完全限定クラス名からパッケージ名を取得する
     *
     * @param className 完全限定クラス名をセットする
     * @return String パッケージ名が戻る
     */
    public static final String getPackageName(String className) {
        StringTokenizer tokennizer = new StringTokenizer(className, ".");
        StringBuilder result = new StringBuilder();
        int tokenCount = tokennizer.countTokens();
        if (tokennizer.hasMoreTokens()) {result.append(tokennizer.nextToken());}
        for ( int i = 0; i < tokenCount-2; i++) {
            result.append(".").append(tokennizer.nextToken());
        }
        return result.toString().trim();
    }

    /**
     * 二つのオブジェクトが等しいかどうかを検査する
     *
     * @param obj1 オブジェクトをセットする
     * @param obj2 比較対象のオブジェクトをセットする
     * @return boolean 二つのオブジェクトが等しい場合にtrueを返す
     */
    public static final boolean areEqual(Object obj1, Object obj2) {
        if (obj1 == null) {
            return obj2 == null;
        } else {
            return obj1.equals(obj2);
        }
    }

    /**
     * 文字列が空かどうかを検査する
     *
     * @param s 文字列オブジェクトをセットする
     * @return boolean 文字列が空ならtrueが戻る
     */
    public static final boolean isEmpty(CharSequence s) {
        return ((s == null) || (s.length() == 0));
    }

    /**
     * コレクションが空かどうかを検査する
     *
     * @param c コレクションをセットする
     * @return boolean コレクションが空ならtrueが戻る
     */
    public static final boolean isEmpty(Collection<?> c) {
        return ((c == null) || (c.size() == 0));
    }

    /**
     * マップが空かどうかを検査する
     *
     * @param m マップをセットする
     * @return boolean マップが空ならtrueが戻る
     */
    public static final boolean isEmpty(Map<?, ?> m) {
        return ((m == null) || (m.size() == 0));
    }

    /**
     * 文字列が空ではないかを検査する
     *
     * @param s CharSequenceオブジェクトをセットする
     * @return boolean 文字列が空ではないならtrueが戻る
     */
    public static final boolean isNotEmpty(CharSequence s) {
        return ((s != null) && (s.length() > 0));
    }
    /**
     * 対象の文字が日本語か否かを検査します
     * @param str 対象の文字列をセット 
     * @return boolean 日本語の場合trueが戻ります
     */
    public static final boolean isJapanaese(String str) {
        return str.matches(
        "[\\p{InHiragana}\\p{InKatakana}\\p{InCJKUnifiedIdeographs}\\p{InCJKSymbolsAndPuctuation}]+");
    }

    /**
     * コレクションが空ではないかどうかを検査する
     *
     * @param c コレクションをセットする
     * @return boolean コレクションが空ではないならtrueが戻る
     */
    public static final boolean isNotEmpty(Collection<?> c) {
        return ((c != null) && (c.size() > 0));
    }

    /**
     * マップが空ではないかどうかを検査する
     *
     * @param m マップをセットする
     * @return boolean マップが空ではないならtrueが戻る
     */
    public static final boolean isNotEmpty(Map<?, ?> m) {
        return ((m != null) && (m.size() > 0));
    }

    /**
     * 半角カタカナ文字一つをを全角カタカナに変換します
     *
     * @param kana 半角カタカナが含まれる文字列をセット
     * @return char 非らかなに変換された文字列が戻ります
     */
    public static final char toKanaFull(char kana) {
        int index;

        if ((index = hanKana.indexOf(kana)) >= 0) {
            kana = zenKana.charAt(index);
        }

        return kana;
    }

    /**
     * 半角カタカナを全角カタカナに変換する日本語正規化
     *
     * @param kana カタカナ文字列をセット
     * @return String 全角カナに変換された文字列が戻ります
     */
    public static final String toKanaFull(String kana) {
        StringBuilder str2;
        char kkv;
        str2 = new StringBuilder();
        for (int i = 0; i < kana.length(); i++) {
            kkv = toKanaFull(kana.charAt(i));
            if ( kkv == '゛' ) {
                kkv = str2.charAt(str2.length() -1);
                kkv ++;
                str2.deleteCharAt(str2.length() -1);
            } else if (kkv == '゛') {
                kkv = str2.charAt(str2.length() -1);
                kkv +=2;
                str2.deleteCharAt(str2.length() -1);
            }
            str2.append(kkv);

        }
        return str2.toString();
    }

    /**
     * 全角/半角カタカナをひらがなにする
     *
     * @param kana カタカナ文字列をセット
     * @return String 全角ひらがなに変換された文字列が戻ります
     */
    public static final String toHirakana(String kana) {
        StringBuilder str2;
        str2 = new StringBuilder();
        char ch;
        kana = toKanaFull(kana);
        for (int i = 0; i < kana.length(); i++) {
            ch = kana.charAt(i);
            //idx = kana2.indexOf(ch);
            if (ch >= 0x30A0 && ch <=0x30FA) {
                ch -= 0x60;
            }
            str2.append(ch);
        }
        return str2.toString();
    }

    /**
     * 文字列の左側の空白をトリムします
     * @param text 対象の文字列をセット
     * @return String トリム済みの文字列が戻ります
     */
    public static final String ltrim(final String text) {
        return ltrim(text, null);
    }

    /**
     * 文字列の左側をトリムします
     * @param text 対象の文字列をセット
     * @param trimText トリム文字列をセット
     * @return String トリム済みの文字列が戻ります
     */
    public static final String ltrim(final String text, String trimText) {
        if (text == null) {
            return null;
        }
        if (trimText == null) {
            trimText = " ";
        }
        int pos = 0;
        for (; pos < text.length(); pos++) {
            if (trimText.indexOf(text.charAt(pos)) < 0) {
                break;
            }
        }
        return text.substring(pos);
    }
    /**
     * 文字列の右側の空白をトリムします
     * @param text 対象の文字列をセット
     * @return String トリム済みの文字列が戻ります
     */
    public static final String rtrim(final String text) {
        return rtrim(text, null);
    }
    /**
     * 文字列の右側をトリムします
     * @param text 対象の文字列をセット
     * @param trimText トリム文字列をセット
     * @return String トリム済みの文字列が戻ります
     */
    public static final String rtrim(final String text, String trimText) {
        if (text == null) {
            return null;
        }
        if (trimText == null) {
            trimText = " ";
        }
        int pos = text.length() - 1;
        for (; pos >= 0; pos--) {
            if (trimText.indexOf(text.charAt(pos)) < 0) {
                break;
            }
        }
        return text.substring(0, pos + 1);
    }
    /**
     * 任意のサフィクスをトリムします
     * @param text 対象の文字列をセット
     * @param suffix トリムするサフィクスをセット
     * @return Sting トリム済みの文字列が戻ります
     */
    public static final CharSequence trimSuffix(final CharSequence text
            , CharSequence suffix) {
        if (text == null) {
            return null;
        }
        if (suffix == null) {
            return text;
        }
        if (String.valueOf(text).endsWith(String.valueOf(suffix))) {
            return text.subSequence(0, text.length() - suffix.length());
        }
        return text;
    }
    /**
     * 任意のプレフィクスをトリムします
     * @param text 対象の文字列をセット
     * @param prefix トリムするプレフィクスをセット
     * @return Sting トリム済みの文字列が戻ります
     */
    public static final CharSequence trimPrefix(final CharSequence text
            , CharSequence prefix) {
        if (text == null) {
            return null;
        }
        if (prefix == null) {
            return text;
        }
        if ( String.valueOf(text).startsWith(String.valueOf(prefix))) {
            return text.subSequence(0, prefix.length());
        }
        return text;
    }
    /**
     * 任意の文字をトリムします
     * @param text 対象の文字列をセット
     * @param trimString トリム対象の文字列をセット
     * @return Sting トリム済みの文字列が戻ります
     */
    public static final CharSequence trim(final CharSequence text) {
        CharSequence result = trimPrefix(text, " ");
        return trimSuffix(result, " ");
    }
    /**
     * 任意の文字をトリムします
     * @param text 対象の文字列をセット
     * @param trimString トリム対象の文字列をセット
     * @return Sting トリム済みの文字列が戻ります
     */
    public static final CharSequence trim(final CharSequence text
            , CharSequence trimString) {
        CharSequence result = trimPrefix(text, trimString);
        return trimSuffix(result, trimString);
    }
    
    /**
     * 文字列の先頭をデキャピタライズします
     * @param name 対象の文字列をセット
     * @return String 加工済みの文字列が戻ります
     */
    public static String decapitalize(String name) {
        if (isEmpty(name)) {
            return name;
        }
        char[] chars = name.toCharArray();
        if (chars.length >= 2 && Character.isUpperCase(chars[0])
                && Character.isUpperCase(chars[1])
                && chars[0] != 'J') { //頭文字'J'はJSDKのクラスなので無視して小文字化
            return name;
        }
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }
    /**
     * 文字列の先頭をキャピタライズします
     * @param name 対象の文字列をセット
     * @return String 加工済みの文字列が戻ります
     */
    public static String capitalize(String name) {
        if (isEmpty(name)) {
            return name;
        }
        char[] chars = name.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }
    /**
     * バイト列を16進数文字列表現で戻します
     * @param bytes 対象のバイト列をセット
     * @return String 16進表記の文字列が戻ります
     */
    public static String toHex(byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; ++i) {
            sb.append(Character.forDigit((bytes[i] & 0xf0) >> 4, 16));
            sb.append(Character.forDigit((bytes[i] & 0x0f), 16));
        }
        return sb.toString();
    }
    /**
     * 対象の文字列をキャメライズします
     * @param s 対象の文字列をセット
     * @return String 加工済みの文字列が戻ります
     */
    public static String camelize(String s) {
        if (s == null) {
            return null;
        }
        s = s.toLowerCase();
        String[] array = s.split("_");
        if (array.length == 1) {
            return StrUtil.capitalize(s);
        }
        StringBuilder buf = new StringBuilder(40);
        for (int i = 0; i < array.length; ++i) {
            buf.append(StrUtil.capitalize(array[i]));
        }
        return buf.toString();
    }
    /**
     * 対象の文字列をデキャメライズします
     * @param s 対象の文字列をセット
     * @return String 加工済みの文字列が戻ります
     */
    public static String decamelize(String s) {
        if (s == null) {
            return null;
        }
        if (s.length() == 1) {
            return s.toUpperCase();
        }
        StringBuilder buf = new StringBuilder(40);
        int pos = 0;
        for (int i = 1; i < s.length(); ++i) {
            if (Character.isUpperCase(s.charAt(i))) {
                if (buf.length() != 0) {
                    buf.append('_');
                }
                buf.append(s.substring(pos, i).toUpperCase());
                pos = i;
            }
        }
        if (buf.length() != 0) {
            buf.append('_');
        }
        buf.append(s.substring(pos, s.length()).toUpperCase());
        return buf.toString();
    }


    /**
     * 文字列を別な文字列に置き換えます
     * @param text 変換対象の文字列をセット
     * @param fromText 元の文字列をセット
     * @param toText 変換後の文字列をセット
     * @return String 変換された文字列が戻ります
     */
    /*
    public static final String replace(String text, String fromText,
            String toText) {

        if (text == null || fromText == null || toText == null) {
            return null;
        }
        StringBuilder buf = new StringBuilder(100);
        int pos = 0;
        int pos2 = 0;
        while (true) {
            pos = text.indexOf(fromText, pos2);
            if (pos == 0) {
                buf.append(toText);
                pos2 = fromText.length();
            } else if (pos > 0) {
                buf.append(text.substring(pos2, pos));
                buf.append(toText);
                pos2 = pos + fromText.length();
            } else {
                buf.append(text.substring(pos2));
                break;
            }
        }
        return buf.toString();
    }
    */
    /**
     * 文字列を特定の区切り文字で分割します
     * @param str 対象の文字列をセット
     * @param delim 区切り文字をセット
     * @return String[] 分割された文字列が配列に格納されて戻ります
     */
    /*
    public static String[] split(String str, String delim) {
        if (str == null) {
            return EMPTY_STRINGS;
        }
        List list = new ArrayList();
        StringTokenizer st = new StringTokenizer(str, delim);
        while (st.hasMoreElements()) {
            list.add(st.nextElement());
        }
        return (String[]) list.toArray(new String[list.size()]);
    }
    */
    
    public static final String STRING_SHORT_NAME = "String";
    public static final String STRING_QUALIFY_NAME = "java.lang.String";
    
    public static final String INT_PRIMITIVE_NAME = "int";
    public static final String INT_SHORT_NAME = "Integer";
    public static final String INT_QUALIFY_NAME = "java.lang.Integer";
    
    public static final String LONG_SHORT_NAME = "Long";
    public static final String LONG_QUALIFY_NAME = "java.lang.Long";
    
    public static final String FLOAT_SHORT_NAME = "Float";
    public static final String FLOAT_QUALIFY_NAME = "java.lang.Float";
    
    public static final String BOOL_PRIMITIVE_NAME = "bool";
    public static final String BOOL_SHORT_NAME = "Boolean";
    public static final String BOOL_QUALIFY_NAME = "java.lang.Boolean";
    /**
     * 型の名称を類推します
     * @param type　型名をセット
     * @return String 型の正式な名前が戻ります
     */
    public static String getPrimitiveTypeName(String dataType) {
        //文字列はOK
        if ((dataType.equalsIgnoreCase(STRING_SHORT_NAME)) 
                || (dataType.equalsIgnoreCase(STRING_QUALIFY_NAME))) {
            return STRING_SHORT_NAME;
        } else
        //整数型の検査
        if ((dataType.equalsIgnoreCase(INT_PRIMITIVE_NAME)) 
                || (dataType.equalsIgnoreCase(INT_SHORT_NAME)) 
                || (dataType.equalsIgnoreCase(INT_QUALIFY_NAME))) {
            return INT_PRIMITIVE_NAME;
        } else
        //長整数型の検査
        if ((dataType.equalsIgnoreCase(LONG_SHORT_NAME)) 
                || (dataType.equalsIgnoreCase(LONG_QUALIFY_NAME))) {
            return LONG_SHORT_NAME;
        } else
        //不動小数型の検査
        if ((dataType.equalsIgnoreCase(FLOAT_SHORT_NAME)) 
                || (dataType.equalsIgnoreCase(FLOAT_QUALIFY_NAME))) {
            return FLOAT_SHORT_NAME;
        } else
        //整数型の検査
        if ((dataType.equalsIgnoreCase(BOOL_PRIMITIVE_NAME)) 
                || (dataType.equalsIgnoreCase(BOOL_SHORT_NAME)) 
                || (dataType.equalsIgnoreCase(BOOL_QUALIFY_NAME))) {
            return BOOL_PRIMITIVE_NAME;
        }
        return STRING_SHORT_NAME;
    }
    /**
     * 文字列をnull、文字ケース関係無く比較する
     * @param str1 文字列1をセット
     * @param str2 文字列2をセット
     * @param comparator 比較に使用するコンパレータをセット
     * @return true 文字列が等しい場合はtrueが戻ります
     */
    public static boolean equals(String str1, String str2,
            Comparator<String> comparator) {
        return comparator.compare(str1, str2) == 0;
    }
}
