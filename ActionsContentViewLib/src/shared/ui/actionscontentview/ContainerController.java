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
import android.view.animation.Animation;

public interface ContainerController {

  /**
   * Setter for ignore touch events key.
   * @param ignore
   */
  public void setIgnoreTouchEvents(boolean ignore);

  /**
   * Getter for ignore touch events key.
   * @return
   */
  public boolean isIgnoringTouchEvents();

  /**
   * Setter for effects.
   * @param resId - id to load effects from resources.
   */
  public void setEffects(int resId);

  /**
   * Setter for effects.
   * @param effects - animation will be used as effect for current layout while swipping.
   */
  public void setEffects(Animation effects);

  /**
   * Getter for effects.
   * @return animation that is used as effect for current layout while swipping.
   */
  public Effect[] getEffects();
}
