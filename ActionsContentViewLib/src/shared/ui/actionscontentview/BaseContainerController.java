/*******************************************************************************
 * Copyright 2013 Steven Rudenko
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

import shared.ui.actionscontentview.EffectsController.Effect;
import android.graphics.Matrix;
import android.view.View;
import android.view.animation.Animation;

public class BaseContainerController implements ContainerController {

  private final View view;
  private final EffectsController mEffectsController = new EffectsController();

  private boolean mIgnoreTouchEvents = false;
  private int mFadeFactor = 0;

  public BaseContainerController(View view) {
    this.view = view;
  }

  @Override
  public void setIgnoreTouchEvents(boolean ignore) {
    mIgnoreTouchEvents = ignore;
  }

  @Override
  public boolean isIgnoringTouchEvents() {
    return mIgnoreTouchEvents;
  }

  void initializeEffects() {
    mEffectsController.initialize(view);
  }

  Matrix getEffectsMatrix() {
    return mEffectsController.getEffectsMatrix();
  }

  float getEffectsAlpha() {
    return mEffectsController.getEffectsAlpha();
  }

  @Override
  public void setEffects(Animation effects) {
    mEffectsController.setEffects(effects);
  }

  @Override
  public void setEffects(int resId) {
    mEffectsController.setEffects(view.getContext(), resId);
  }

  @Override
  public Effect[] getEffects() {
    return mEffectsController.getEffects();
  }

  int getFadeFactor() {
    return mFadeFactor;
  }

  /**
   * Indicates that scrolling was performed.
   * @param factor - factor of scrolling. Can be in range from 0f to 1f.
   * @param fadeFactor - fade factor for current scroll factor.
   */
  public void onScroll(float factor, int fadeFactor, boolean isOpening, boolean enableEffects) {
    mFadeFactor = fadeFactor;

    final boolean updateEffects;
    if (enableEffects)
      updateEffects = mEffectsController.apply(factor, isOpening ? EffectsController.EFFECT_OPEN : EffectsController.EFFECT_CLOSE);
    else {
      mEffectsController.reset();
      updateEffects = false;
    }

    if (updateEffects || mFadeFactor > 0)
      view.postInvalidate();
  }
}
