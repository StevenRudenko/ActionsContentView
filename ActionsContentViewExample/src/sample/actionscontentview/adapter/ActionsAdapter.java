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
package sample.actionscontentview.adapter;

import sample.actionscontentview.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ActionsAdapter extends BaseAdapter {

  private static final int VIEW_TYPE_CATEGORY = 0;
  private static final int VIEW_TYPE_SETTINGS = 1;
  private static final int VIEW_TYPE_SITES = 2;
  private static final int VIEW_TYPES_COUNT = 3;

  private final Context mContext;
  private final LayoutInflater mInflater;

  private final String[] mTitles;
  private final String[] mUrls;
  private final int[] mIcons;

  public ActionsAdapter(Context context) {
    mContext = context;
    mInflater = LayoutInflater.from(context);

    final Resources res = context.getResources();
    mTitles = res.getStringArray(R.array.actions_names);
    mUrls = res.getStringArray(R.array.actions_links);

    final TypedArray iconsArray = res.obtainTypedArray(R.array.actions_icons);
    final int count = iconsArray.length();
    mIcons = new int[count];
    for ( int i=0; i<count; ++i ) {
      mIcons[i] = iconsArray.getResourceId(i, 0);
    }
    iconsArray.recycle();
  }

  @Override
  public int getCount() {
    return mUrls.length;
  }

  @Override
  public Uri getItem(int position) {
    return Uri.parse(mUrls[position]);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @SuppressLint("DefaultLocale")
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    final int type = getItemViewType(position);

    final ViewHolder holder;
    if (convertView == null) {
      if (type == VIEW_TYPE_CATEGORY)
        convertView = mInflater.inflate(R.layout.category_list_item, parent, false);
      else
        convertView = mInflater.inflate(R.layout.action_list_item, parent, false);

      holder = new ViewHolder();
      holder.text = (TextView) convertView.findViewById(android.R.id.text1);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    if (type != VIEW_TYPE_CATEGORY) {
      final Drawable icon = mContext.getResources().getDrawable(mIcons[position]);
      icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
      holder.text.setCompoundDrawables(icon, null, null, null);
      holder.text.setText(mTitles[position]);
    } else {
      holder.text.setText(mTitles[position].toUpperCase());
    }

    return convertView;
  }

  @Override
  public int getViewTypeCount() {
    return VIEW_TYPES_COUNT;
  }

  @Override
  public int getItemViewType(int position) {
    final Uri uri = Uri.parse(mUrls[position]);
    final String scheme = uri.getScheme();
    if ("category".equals(scheme))
      return VIEW_TYPE_CATEGORY;
    else if ("settings".equals(scheme))
      return VIEW_TYPE_SETTINGS;
    return VIEW_TYPE_SITES;
  }

  @Override
  public boolean isEnabled(int position) {
    return getItemViewType(position) != VIEW_TYPE_CATEGORY;
  }

  private static class ViewHolder {
    TextView text;
  }
}
