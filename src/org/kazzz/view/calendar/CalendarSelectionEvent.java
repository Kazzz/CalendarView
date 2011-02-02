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

import java.util.EventObject;


/**
 * カレンダが選択された時の発生するイベントオブジェクトを提供します
 * 
 * @author Kazz.
 * @since JDK1.6 Android Level 4
 *
 */
@SuppressWarnings("serial")
public class CalendarSelectionEvent extends EventObject {
    protected CalendarView calendarView;
    protected DateInfo dateInfo;
    /**
     * コンストラクタ
     * @param source イベントソースをセット
     */
    public CalendarSelectionEvent(CalendarView calendarView
            , DateInfo dateInfo) {
        super(calendarView);
        this.calendarView = calendarView;
        this.dateInfo = dateInfo;
    }
    /**
     * 内部カレンダービューを取得します
     * @return CalendarView カレンダービューが戻ります
     */
    public CalendarView getCalendarView() {
        return this.calendarView;
    }
    /**
     * 選択したカレンダーから取得したDateInfoオブジェクトを取得します
     * @return DateInfo 日付情報が戻ります
     */
    public DateInfo getDateInfo() {
        return this.dateInfo;
    }
}
