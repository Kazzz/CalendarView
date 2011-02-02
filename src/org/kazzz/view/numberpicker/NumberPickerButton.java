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
package org.kazzz.view.numberpicker;

import org.kazzz.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.ImageButton;

/**
 * NumberPicker用ボタンを提供します
 * 
 * @author Kazz.
 * @since JDK1.5 Android Level 4
 *
 */

public class NumberPickerButton extends ImageButton {
    private NumberPicker numberPicker;

    /**
     * コンストラクタ
     * @param context コンテキストをセット
     * @param attrs XMLから入力された属性をセット
     * @param defStyle デフォルトのスタイルをセット
     */
    public NumberPickerButton(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
    }
    /**
     * コンストラクタ
     * @param context コンテキストをセット
     * @param attrs XMLから入力された属性をセット
     */
    public NumberPickerButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    /**
     * コンストラクタ
     * @param context コンテキストをセット
     */
    public NumberPickerButton(Context context) {
        super(context);
    }

    /**
     * このボタンを使用するNumberPuckerを設定します
     * @param picker NUmberPickerをセット
     */
    public void setNumberPicker(NumberPicker picker) {
        this.numberPicker = picker;
    }

    /* (non-Javadoc)
     * @see android.view.View#onKeyUp(int, android.view.KeyEvent)
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_DPAD_CENTER)
                || (keyCode == KeyEvent.KEYCODE_ENTER)) {
            this.cancelLongpress();
        }
        return super.onKeyUp(keyCode, event);
    }
    /* (non-Javadoc)
     * @see android.view.View#onTrackballEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onTrackballEvent(MotionEvent event) {
        this.cancelLongpressIfRequired(event);
        return super.onTrackballEvent(event);
    }
    /* (non-Javadoc)
     * @see android.view.View#onTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.cancelLongpressIfRequired(event);
        return super.onTouchEvent(event);
    }
    
    /**
     * 必要であれば長押しをキャンセルします
     * @param event MotionEventをセット
     */
    private void cancelLongpressIfRequired(MotionEvent event) {
        if ((event.getAction() == MotionEvent.ACTION_CANCEL)
                || (event.getAction() == MotionEvent.ACTION_UP)) {
            this.cancelLongpress();
        }
    }
    /**
     * 長押しをキャンセルします
     */
    private void cancelLongpress() {
        if (R.id.increment == getId()) {
            this.numberPicker.cancelIncrement();
        } else if (R.id.decrement == getId()) {
            this.numberPicker.cancelDecrement();
        }
    }

}
