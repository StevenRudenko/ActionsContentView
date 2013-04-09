package sample.actionscontentview;

import sample.actionscontentview.adapter.EffectsAdapter;
import shared.ui.actionscontentview.ActionsContentView;
import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class EffectsExampleActivity extends FragmentActivity {

  private static final String STATE_POSITION = "state:layout_id";

  private static final String SCHEME = "settings";
  private static final String AUTHORITY = "effects";
  public static final Uri URI = new Uri.Builder()
  .scheme(SCHEME)
  .authority(AUTHORITY)
  .build();

  private EffectsAdapter mAdapter;
  private ListView viewList;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mAdapter = new EffectsAdapter(this);

    final int selectedPosition;
    if (savedInstanceState != null) {
      selectedPosition = savedInstanceState.getInt(STATE_POSITION, 0);
    } else {
      selectedPosition = 0;
    }

    init(selectedPosition);
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    final int position = viewList.getSelectedItemPosition();
    if (position != ListView.INVALID_POSITION)
      outState.putInt(STATE_POSITION, position);
  }

  @SuppressLint("DefaultLocale")
  private void init(int position) {
    final int layoutId = mAdapter.getItem(position);
    setContentView(layoutId);

    final ActionsContentView viewActionsContentView = (ActionsContentView) findViewById(R.id.actionsContentView);
    viewActionsContentView.setOnActionsContentListener(new ActionsContentView.OnActionsContentListener() {
      @Override
      public void onContentStateChanged(ActionsContentView v, boolean isContentShown) {
        v.getContentController().setIgnoreTouchEvents(!isContentShown);
      }
    });

    final TextView name = (TextView) findViewById(R.id.effect_name);
    name.setText(mAdapter.getEffectTitle(position));

    final TextView actions = (TextView) findViewById(R.id.actions_html);
    final String actionsHtml = mAdapter.getActionsHtml(position);
    if (!TextUtils.isEmpty(actionsHtml)) {
      findViewById(R.id.effect_actions_layout).setVisibility(View.VISIBLE);
      actions.setText(Html.fromHtml(actionsHtml));
    }

    final TextView content = (TextView) findViewById(R.id.content_html);
    final String contentHtml = mAdapter.getContentHtml(position);
    if (!TextUtils.isEmpty(contentHtml)) {
      findViewById(R.id.effect_content_layout).setVisibility(View.VISIBLE);
      content.setText(Html.fromHtml(contentHtml));
    }

    viewList = (ListView) findViewById(R.id.actions);
    viewList.setAdapter(mAdapter);
    viewList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapter, View v, int position,
          long flags) {
        init(position);
      }
    });
  }

}
