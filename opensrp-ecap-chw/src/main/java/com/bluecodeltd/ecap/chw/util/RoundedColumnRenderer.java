package com.bluecodeltd.ecap.chw.util;

import android.graphics.Canvas;
import android.graphics.RectF;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

/** Draws vertical bars with rounded tops and square bases, so each column sits flat on the baseline. */
public class RoundedColumnRenderer extends BarChartRenderer {

    private final float radius;
    private final RectF rect = new RectF();

    public RoundedColumnRenderer(BarDataProvider chart, ChartAnimator animator,
                                 ViewPortHandler viewPortHandler, float radiusDp) {
        super(chart, animator, viewPortHandler);
        radius = Utils.convertDpToPixel(radiusDp);
        mRenderPaint.setAntiAlias(true);
    }

    @Override
    protected void drawDataSet(Canvas c, IBarDataSet dataSet, int index) {
        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        BarBuffer buffer = mBarBuffers[index];
        buffer.setPhases(mAnimator.getPhaseX(), mAnimator.getPhaseY());
        buffer.setDataSet(index);
        buffer.setInverted(mChart.isInverted(dataSet.getAxisDependency()));
        buffer.setBarWidth(mChart.getBarData().getBarWidth());
        buffer.feed(dataSet);
        trans.pointValuesToPixel(buffer.buffer);

        boolean singleColor = dataSet.getColors().size() == 1;
        if (singleColor) {
            mRenderPaint.setColor(dataSet.getColor());
        }

        for (int j = 0; j < buffer.size(); j += 4) {
            float left = buffer.buffer[j];
            float top = buffer.buffer[j + 1];
            float right = buffer.buffer[j + 2];
            float bottom = buffer.buffer[j + 3];
            if (!mViewPortHandler.isInBoundsLeft(right)) continue;
            if (!mViewPortHandler.isInBoundsRight(left)) break;
            if (bottom - top <= 0f) continue;
            if (!singleColor) {
                mRenderPaint.setColor(dataSet.getColor(j / 4));
            }
            // Round the top, then square off the bottom corners where the column meets the baseline
            float r = Math.min(radius, Math.min((right - left) / 2f, bottom - top));
            rect.set(left, top, right, bottom);
            c.drawRoundRect(rect, r, r, mRenderPaint);
            c.drawRect(left, Math.max(top, bottom - r), right, bottom, mRenderPaint);
        }
    }
}
