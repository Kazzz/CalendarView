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

import java.util.Calendar;

import org.kazzz.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
/**
 * マンスリー表示を行うビューを提供します
 * 
 * @author Kazzz.
 * @since JDK1.5 Android Level 4
 *
 */

public class MonthlyCalendarView extends View {
    protected static final String TAG = MonthlyCalendarView.class.getSimpleName();
    protected static final String SELECT_COL = "selectCol"; 
    protected static final String SELECT_ROW = "selectRow";
    protected static final String VIEW_STATE = "viewState";
    protected static final int DEF_WIDTH = 320; //HT-03A他
    protected static final int DEF_HEIGHT_CAPTION = (DEF_WIDTH / 7)/2; //曜日表示部の高さ
    protected static final int DEF_HEIGHT = DEF_WIDTH - DEF_HEIGHT_CAPTION; 
    
    protected float cellWidth;    // セルの横の長さ
    protected float cellHeight;   // セルの縦の長さ
    protected float captionHeight;// 曜日セルの高さ
    protected int selCol;         // 選択されたセルの列の添字
    protected int selRow;         // 選択されたセルの行の添字
    protected final Rect selRect = new Rect();

    //内部カレンダ
    protected Calendar calendar = Calendar.getInstance();
    protected int today = calendar.get(Calendar.DAY_OF_MONTH);
    protected DateInfo[][] matrix = new DateInfo[6][7]; //[週][日]
    
    //各色のデフォルト値 (デザイン時に使用する)
    protected int c_backgroud = Color.parseColor("#f0ffffff"); 
    protected int c_foregroud = Color.parseColor("#ff000000");
    protected int c_dark = Color.parseColor("#6456648f");
    protected int c_hilite = Color.parseColor("#ffffffff");
    protected int c_light = Color.parseColor("#64c6d4ef");
    protected int c_holidaty = Color.parseColor("#ffFF0000");
    protected int c_saturday = Color.parseColor("#ff0000FF");
    protected int c_selected = Color.parseColor("#64FFA500");
    
    //各種描画情報
    protected Paint background = new Paint();
    protected Paint dark = new Paint();
    protected Paint hilite = new Paint();
    protected Paint light = new Paint();
    protected Paint weekdayText = new Paint(Paint.SUBPIXEL_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);
    protected Paint holidayText = new Paint(Paint.SUBPIXEL_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);
    protected Paint selected = new Paint();
    protected FontMetrics metricsForD;
    protected FontMetrics metricsForH;
    
    //ロケール舞に曜日名の配列を取得しておく
    protected static final String[] weekdays;
    static {
        weekdays = new String[] { 
            DateUtils.getDayOfWeekString(Calendar.SUNDAY, DateUtils.LENGTH_MEDIUM), 
            DateUtils.getDayOfWeekString(Calendar.MONDAY, DateUtils.LENGTH_MEDIUM), 
            DateUtils.getDayOfWeekString(Calendar.TUESDAY, DateUtils.LENGTH_MEDIUM), 
            DateUtils.getDayOfWeekString(Calendar.WEDNESDAY, DateUtils.LENGTH_MEDIUM), 
            DateUtils.getDayOfWeekString(Calendar.THURSDAY, DateUtils.LENGTH_MEDIUM), 
            DateUtils.getDayOfWeekString(Calendar.FRIDAY, DateUtils.LENGTH_MEDIUM), 
            DateUtils.getDayOfWeekString(Calendar.SATURDAY, DateUtils.LENGTH_MEDIUM), 
        };
        //new DateFormatSymbols().getShortWeekdays();
    }
    
    /**
     * コンストラクタ (デザイン時はこちらが呼ばれる)
     * @param context
     */
    public MonthlyCalendarView(Context context) {
       super(context);
       this.setFocusable(true);
       this.setFocusableInTouchMode(true);
       this.setClickable(true);
       this.initResource(null);
    }
    /**
     * コンストラクタ
     * @param context コンテキストをセット
     * @param attrs 外部(XML)から取り込むアトリビュートをセット
     */
    public MonthlyCalendarView(Context context, AttributeSet attrs) {
       super(context, attrs);
       this.setFocusable(true);
       this.setFocusableInTouchMode(true);
       this.setClickable(true);
       this.initResource(attrs);
    }
    /**
     * リソースを初期化する
     */
    private void initResource(AttributeSet attrs) {
        //フレームワークリソースから色を取得
        Resources res = this.getContext().getResources();
        if ( res != null ) {
            this.c_backgroud = res.getColor(R.color.calendar_background);
            this.c_foregroud = res.getColor(R.color.calendar_foreground);
            this.c_dark = res.getColor(R.color.calendar_dark);
            this.c_hilite = res.getColor(R.color.calendar_hilite);
            this.c_light = res.getColor(R.color.calendar_light);
            this.c_holidaty = res.getColor(R.color.calendar_holiday);
            this.c_saturday = res.getColor(R.color.calendar_saturday);        
            this.c_selected = res.getColor(R.color.calendar_selected);  
        }

        /**
         * 描画情報の初期化
         */

        //色の設定
        this.background.setColor(this.c_backgroud);
        this.dark.setColor(this.c_dark);
        this.hilite.setColor(this.c_hilite);
        this.light.setColor(this.c_light);
        this.selected.setColor(this.c_selected);
        this.holidayText.setColor(this.c_holidaty);
        
        //カレンダマトリクスの計算
        this.calcCalendarMatrix();
    }
    /* (non-Javadoc)
     * @see android.view.View#onRestoreInstanceState(android.os.Parcelable)
     */
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        //Log.d(TAG, "onRestoreInstanceState");
        Bundle bundle = (Bundle) state;
        this.select(bundle.getInt(SELECT_COL), bundle.getInt(SELECT_ROW));
        super.onRestoreInstanceState(bundle.getParcelable(VIEW_STATE));
        return;
    }
    /* (non-Javadoc)
     * @see android.view.View#onSaveInstanceState()
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable p = super.onSaveInstanceState();
        //Log.d(TAG, "onSaveInstanceState");
        Bundle bundle = new Bundle();
        bundle.putInt(SELECT_COL, selCol);
        bundle.putInt(SELECT_ROW, selRow);
        bundle.putParcelable(VIEW_STATE, p);
        return bundle;
    }
    
    
    /* (non-Javadoc)
     * @see android.view.View#onSizeChanged(int, int, int, int)
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.cellWidth = w / 7f;
        this.captionHeight = this.cellWidth / 2f;
        this.cellHeight = (h - this.captionHeight) / 6f;
        this.getRect(this.selCol, this.selRow, this.selRect);

        //Log.d(TAG, "onSizeChanged: width " + cellWidth + "" +
        //		", height " + cellHeight);
        // テキスト描画属性の設定 (平日)
        this.weekdayText.setStyle(Style.FILL);
        this.weekdayText.setTextSize(this.cellHeight * 0.35f); //テキストサイズはセルの高さの35%
        this.weekdayText.setTextScaleX(this.cellWidth / this.cellHeight); //セルのアスペクト比
        this.weekdayText.setTextAlign(Paint.Align.LEFT);
        this.weekdayText.setTypeface(Typeface.DEFAULT);
        this.metricsForD = weekdayText.getFontMetrics();
        
        // テキスト描画属性の設定 (休日)
        this.holidayText.setStyle(Style.FILL);
        this.holidayText.setTextSize(this.cellHeight * 0.17f); //テキストサイズはセルの高さの17%
        this.holidayText.setTextScaleX(this.cellWidth / this.cellHeight); //セルのアスペクト比
        this.holidayText.setTextAlign(Paint.Align.LEFT);
        this.metricsForH = holidayText.getFontMetrics();

        super.onSizeChanged(w, h, oldw, oldh);
    }

    /* (non-Javadoc)
     * @see android.view.View#onMeasure(int, int)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.setMeasuredDimension(
                this.measureWidth(widthMeasureSpec)
                , this.measureHeight(heightMeasureSpec));    
    }
    /**
     * 適切な幅を計算します
     * @param widthMeasureSpec 計算のために必要なスペックをセット
     * @return int 適切な幅を返します
     */
    private int measureWidth(int widthMeasureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            //親から指定されたサイズをそのまま受け入れる
            result = specSize;
        } else {
            //規定のサイズにパディングを加算
            result = DEF_WIDTH 
                + getPaddingLeft() + getPaddingRight();
            if (specMode == MeasureSpec.AT_MOST) {
                //最大制限サイズと計算したサイズの大きい方を使う
                result = Math.max(result, specSize);
            }
        }
        return result; 
    }
    /**
     * 適切な高さを返します
     * @param heightMeasureSpec 計算のために必要なスペックをセット
     * @return 適切な高さを返します
     */
    private int measureHeight(int heightMeasureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            //親から指定されたサイズをそのまま受け入れる
            result = specSize;
        } else {
            //規定のサイズにパディングを加算
            result = DEF_HEIGHT
                + getPaddingTop() + getPaddingBottom();
            if (specMode == MeasureSpec.AT_MOST) {
                //最大制限サイズと計算したサイズの小さい方を使う
                result = Math.min(result, specSize);
            }
        }
        return result;  
    }
    /* (non-Javadoc)
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override
    protected void onDraw(Canvas canvas) {
        //描画対象の幅と高さを取得しておく
        int width = this.getWidth();
        int height = this.getHeight();
        
        //背景を描画
        //canvas.drawRect(0, 0, width, height , this.background);
        

        // セル目を区切る線を描画
        float offsetY = 0;
        for (int i = 0; i < 8; i++) {
            // |
            canvas.drawLine(i * this.cellWidth, 0
                    , i * this.cellWidth, height, this.light);
            // ||
            canvas.drawLine(i * this.cellWidth + 1, 0
                    , i * this.cellWidth + 1, height, this.hilite);

            switch (i) {
                case 0:
                    offsetY =  0;
                    break;
                case 1:
                    offsetY +=  this.captionHeight;
                    break;
    
                default:
                    offsetY +=  this.cellHeight;
                    break;
            }
            
            // --
            canvas.drawLine(0, offsetY, width
                    , offsetY, this.dark);
            // ==
            canvas.drawLine(0, offsetY + 1
                    , width, offsetY + 1, this.hilite);
                
        }


        //曜日見出しを描画
        //センタリング
        this.weekdayText.setTextAlign(Paint.Align.CENTER);
        float alignX = this.cellWidth / 2;
        // Y軸方向でセンタリングする。
        //float alignY = this.cellHeight / 2 - (metricsForD.ascent + metricsForD.descent) / 2;
        float alignY = (this.cellHeight / 4) - ((metricsForD.ascent + metricsForD.descent) / 4);
        for (int col = 0; col < 7; col++) {
            //曜日で色を変える
            this.setForground(weekdayText, col);
            //曜日描画
            canvas.drawText(weekdays[col], (col * this.cellWidth) + alignX
                    , alignY, weekdayText);
        }
        
        //日付
        this.weekdayText.setTextAlign(Paint.Align.LEFT);
        alignX = this.cellWidth / 10;
        // Y軸方向で上寄せする。
        alignY = this.cellHeight / 4 - (metricsForD.ascent + metricsForD.descent) / 2;
        
        //祭日名
        float holidayAlignX = this.cellWidth / 10;
        // Y軸方向で上寄せする。
        float holidayAlignY = 
            this.cellHeight / 3 - (metricsForH.ascent + metricsForH.descent) / 2;
        
        offsetY = this.captionHeight;
        for (int row = 1; row < 7; row++) {
            for (int col = 0; col < 7; col++) {
                if (this.matrix[row-1][col] != null) {
                    //祭日で色を変える
                    if (this.matrix[row-1][col].isHoliday) {
                        this.weekdayText.setColor(this.c_holidaty);
                    } else {
                        //曜日で色を変える              
                        this.setForground(weekdayText, col);
                    }
                    String formatted = String.valueOf(this.matrix[row-1][col].day);
                    if ( this.isToday(this.matrix[row-1][col])) {
                        this.weekdayText.setTypeface(Typeface.DEFAULT_BOLD);
                    } else {
                        this.weekdayText.setTypeface(Typeface.DEFAULT);
                    }
                    canvas.drawText( formatted 
                            , ( col * this.cellWidth ) + alignX
                            , offsetY + alignY //( row * this.cellHeight ) + y
                            , this.weekdayText);
                    
                    //祭日名を描画
                    if ( this.matrix[row-1][col].isHoliday ) {
                        canvas.drawText( this.matrix[row-1][col].holidayName 
                                , ( col * this.cellWidth ) + holidayAlignX
                                , offsetY + holidayAlignY + alignY // ( row * this.cellHeight ) + hy + y 
                                , this.holidayText);
                    }

                }
            }
            offsetY =  offsetY + this.cellHeight;
        }

        // 選択されたセルを描画する...
        //Log.d(TAG, "selRect=" + selRect);
        canvas.drawRect(this.selRect, this.selected);
    }
    /**
     * 今日か否かを検査します
     * @param dateInfo 日付情報をセット
     * @return boolean　日付情報が今日を示している場合trueが戻ります
     */
    private boolean isToday(DateInfo dateInfo) {
        if ( dateInfo == null ) return false;
        //今日の情報を取得
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH)+1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        
        if ( year != dateInfo.year 
                || month != dateInfo.month 
                || day != dateInfo.day) return false;
        
        DateInfo todayInfo = new DateInfo(year, month, day); 
        return ( todayInfo.equals(dateInfo) );
    }
    /**
     * フォアグラウンド
     * @param foreground　対象のペイントオブジェクトをセット
     * @param col 曜日を表す序数をセット
     */
    private void setForground(Paint foreground, int dayOfWeek) {
        //曜日で色を変える
        switch (dayOfWeek) {
        case 0: //日曜日
            foreground.setColor(this.c_holidaty);
            break;
        case 6: //土曜日
            foreground.setColor(this.c_saturday);
            break;
        default:
            foreground.setColor(this.c_foregroud);
            break;
        }
    }
    /**
     * カレンダーを計算する
     */
    protected void calcCalendarMatrix() {
        // 月の初めの曜日を取得

        //配列クリア
        for ( int row = 0; row < 6; row++ ) {
            for ( int col = 0; col < 7; col++ ) {
                this.matrix[row][col] = null;
            }
        }
        calendar.set(Calendar.DATE, 1); //初日にセット
        int startDay = calendar.get(Calendar.DAY_OF_WEEK); //曜日
        
        // 月末の日付を取得(次の月の初日-1日)
        this.calendar.add(Calendar.MONTH, 1);
        this.calendar.add(Calendar.DATE, -1);
        int lastDate = calendar.get(Calendar.DATE);
        
        //ここの時点でカレンダは3/30
        
        // マトリクス生成
        int row = 0;
        int column = startDay - 1; // 曜日は1オリジンなので-1する: 日曜日 = 1, 月曜日 = 2, ...
        for (int date = 1; date <= lastDate; date++) {
            this.matrix[row][column] = new DateInfo(
                    this.calendar.get(Calendar.YEAR)
                    , this.calendar.get(Calendar.MONTH) + 1 //月は0オリジン
                    , date);
            if (column == 6) {
                row++;
                column = 0;
            } else {
                column++;
            }
        }
        this.invalidate();
    }
    /* (non-Javadoc)
     * @see android.view.View#onTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN) {
            return super.onTouchEvent(event);
        }
        int col = (int)(event.getX() / this.cellWidth);

        //曜日見出しをオフセットとする
        float y = event.getY() - this.captionHeight;
        if ( y <= 0 ) {
            this.select(col, 0);
        } else {
            this.select(col, 1 + (int)( y / this.cellHeight));
        }
        //Log.d(TAG, "onTouchEvent: x " + selCol + ", y " + selRow);
        return false; //false 親にイベントを戻す  true ここでイベント止める
    }
    /* (non-Javadoc)
     * @see android.view.View#onTrackballEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onTrackballEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int col = (int)( (event.getX() * event.getXPrecision())  / this.cellWidth);

            //曜日見出しをオフセットとする
            float y = ( event.getY() * event.getYPrecision()) - this.captionHeight;
            if ( y <= 0 ) {
                this.select(col, 0);
            } else {
                this.select(col, 1 + (int)( y / this.cellHeight));
            }
            return false; //false 親にイベントを戻す  true ここでイベント止める
        }
        return super.onTrackballEvent(event);
    }

    /* (non-Javadoc)
     * @see android.view.View#onKeyDown(int, android.view.KeyEvent)
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
         //Log.d(TAG, "onKeyDown: keycode=" + keyCode + ", event="
         //       + event);
          switch (keyCode) {
          case KeyEvent.KEYCODE_DPAD_UP:
              this.select(this.selCol, this.selRow - 1);
              break;
          case KeyEvent.KEYCODE_DPAD_DOWN:
              this.select(this.selCol, this.selRow + 1);
              break;
          case KeyEvent.KEYCODE_DPAD_LEFT:
              this.select(this.selCol - 1, this.selRow);
              break;
          case KeyEvent.KEYCODE_DPAD_RIGHT:
              this.select(this.selCol + 1, this.selRow);
              break;
          case KeyEvent.KEYCODE_ENTER:
              this.select(this.selCol, this.selRow);
              break;
          case KeyEvent.KEYCODE_SPACE:
              this.select(this.selCol, this.selRow);
              break;
          default:
             return super.onKeyDown(keyCode, event);
          }
          return true;
    }

    /**
     * calendarを取得します
     * @return Calendar calendarが戻ります
     */
    public Calendar getCalendar() {
        if ( this.calendar == null ) {
            this.calendar = Calendar.getInstance();
        }
        return this.calendar;
    }
    /**
     * calendarを設定します
     * @param calendar calendarをセットします
     */
    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
        this.setToDay(today);
        this.calcCalendarMatrix();
    }
    /**
     * cellWidthを取得します
     * @return float cellWidthが戻ります
     */
    public float getCellWidth() {
        return this.cellWidth;
    }
    /**
     * cellHeightを取得します
     * @return float cellHeightが戻ります
     */
    public float getCellHeight() {
        return this.cellHeight;
    }
    /**
     * 対象の年、月を設定します
     * @param year 年をセットします
     * @param month 月をセットします
     */
    public void setCalendar(int year, int month) {
        this.calendar.clear();
        this.calendar.set(year, month-1, 1); // 引数: 1月: 0, 2月: 1, ...
        this.calcCalendarMatrix();
    }
    /**
     * 対象の年、月、日を設定します
     * @param year 年をセットします
     * @param month 月をセットします
     * @param day 日をセットします
     */
    public void setCalendar(int year, int month, int day) {
        this.calendar.clear();
        this.calendar.set(year, month-1, day); // 引数: 1月: 0, 2月: 1, ...
        this.calcCalendarMatrix();
    }
    /**
     * 今日を設定します
     * @param day 1～31までの日をセット
     */
    public void setToDay(int day) {
        for ( int row = 0; row < 6; row++ ) {
            for ( int col = 0; col < 7; col++ ) {
                if ( this.matrix[row][col] != null ) {
                    if ( this.matrix[row][col].day == day ) {
                        this.select(col, row + 1); // 0行目は見出し
                    }
                }
            }
        }
    }
    
    /**
     * 選択された情報を取得します
     * @return DateInfo 選択されたセルの情報が戻ります
     */
    public DateInfo getSelectedDateInfo() {
        //Log.d(TAG, "getSelectedInfo: row=" + this.selRow + " col=" + this.selCol );
        if ( this.selRow == 0 ) return null;
        return this.matrix[this.selRow-1][this.selCol]; //選択されるのはマトリクスではなく、画面なので行-1
    }
    /**
     * 任意の列、行を選択します 
     * @param col 列の序数をセット
     * @param row 行の序数をセット
     */
    private void select(int col, int row) {
       this.invalidate(this.selRect);
       
       this.selCol = Math.min(Math.max(col, 0), 7);
       this.selRow = Math.min(Math.max(row, 0), 6);
       this.getRect(this.selCol, this.selRow, this.selRect);
       this.invalidate(this.selRect);
    }

    /**
     * 列、行に対応した矩形領域を取得します
     * @param col 列の序数をセット
     * @param row 行の序数をセット
     * @param rect 描画する矩形をセット 
     */
    private void getRect(int col, int row, Rect rect) {
        int left, top, right, bottom;
        float offset = this.captionHeight; //曜日見出しはオフセットと見なす
        if ( row == 0 ) {
           //曜日部分の選択
           left = (int) (col * this.cellWidth);
           top = (int) (row * this.cellHeight);
           right = (int) (col * this.cellWidth + this.cellWidth);
           bottom = (int) (row * this.cellHeight + offset);
        } else {
           //日付部分の選択
           left = (int) (col * this.cellWidth);
           right = (int) ((col * this.cellWidth ) + this.cellWidth);
           //selectメソッドで行は1加算されているので、矩形の計算では1減じて元に戻す
           top = (int) ( offset +  ( (row-1) * this.cellHeight) ) ;
           bottom = (int) (offset + ( (row-1) * this.cellHeight) + this.cellHeight);
        }
        rect.set(left, top, right, bottom);
    }
}
