package sample.actionscontentview;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SitesAdapter extends BaseAdapter {
  private final LayoutInflater mInflater;

  private final String[] mTitles;
  private final String[] mUrls;
  private final TypedArray mIcons;

  public SitesAdapter(Context context, int titleArrayRes, int urlsArrayRes, int iconsArrayRes) {
    mInflater = LayoutInflater.from(context);

    final Resources res = context.getResources();
    mTitles = res.getStringArray(titleArrayRes);
    mUrls = res.getStringArray(urlsArrayRes);
    mIcons = res.obtainTypedArray(iconsArrayRes);
  }

  @Override
  public int getCount() {
    return mUrls.length;
  }

  @Override
  public String getItem(int position) {
    return mUrls[position];
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    final TextView v;
    if (convertView == null) {
      v = (TextView) mInflater.inflate(R.layout.action_list_item, parent, false);
      convertView = v;
    } else {
      v = (TextView) convertView;
    }

    v.setText(mTitles[position]);
    final Drawable icon = mIcons.getDrawable(position);
    icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
    v.setText(mTitles[position]);
    v.setCompoundDrawables(icon, null, null, null);

    return convertView;
  }

}
