package panyi.xyz.copy.library;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.text.Layout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.BackgroundColorSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;

import panyi.xyz.copy.R;

public class SelectableText  extends androidx.appcompat.widget.AppCompatTextView {

    public static final String TAG = "SelectableText";

    private SelectCursorView leftCursorView;
    private SelectCursorView rightCursorView;

    //操作浮框
    private OperateWindow operationWindow;

    private int selectBegin = 0;
    private int selectEnd = 0;

    //选中文本颜色
    private int selectedBackgroundColor = Color.RED;

    //
    private int[] viewPositionInWindow = new int[2];

    //滑动监听
    private ViewTreeObserver.OnScrollChangedListener onScrollChangedListener = ()->{
        onScrollChanged();
    };

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

    }

    //外部容器滑动时 隐藏
    private void onScrollChanged(){
        int lastLocationY = viewPositionInWindow[1];
        getLocationInWindow(viewPositionInWindow);
//        LogUtil.i(TAG , "onScrollChanged!  lastLocationY = " + lastLocationY +"  currentY = " + viewPositionInWindow[1]);
        if(Math.abs(lastLocationY - viewPositionInWindow[1]) > 0){ //超出阈值才做隐藏
            hideSelectable();
        }
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        SpannableString ss = new SpannableString(text);
        super.setText(ss, TextView.BufferType.SPANNABLE);
    }

    /**
     * 选择文本范围 发生修改
     */
    protected void onSelectRangeChanged(int oldBeginIndex , int newBeginIndex , int oldEndIndex , int newEndIndex){
        selectBegin = newBeginIndex;
        selectEnd = newEndIndex;

        // LogUtil.i(TAG , "select range change " + selectBegin +"  " + selectEnd);
        updateSelectedSpan(selectBegin , selectEnd);

        if(operationWindow != null){
            float startPoint[] = leftCursorView.getXY();

            operationWindow.updatePos((int)startPoint[0] , (int)startPoint[1] - operationWindow.mHeight);
        }
    }

    /**
     *  选中文本 背景框变色
     * @param start
     * @param end
     */
    private void updateSelectedSpan(int start , int end){
        clearAllSelected();
        if(end <= start){
            return;
        }

        if(getText() instanceof SpannableString){
            // LogUtil.i(TAG , "set selected span");
            SpannableString ss = (SpannableString)getText();
            ss.setSpan(new BackgroundColorSpan(selectedBackgroundColor), start , end , Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }
    }

    /**
     * 显示文本选择控件
     *
     */
    public void showSelectable(){
        showSelectCursor();

        if(operationWindow == null){
            operationWindow = new OperateWindow(getContext());
        }
        operationWindow.show();

        addScrollListener();
    }

    public void showSelectCursor(){
        if(leftCursorView == null){
            leftCursorView = new SelectCursorView(this.getContext() ,this, true);
        }

        if(rightCursorView == null){
            rightCursorView =new SelectCursorView(this.getContext() ,this, false);
        }

        leftCursorView.show();
        rightCursorView.show();
    }

    private void addScrollListener(){
        getViewTreeObserver().addOnScrollChangedListener(onScrollChangedListener);
        getLocationInWindow(viewPositionInWindow);
    }

    public void hideSelectable(){
        if(leftCursorView != null){
            leftCursorView.hide();
        }

        if(rightCursorView != null){
            rightCursorView.hide();
        }

        clearAllSelected();

        if(operationWindow != null){
            operationWindow.dismiss();
        }

        //隐藏了 就移除滑动监听
        getViewTreeObserver().removeOnScrollChangedListener(onScrollChangedListener);
    }

    private void clearAllSelected(){
        if(getText() instanceof SpannableString){
            SpannableString ss = (SpannableString)getText();
            ss.setSpan(new BackgroundColorSpan(Color.TRANSPARENT), 0 , getText().length() , Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
    }

    public int getSelectBegin() {
        return selectBegin;
    }

    public int getSelectEnd() {
        return selectEnd;
    }

    /**
     *  操作弹窗
     *
     */
    private class OperateWindow {
        private PopupWindow mWindow;
        private int[] mTempCoors = new int[2];

        private int mWidth;
        private int mHeight;

        public OperateWindow(final Context context) {
            final View contentView = LayoutInflater.from(context).inflate(R.layout.layout_operate_windows, null);
            contentView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            mWidth = contentView.getMeasuredWidth();
            mHeight = contentView.getMeasuredHeight();

            LogUtil.i(TAG , "operation window size " + mWidth +" , " + mHeight);
            mWindow =
                    new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT, false);
            mWindow.setClippingEnabled(false);
            mWindow.setWidth(mWidth);
            mWindow.setHeight(mHeight);

            contentView.findViewById(R.id.tv_copy).setOnClickListener((v)->{
                doCopy();
            });
            contentView.findViewById(R.id.tv_select_all).setOnClickListener((v)->{
                showSelectCursor();
            });
        }

        private void doCopy(){
            String contentString = getText().toString();
            try{
                final String copyStr = contentString.substring(selectBegin , selectEnd);
                LogUtil.i(TAG , "copy : " + copyStr);
                //todo copy
            }catch (Exception e){
                e.printStackTrace();
            }
            hideSelectable();
        }

        public void show() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mWindow.setElevation(8.0f);
            }
            mWindow.showAtLocation(SelectableText.this, Gravity.NO_GRAVITY, 200, 500);
        }

        /**
         * 更新浮窗位置
         *
         * @param _x
         * @param _y
         */
        public void updatePos(int _x ,int _y){
            mWindow.update(_x , _y , mWidth , mHeight);
        }

        public void dismiss() {
            mWindow.dismiss();
        }

        public boolean isShowing() {
            return mWindow.isShowing();
        }
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

    /**
     *   是否是此行地最后一个字符
     * @param layout
     * @param offset
     * @return
     */
    public static boolean isEndOfLineOffset(Layout layout, int offset) {
        if(layout == null){
            return false;
        }
        return offset > 0 && layout.getLineForOffset(offset) == layout.getLineForOffset(offset - 1) + 1;
    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }
}//end class
