package com.kickstarter.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;

import com.kickstarter.R;
import com.kickstarter.libs.BaseActivity;
import com.kickstarter.libs.qualifiers.RequiresViewModel;
import com.kickstarter.libs.utils.ViewUtils;
import com.kickstarter.ui.adapters.ManageNotificationsAdapter;
import com.kickstarter.viewmodels.ManageNotificationsViewModel;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;

import static com.kickstarter.libs.utils.TransitionUtils.slideInFromLeft;

@RequiresViewModel(ManageNotificationsViewModel.class)
public final class ManageNotificationActivity extends BaseActivity<ManageNotificationsViewModel> {
  protected @Bind(R.id.project_notifications_recycler_view) RecyclerView recyclerView;

  protected @BindString(R.string.general_error_something_wrong) String generalErrorString;

  @Override
  protected void onCreate(final @Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.manage_notifications_layout);
    ButterKnife.bind(this);

    final ManageNotificationsAdapter adapter = new ManageNotificationsAdapter();
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));

    viewModel.outputs.notifications()
      .compose(bindToLifecycle())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(n -> adapter.takeNotifications(n, component().environment()));

    viewModel.errors.unableToFetchNotificationsError()
      .compose(bindToLifecycle())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(__ -> ViewUtils.showToast(this, generalErrorString));
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    recyclerView.setAdapter(null);
  }

  protected @Nullable Pair<Integer, Integer> exitTransition() {
    return slideInFromLeft();
  }
}
