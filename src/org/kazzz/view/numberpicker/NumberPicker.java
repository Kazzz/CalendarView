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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.kazzz.R;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.method.NumberKeyListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * android SDKで公開されていない、ナンバーピッカーを提供します
 * 
 * @author Kazz.
 * @since JDK1.5 Android Level 4
 *
 */

public class NumberPicker extends LinearLayout {
    @SuppressWarnings("unused")
    private static final String TAG = "NumberPicker";
    private static final String NUMBER_PICKER_CLASS_NAME;
    static {
        final int sdkVersion = Build.VERSION.SDK_INT;
        // 8=Build.VERSION_CODES.FROYO
        if (sdkVersion < 8) {
            NUMBER_PICKER_CLASS_NAME = "com.android.internal.widget.NumberPicker";
        } else {
            NUMBER_PICKER_CLASS_NAME = "android.widget.NumberPicker";
        }
    }
   /**
     * 数値が変更された際のコールバックインタフェース 
     */
    public interface OnChangedListener {
        /**
         * @param picker リスナに関連付けられたNumberPickerのインスタンスをセット
         * @param oldVal 以前の値をセット
         * @param newVal 新たな値をセット
         */
        void onChanged(NumberPicker picker, int oldVal, int newVal);
    }

    /**
     * 数値のフォーマット用インタフェース
     */
    public interface Formatter {
        String toString(int value);
    }

    /*
     * 数値2桁のためのFormetterインタフェース実装クラスを提供します
     */
    public static final NumberPicker.Formatter TWO_DIGIT_FORMATTER =
            new NumberPicker.Formatter() {
                final StringBuilder mBuilder = new StringBuilder();
                final java.util.Formatter mFmt = new java.util.Formatter(mBuilder);
                final Object[] mArgs = new Object[1];
                public String toString(int value) {
                    mArgs[0] = value;
                    mBuilder.delete(0, mBuilder.length());
                    mFmt.format("%02d", mArgs);
                    return mFmt.toString();
                }
        };
    /* ハンドラ */
    private final Handler handler;
    
    /* 非同期処理用のRunnable */
    private final Runnable runnable = new Runnable() {
        public void run() {
            if (NumberPicker.this.increment) {
                NumberPicker.this.changeCurrent(NumberPicker.this.current 
                        + NumberPicker.this.nextIncrementTerms());
                NumberPicker.this.handler.postDelayed(this, NumberPicker.this.speed);
            } else if (NumberPicker.this.decrement) {
                NumberPicker.this.changeCurrent(NumberPicker.this.current - 
                        NumberPicker.this.prevIncrementTerms());
                NumberPicker.this.handler.postDelayed(this, NumberPicker.this.speed);
            }
        }
    };

    private final EditText text;
    private final InputFilter numberInputFilter;

    private String[] displayedValues;

    /**
     * 初期値(開始時の値)
     */
    private int start;

    /**
     * 終了値(最大値)
     */
    private int end;

    /**
     * 加減値の組
     */
    private int[] incrementTerms = new int[]{1};
    
    private int currentTerms = 0;
    
    /**
     * 現在の値
     */
    private int current;

    /**
     * 一つ前の値
     */
    private int previous;
    
    private OnChangedListener listener;
    private Formatter formatter;
    private long speed = 300;

    private boolean increment;
    private boolean decrement;
    
    private boolean buttonBackgroundInitialized;

    /**
     * コンストラクタ
     * @param context コンテキストをセット
     */
    public NumberPicker(Context context) {
        this(context, null);
    }

    /**
     * コンストラクタ
     * @param context コンテキストをセット
     * @param attrs XMLから入力された属性をセット
     */
    public NumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOrientation(VERTICAL);
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.number_picker, this, true);
        handler = new Handler();

        OnClickListener clickListener = new OnClickListener() {
            public void onClick(View v) {
                validateInput(text);
                if (!text.hasFocus()) text.requestFocus();

                // now perform the increment/decrement
                if (R.id.increment == v.getId()) {
                    changeCurrent(NumberPicker.this.current 
                            + NumberPicker.this.nextIncrementTerms());
                } else if (R.id.decrement == v.getId()) {
                    changeCurrent(NumberPicker.this.current 
                            - NumberPicker.this.prevIncrementTerms());
                }
            }
        };

        OnFocusChangeListener focusListener = new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {

                /* When focus is lost check that the text field
                 * has valid values.
                 */
                if (!hasFocus) {
                    validateInput(v);
                }
            }
        };

        OnLongClickListener longClickListener = new OnLongClickListener() {
            /**
             * We start the long click here but rely on the {@link NumberPickerButton}
             * to inform us when the long click has ended.
             */
            public boolean onLongClick(View v) {
                /* The text view may still have focus so clear it's focus which will
                 * trigger the on focus changed and any typed values to be pulled.
                 */
                text.clearFocus();

                if (R.id.increment == v.getId()) {
                    NumberPicker.this.increment = true;
                    handler.post(NumberPicker.this.runnable);
                } else if (R.id.decrement == v.getId()) {
                    NumberPicker.this.decrement = true;
                    handler.post(NumberPicker.this.runnable);
                }
                return true;
            }
        };

        InputFilter inputFilter = new NumberPickerInputFilter();
        this.numberInputFilter = new NumberRangeKeyListener();
        this.incrementButton = (NumberPickerButton) findViewById(R.id.increment);
        this.incrementButton.setOnClickListener(clickListener);
        this.incrementButton.setOnLongClickListener(longClickListener);
        this.incrementButton.setNumberPicker(this);

        this.decrementButton = (NumberPickerButton) findViewById(R.id.decrement);
        this.decrementButton.setOnClickListener(clickListener);
        this.decrementButton.setOnLongClickListener(longClickListener);
        this.decrementButton.setNumberPicker(this);

        this.text = (EditText) findViewById(R.id.timepicker_input);
        this.text.setOnFocusChangeListener(focusListener);
        this.text.setFilters(new InputFilter[] {inputFilter});
        this.text.setRawInputType(InputType.TYPE_CLASS_NUMBER);

        if (!this.isEnabled()) {
            this.setEnabled(false);
        }
    }

    /* (non-Javadoc)
     * @see android.widget.LinearLayout#onLayout(boolean, int, int, int, int)
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        this.setWidgetResource();
    }
    protected void setWidgetResource() {
        if (this.buttonBackgroundInitialized) {
            return;
        }

        try {
            final Context context = getContext();
            final ClassLoader cl = context.getClassLoader();
            final Class<?> clazz = cl.loadClass(NUMBER_PICKER_CLASS_NAME);
            final Constructor<?> constructor = clazz.getConstructor(Context.class);
            final Object obj = constructor.newInstance(context);
            final Class<?> c = obj.getClass();

            {
                final Field field = c.getDeclaredField("mIncrementButton");
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                final ImageButton internalIncrementButton = (ImageButton) field.get(obj);
                this.incrementButton.setBackgroundDrawable(internalIncrementButton.getBackground());
            }

            {
                final Field field = c.getDeclaredField("mText");
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                final EditText internalText = (EditText) field.get(obj);
                this.text.setBackgroundDrawable(internalText.getBackground());
                this.text.setTextColor(internalText.getTextColors());
                // TextSizeを適用すると Android 2.2 800x480(hdpi) で、テキストが大きくなってしまうので、コメントアウト。
                //this.text.setTextSize(internalText.getTextSize());
            }

            {
                final Field field = c.getDeclaredField("mDecrementButton");
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                final ImageButton internalDecrementButton = (ImageButton) field.get(obj);
                this.decrementButton.setBackgroundDrawable(internalDecrementButton.getBackground());
            }
            this.buttonBackgroundInitialized = true;
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * 一つ前の増分(つまり減分)を取得します
     * @return 一つ前の増分が戻ります
     */
    protected int prevIncrementTerms() {
        if ( this.currentTerms -1  < 0 ) {
            this.currentTerms = this.incrementTerms.length-1;
        } else {
            this.currentTerms -= 1;
        }
        return this.incrementTerms[this.currentTerms];
    }
    /**
     * 次の増分を取得します
     * @return int 次の贈分値が戻ります
     */
    protected int nextIncrementTerms() {
        if ( this.currentTerms +1  >= this.incrementTerms.length ) {
            this.currentTerms = 0;
        } else {
            this.currentTerms += 1;
        }
        return this.incrementTerms[this.currentTerms];
    }

    /* (non-Javadoc)
     * @see android.view.View#setEnabled(boolean)
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.incrementButton.setEnabled(enabled);
        this.decrementButton.setEnabled(enabled);
        this.text.setEnabled(enabled);
    }

    /**
     * OnChangedListenerを設定します
     * @param listener リスナをセット
     */
    public void setOnChangeListener(OnChangedListener listener) {
        this.listener = listener;
    }

    /**
     * フォーマッタを設定します
     * @param formatter フォーマッタをセット
     * will be used
     */
    public void setFormatter(Formatter formatter) {
        this.formatter = formatter;
    }

    /**
     * 値のとり得る範囲を設定します
     *
     * @param start (この値を含む)開始値をセット
     * @param end (この値を含む)最終値をセット
     */
    public void setRange(int start, int end) {
        this.setRange(start, end, null/*displayedValues*/);
    }

    /**
     * 値のとり得る範囲を設定します(このメソッドはピッカーの表示をリセットします)
     *
     * @param start (この値を含む)開始値をセット
     * @param end (この値を含む)最終値をセット
     * @param displayedValues ユーザに表示される値の組をセット
     */
    public void setRange(int start, int end, String[] displayedValues) {
        this.displayedValues = displayedValues;
        this.start = start;
        this.end = end;
        if ( this.current == 0) this.current = start;
        this.updateView();
    }

    /**
     * 現在の値を設定します
     *
     * @param current 現在の値をセット
     * @throws IllegalArgumentException 値がとりうる範囲内に無い場合にスロー
     */
    public void setCurrent(int current) {
        if (current < this.start || current > this.end) {
            throw new IllegalArgumentException(
                    "current should be >= start and <= end");
        }
        this.current = current;
        this.updateView();
        //Log.d(TAG, "++ setCurrent : " + current + " ++ " );
    }

    /**
     * +/-ボタンを長押しした場合の加減の速度を設定します
     *
     * @param speed 値の変わるスピード(msec/値)を設定します。デフォルトは300msです
     */
    public void setSpeed(long speed) {
        this.speed = speed;
    }
    /**
     * 数値を文字列にフォーマットします
     * @param value 値をセット
     * @return String フォーマットされた値が戻ります
     */
    private String formatNumber(int value) {
        return (this.formatter != null)
                ? this.formatter.toString(value)
                : String.valueOf(value);
    }

    /**
     * 現在の値を変更するとともに、過去の値を上書きします
     * @param current ナンバーピッカーの新たな値をセットします
     */
    protected void changeCurrent(int current) {
        // Wrap around the values if we go past the start or end
        if (current > this.end) {
            current = this.start;
        } else if (current < this.start) {
            current = this.end;
        }
        this.previous = this.current;
        this.current = current;
        this.notifyChange();
        this.updateView();
    }

    /**
     * リスナナンバーピッカーの値の変更を通知します
     */
    private void notifyChange() {
        if (this.listener != null) {
            this.listener.onChanged(this, this.previous, this.current);
        }
    }

    /**
     * ナンバーピッカーのビューを更新します
     * {displayedValues}が設定されている場合、序数が一致するその中の値を表示します
     */
    private void updateView() {
        /* If we don't have displayed values then use the
         * current number else find the correct value in the
         * displayed values for the current number.
         */
        if (this.displayedValues == null) {
            text.setText(formatNumber(this.current));
        } else {
            text.setText(this.displayedValues[this.current - start]);
        }
        text.setSelection(text.getText().length());
        //Log.d(TAG, "++ updateView : " + this.current + " ++ " );
    }
    /**
     * 現在のビューを書き換えます
     * @param str 現在の値を表した文字列をセット
     */
    private void validateCurrentView(CharSequence str) {
        int val = getSelectedPos(str.toString());
        if ((val >= start) && (val <= this.end)) {
            if (this.current != val) {
                this.previous = this.current;
                this.current = val;
                this.notifyChange();
            }
        }
        this.updateView();
    }
    /**
     * 現在の値を検証します
     * @param v ビューをセット
     */
    private void validateInput(View v) {
        String str = String.valueOf(((TextView) v).getText());
        if ("".equals(str)) {

            // Restore to the old value as we don't allow empty values
            this.updateView();
        } else {

            // Check the new value and ensure it's in range
            this.validateCurrentView(str);
        }
    }

    /**
     * 値を増加を取り消します
     */
    public void cancelIncrement() {
        this.increment = false;
    }

    /**
     * 値の減少を取り消します
     */
    public void cancelDecrement() {
        this.decrement = false;
    }

    /**
     * 数値の配列
     */
    private static final char[] DIGIT_CHARACTERS = new char[] {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };

    /* 値の増減に使用するナンバーピッカーボタン */
    private NumberPickerButton incrementButton;
    private NumberPickerButton decrementButton;

    /**
     * ナンバーピッカーのための入力フィルタを提供します
     * 
     * @author Kazzz.
     * @since JDK1.5 Android Level 4
     *
     */
    private class NumberPickerInputFilter implements InputFilter {
        public CharSequence filter(CharSequence source, int start, int end,
                Spanned dest, int dstart, int dend) {
            if (displayedValues == null) {
                return numberInputFilter.filter(source, start, end, dest, dstart, dend);
            }
            CharSequence filtered = String.valueOf(source.subSequence(start, end));
            String result = String.valueOf(dest.subSequence(0, dstart))
                    + filtered
                    + dest.subSequence(dend, dest.length());
            String str = String.valueOf(result).toLowerCase();
            for (String val : displayedValues) {
                val = val.toLowerCase();
                if (val.startsWith(str)) {
                    return filtered;
                }
            }
            return "";
        }
    }

    /**
     * ナンバーピッカー用のNumberKeyListenerを提供します
     * 
     * @author Kazzz.
     * @since JDK1.5 Android Level 4
     *
     */
    private class NumberRangeKeyListener extends NumberKeyListener {

        // XXX This doesn't allow for range limits when controlled by a
        // soft input method!
        public int getInputType() {
            return InputType.TYPE_CLASS_NUMBER;
        }

        @Override
        protected char[] getAcceptedChars() {
            return DIGIT_CHARACTERS;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                Spanned dest, int dstart, int dend) {

            CharSequence filtered = super.filter(source, start, end, dest, dstart, dend);
            if (filtered == null) {
                filtered = source.subSequence(start, end);
            }

            String result = String.valueOf(dest.subSequence(0, dstart))
                    + filtered
                    + dest.subSequence(dend, dest.length());

            if ("".equals(result)) {
                return result;
            }
            int val = getSelectedPos(result);

            /* Ensure the user can't type in a value greater
             * than the max allowed. We have to allow less than min
             * as the user might want to delete some numbers
             * and then type a new number.
             */
            if (val > NumberPicker.this.end) {
                return "";
            } else {
                return filtered;
            }
        }
    }

    /**
     * 文字列から、現在のとり得る値の位置を取得します
     * @param str 文字列をセット
     * @return int 位置を表す序数が戻ります
     */
    private int getSelectedPos(String str) {
        if (this.displayedValues == null) {
            try {
                return Integer.parseInt(str);
            } catch (NumberFormatException e) {
                /* Ignore as if it's not a number we don't care */
            }
        } else {
            for (int i = 0; i < this.displayedValues.length; i++) {
                /* Don't force the user to type in jan when ja will do */
                str = str.toLowerCase();
                if (this.displayedValues[i].toLowerCase().startsWith(str)) {
                    return this.start + i;
                }
            }

            /* The user might have typed in a number into the month field i.e.
             * 10 instead of OCT so support that too.
             */
            try {
                return Integer.parseInt(str);
            } catch (NumberFormatException e) {

                /* Ignore as if it's not a number we don't care */
            }
        }
        return this.start;
    }

    /**
     * 現在の値を取得します
     * @return int 現在の値が戻ります
     */
    public int getCurrent() {
        return this.current;
    }

    /**
     * とり得る値中の最大値を取得します
     * @return int 最大値が戻ります
     */
    protected int getEndRange() {
        return this.end;
    }

    /**
     * とり得る値中の最小値を取得します
     * @return int 最小値が戻ります
     */
    protected int getBeginRange() {
        return this.start;
    }

    /**
     * incrementTermsを取得します
     * @return int[] incrementTermsが戻ります
     */
    public int[] getIncrementTerms() {
        return this.incrementTerms;
    }

    /**
     * incrementTermsを設定します
     * @param incrementTerms incrementTermsをセットします
     */
    public void setIncrementTerms(int... incrementTerms) {
        this.incrementTerms = incrementTerms;
    }
}
