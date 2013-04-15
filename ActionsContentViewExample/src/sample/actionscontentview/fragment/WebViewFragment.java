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
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class WebViewFragment extends Fragment {
  public final static String TAG = WebViewFragment.class.getSimpleName();

  private WebView viewContentWebView;
  private String url;

  private boolean resetHistory = true;

  @SuppressLint("SetJavaScriptEnabled")
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    final View v = inflater.inflate(R.layout.webview, container, false);

    final ProgressBar viewContentProgress = (ProgressBar) v.findViewById(R.id.progress);
    viewContentWebView = (WebView) v.findViewById(R.id.webview);
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

        if (newProgress == 100 && resetHistory) {
            viewContentWebView.clearHistory();
            resetHistory = false;
        }
      }
    });
    return v;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    reload();
  }

  @Override
  public void onHiddenChanged(boolean hidden) {
    super.onHiddenChanged(hidden);
    if (hidden)
      viewContentWebView.stopLoading();
    else
      reload();
  }

  public void setUrl(String url) {
    this.url = url;

    if (viewContentWebView != null)
      viewContentWebView.stopLoading();

    resetHistory = true;
  }

  public void reload() {
    if (TextUtils.isEmpty(url))
      return;

    viewContentWebView.loadUrl(url);
  }

  public boolean onBackPressed() {
    if (viewContentWebView.canGoBack()) {
      viewContentWebView.goBack();
      return true;
    }
    return false;
  }
}
