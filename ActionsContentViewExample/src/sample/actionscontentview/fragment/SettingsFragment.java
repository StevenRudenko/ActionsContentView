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
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

public class SettingsFragment extends Fragment {
  public static final String TAG = SettingsFragment.class.getSimpleName();

  public interface OnSettingsChangedListener {
    void onSettingChanged(int prefId, int value);
  }

  private static final String SETTINGS_SCHEME = "settings";
  private static final String SETTINGS_AUTHORITY = "settings";
  public static final Uri SETTINGS_URI = new Uri.Builder()
  .scheme(SETTINGS_SCHEME)
  .authority(SETTINGS_AUTHORITY)
  .build();

  public static final int PREF_SPACING_TYPE = R.id.prefSpacingType;
  public static final int PREF_SPACING_WIDTH = R.id.prefSpacingWidth;
  public static final int PREF_SPACING_ACTIONS_WIDTH = R.id.prefSpacingActionsWidth;
  public static final int PREF_SHOW_SHADOW = R.id.prefShowShadow;
  public static final int PREF_SHADOW_WIDTH = R.id.prefShadowWidth;

  private OnSettingsChangedListener mSettingsChangedListener;

  public void setOnSettingsChangedListener(OnSettingsChangedListener listener) {
    mSettingsChangedListener = listener;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    final View v = inflater.inflate(R.layout.settings, container, false);

    v.findViewById(PREF_SPACING_TYPE).setOnClickListener(onSettingsClicked);
    v.findViewById(PREF_SPACING_WIDTH).setOnClickListener(onSettingsClicked);
    v.findViewById(PREF_SPACING_ACTIONS_WIDTH).setOnClickListener(onSettingsClicked);
    v.findViewById(PREF_SHOW_SHADOW).setOnClickListener(onSettingsClicked);
    v.findViewById(PREF_SHADOW_WIDTH).setOnClickListener(onSettingsClicked);
    return v;
  }

  private View.OnClickListener onSettingsClicked = new View.OnClickListener() {
    @Override
    public void onClick(final View v) {
      final int id = v.getId();

      final int titleId;
      final int itemsArrayId;
      final int valuesArrayId;

      switch (id) {
      case PREF_SPACING_TYPE:
        titleId = R.string.pref_spacing_type;
        itemsArrayId = R.array.spacing_types;
        valuesArrayId = R.array.spacing_types_values;
        break;
      case PREF_SPACING_WIDTH:
        titleId = R.string.pref_spacing_width;
        itemsArrayId = R.array.width_strings;
        valuesArrayId = R.array.width_values;
        break;
      case PREF_SPACING_ACTIONS_WIDTH:
        titleId = R.string.pref_spacing_actions;
        itemsArrayId = R.array.width_strings;
        valuesArrayId = R.array.width_values;
        break;
      case PREF_SHOW_SHADOW:
        final CheckBox viewValue = (CheckBox) v.findViewById(R.id.value);
        final boolean checked = !viewValue.isChecked();
        viewValue.setChecked(checked);
        mSettingsChangedListener.onSettingChanged(id, checked ? 1 : 0);
        return;
      case PREF_SHADOW_WIDTH:
        titleId = R.string.pref_shadow_width;
        itemsArrayId = R.array.width_strings;
        valuesArrayId = R.array.width_values;
        break;

      default:
        return;
      }

      final FragmentTransaction ft = getFragmentManager().beginTransaction();
      Fragment prev = getFragmentManager().findFragmentByTag(SettingsChooserDialogFragment.TAG);
      if (prev != null) {
        ft.remove(prev);
      }
      ft.addToBackStack(null);

      final SettingsChooserDialogFragment fragment = SettingsChooserDialogFragment.newInstance(id, titleId, itemsArrayId);
      fragment.setOnSettingsSelectedListener(new SettingsChooserDialogFragment.OnSettingSelectedListener() {
        @Override
        public void onSettingSelected(int id, int item) {
          if (mSettingsChangedListener != null) {
            final int[] values = getResources().getIntArray(valuesArrayId);

            switch (id) {
            case PREF_SPACING_TYPE: {
              final TextView viewValue = (TextView) v.findViewById(R.id.value);
              final String value = getResources().getStringArray(R.array.spacing_types_short)[item];
              viewValue.setText(value);
              break;
            }
            case PREF_SPACING_WIDTH:
            case PREF_SPACING_ACTIONS_WIDTH:
            case PREF_SHADOW_WIDTH: {
              final TextView viewValue = (TextView) v.findViewById(R.id.value);
              final String value = Integer.toString(values[item]);
              viewValue.setText(value);
              break;
            }
            case PREF_SHOW_SHADOW:
              break;
            }

            mSettingsChangedListener.onSettingChanged(id, values[item]);
          }
        }
      });
      fragment.show(getFragmentManager(), SettingsChooserDialogFragment.TAG);
    }
  };
}
