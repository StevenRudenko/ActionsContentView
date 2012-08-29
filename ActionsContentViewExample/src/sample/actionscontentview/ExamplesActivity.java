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
package sample.actionscontentview;

import sample.actionscontentview.adapter.ActionsAdapter;
import sample.actionscontentview.fragment.AboutFragment;
import sample.actionscontentview.fragment.SettingsFragment;
import sample.actionscontentview.fragment.WebViewFragment;
import shared.ui.actionscontentview.ActionsContentView;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class ExamplesActivity extends FragmentActivity {
  private ActionsContentView viewActionsContentView;

  private String currentContentFragmentTag = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.example);

    viewActionsContentView = (ActionsContentView) findViewById(R.id.actionsContentView);

    final ListView viewActionsList = (ListView) findViewById(R.id.actions);
    final ActionsAdapter actionsAdapter = new ActionsAdapter(this);
    viewActionsList.setAdapter(actionsAdapter);
    viewActionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapter, View v, int position,
          long flags) {
        final Uri uri = actionsAdapter.getItem(position);
        updateContent(uri);
        viewActionsContentView.showContent();
      }
    });

    updateContent(AboutFragment.ABOUT_URI);
  }

  public void onActionsButtonClick(View view) {
    if (viewActionsContentView.isActionsShown())
      viewActionsContentView.showContent();
    else
      viewActionsContentView.showActions();
  }

  public void onSourceCodeClick(View view) {
    final Intent i = new Intent(Intent.ACTION_VIEW);
    i.setData(Uri.parse(getString(R.string.sources_link)));
    startActivity(i);
  }

  public void updateContent(Uri uri) {
    final Fragment fragment;
    final String tag;

    final FragmentManager fm = getSupportFragmentManager();
    final FragmentTransaction tr = fm.beginTransaction();

    if (currentContentFragmentTag != null) {
      final Fragment currentFragment = fm.findFragmentByTag(currentContentFragmentTag);
      if (currentFragment != null)
        tr.hide(currentFragment);
    }

    if (AboutFragment.ABOUT_URI.equals(uri)) {
      tag = AboutFragment.TAG;
      final Fragment foundFragment = fm.findFragmentByTag(tag);
      if (foundFragment != null) {
        fragment = foundFragment;
      } else {
        fragment = new AboutFragment();
      }
    } else if (SettingsFragment.SETTINGS_URI.equals(uri)) {
      tag = SettingsFragment.TAG;
      final Fragment foundFragment = fm.findFragmentByTag(tag);
      if (foundFragment != null) {
        fragment = foundFragment;
      } else {
        final SettingsFragment settingsFragment = new SettingsFragment();
        settingsFragment.setOnSettingsChangedListener(getSettingsChangedListener());
        fragment = settingsFragment;
      }
    } else if (uri != null) {
      tag = WebViewFragment.TAG;
      final WebViewFragment webViewFragment;
      final Fragment foundFragment = fm.findFragmentByTag(tag);
      if (foundFragment != null) {
        fragment = foundFragment;
        webViewFragment = (WebViewFragment) fragment;
      } else {
        webViewFragment = new WebViewFragment();
        fragment = webViewFragment;
      }
      webViewFragment.setUrl(uri.toString());
    } else {
      return;
    }

    if (fragment.isAdded()) {
      tr.show(fragment);
    } else {
      tr.add(R.id.content, fragment, tag);
    }
    tr.commit();

    currentContentFragmentTag = tag;
  }

  private SettingsFragment.OnSettingsChangedListener getSettingsChangedListener() {
    return new SettingsFragment.OnSettingsChangedListener() {
      private final float mDensity = getResources().getDisplayMetrics().density;

      @Override
      public void onSettingChanged(int prefId, int value) {
        switch (prefId) {
        case SettingsFragment.PREF_SPACING_TYPE:
          viewActionsContentView.setSpacingType(value);
          return;
        case SettingsFragment.PREF_SPACING_WIDTH:
          viewActionsContentView.setSpacingWidth((int) (value * mDensity));
          return;
        case SettingsFragment.PREF_SPACING_ACTIONS_WIDTH:
          viewActionsContentView.setActionsSpacingWidth((int) (value * mDensity));
          return;
        case SettingsFragment.PREF_SHOW_SHADOW:
          viewActionsContentView.setShadowVisible(value == 1);
          return;
        case SettingsFragment.PREF_SHADOW_WIDTH:
          viewActionsContentView.setShadowWidth((int) (value * mDensity));
          return;
        default:
          return;
        }
      }
    };
  }
}
