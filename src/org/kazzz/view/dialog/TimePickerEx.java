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
import org.kazzz.view.numberpicker.NumberPicker;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

/**
 * 隠されたandroid SDKの代わりにに拡張されたタイムピッカークラスを提供します
 * 
 * @author Kazzz.
 * @since JDK1.5 Android Level 4
 *
 */

public class TimePickerEx extends FrameLayout {
    private static final String TAG = "TimePickerEx";   
    /**
     * コンストラクタで使用するダミーのリスナインスタンス
     * 
     */
    private static final OnTimeChangedListener NO_OP_CHANGE_LISTENER = 
        new OnTimeChangedListener() {
            public void onTimeChanged(TimePickerEx view, int hourOfDay, int minute) {
            }
    };
    
    private int currentHour = 0; // 0-23
    private int currentMinute = 0; // 0-59

    // ui components
    private final NumberPicker hourPicker;
    private final NumberPicker minutePicker;
    
    // callbacks
    private OnTimeChangedListener onTimeChangedListener;

    /**
     * 時刻が変更されたことを監視通知するためのリスナインタフェース
     */
    public interface OnTimeChangedListener {

        /**
         * @param view このビューをセット
         * @param hourOfDay 時間をセット
         * @param minute 分をセット
         */
        void onTimeChanged(TimePickerEx view, int hourOfDay, int minute);
    }
    /**
     * コンストラクタ
     * @param context コンテキスト(大抵はActivity)をセット
     */
    public TimePickerEx(Context context) {
        this(context, null);
    }
    /**
     * コンストラクタ
     * @param context コンテキスト(大抵はActivity)をセット
     * @param attrs XMLから入力される属性リストをセット
     */
    public TimePickerEx(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    /**
     * コンストラクタ
     * @param context コンテキスト(大抵はActivity)をセット
     * @param attrs XMLから入力される属性リストをセット
     * @param defStyle 既定のスタイルをセット
     */
    public TimePickerEx(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.hour_minute_pair,            this, // we are the parent
            true);

        // 時
        this.hourPicker = (NumberPicker) findViewById(R.id.pickerHour);
        this.hourPicker.setOnChangeListener(new NumberPicker.OnChangedListener() {
            public void onChanged(NumberPicker spinner, int oldVal, int newVal) {
                currentHour = newVal;
                onTimeChanged();
            }
        });

        // 分
        this.minutePicker = (NumberPicker) findViewById(R.id.pickerMinute);
        this.minutePicker.setRange(0, 59);
        this.minutePicker.setSpeed(100);
        this.minutePicker.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);
        this.minutePicker.setOnChangeListener(new NumberPicker.OnChangedListener() {
            public void onChanged(NumberPicker spinner, int oldVal, int newVal) {
                currentMinute = newVal;
                onTimeChanged();
            }
        });

        //ピッカーの初期設定
        this.configurePickerRanges();

        //カレンダの時刻を初期化
        Calendar cal = Calendar.getInstance();
        this.setOnTimeChangedListener(NO_OP_CHANGE_LISTENER);
        
        //24Hモード固定
        this.setCurrentHour(cal.get(Calendar.HOUR));
        this.setCurrentMinute(cal.get(Calendar.MINUTE));
        
        if (!this.isEnabled()) {
            this.setEnabled(false);
        }
    }
    

    /**
     * hourPickerを取得します
     * @return NumberPicker hourPickerが戻ります
     */
    public NumberPicker getHourPicker() {
        return this.hourPicker;
    }
    /**
     * minutePickerを取得します
     * @return NumberPicker minutePickerが戻ります
     */
    public NumberPicker getMinutePicker() {
        return this.minutePicker;
    }
    /* (non-Javadoc)
     * @see android.view.View#setEnabled(boolean)
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.minutePicker.setEnabled(enabled);
        this.hourPicker.setEnabled(enabled);
    }

    /**
     * ピッカーの保持値を待避/復旧します
     */
    private static class SavedState extends BaseSavedState {

        private final int hour;
        private final int minute;

        /**
         * コンストラクタ
         * @param superState 保持対象のParcelableをセット
         * @param hour 時間をセット
         * @param minute 分をセット
         */
        private SavedState(Parcelable superState, int hour, int minute) {
            super(superState);
            this.hour = hour;
            this.minute = minute;
        }
        /**
         * コンストラクタ
         * @param in 入力となるParcelオブジェクトをセット
         */
        private SavedState(Parcel in) {
            super(in);
            this.hour = in.readInt();
            this.minute = in.readInt();
        }

        /**
         * 時間を取得します
         * @return int 時間が戻ります
         */
        public int getHour() {
            return this.hour;
        }
        /**
         * 分を取得します
         * @return int 分が戻ります
         */
        public int getMinute() {
            return this.minute;
        }

        /* (non-Javadoc)
         * @see android.view.AbsSavedState#writeToParcel(android.os.Parcel, int)
         */
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(this.hour);
            dest.writeInt(this.minute);
        }

        /**
         * シリアライズに必要なCREATEフィールドを定義します
         */
        @SuppressWarnings("unused")
        public static final Parcelable.Creator<SavedState> CREATOR
                = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }


    /* (non-Javadoc)
     * @see android.view.View#onSaveInstanceState()
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState, this.currentHour, this.currentMinute);
    }


    /* (non-Javadoc)
     * @see android.view.View#onRestoreInstanceState(android.os.Parcelable)
     */
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        this.setCurrentHour(ss.getHour());
        this.setCurrentMinute(ss.getMinute());
    }
    
    /**
     * 監視に使用するリスナインタフェースをセットします
     * @param onTimeChangedListener リスナをセット
     */
    public void setOnTimeChangedListener(OnTimeChangedListener onTimeChangedListener) {
        this.onTimeChangedListener = onTimeChangedListener;
    }

    /**
     * 現在の時間(0-23)を取得します
     * @return Integer 時間が戻ります
     */
    public Integer getCurrentHour() {
        return this.currentHour;
    }

    /**
     * 現在の時間を設定します
     * @param currentHour 現在の時間をセット
     */
    public void setCurrentHour(Integer currentHour) {
        Log.d(TAG, "++ setCurrentHour : " + currentHour + " ++ " );
        this.currentHour = currentHour;
        this.updateHourDisplay();
    }

    /**
     * 現在の分を取得します
     * @return Integer 分が戻ります
     */
    public Integer getCurrentMinute() {
        return this.currentMinute;
    }

    /**
     * 現在の分を設定します
     * @param currentMiunte 分をセットします
     */
    public void setCurrentMinute(Integer currentMinute) {
        Log.d(TAG, "++ setCurrentMinute : " + currentMinute + " ++ " );
        this.currentMinute = currentMinute;
        this.updateMinuteDisplay();
    }

    /* (non-Javadoc)
     * @see android.view.View#getBaseline()
     */
    @Override
    public int getBaseline() {
        return hourPicker.getBaseline(); 
    }

    /**
     * 現在の時間でビューを更新します
     */
    private void updateHourDisplay() {
        int currentHour = this.currentHour;
        this.hourPicker.setCurrent(currentHour);
        this.hourPicker.invalidate();
        this.onTimeChanged();
    }
    /**
     * 入力範囲のの初期化を実施します
     */
    private void configurePickerRanges() {
        this.hourPicker.setRange(0, 23);
        this.hourPicker.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);
    }
    /**
     * リスナに通知を行います
     */
    private void onTimeChanged() {
        this.onTimeChangedListener.onTimeChanged(this
                , this.getCurrentHour(), this.getCurrentMinute());
    }

    /**
     * 現在の分でビューを更新します
     */
    private void updateMinuteDisplay() {
        int currentMin = this.currentMinute;
        this.minutePicker.setCurrent(currentMin);
        this.minutePicker.invalidate();
        this.onTimeChanged();
    }
}
