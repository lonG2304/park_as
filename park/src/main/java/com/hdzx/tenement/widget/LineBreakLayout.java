package com.hdzx.tenement.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class LineBreakLayout extends ViewGroup
{
    private static final int PADDING_HOR = 10;// 水平方向padding
    private static final int PADDING_VERTICAL = 5;// 垂直方向padding
    private static final int SIDE_MARGIN = 20;// 左右间距
    private static final int TEXT_MARGIN = 20;

    /**
     * @param context
     */
    public LineBreakLayout(Context context)
    {
        super(context);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public LineBreakLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    /**
     * @param context
     * @param attrs
     */
    public LineBreakLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        int childCount = getChildCount();
        int autualWidth = r - l;
        int x = SIDE_MARGIN;// 横坐标开始
        int y = 0;// 纵坐标开始
        int rows = 1;
        for (int i = 0; i < childCount; i++)
        {
            View view = getChildAt(i);
            int width = view.getMeasuredWidth();
            int height = view.getMeasuredHeight();
            x += width + TEXT_MARGIN;
            if (x > autualWidth)
            {
                x = SIDE_MARGIN + width + TEXT_MARGIN;
                rows++;
            }
            y = rows * (height + TEXT_MARGIN);
            view.layout(x - width - TEXT_MARGIN, y - height, x - TEXT_MARGIN, y);
        }
    };

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int x = 0;// 横坐标
        int y = 0;// 纵坐标
        int rows = 1;// 总行数
        int specWidth = MeasureSpec.getSize(widthMeasureSpec);
        int actualWidth = specWidth - SIDE_MARGIN * 2;// 实际宽度
        int childCount = getChildCount();
        for (int index = 0; index < childCount; index++)
        {
            View child = getChildAt(index);
            child.setPadding(PADDING_HOR, PADDING_VERTICAL, PADDING_HOR, PADDING_VERTICAL);
            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();
            x += width + TEXT_MARGIN;
            if (x > actualWidth)
            {// 换行
                x = width;
                rows++;
            }
            y = rows * (height + TEXT_MARGIN);
        }
        setMeasuredDimension(actualWidth, y);
    }
}
