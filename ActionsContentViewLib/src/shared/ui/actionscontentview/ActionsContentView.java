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
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class ActionsContentView extends ViewGroup {
  private static final String TAG = ActionsContentView.class.getSimpleName();
  private static final boolean DEBUG = false;

  /**
   * Spacing will be calculated as offset from right bound of view.
   */
  public static final int SPACING_RIGHT_OFFSET = 0;
  /**
   * Spacing will be calculated as right bound of actions bar container.
   */
  public static final int SPACING_ACTIONS_WIDTH = 1;

  /**
   * Fade is disabled.
   */
  public static final int FADE_NONE = 0;
  /**
   * Fade applies to actions container.
   */
  public static final int FADE_ACTIONS = 1;
  /**
   * Fade applies to content container.
   */
  public static final int FADE_CONTENT = 2;
  /**
   * Fade applies to every container.
   */
  public static final int FADE_BOTH = 3;

  /**
   * Swiping will be handled at any point of screen.
   */
  public static final int SWIPING_ALL = 0;
  /**
   * Swiping will be handled starting from screen edge only.
   */
  public static final int SWIPING_EDGE = 1;

  private final ContentScrollController mContentScrollController;
  private final GestureDetector mGestureDetector;

  private final View viewShadow;
  private final ActionsLayout viewActionsContainer;
  private final ContentLayout viewContentContainer;

  /**
   * Spacing type.
   */
  private int mSpacingType = SPACING_RIGHT_OFFSET;
  /**
   * Value of spacing to use.
   */
  private int mSpacing;

  /**
   * Value of actions container spacing to use.
   */
  private int mActionsSpacing;

  /**
   * Value of shadow width.
   */
  private int mShadowWidth = 0;

  /**
   * Indicates how long flinging will take time in milliseconds.
   */
  private int mFlingDuration = 250;

  /**
   * Fade type.
   */
  private int mFadeType = FADE_NONE;
  /**
   * Max fade value.
   */
  private int mFadeValue;

  /**
   * Indicates whether swiping is enabled or not.
   */
  private boolean isSwipingEnabled = true;
  /**
   * Swiping type.
   */
  private int mSwipeType = FADE_NONE;
  /**
   * Swiping edge width.
   */
  private int mSwipeEdgeWidth;

  /**
   * Indicates whether refresh of content position should be done on next layout calculation.
   */
  private boolean mForceRefresh = false;

  public ActionsContentView(Context context) {
    this(context, null);
  }

  public ActionsContentView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ActionsContentView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);

    setClipChildren(false);
    setClipToPadding(false);

    // reading attributes
    final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ActionsContentView);
    mSpacingType = a.getInteger(R.styleable.ActionsContentView_spacing_type, SPACING_RIGHT_OFFSET);
    final int spacingDefault = context.getResources().getDimensionPixelSize(R.dimen.default_actionscontentview_spacing);
    mSpacing = a.getDimensionPixelSize(R.styleable.ActionsContentView_spacing, spacingDefault);
    final int actionsSpacingDefault = context.getResources().getDimensionPixelSize(R.dimen.default_actionscontentview_actions_spacing);
    mActionsSpacing = a.getDimensionPixelSize(R.styleable.ActionsContentView_actions_spacing, actionsSpacingDefault);

    final int actionsLayout = a.getResourceId(R.styleable.ActionsContentView_actions_layout, 0);
    final int contentLayout = a.getResourceId(R.styleable.ActionsContentView_content_layout, 0);

    mShadowWidth = a.getDimensionPixelSize(R.styleable.ActionsContentView_shadow_width, 0);
    final int shadowDrawableRes = a.getResourceId(R.styleable.ActionsContentView_shadow_drawable, 0);

    mFadeType = a.getInteger(R.styleable.ActionsContentView_fade_type, FADE_NONE);
    final int fadeValueDefault = context.getResources().getInteger(R.integer.default_actionscontentview_fade_max_value);
    mFadeValue = (int) a.getInt(R.styleable.ActionsContentView_fade_max_value, fadeValueDefault);

    setFadeValue(mFadeValue);

    final int flingDurationDefault = context.getResources().getInteger(R.integer.default_actionscontentview_fling_duration);
    mFlingDuration = a.getInteger(R.styleable.ActionsContentView_fling_duration, flingDurationDefault);

    mSwipeType = a.getInteger(R.styleable.ActionsContentView_swiping_type, SWIPING_EDGE);
    final int swipingEdgeWidthDefault = context.getResources().getDimensionPixelSize(R.dimen.default_actionscontentview_swiping_edge_width);
    mSwipeEdgeWidth = a.getDimensionPixelSize(R.styleable.ActionsContentView_swiping_edge_width, swipingEdgeWidthDefault);

    a.recycle();

    if (DEBUG) {
      Log.d(TAG, "Values from layout");
      Log.d(TAG, "  spacing type: " + mSpacingType);
      Log.d(TAG, "  spacing value: " + mSpacing);
      Log.d(TAG, "  actions spacing value: " + mActionsSpacing);
      Log.d(TAG, "  actions layout id: " + actionsLayout);
      Log.d(TAG, "  content layout id: " + contentLayout);
      Log.d(TAG, "  shadow drawable: " + shadowDrawableRes);
      Log.d(TAG, "  shadow width: " + mShadowWidth);
      Log.d(TAG, "  fade type: " + mFadeType);
      Log.d(TAG, "  fade max value: " + mFadeValue);
      Log.d(TAG, "  fling duration: " + mFlingDuration);
      Log.d(TAG, "  swiping type: " + mSwipeType);
      Log.d(TAG, "  swiping edge width: " + mSwipeEdgeWidth);
    }

    mContentScrollController = new ContentScrollController(new Scroller(context));
    mGestureDetector = new GestureDetector(context, mContentScrollController);
    mGestureDetector.setIsLongpressEnabled(true);

    final LayoutInflater inflater = LayoutInflater.from(context);
    viewActionsContainer = new ActionsLayout(context);
    if (actionsLayout != 0)
      inflater.inflate(actionsLayout, viewActionsContainer, true);

    addView(viewActionsContainer, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

    viewContentContainer = new ContentLayout(context);
    viewContentContainer.setOnSwipeListener(new ContentLayout.OnSwipeListener() {
      @Override
      public void onSwipe(int scrollPosition) {
        fadeViews();
      }
    });

    viewShadow = new View(context);
    viewShadow.setBackgroundResource(shadowDrawableRes);
    final LinearLayout.LayoutParams shadowParams = new LinearLayout.LayoutParams(mShadowWidth, LinearLayout.LayoutParams.MATCH_PARENT);
    viewShadow.setLayoutParams(shadowParams);
    viewContentContainer.addView(viewShadow);

    if (mShadowWidth <= 0 || shadowDrawableRes == 0) {
      viewShadow.setVisibility(GONE);
    }

    if (contentLayout != 0)
      inflater.inflate(contentLayout, viewContentContainer, true);

    addView(viewContentContainer, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
  }

  public Parcelable onSaveInstanceState() {
    final Parcelable superState = super.onSaveInstanceState();
    final SavedState ss = new SavedState(superState);
    ss.isContentShown = isContentShown();
    ss.mSpacingType = getSpacingType();
    ss.mSpacing = getSpacingWidth();
    ss.mActionsSpacing = getActionsSpacingWidth();
    ss.isShadowVisible  = isShadowVisible();
    ss.mShadowWidth = getShadowWidth();
    ss.isSwipingEnabled = isSwipingEnabled();
    ss.mFlingDuration = getFlingDuration();
    ss.mFadeType = getFadeType();
    ss.mFadeValue = getFadeValue();
    ss.mSwipeType = getSwipingType();
    ss.mSwipeEdgeWidth = getSwipingEdgeWidth();
    return ss;
  }

  public void onRestoreInstanceState(Parcelable state) {
    if (!(state instanceof SavedState)) {
      super.onRestoreInstanceState(state);
      return;
    }

    final SavedState ss = (SavedState)state;
    super.onRestoreInstanceState(ss.getSuperState());

    mContentScrollController.isContentShown = ss.isContentShown;

    mSpacingType = ss.mSpacingType;
    mSpacing = ss.mSpacing;
    mActionsSpacing = ss.mActionsSpacing;
    isSwipingEnabled = ss.isSwipingEnabled;
    mSwipeType = ss.mSwipeType;
    mSwipeEdgeWidth = ss.mSwipeEdgeWidth;
    mFlingDuration = ss.mFlingDuration;
    mFadeType = ss.mFadeType;
    mFadeValue = ss.mFadeValue;

    viewShadow.setVisibility(ss.isShadowVisible ? VISIBLE : GONE);

    // this will call requestLayout() to calculate layout according to values
    setShadowWidth(ss.mShadowWidth);
  }

  public ViewGroup getActionsContainer() {
    return viewActionsContainer;
  }

  public ViewGroup getContentContainer() {
    return viewContentContainer;
  }

  public boolean isActionsShown() {
    return !mContentScrollController.isContentShown();
  }

  public void showActions() {
    mContentScrollController.hideContent(mFlingDuration);
  }

  public boolean isContentShown() {
    return mContentScrollController.isContentShown();
  }

  public void showContent() {
    mContentScrollController.showContent(mFlingDuration);
  }

  public void toggleActions() {
    if (isActionsShown())
      showContent();
    else
      showActions();
  }

  public void setSpacingType(int type) {
    if (mSpacingType == type)
      return;

    if (type != SPACING_RIGHT_OFFSET && type != SPACING_ACTIONS_WIDTH)
      return;

    if (DEBUG)
      Log.d(TAG, "- spacing type: " + type);

    mSpacingType = type;
    mForceRefresh = true;
    requestLayout();
  }

  public int getSpacingType() {
    return mSpacingType;
  }

  public void setSpacingWidth(int width) {
    if (mSpacing == width)
      return;

    if (DEBUG)
      Log.d(TAG, "- spacing width: " + width);

    mSpacing = width;
    mForceRefresh = true;
    requestLayout();
  }

  public int getSpacingWidth() {
    return mSpacing;
  }

  public void setActionsSpacingWidth(int width) {
    if (mActionsSpacing == width)
      return;

    mActionsSpacing = width;
    mForceRefresh = true;
    requestLayout();
  }

  public int getActionsSpacingWidth() {
    return mActionsSpacing;
  }

  public void setShadowVisible(boolean visible) {
    viewShadow.setVisibility(visible ? VISIBLE : GONE);
    mForceRefresh = true;
    requestLayout();
  }

  public boolean isShadowVisible() {
    return viewShadow.getVisibility() == VISIBLE;
  }

  public void setShadowWidth(int width) {
    if (mShadowWidth == width)
      return;

    if (DEBUG)
      Log.d(TAG, "- shadow width: " + width);

    mShadowWidth = width;
    viewShadow.getLayoutParams().width = mShadowWidth;
    mForceRefresh = true;
    requestLayout();
  }

  public int getShadowWidth() {
    return mShadowWidth;
  }

  public void setFlingDuration(int duration) {
    mFlingDuration = duration;
  }

  public int getFlingDuration() {
    return mFlingDuration;
  }

  public void setFadeType(int type) {
    if (type != FADE_NONE && type != FADE_ACTIONS && type != FADE_CONTENT && type != FADE_BOTH)
      return;

    mFadeType = type;
    fadeViews();
  }

  public int getFadeType() {
    return mFadeType;
  }

  public void setFadeValue(int value) {
    if (value < 0)
      value = 0;
    else if (value > 255)
      value = 255;

    mFadeValue = value;
    fadeViews();
  }

  public int getFadeValue() {
    return mFadeValue;
  }

  public boolean isSwipingEnabled() {
    return isSwipingEnabled;
  }

  public void setSwipingEnabled(boolean enabled) {
    isSwipingEnabled = enabled;
  }

  public void setSwipingType(int type) {
    if (type != SWIPING_ALL && type != SWIPING_EDGE)
      return;

    mSwipeType = type;
  }

  public int getSwipingType() {
    return mSwipeType;
  }

  public void setSwipingEdgeWidth(int width) {
    mSwipeEdgeWidth = width;
  }

  public int getSwipingEdgeWidth() {
    return mSwipeEdgeWidth;
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    // return true always as far we should handle touch event for swiping
    if (isSwipingEnabled)
      return true;

    return super.onTouchEvent(event);
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    if (!isSwipingEnabled)
      return super.dispatchTouchEvent(ev);

    final int action = ev.getAction();
    // if current touch event should be handled
    if (mContentScrollController.isHandled() && action == MotionEvent.ACTION_UP) {
      mContentScrollController.onUp(ev);
      return false;
    }

    if (mGestureDetector.onTouchEvent(ev) || mContentScrollController.isHandled()) {
      clearPressedState(this);
      return false;
    }

    return super.dispatchTouchEvent(ev);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    final int width = MeasureSpec.getSize(widthMeasureSpec);
    final int height = MeasureSpec.getSize(heightMeasureSpec);

    if (DEBUG)
      Log.d(TAG, "width: " + width + " height: " + height);

    final int childrenCount = getChildCount();
    for (int i=0; i<childrenCount; ++i) {
      final View v = getChildAt(i);
      if (v == viewActionsContainer) {
        // setting size of actions according to spacing parameters
        if (mSpacingType == SPACING_ACTIONS_WIDTH)
          viewActionsContainer.measure(MeasureSpec.makeMeasureSpec(mSpacing, MeasureSpec.EXACTLY), heightMeasureSpec);
        else // all other situations are handled as SPACING_RIGHT_OFFSET
          viewActionsContainer.measure(MeasureSpec.makeMeasureSpec(width - mSpacing, MeasureSpec.EXACTLY), heightMeasureSpec);
      } else if (v == viewContentContainer) {
        final int shadowWidth = isShadowVisible() ? mShadowWidth : 0;
        final int contentWidth = MeasureSpec.getSize(widthMeasureSpec) - mActionsSpacing + shadowWidth;
        v.measure(MeasureSpec.makeMeasureSpec(contentWidth, MeasureSpec.EXACTLY), heightMeasureSpec);
      } else {
        v.measure(widthMeasureSpec, heightMeasureSpec);
      }
    }

    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    if (DEBUG) {
      final Rect layout = new Rect(l, t, r, b);
      Log.d(TAG, "layout: " + layout.toShortString());
    }

    // putting every child view to top-left corner
    final int childrenCount = getChildCount();
    for (int i=0; i<childrenCount; ++i) {
      final View v = getChildAt(i);
      if (v == viewContentContainer) {
        final int shadowWidth = isShadowVisible() ? mShadowWidth : 0;
        v.layout(mActionsSpacing - shadowWidth, 0, mActionsSpacing + v.getMeasuredWidth(), v.getMeasuredHeight());
      } else {
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
      }
    }

    if (mForceRefresh) {
      mForceRefresh = false;
      mContentScrollController.init();
    }
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);

    // set correct position of content view after view size was changed
    if (w != oldw || h != oldh) {
      mContentScrollController.init();
    }
  }

  private void fadeViews() {
    if ( viewActionsContainer == null )
      return;

    final float scrollFactor = mContentScrollController.getScrollFactor();
    if ((mFadeType & FADE_ACTIONS) == FADE_ACTIONS) {
      final int fadeFactor = (int) (scrollFactor * mFadeValue);
      viewActionsContainer.invalidate(fadeFactor);
    } else {
      viewActionsContainer.invalidate(0);
    }
    if ((mFadeType & FADE_CONTENT) == FADE_CONTENT) {
      final int fadeFactor = (int) ((1f - scrollFactor) * mFadeValue);
      viewContentContainer.invalidate(fadeFactor);
    } else {
      viewContentContainer.invalidate(0);
    }
  }

  /**
   * Clears pressed state for all views hierarchy starting from parent view.
   * @param parent - parent view
   * @return true is press state was cleared
   */
  private static boolean clearPressedState(ViewGroup parent) {
    if (parent.isPressed()) {
      parent.setPressed(false);
      return true;
    }

    final int count = parent.getChildCount();
    for (int i=0; i<count; ++i) {
      final View v = parent.getChildAt(i);
      if (v.isPressed()) {
        v.setPressed(false);
        return true;
      }

      if (!(v instanceof ViewGroup))
        continue;

      final ViewGroup vg = (ViewGroup) v;
      if (clearPressedState(vg))
        return true;
    }
    return false;
  }

  public static class SavedState extends BaseSavedState {
    /**
     * Indicates whether content was shown while saving state.
     */
    private boolean isContentShown;

    /**
     * Spacing type.
     */
    private int mSpacingType = SPACING_RIGHT_OFFSET;
    /**
     * Value of spacing to use.
     */
    private int mSpacing;

    /**
     * Value of actions container spacing to use.
     */
    private int mActionsSpacing;

    /**
     * Indicates whether shadow is visible.
     */
    private boolean isShadowVisible;

    /**
     * Value of shadow width.
     */
    private int mShadowWidth = 0;

    /**
     * Indicates whether swiping is enabled or not.
     */
    private boolean isSwipingEnabled = true;

    /**
     * Indicates how long flinging will take time in milliseconds.
     */
    private int mFlingDuration = 250;

    /**
     * Fade type.
     */
    private int mFadeType = FADE_NONE;
    /**
     * Max fade value.
     */
    private int mFadeValue;

    /**
     * Swiping type.
     */
    private int mSwipeType = FADE_NONE;
    /**
     * Swiping edge width.
     */
    private int mSwipeEdgeWidth;

    public SavedState(Parcelable superState) {
      super(superState);
    }

    public void writeToParcel(Parcel out, int flags) {
      super.writeToParcel(out, flags);

      out.writeInt(isContentShown ? 1 : 0);
      out.writeInt(mSpacingType);
      out.writeInt(mSpacing);
      out.writeInt(mActionsSpacing);
      out.writeInt(isShadowVisible ? 1 : 0);
      out.writeInt(mShadowWidth);
      out.writeInt(isSwipingEnabled ? 1 : 0);
      out.writeInt(mFlingDuration);
      out.writeInt(mFadeType);
      out.writeInt(mFadeValue);
      out.writeInt(mSwipeType);
      out.writeInt(mSwipeEdgeWidth);
    }

    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
      public SavedState[] newArray(int size) {
        return new SavedState[size];
      }

      @Override
      public SavedState createFromParcel(Parcel source) {
        return new SavedState(source);
      }
    };

    SavedState(Parcel in) {
      super(in);

      isContentShown = in.readInt() == 1;
      mSpacingType = in.readInt();
      mSpacing = in.readInt();
      mActionsSpacing = in.readInt();
      isShadowVisible  = in.readInt() == 1;
      mShadowWidth = in.readInt();
      isSwipingEnabled = in.readInt() == 1;
      mFlingDuration = in.readInt();
      mFadeType = in.readInt();
      mFadeValue = in.readInt();
      mSwipeType = in.readInt();
      mSwipeEdgeWidth = in.readInt();
    }
  }

  /**
   * Used to handle scrolling events and scroll content container
   * on top of actions one.
   * @author steven
   *
   */
  private class ContentScrollController implements GestureDetector.OnGestureListener, Runnable {
    /**
     * Used to auto-scroll to closest bound on touch up event.
     */
    private final Scroller mScroller;

    // using Boolean object to initialize while first scroll event
    private Boolean mHandleEvent = null;

    private int mLastFlingX = 0;

    /**
     * Indicates whether we need initialize position of view after measuring is finished.
     */
    private boolean isContentShown = true;

    public ContentScrollController(Scroller scroller) {
      mScroller = scroller;
    }

    /**
     * Initializes visibility of content after views measuring is finished.
     */
    public void init() {
      if (DEBUG)
        Log.d(TAG, "Scroller: init");

      if (isContentShown)
        showContent(0);
      else
        hideContent(0);

      fadeViews();
    }

    /**
     * Returns handling lock value. It indicates whether all events
     * should be marked as handled.
     * @return
     */
    public boolean isHandled() {
      return mHandleEvent != null && mHandleEvent;
    }

    @Override
    public boolean onDown(MotionEvent e) {
      mHandleEvent = null;
      reset();
      return false;
    }

    public boolean onUp(MotionEvent e) {
      if (!isHandled())
        return false;

      mHandleEvent = null;
      completeScrolling();
      return true;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
      return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
      // No-op
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
        float distanceY) {

      // if there is first scroll event after touch down
      if (mHandleEvent == null) {
        if (Math.abs(distanceX) < Math.abs(distanceY)) {
          // if first event is more scroll by Y axis than X one
          // ignore all events until event up
          mHandleEvent = Boolean.FALSE;
        } else {
          final int contentLeftBound = viewContentContainer.getLeft() - viewContentContainer.getScrollX() + mShadowWidth;
          final int firstTouchX = (int) e1.getX();

          if (DEBUG) {
            Log.d(TAG, "Scroller: first touch: " + firstTouchX + ", " + e1.getY());
            Log.d(TAG, "Content left bound: " + contentLeftBound);
          }

          // if content is not shown we handle all horizontal swipes
          // it content shown and there is edge mode we should check start
          // swiping area first
          if (mSwipeType == SWIPING_ALL
              || (isContentShown() && firstTouchX <= mSwipeEdgeWidth
              || (!isContentShown() && firstTouchX >= contentLeftBound))) {
            // handle all events of scrolling by X axis
            mHandleEvent = Boolean.TRUE;
            scrollBy((int) distanceX);
          } else {
            mHandleEvent = Boolean.FALSE;
          }
        }
      } else if (mHandleEvent) {
        // it is not first event we should handle as scrolling by X axis
        scrollBy((int) distanceX);
      }

      return mHandleEvent;
    }

    @Override
    public void onLongPress(MotionEvent e) {
      // No-op
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
        float velocityY) {
      // does not work because onDown() method returns false always
      return false;
    }

    public boolean isContentShown() {
      return isContentShown;
    }

    public void hideContent(int duration) {
      if (DEBUG)
        Log.d(TAG, "Scroller: hide content by " + duration + "ms");

      isContentShown = false;
      if (viewContentContainer.getMeasuredWidth() == 0 || viewContentContainer.getMeasuredHeight() == 0) {
        return;
      }

      final int startX = viewContentContainer.getScrollX();
      final int dx = getRightBound() + startX;
      fling(startX, dx, duration);
    }

    public void showContent(int duration) {
      if (DEBUG)
        Log.d(TAG, "Scroller: show content by " + duration + "ms");

      isContentShown = true;
      if (viewContentContainer.getMeasuredWidth() == 0 || viewContentContainer.getMeasuredHeight() == 0) {
        return;
      }

      final int startX = viewContentContainer.getScrollX();
      final int dx = startX;
      fling(startX, dx, duration);
    }

    public float getScrollFactor() {
      return 1f + (float) viewContentContainer.getScrollX() / (float) getRightBound();
    }

    /**
     * Processes auto-scrolling to bound which is closer to current position.
     */
    @Override
    public void run() {
      if (mScroller.isFinished()) {
        if (DEBUG)
          Log.d(TAG, "scroller is finished, done with fling");
        return;
      }

      final boolean more = mScroller.computeScrollOffset();
      final int x = mScroller.getCurrX();
      final int diff = mLastFlingX - x;
      if (diff != 0) {
        viewContentContainer.scrollBy(diff, 0);
        mLastFlingX = x;
      }

      if (more) {
        viewContentContainer.post(this);
      }
    }

    /**
     * Resets scroller controller. Stops flinging on current position.
     */
    public void reset() {
      if (DEBUG)
        Log.d(TAG, "Scroller: reset");

      if (!mScroller.isFinished()) {
        mScroller.forceFinished(true);
      }
    }

    /**
     * Starts auto-scrolling to bound which is closer to current position.
     */
    private void completeScrolling() {
      final int startX = viewContentContainer.getScrollX();

      final int rightBound = getRightBound();
      final int middle = -rightBound / 2;
      if (startX > middle) {
        showContent(mFlingDuration);
      } else {
        hideContent(mFlingDuration);
      }
    }

    private void fling(int startX, int dx, int duration) {
      reset();

      if (dx == 0)
        return;

      if (duration <= 0) {
        viewContentContainer.scrollBy(-dx, 0);
        return;
      }

      mScroller.startScroll(startX, 0, dx, 0, duration);

      if (DEBUG)
        Log.d(TAG, "starting fling at " + startX + " for " + dx + " by " + duration);

      mLastFlingX = startX;
      viewContentContainer.post(this);
    }

    /**
     * Scrolling content view according by given value.
     * @param dx
     */
    private void scrollBy(int dx) {
      final int x = viewContentContainer.getScrollX();

      if (DEBUG)
        Log.d(TAG, "scroll from " + x + " by " + dx);

      final int scrollBy;
      if (dx < 0) { // scrolling right
        final int rightBound = getRightBound();
        if (x + dx < -rightBound)
          scrollBy = -rightBound - x;
        else
          scrollBy = dx;
      } else { // scrolling left
        // don't scroll if we are at left bound
        if (x == 0)
          return;

        if (x + dx > 0)
          scrollBy = -x;
        else
          scrollBy = dx;
      }

      viewContentContainer.scrollBy(scrollBy, 0);
    }

    /**
     * Returns right bound (limit) for scroller.
     * @return right bound (limit) for scroller.
     */
    private int getRightBound() {
      if (mSpacingType == SPACING_ACTIONS_WIDTH) {
        return mSpacing - mActionsSpacing;
      } else { // all other situations are handled as SPACING_RIGHT_OFFSET
        return getWidth() - mSpacing - mActionsSpacing;
      }
    }
  };

}
