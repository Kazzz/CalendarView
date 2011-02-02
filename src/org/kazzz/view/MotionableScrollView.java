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
package org.kazzz.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

/**
 * 拡張されたスクロールビューを提供します
 * <p>
 * 標準のScrollViewは子のViewのY軸のモーションイベントを捕捉できません。
 * 一定の条件(子のViewの大きさが自身と等しいか小さく、スクロールが必要無い場合は子のモーション
 * イベントを阻害しない実装をしています。
 * </p>
 * @author Kazz.
 * @since JDK1.5 Android API Level 4
 *
 */

public class MotionableScrollView extends ScrollView {
    public static final int ACTION_MASK = 0xff;
    /**
     * コンストラクタ
     * @param context コンテキストをセット
     */
    public MotionableScrollView(Context context) {
        super(context);
    }
    /**
     * コンストラクタ 
     * 
     * @param context コンテキストをセット
     * @param attrs 属性セットをセット
     */
    public MotionableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * コンストラクタ
     * 
     * @param context コンテキストをセット
     * @param attrs 属性セットをセット
     * @param defStyle デフォルトスタイルをセット
     */
    public MotionableScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    
    /* (non-Javadoc)
     * @see android.widget.ScrollView#onInterceptTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();

        switch (action & ACTION_MASK) {
            case MotionEvent.ACTION_MOVE: {
                if ( this.canScroll() ) {
                    //スクロール可能な場合だけ、既定の処理を実施
                    return super.onInterceptTouchEvent(ev);
                } else {
                    //スクロールできない場合は子供のビューの邪魔しない
                    return false;
                }
            }
        }

        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 現在、スクロールが可能か否かを検査します
     * @return boolean スクロール可能な場合trueが戻ります
     */
    private boolean canScroll() {
        View child = this.getChildAt(0);
        if (child != null) {
            int childHeight = child.getHeight();
            return this.getHeight() < childHeight + this.getPaddingTop() + this.getPaddingBottom();
        }
        return false;
    }
   
}
