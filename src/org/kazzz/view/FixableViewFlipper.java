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
import android.widget.ViewFlipper;

/**
 * 拡張されたViewFlipperを提供します 
 * (android.widget.ViewFlipperのバグFixのために提供されました)
 * 
 * @author Kazz.
 * @since JDK1.5 Android Level 4
 *
 */

public class FixableViewFlipper extends ViewFlipper {
    /**
     * コンストラクタ
     * @param context コンテキストをセット
     */
    public FixableViewFlipper(Context context) {
        super(context);
    }

    /* (non-Javadoc)
     * @see android.view.View#onAttachedToWindow()
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    /* (non-Javadoc)
     * @see android.view.View#onDetachedFromWindow()
     */
    @Override
    protected void onDetachedFromWindow() {
        try {
            /**
             * getContext().unregisterReceiver(mReceiver)で回避不能な例外が発生する
             * のを避けるためにトラップ
             */
            super.onDetachedFromWindow();
        } catch (IllegalArgumentException e) {
            //ignore
        }
    }

    
}
