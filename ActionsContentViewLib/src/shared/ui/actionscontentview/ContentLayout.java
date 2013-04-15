/*******************************************************************************
 * Copyright 2012 Steven Rudenko
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package shared.ui.actionscontentview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

class ContentLayout extends LinearLayout {

  public interface OnSwipeListener {
    public void onSwipe(int scrollPosition);
  }

  private final BaseContainerController mController = new BaseContainerController(this);

  private final Rect mHitRect = new Rect();
  private final RectF mEffectedHitRect = new RectF();
  private final Paint mFadePaint = new Paint();

  private OnSwipeListener mOnSwipeListener;

  public ContentLayout(Context context) {
    this(context, null);
  }

  public ContentLayout(Context context, AttributeSet attrs) {
    super(context, attrs);

    // we need to be sure we have horizontal layout to add shadow to left border
    setOrientation(LinearLayout.HORIZONTAL);
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public ContentLayout(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);

    // we need to be sure we have horizontal layout to add shadow to left border
    setOrientation(LinearLayout.HORIZONTAL);
  }

  public BaseContainerController getController() {
    return mController;
  }

  public void setOnSwipeListener(OnSwipeListener listener) {
    mOnSwipeListener = listener;
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    mController.initializeEffects();
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (mController.isIgnoringTouchEvents())
      return false;

    // prevent ray cast of touch events to actions container
    getHitRect(mHitRect);
    mHitRect.offset(-getScrollX(), -getScrollY());

    // applying effects
    mEffectedHitRect.set(mHitRect);
    mController.getEffectsMatrix().mapRect(mEffectedHitRect);

    if (mEffectedHitRect.contains((int)event.getX(), (int)event.getY())) {
      return true;
    }

    return super.onTouchEvent(event);
  }

  @Override
  protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    super.onScrollChanged(l, t, oldl, oldt);
    if (mOnSwipeListener != null)
      mOnSwipeListener.onSwipe(-getScrollX());
  }

  @Override
  protected void dispatchDraw(Canvas canvas) {
    final int saveCount = canvas.save();

    final Matrix m = mController.getEffectsMatrix();
    if (!m.isIdentity())
      canvas.concat(m);

    final float alpha = mController.getEffectsAlpha();
    if (alpha != 1f)
      canvas.saveLayerAlpha(0, 0, canvas.getWidth(), canvas.getHeight(), (int)(255 * alpha), Canvas.HAS_ALPHA_LAYER_SAVE_FLAG);

    super.dispatchDraw(canvas);

    final int fadeFactor = mController.getFadeFactor();
    if (fadeFactor > 0f) {
      mFadePaint.setColor(Color.argb(fadeFactor, 0, 0, 0));
      canvas.drawRect(0, 0, getWidth(), getHeight(), mFadePaint);
    }

    canvas.restoreToCount(saveCount);
  }
}
