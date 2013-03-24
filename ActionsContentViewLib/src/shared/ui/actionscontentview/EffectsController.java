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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Transformation;

class EffectsController {
  private static final Method APPLY_TRANSFORMATION;

  static {
    APPLY_TRANSFORMATION = getApplyTransformation();
  }

  private final Transformation mTransformation = new Transformation();
  private final Matrix mMatrix = new Matrix();

  private Animation mEffectsAnimation;
  private float mEffectsAlpha = 1f;

  public void setEffects(Animation animation) {
    animation.initialize(0, 0, 0, 0);
    mEffectsAnimation = animation;
  }

  public Animation getEffects() {
    return mEffectsAnimation;
  }

  public Matrix getEffectsMatrix() {
    return mMatrix;
  }

  public float getEffectsAlpha() {
    return mEffectsAlpha;
  }
  
  public boolean apply(float factor) {
    if (mEffectsAnimation == null)
      return false;

    mMatrix.reset();
    mEffectsAlpha = 1f;

    if (mEffectsAnimation instanceof AnimationSet) {
      return apply(factor, (AnimationSet) mEffectsAnimation);
    } else {
      return apply(factor, mEffectsAnimation);
    }
  }

  float[] v = new float[9];
  
  private boolean apply(float factor, Animation animation) {
    try {
      APPLY_TRANSFORMATION.invoke(animation, factor, mTransformation);
      mMatrix.postConcat(mTransformation.getMatrix());
      mEffectsAlpha *= mTransformation.getAlpha();
      return true;
    } catch (IllegalArgumentException e) {
      // we don't care because this exception should never happen
    } catch (IllegalAccessException e) {
      // we don't care because this exception should never happen
    } catch (InvocationTargetException e) {
      // we don't care because this exception should never happen
    }
    return false;
  }

  private boolean apply(float factor, AnimationSet set) {
    final List<Animation> animations = set.getAnimations();
    for (Animation a : animations) {
      if (a instanceof AnimationSet) {
        if (!apply(factor, (AnimationSet) a))
          return false;
      } else {
        if (!apply(factor, a))
          return false;
      }
    }
    return true;
  }

  private static Method getApplyTransformation() {
    try {
      final Method m = Animation.class.getDeclaredMethod("applyTransformation", float.class, Transformation.class);
      m.setAccessible(true);
      return m;
    } catch (NoSuchMethodException e) {
      // we don't care because this exception should never happen
    }
    return null;
  }
}
