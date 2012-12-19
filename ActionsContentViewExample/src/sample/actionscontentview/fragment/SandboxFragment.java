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
package sample.actionscontentview.fragment;

import sample.actionscontentview.R;
import shared.ui.actionscontentview.ActionsContentView;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class SandboxFragment extends Fragment implements View.OnClickListener {
  public static final String TAG = SandboxFragment.class.getSimpleName();
  private static final boolean DEBUG = false;

  public interface OnSettingsChangedListener {
    void onSettingChanged(int prefId, int value);
  }

  private static final String SETTINGS_SCHEME = "settings";
  private static final String SETTINGS_AUTHORITY = "sandbox";
  public static final Uri SETTINGS_URI = new Uri.Builder()
  .scheme(SETTINGS_SCHEME)
  .authority(SETTINGS_AUTHORITY)
  .build();

  public static final int PREF_SPACING_TYPE = R.id.prefSpacingType;
  public static final int PREF_SPACING_WIDTH = R.id.prefSpacingWidth;
  public static final int PREF_SPACING_ACTIONS_WIDTH = R.id.prefSpacingActionsWidth;
  public static final int PREF_SHOW_SHADOW = R.id.prefShowShadow;
  public static final int PREF_SHADOW_WIDTH = R.id.prefShadowWidth;
  public static final int PREF_FADE_TYPE = R.id.prefFadeType;
  public static final int PREF_FADE_MAX_VALUE = R.id.prefFadeMaxValue;
  public static final int PREF_FLING_DURATION = R.id.prefFlingDuration;

  private static final int PREF_SPACING_TYPE_VALUE = R.id.prefSpacingTypeValue;
  private static final int PREF_SPACING_WIDTH_VALUE = R.id.prefSpacingWidthValue;
  private static final int PREF_SPACING_ACTIONS_WIDTH_VALUE = R.id.prefSpacingActionsWidthValue;
  private static final int PREF_SHOW_SHADOW_VALUE = R.id.prefShowShadowValue;
  private static final int PREF_SHADOW_WIDTH_VALUE = R.id.prefShadowWidthValue;
  private static final int PREF_FADE_TYPE_VALUE = R.id.prefFadeTypeValue;
  private static final int PREF_FADE_MAX_VALUE_VALUE = R.id.prefprefFadeMaxValueValue;
  private static final int PREF_FLING_DURATION_VALUE = R.id.prefFlingDurationValue;

  private View viewRoot;
  private OnSettingsChangedListener mSettingsChangedListener;

  public void setOnSettingsChangedListener(OnSettingsChangedListener listener) {
    mSettingsChangedListener = listener;
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    saveStringPrefState(outState, PREF_SPACING_TYPE_VALUE);
    saveStringPrefState(outState, PREF_SPACING_WIDTH_VALUE);
    saveStringPrefState(outState, PREF_SPACING_ACTIONS_WIDTH_VALUE);
    saveBooleanPrefState(outState, PREF_SHOW_SHADOW_VALUE);
    saveStringPrefState(outState, PREF_SHADOW_WIDTH_VALUE);
    saveStringPrefState(outState, PREF_FADE_TYPE_VALUE);
    saveStringPrefState(outState, PREF_FADE_MAX_VALUE_VALUE);
    saveStringPrefState(outState, PREF_FLING_DURATION_VALUE);
    super.onSaveInstanceState(outState);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    if (savedInstanceState != null) {
      final String spacingType = restoreStringPrefState(savedInstanceState, PREF_SPACING_TYPE_VALUE);
      restoreStringPrefState(savedInstanceState, PREF_SPACING_WIDTH_VALUE);
      restoreStringPrefState(savedInstanceState, PREF_SPACING_ACTIONS_WIDTH_VALUE);
      restoreBooleanPrefState(savedInstanceState, PREF_SHOW_SHADOW_VALUE);
      restoreStringPrefState(savedInstanceState, PREF_SHADOW_WIDTH_VALUE);
      restoreStringPrefState(savedInstanceState, PREF_FADE_TYPE_VALUE);
      restoreStringPrefState(savedInstanceState, PREF_FADE_MAX_VALUE_VALUE);
      restoreStringPrefState(savedInstanceState, PREF_FLING_DURATION_VALUE);

      final int[] spacingTypes = getResources().getIntArray(R.array.spacing_types_values);
      final String[] spacingTypeValues = getResources().getStringArray(R.array.spacing_types_short);
      final int count = spacingTypeValues.length;
      for (int i=0; i<count; ++i) {
        if (spacingType.equals(spacingTypeValues[i])) {
          // showing additional value for spacing
          if (spacingTypes[i] == ActionsContentView.SPACING_ACTIONS_WIDTH) {
            viewRoot.findViewById(R.id.prefSpacingWidthAdditionalValue).setVisibility(View.VISIBLE);
          } else {
            viewRoot.findViewById(R.id.prefSpacingWidthAdditionalValue).setVisibility(View.GONE);
          }
          break;
        }
      }
    }

    super.onActivityCreated(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    viewRoot= inflater.inflate(R.layout.sandbox, container, false);

    viewRoot.findViewById(PREF_SPACING_TYPE).setOnClickListener(this);
    viewRoot.findViewById(PREF_SPACING_WIDTH).setOnClickListener(this);
    viewRoot.findViewById(PREF_SPACING_ACTIONS_WIDTH).setOnClickListener(this);
    viewRoot.findViewById(PREF_SHOW_SHADOW).setOnClickListener(this);
    viewRoot.findViewById(PREF_SHADOW_WIDTH).setOnClickListener(this);
    viewRoot.findViewById(PREF_FADE_MAX_VALUE).setOnClickListener(this);
    viewRoot.findViewById(PREF_FADE_TYPE).setOnClickListener(this);
    viewRoot.findViewById(PREF_FLING_DURATION).setOnClickListener(this);
    return viewRoot;
  }

  @Override
  public void onClick(final View v) {
    final int id = v.getId();

    final int titleId;
    final int valueId;
    final int itemsArrayId;
    final int valuesArrayId;

    switch (id) {
    case PREF_SPACING_TYPE:
      titleId = R.string.pref_spacing_type;
      valueId = PREF_SPACING_TYPE_VALUE;
      itemsArrayId = R.array.spacing_types;
      valuesArrayId = R.array.spacing_types_values;
      break;
    case PREF_SPACING_WIDTH:
      titleId = R.string.pref_spacing_width;
      valueId = PREF_SPACING_WIDTH_VALUE;
      itemsArrayId = R.array.width_strings;
      valuesArrayId = R.array.width_values;
      break;
    case PREF_SPACING_ACTIONS_WIDTH:
      titleId = R.string.pref_spacing_actions;
      valueId = PREF_SPACING_ACTIONS_WIDTH_VALUE;
      itemsArrayId = R.array.width_strings;
      valuesArrayId = R.array.width_values;
      break;
    case PREF_SHOW_SHADOW:
      final CheckBox viewValue = (CheckBox) v.findViewById(PREF_SHOW_SHADOW_VALUE);
      final boolean checked = !viewValue.isChecked();
      viewValue.setChecked(checked);
      if (mSettingsChangedListener != null) {
        mSettingsChangedListener.onSettingChanged(id, checked ? 1 : 0);
      } else if (DEBUG) {
        Log.w(TAG, "OnSettingsChangedListener is not set");
      }
      return;
    case PREF_SHADOW_WIDTH:
      valueId = PREF_SHADOW_WIDTH_VALUE;
      titleId = R.string.pref_shadow_width;
      itemsArrayId = R.array.width_strings;
      valuesArrayId = R.array.width_values;
      break;
    case PREF_FADE_TYPE:
      titleId = R.string.pref_fade_type;
      valueId = PREF_FADE_TYPE_VALUE;
      itemsArrayId = R.array.fade_types;
      valuesArrayId = R.array.fade_types_values;
      break;
    case PREF_FADE_MAX_VALUE:
      titleId = R.string.pref_fade_max_value;
      valueId = PREF_FADE_MAX_VALUE_VALUE;
      itemsArrayId = R.array.fade_max_value_strings;
      valuesArrayId = R.array.fade_max_value_values;
      break;
    case PREF_FLING_DURATION:
      titleId = R.string.pref_other_fling_duration;
      valueId = PREF_FLING_DURATION_VALUE;
      itemsArrayId = R.array.fling_duration_strings;
      valuesArrayId = R.array.fling_duration_values;
      break;
    default:
      return;
    }

    final FragmentTransaction ft = getFragmentManager().beginTransaction();
    Fragment prev = getFragmentManager().findFragmentByTag(ValueChooserDialogFragment.TAG);
    if (prev != null) {
      ft.remove(prev);
    }
    ft.addToBackStack(null);

    final ValueChooserDialogFragment fragment = ValueChooserDialogFragment.newInstance(id, titleId, itemsArrayId);
    fragment.setOnSettingsSelectedListener(new ValueChooserDialogFragment.OnSettingSelectedListener() {
      @Override
      public void onSettingSelected(int id, int item) {
        final int[] values = getResources().getIntArray(valuesArrayId);

        switch (id) {
        case PREF_SPACING_TYPE: {
          final TextView viewValue = (TextView) v.findViewById(valueId);
          final String value = getResources().getStringArray(R.array.spacing_types_short)[item];
          viewValue.setText(value);

          // showing additional value for spacing
          if (values[item] == ActionsContentView.SPACING_ACTIONS_WIDTH) {
            viewRoot.findViewById(R.id.prefSpacingWidthAdditionalValue).setVisibility(View.VISIBLE);
          } else {
            viewRoot.findViewById(R.id.prefSpacingWidthAdditionalValue).setVisibility(View.GONE);
          }
          break;
        }
        case PREF_FADE_TYPE: {
          final TextView viewValue = (TextView) v.findViewById(valueId);
          final String value = getResources().getStringArray(R.array.fade_types)[item];
          viewValue.setText(value);
          break;
        }
        case PREF_SPACING_WIDTH:
        case PREF_SHADOW_WIDTH:
        case PREF_FADE_MAX_VALUE:
        case PREF_FLING_DURATION: {
          final TextView viewValue = (TextView) v.findViewById(valueId);
          final String value = Integer.toString(values[item]);
          viewValue.setText(value);
          break;
        }
        case PREF_SHOW_SHADOW:
          break;
        }

        if (mSettingsChangedListener != null) {
          mSettingsChangedListener.onSettingChanged(id, values[item]);
        } else if (DEBUG) {
          Log.w(TAG, "OnSettingsChangedListener is not set");
        }
      }
    });
    fragment.show(getFragmentManager(), ValueChooserDialogFragment.TAG);
  }

  private void saveStringPrefState(Bundle outState, int prefValue) {
    final TextView viewValue = (TextView) viewRoot.findViewById(prefValue);
    outState.putString(String.valueOf(prefValue), viewValue.getText().toString());
  }

  private void saveBooleanPrefState(Bundle outState, int prefValue) {
    final CompoundButton viewValue = (CompoundButton) viewRoot.findViewById(prefValue);
    outState.putBoolean(String.valueOf(prefValue), viewValue.isChecked());
  }

  private String restoreStringPrefState(Bundle savedInstanceState, int prefValue) {
    final String value = savedInstanceState.getString(String.valueOf(prefValue));
    final TextView viewValue = (TextView) viewRoot.findViewById(prefValue);
    viewValue.setText(value);
    return value;
  }

  private boolean restoreBooleanPrefState(Bundle savedInstanceState, int prefValue) {
    final boolean value = savedInstanceState.getBoolean(String.valueOf(prefValue));
    final CompoundButton viewValue = (CompoundButton) viewRoot.findViewById(prefValue);
    viewValue.setChecked(value);
    return value;
  }
}
