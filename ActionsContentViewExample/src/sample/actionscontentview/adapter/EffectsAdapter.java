package sample.actionscontentview.adapter;

import sample.actionscontentview.R;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class EffectsAdapter extends BaseAdapter {
  private final LayoutInflater mInflater;

  private final String[] mTitles;
  private final int[] mLayouts;

  public EffectsAdapter(Context context) {
    mInflater = LayoutInflater.from(context);

    final Resources res = context.getResources();
    mTitles = res.getStringArray(R.array.effects_name);

    final TypedArray layoutsArray = res.obtainTypedArray(R.array.effect_layouts);
    final int count = layoutsArray.length();
    mLayouts = new int[count];
    for ( int i=0; i<count; ++i ) {
      mLayouts[i] = layoutsArray.getResourceId(i, 0);
    }
    layoutsArray.recycle();
  }

  @Override
  public int getCount() {
    return mLayouts.length;
  }

  @Override
  public Integer getItem(int position) {
    return mLayouts[position];
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    final ViewHolder holder;
    if (convertView == null) {
      convertView = mInflater.inflate(R.layout.action_list_item, parent, false);

      holder = new ViewHolder();
      holder.text = (TextView) convertView.findViewById(android.R.id.text1);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    holder.text.setText(mTitles[position]);

    return convertView;
  }

  private static class ViewHolder {
    TextView text;
  }
}
