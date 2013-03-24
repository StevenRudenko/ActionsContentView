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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.widget.FrameLayout;

class ActionsLayout extends FrameLayout {

  private final EffectsController mEffectsController = new EffectsController();

  private final Paint mFadePaint = new Paint();

  private int mFadeFactor = 0;

  public ActionsLayout(Context context) {
    this(context, null);
  }

  public ActionsLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ActionsLayout(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  public void setAnimation(Animation animation) {
    mEffectsController.setEffects(animation);
  }

  public void scroll(float factor, int fadeFactor) {
    mFadeFactor = fadeFactor;
    if (mEffectsController.apply(factor) || mFadeFactor > 0)
      invalidate();
  }

  @Override
  protected void dispatchDraw(Canvas canvas) {
    final int saveCount = canvas.save();
    canvas.concat(mEffectsController.getEffectsMatrix());
    canvas.saveLayerAlpha(0, 0, canvas.getWidth(), canvas.getHeight(), (int)(255 * mEffectsController.getEffectsAlpha()), Canvas.HAS_ALPHA_LAYER_SAVE_FLAG);

    super.dispatchDraw(canvas);

    if (mFadeFactor > 0) {
      mFadePaint.setColor(Color.argb(mFadeFactor, 0, 0, 0));
      canvas.drawRect(0, 0, getWidth(), getHeight(), mFadePaint);
    }

    canvas.restoreToCount(saveCount);
  }
}
