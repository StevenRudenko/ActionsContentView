package shared.ui.actionscontentview;

import android.view.animation.Animation;

public interface BaseLayout {

  /**
   * Setter for effects.
   * @param effects - animation will be used as effect for current layout while swipping.
   */
  public void setEffects(Animation effects);

  /**
   * Getter for effects.
   * @return animation that is used as effect for current layout while swipping.
   */
  public Animation getEffects();
  
  /**
   * Indicates that scrolling was performed.
   * @param factor - factor of scrolling. Can be in range from 0f to 1f.
   * @param fadeFactor - fade factor for current scroll factor.
   */
  public void onScroll(float factor, int fadeFactor);
}
