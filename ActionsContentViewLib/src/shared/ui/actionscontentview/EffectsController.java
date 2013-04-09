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

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;

class EffectsController {
  private static final String TAG = EffectsController.class.getSimpleName();
  private static final boolean DEBUG = false;

  private static final Method APPLY_TRANSFORMATION;

  public static final int EFFECT_OPEN = 0;
  public static final int EFFECT_CLOSE = 1;

  private static final int EFFECTS_COUNT = 2;

  static {
    APPLY_TRANSFORMATION = getApplyTransformation();
  }

  private final Transformation mTransformation = new Transformation();
  private final Matrix mMatrix = new Matrix();
  private float mEffectsAlpha = 1f;

  private final Effect[] mEffects = new Effect[EFFECTS_COUNT];

  public void setEffects(Context context, int resId) {
    final String resourceType = context.getResources().getResourceTypeName(resId);

    if ( !resourceType.equals("array") ) {
      final Animation anim = AnimationUtils.loadAnimation(context, resId);
      setEffects(anim);
      return;
    }

    final TypedArray effects = context.getResources().obtainTypedArray(resId);

    final int count = effects.length();
    final int size = Math.min(EFFECTS_COUNT, count);
    for ( int i=0; i<size; ++i ) {
      final int id = effects.getResourceId(i, -1);
      if (id > 0) {
        final Animation anim = AnimationUtils.loadAnimation(context, id);
        mEffects[i] = new Effect(anim);
      }
    }
    effects.recycle();
  }

  public void setEffects(Animation animation) {
    mEffects[0] = mEffects[1] = new Effect(animation);
  }

  public Effect[] getEffects() {
    return mEffects;
  }

  public void initialize(View v) {
    if ( mEffects == null )
      return;

    final ViewGroup parent = (ViewGroup) v.getParent();
    if ( parent != null ) {
      for ( Effect effect : mEffects ) {
        if ( effect == null )
          continue;
        effect.anim.initialize(v.getWidth(), v.getHeight(), parent.getWidth(), parent.getHeight());
      }
    }
  }

  public Matrix getEffectsMatrix() {
    return mMatrix;
  }

  public float getEffectsAlpha() {
    return mEffectsAlpha;
  }

  public void reset() {
    mMatrix.reset();
    mEffectsAlpha = 1f;
  }

  public boolean apply(float factor, int effectType) {
    if (mEffects == null)
      return false;

    reset();

    final Effect effect = mEffects[effectType];
    if ( effect == null )
      return false;

    final Animation anim = effect.anim;
    final long totalTime = effect.totalTime;

    if (anim instanceof AnimationSet) {
      return apply(factor, (AnimationSet) anim, totalTime);
    } else {
      return apply(factor, anim, totalTime);
    }
  }

  private boolean apply(float factor, Animation animation, long totalTime) {
    final float animationFactor;

    final long animationDuration = animation.getDuration();
    if (animationDuration == 0 || totalTime == 0) {
      animationFactor = factor;
    } else {
      final long effectTime = (int) (totalTime * factor);

      final long animationStartOffset = animation.getStartOffset();
      final long animationEndTime = animationStartOffset + animationDuration;

      if (effectTime < animationStartOffset || effectTime > animationEndTime)
        return true;

      animationFactor = (float)(effectTime - animationStartOffset) / (float)animationDuration;
    }

    try {
      // we need reset transformation for every animation
      mTransformation.clear();

      final float interpolatedFactor = animation.getInterpolator().getInterpolation(animationFactor);
      APPLY_TRANSFORMATION.invoke(animation, interpolatedFactor, mTransformation);
      if ((mTransformation.getTransformationType() & Transformation.TYPE_MATRIX) == Transformation.TYPE_MATRIX)
        mMatrix.postConcat(mTransformation.getMatrix());
      if ((mTransformation.getTransformationType() & Transformation.TYPE_ALPHA) == Transformation.TYPE_ALPHA)
        mEffectsAlpha *= mTransformation.getAlpha();

      if (DEBUG) {
        Log.d(TAG, "Transformation: " + animation);
        Log.d(TAG, " - " + mTransformation.toShortString());
      }

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

  private boolean apply(float factor, AnimationSet set, long totalTime) {
    final List<Animation> animations = set.getAnimations();
    for (Animation a : animations) {
      if (a instanceof AnimationSet) {
        if (!apply(factor, (AnimationSet) a, totalTime))
          return false;
      } else {
        if (!apply(factor, a, totalTime))
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

  public static class Effect {
    public final Animation anim;
    public final long totalTime;

    public Effect(Animation anim) {
      this.anim = anim;
      totalTime = anim.computeDurationHint();
    }
  }
}
