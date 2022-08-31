package panyi.xyz.copy.library;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class SelectableText  extends androidx.appcompat.widget.AppCompatTextView {

    public static final String TAG = "SelectableText";

    private SelectCursorView leftCursorView;
    private SelectCursorView rightCursorView;

    private int selectBegin = 0;
    private int selectEnd = 0;

    private SpannableString selectedSpan;

    public SelectableText(Context context) {
        super(context);
        init(context);
    }

    public SelectableText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SelectableText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        getViewTreeObserver().addOnScrollChangedListener(()->{
            onScrollChanged();
        });

        selectedSpan = new SpannableStringBuilder();
    }

    //外部容器滑动时 隐藏
    private void onScrollChanged(){
        //LogUtil.i(TAG , "onScrollChanged!");
        hideSelectable();
    }

//    @Override
//    public void setText(CharSequence text, BufferType type) {
//        super.setText(text, type);
//    }

    /**
     * 选择文本范围 发生修改
     */
    protected void onSelectRangeChanged(int oldBeginIndex , int newBeginIndex , int oldEndIndex , int newEndIndex){
        selectBegin = newBeginIndex;
        selectEnd = newEndIndex;

        LogUtil.i(TAG , "select range change " + selectBegin +"  " + selectEnd);
    }

    /**
     * 显示文本选择控件
     *
     */
    public void showSelectable(){
        if(leftCursorView == null){
            leftCursorView = new SelectCursorView(this.getContext() ,this, true);
        }

        if(rightCursorView == null){
            rightCursorView =new SelectCursorView(this.getContext() ,this, false);
        }

        leftCursorView.show();
        rightCursorView.show();
    }

    public void hideSelectable(){
        if(leftCursorView != null){
            leftCursorView.hide();
        }

        if(rightCursorView != null){
            rightCursorView.hide();
        }
    }


    public int getSelectBegin() {
        return selectBegin;
    }

    public int getSelectEnd() {
        return selectEnd;
    }


    /**
     *
     * @param textView
     * @param x
     * @param y
     * @return
     */
    public static int getPreciseOffset(TextView textView, int x, int y) {
        Layout layout = textView.getLayout();
        if (layout != null) {
            int topVisibleLine = layout.getLineForVertical(y);
            int offset = layout.getOffsetForHorizontal(topVisibleLine, x);

            int offsetX = (int) layout.getPrimaryHorizontal(offset);

            if (offsetX > x) {
                return layout.getOffsetToLeftOf(offset);
            } else {
                return offset;
            }
        } else {
            LogUtil.i(TAG , "layout is null!");
            return -1;
        }
    }

    public static float[] getCharPositionInTextView(TextView textView , int offset){
        float result[] = new float[]{0.0f , 0.0f};
        Layout layout = textView.getLayout();
        if (layout == null) {
            return result;
        }

        int lineOfText = layout.getLineForOffset(offset);
        int xCoordinate = (int) layout.getPrimaryHorizontal(offset);
        int yCoordinate = layout.getLineBottom(lineOfText) - 8;

        result[0] = xCoordinate;
        result[1] = yCoordinate;
        return result;
    }
}//end class
