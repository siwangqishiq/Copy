package panyi.xyz.copy.library;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

public class SelectCursorView extends View {
    private static final String TAG = "SelectCursorView";

    private PopupWindow popWindow;

    private boolean isLeftCursor = true;

    private static final int SIZE = 80;

    private Paint mPaint;

    private SelectableText mSelectableTextView;

    private float x;
    private float y;

    private float lastX;
    private float lastY;

    private int posX = 0;
    private int posY = 0;

    private int tmpXY[] = new int[2];
    private Rect tmpRect = new Rect();

    private int paintColor = Color.parseColor("#04BE02");

    public float[] getPosXY(){
        float[] result = new float[2];
        result[0] = posX;
        result[1] = posY;
        return result;
    }

    public SelectCursorView(Context context ,SelectableText textView, boolean isLeft) {
        super(context);
        mSelectableTextView = textView;
        isLeftCursor = isLeft;
        init(context);
    }

    private void init(Context context){
        popWindow = new PopupWindow(this);
        popWindow.setWidth(SIZE);
        popWindow.setHeight(SIZE);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(paintColor);
    }

    public void show(){
        float pos[];
        if(isLeftCursor){
            pos = SelectableText.getCharPositionInTextView(mSelectableTextView , 0);
            LogUtil.i(TAG , "left :" + pos[0] +"  " + pos[1]);
        }else{
            pos = SelectableText.getCharPositionInTextView(mSelectableTextView , mSelectableTextView.getText().length());
            LogUtil.i(TAG , "right :" + pos[0] +"  " + pos[1]);
        }

        float screenPos[] = convertViewToScreenCoord(pos[0] , pos[1]);
        x = screenPos[0];
        y = screenPos[1];
        limitPositionRect();

        showByCurrentPosition(true);
        updateTextViewSelectedRange();

        showByCurrentPosition(false);
    }

    /**
     *  根据xy坐标 更新TextView选择范围
     */
    private void updateTextViewSelectedRange(){
        int oldBegin = mSelectableTextView.getSelectBegin();
        int oldEnd = mSelectableTextView.getSelectEnd();

        int begin = mSelectableTextView.getSelectBegin();
        int end = mSelectableTextView.getSelectEnd();
        float textViewCoord[] = convertScreenToViewCoord(x , y);
        if(isLeftCursor){
            begin =  SelectableText.getPreciseOffset(mSelectableTextView , (int)textViewCoord[0] , (int)textViewCoord[1]);
        }else{
            end = SelectableText.getPreciseOffset(mSelectableTextView , (int)textViewCoord[0] , (int)textViewCoord[1]);
        }

        if(begin != oldBegin || end != oldEnd){
            mSelectableTextView.onSelectRangeChanged(oldBegin , begin , oldEnd , end);
        }
    }

    private void showByCurrentPosition(boolean isShow){
        float showPosX = 0;
        float showPosY = 0;

        if(isLeftCursor){
            float[] textViewCoord = SelectableText.getCharPositionInTextView(mSelectableTextView , mSelectableTextView.getSelectBegin());
            float[] leftCoord = convertViewToScreenCoord(textViewCoord[0] , textViewCoord[1]);
            showPosX = leftCoord[0];
            showPosY = leftCoord[1];
        }else{
            float[] textViewCoord = SelectableText.getCharPositionInTextView(mSelectableTextView , mSelectableTextView.getSelectEnd());
            float[] rightCoord = convertViewToScreenCoord(textViewCoord[0] , textViewCoord[1]);
            showPosX = rightCoord[0];
            showPosY = rightCoord[1];
        }

        LogUtil.i(TAG , "showPosX = " +showPosX +"  showPosY = " + showPosY);
        if(isLeftCursor){
            posX = (int)showPosX- popWindow.getWidth();
            posY = (int)showPosY;
        }else{
            posX = (int)showPosX;
            posY = (int)showPosY;
        }

        if(isShow){//show
            popWindow.showAtLocation(mSelectableTextView ,  Gravity.NO_GRAVITY ,
                    posX,posY);
        }else{//move position
            popWindow.update(posX , posY, popWindow.getWidth() , popWindow.getHeight());
        }
    }

    public void hide(){
        popWindow.dismiss();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = super.onTouchEvent(event);
        switch (event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                ret = true;
//                LogUtil.i(TAG , "action down");
                lastX = event.getRawX();
                lastY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                moveCursor(event.getRawX() - lastX , event.getRawY() - lastY);
                lastX = event.getRawX();
                lastY = event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                LogUtil.i(TAG , "action up");
                break;
            case MotionEvent.ACTION_CANCEL:
                LogUtil.i(TAG , "action cancel");
                break;
        }//end switch
        return ret;
    }

    private void moveCursor(float dx , float dy){
        x += dx;
        y += dy;

        limitPositionRect();
//        LogUtil.i(TAG , "action move " + x +"  " + y);
        showByCurrentPosition(false);
//        float viewXY[] = convertScreenToViewCoord(x , y);
        // LogUtil.i(TAG , "action charIndex = " +viewXY[0] +"   " + viewXY[1]);

        updateTextViewSelectedRange();

        if(isLeftCursor){
            showByCurrentPosition(false);
        }
    }

    /**
     * 将屏幕坐标 转为TextView的View坐标
     * @param _x
     * @param _y
     * @return
     */
    public float[] convertScreenToViewCoord(float _x , float _y){
        float[] result = new float[2];
        mSelectableTextView.getLocationInWindow(tmpXY);
        int transX = tmpXY[0];
        int transY = tmpXY[1];
        result[0] = _x - transX;
        result[1] = _y - transY;
        return result;
    }

    /**
     * 将屏幕坐标 转为TextView的View坐标
     * @param _x
     * @param _y
     * @return
     */
    public float[] convertViewToScreenCoord(float _x , float _y){
        float[] result = new float[2];
        mSelectableTextView.getLocationInWindow(tmpXY);
        int transX = tmpXY[0];
        int transY = tmpXY[1];
        result[0] = _x + transX;
        result[1] = _y + transY;
        return result;
    }

    /**
     * 限制坐标范围 不要超出TextView界面
     *
     */
    private void limitPositionRect(){
        mSelectableTextView.getLocationOnScreen(tmpXY);
        mSelectableTextView.getGlobalVisibleRect(tmpRect);

        int minX = tmpXY[0];
        int maxX = tmpXY[0] + tmpRect.width();

        int minY = tmpXY[1];
        int maxY = tmpXY[1] + tmpRect.height();

        if(x < minX){
            x = minX;
        }else if(x > maxX){
            x = maxX;
        }

        if(y < minY){
            y = minY;
        }else if(y > maxY){
            y = maxY;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float cx = getMeasuredWidth() / 2.0f;
        float cy = getMeasuredHeight() / 2.0f;
        int radius = SIZE / 2;

        canvas.drawCircle(cx , cy , radius , mPaint);
        if(isLeftCursor){
            canvas.drawRect(cx , 0 , cx + radius , radius , mPaint);
        }else{
            canvas.drawRect(0 , 0 , radius , radius , mPaint);
        }
    }

    /**
     * 切换左右
     */
    public void swap() {
        isLeftCursor = !isLeftCursor;
        invalidate();

        //更新位置
        showByCurrentPosition(false);
    }

    public boolean isLeftCursor() {
        return isLeftCursor;
    }
}
