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
package org.kazzz.view.dialog;

import java.util.Calendar;

import org.kazzz.R;
import org.kazzz.view.dialog.TimePickerEx.OnTimeChangedListener;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;

/**
 * 隠されているandroid SDKの代わりに時間入力ダイアログを提供します
 * 
 * @author Kazzz.
 * @since JDK1.5 Android Level 4
 *
 */

public class TimePickerDialogEx extends AlertDialog implements OnClickListener, OnTimeChangedListener {
    /**
     * 時間がセットされたことを監視、通知するためのリスナインタフェース
     */
    public interface OnTimeSetListener {

        /**
         * @param view リスナに関連づけられたNumberPickerがセットされます
         * @param hourOfDay 時間(24H)がセットされます
         * @param minute 分がセットされます
         */
        void onTimeSet(TimePickerEx view, int hourOfDay, int minute);
    }

    @SuppressWarnings("unused")
    private static final String TAG = "TimePickerDialogEx";
    private static final String HOUR = "hour";
    private static final String MINUTE = "minute";
    
    private final TimePickerEx timePicker;
    private final OnTimeSetListener callback;
    private final Calendar calendar;
    private final java.text.DateFormat dateFormat;
    
    int initialHourOfDay;
    int initialMinute;

    /**
     * コンストラクタ
     * 
     * @param context コンテキスト(大抵はActivityです)
     * @param callBack 監視通知のためのコールバックリスナをセット
     * @param hourOfDay 時間の初期値をセット
     * @param minute 分の初期値をセット
     */
    public TimePickerDialogEx(Context context, OnTimeSetListener callBack,
            int hourOfDay, int minute) {
        super(context);
        this.callback = callBack;
        this.initialHourOfDay = hourOfDay;
        this.initialMinute = minute;

        this.dateFormat = DateFormat.getTimeFormat(context);
        this.calendar = Calendar.getInstance();
        this.updateTitle(this.initialHourOfDay, this.initialMinute);
        
        this.setButton(context.getText( R.string.button_set), this);
        this.setButton2(context.getText(R.string.button_cancel), (OnClickListener) null);
        this.setIcon(R.drawable.ic_dialog_time);
        
        LayoutInflater inflater = 
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate( R.layout.time_picker_dialog, null);
        this.setView(view);
        this.timePicker = (TimePickerEx) view.findViewById(R.id.timePicker);

        // initialize state
        this.timePicker.setCurrentHour(this.initialHourOfDay);
        this.timePicker.setCurrentMinute(this.initialMinute);
        this.timePicker.setOnTimeChangedListener(this);
    }
    /**
     * timePickerを取得します
     * @return TimePickerEx timePickerが戻ります
     */
    public TimePickerEx getTimePicker() {
        return this.timePicker;
    }
    /*
     * (non-Javadoc)
     * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface, int)
     */
    public void onClick(DialogInterface dialog, int which) {
        if (this.callback != null) {
            this.timePicker.clearFocus();
            this.callback.onTimeSet(this.timePicker, this.timePicker.getCurrentHour(), 
                    this.timePicker.getCurrentMinute());
        }
    }

    /* (non-Javadoc)
     * @see org.kazzz.view.dialog.TimePickerEx.OnTimeChangedListener#onTimeChanged(org.kazzz.view.dialog.TimePickerEx, int, int)
     */
    @Override
    public void onTimeChanged(TimePickerEx view, int hourOfDay, int minutOfHour) {
        this.updateTitle(hourOfDay, minutOfHour);
        //Log.d(TAG, "++ onTimeChanged now " + hourOfDay + ":" + minutOfHour + " ++ " );
    }
    /**
     * TimePickerの時間を更新します
     * @param hourOfDay 時間をセット
     * @param minutOfHour 分をセット
     */
    public void updateTime(int hourOfDay, int minutOfHour) {
        this.timePicker.setCurrentHour(hourOfDay);
        this.timePicker.setCurrentMinute(minutOfHour);
        //Log.d(TAG, "++ updateTime now " + hourOfDay + ":" + minutOfHour + " ++ " );
    }
    /**
     * ダイアログのタイトルを設定します
     * @param hour 時間をセット
     * @param minute 分をセット
     */
    private void updateTitle(int hour, int minute) {
        this.calendar.set(Calendar.HOUR_OF_DAY, hour);
        this.calendar.set(Calendar.MINUTE, minute);
        this.setTitle(this.dateFormat.format(this.calendar.getTime()));
    }
    /*
     * (non-Javadoc)
     * @see android.app.Dialog#onSaveInstanceState()
     */
    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt(HOUR, this.timePicker.getCurrentHour());
        state.putInt(MINUTE, this.timePicker.getCurrentMinute());
        return state;
    }
    /*
     * (non-Javadoc)
     * @see android.app.Dialog#onRestoreInstanceState(android.os.Bundle)
     */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int hour = savedInstanceState.getInt(HOUR);
        int minute = savedInstanceState.getInt(MINUTE);
        this.timePicker.setCurrentHour(hour);
        this.timePicker.setCurrentMinute(minute);
        this.timePicker.setOnTimeChangedListener(this);
        this.updateTitle(hour, minute);
    }

}
