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

import sample.actionscontentview.R;

import shared.ui.actionscontentview.ActionsContentView;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

public class ExamplesActivity extends Activity {
  private ActionsContentView viewActionsContentView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.example);

    viewActionsContentView = (ActionsContentView) findViewById(R.id.content);

    final ProgressBar viewContentProgress = (ProgressBar) findViewById(R.id.progress);
    final WebView viewContentWebView = (WebView) findViewById(R.id.webview);
    viewContentWebView.getSettings().setJavaScriptEnabled(true);
    viewContentWebView.setWebViewClient(new WebViewClient() {
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return super.shouldOverrideUrlLoading(view, url);
      }
    });
    viewContentWebView.setWebChromeClient(new WebChromeClient() {
      @Override
      public void onProgressChanged(WebView view, int newProgress) {
        viewContentProgress.setProgress(newProgress);
        viewContentProgress.setVisibility(newProgress == 100 ? View.GONE : View.VISIBLE);
      }
    });

    final String[] titles = getResources().getStringArray(R.array.site_names);
    final String[] linkes = getResources().getStringArray(R.array.site_links);

    final ListView viewActionsList = (ListView) findViewById(R.id.actions);
    final ArrayAdapter<String> actionsAdapter = new ArrayAdapter<String>(this, R.layout.action_list_item, titles);
    viewActionsList.setAdapter(actionsAdapter);
    viewActionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapter, View v, int position,
          long flags) {
        final String url = linkes[position];
        viewContentWebView.loadUrl(url);
        viewActionsContentView.showContent();
      }
    });

    viewContentWebView.loadUrl(linkes[0]);
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
}
