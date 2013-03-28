package sample.actionscontentview;

import sample.actionscontentview.adapter.EffectsAdapter;
import shared.ui.actionscontentview.ActionsContentView;
import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class EffectsExampleActivity extends FragmentActivity {

  private static final String STATE_LAYOUT_ID = "state:layout_id";

  private static final String SCHEME = "settings";
  private static final String AUTHORITY = "effects";
  public static final Uri URI = new Uri.Builder()
  .scheme(SCHEME)
  .authority(AUTHORITY)
  .build();

  private EffectsAdapter effectsAdapter;
  private int mLayoutId = R.layout.effect_parallax;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    effectsAdapter = new EffectsAdapter(this);

    if (savedInstanceState != null) {
      mLayoutId = savedInstanceState.getInt(STATE_LAYOUT_ID);
    }

    init(mLayoutId);
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    outState.putInt(STATE_LAYOUT_ID, mLayoutId);

    super.onSaveInstanceState(outState);
  }

  @SuppressLint("DefaultLocale")
  private void init(int layoutId) {
    setContentView(layoutId);

    final ActionsContentView viewActionsContentView = (ActionsContentView) findViewById(R.id.actionsContentView);
    viewActionsContentView.setOnActionsContentListener(new ActionsContentView.OnActionsContentListener() {
      @Override
      public void onContentStateChanged(ActionsContentView v, boolean isContentShown) {
        v.getContentController().setIgnoreTouchEvents(!isContentShown);
      }
    });

    final TextView title = (TextView) findViewById(android.R.id.text1);
    final String titleText = getString(R.string.action_effects).toUpperCase();
    title.setText(titleText);

    final ListView viewActionsList = (ListView) findViewById(R.id.actions);
    viewActionsList.setAdapter(effectsAdapter);
    viewActionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapter, View v, int position,
          long flags) {
        init(effectsAdapter.getItem(position));
      }
    });
  }
}
