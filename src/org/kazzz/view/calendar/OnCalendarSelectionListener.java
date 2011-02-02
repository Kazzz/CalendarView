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

import java.util.EventListener;


/**
 * カレンダーが選択されたイベントの監視を行うリスナインタフェースをを提供します
 * 
 * @author Kazzz
 * @since JDK1.5 Android Level 4
 *
 */

public interface OnCalendarSelectionListener extends EventListener {
    /**
     * カレンダーが選択された際に呼ばれるリスナ通知メソッド
     * @param event 発生したイベント
     */
    void onCalendarSelection(CalendarSelectionEvent event);
}
