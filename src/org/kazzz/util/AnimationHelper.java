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

import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * アニメーション描画のためのユーティリティを提供します
 * 
 * @author Kazz.
 * @since JDK1.5 Android Level 4
 *
 */

public class AnimationHelper {
    /**
     * 右から入ってくる動作表現するアニメーションを生成、取得します
     * @return Animation 生成したアニメーションが戻ります
     */
    public static Animation inFromRightAnimation() {

        Animation inFromRight = 
            new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT
                    , +1.0f
                    , Animation.RELATIVE_TO_PARENT
                    , 0.0f
                    , Animation.RELATIVE_TO_PARENT
                    , 0.0f
                    , Animation.RELATIVE_TO_PARENT
                    , 0.0f);
        inFromRight.setDuration(350);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }
    /**
     * 左に通過していく動作表現するアニメーションを生成、取得します
     * @return Animation 生成したアニメーションが戻ります
     */
    public static Animation outToLeftAnimation() {
        Animation outtoLeft = 
            new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT
                    , 0.0f
                    , Animation.RELATIVE_TO_PARENT
                    , -1.0f
                    , Animation.RELATIVE_TO_PARENT
                    , 0.0f
                    , Animation.RELATIVE_TO_PARENT
                    , 0.0f);
        outtoLeft.setDuration(350);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }    
    /**
     * 左から入ってくる動作を表現するアニメーションを生成、取得します
     * @return Animation 生成したアニメーションが戻ります
     */
    public static Animation inFromLeftAnimation() {
        Animation inFromLeft = 
            new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT
                    , -1.0f
                    , Animation.RELATIVE_TO_PARENT
                    , 0.0f
                    , Animation.RELATIVE_TO_PARENT
                    , 0.0f
                    , Animation.RELATIVE_TO_PARENT
                    , 0.0f);
        inFromLeft.setDuration(350);
        inFromLeft.setInterpolator(new AccelerateInterpolator());
        return inFromLeft;
    }
    /**
     * 右に通過していく動作を表現するアニメーションを生成、取得します
     * @return Animation 生成したアニメーションが戻ります
     */
    public static Animation outToRightAnimation() {
        Animation outtoRight = 
            new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT
                    , 0.0f
                    , Animation.RELATIVE_TO_PARENT
                    , +1.0f
                    , Animation.RELATIVE_TO_PARENT
                    , 0.0f
                    , Animation.RELATIVE_TO_PARENT
                    , 0.0f );
        outtoRight.setDuration(350);
        outtoRight.setInterpolator(new AccelerateInterpolator());
        return outtoRight;
    }           

    /**
     * 上から下がってくる動作表現するアニメーションを生成、取得します
     * @return Animation 生成したアニメーションが戻ります
     */
    public static Animation inFromTopAnimation() {

        Animation inFromTop = 
            new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT
                    , 0.0f
                    , Animation.RELATIVE_TO_PARENT
                    , 0.0f
                    , Animation.RELATIVE_TO_PARENT
                    , -1.0f
                    , Animation.RELATIVE_TO_PARENT
                    , 0.0f);
        inFromTop.setDuration(350);
        inFromTop.setInterpolator(new AccelerateInterpolator());
        return inFromTop;
    }
    /**
     * 下に通過していく動作表現するアニメーションを生成、取得します
     * @return Animation 生成したアニメーションが戻ります
     */
    public static Animation outToBottomAnimation() {
        Animation outToBottom = 
            new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT
                    , 0.0f
                    , Animation.RELATIVE_TO_PARENT
                    , 0.0f
                    , Animation.RELATIVE_TO_PARENT
                    , 0.0f
                    , Animation.RELATIVE_TO_PARENT
                    , +1.0f);
        outToBottom.setDuration(350);
        outToBottom.setInterpolator(new AccelerateInterpolator());
        return outToBottom;
    }    

    /**
     * 下から昇って入ってくる動作を表現するアニメーションを生成、取得します
     * @return Animation 生成したアニメーションが戻ります
     */
    public static Animation inFromBottomAnimation() {
        Animation inFromBottom = 
            new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT
                    , 0.0f
                    , Animation.RELATIVE_TO_PARENT
                    , 0.0f
                    , Animation.RELATIVE_TO_PARENT
                    , 1.0f
                    , Animation.RELATIVE_TO_PARENT
                    , 0.0f);
        inFromBottom.setDuration(350);
        inFromBottom.setInterpolator(new AccelerateInterpolator());
        return inFromBottom;
    }
    /**
     * 上に通過していく動作を表現するアニメーションを生成、取得します
     * @return Animation 生成したアニメーションが戻ります
     */
    public static Animation outToTopAnimation() {
        Animation outToTop = 
            new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT
                    , 0.0f
                    , Animation.RELATIVE_TO_PARENT
                    , 0.0f
                    , Animation.RELATIVE_TO_PARENT
                    , 0.0f
                    , Animation.RELATIVE_TO_PARENT
                    , -1.0f );
        outToTop.setDuration(350);
        outToTop.setInterpolator(new AccelerateInterpolator());
        return outToTop;
    }           
}
