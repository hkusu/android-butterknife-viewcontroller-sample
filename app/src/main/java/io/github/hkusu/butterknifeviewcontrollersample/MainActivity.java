package io.github.hkusu.butterknifeviewcontrollersample;

import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.todoEditText)
    EditText mTodoEditText;
    @Bind(R.id.createButton)
    Button mCreateButton;
    @Bind(R.id.countTextView)
    TextView mCountTextView;
    @Bind(R.id.todoListView)
    ListView mTodoListView;

    /** Todoデータ表示用ListViewにセットするListAdapter */
    private TodoListAdapter mTodoListAdapter;
    /** ユーザイベントをハンドリングするController */
    private UserEventViewController mUserEventViewController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this); // ButterKnife

        // ToolBarの設定
        mToolbar.setTitle(R.string.app_name);
        setSupportActionBar(mToolbar);

        // ListAdapterを作成
        mTodoListAdapter = new TodoListAdapter(
                this,
                R.layout.adapter_todo_list,
                TodoModel.getInstance().get() // ListViewに表示するデータセット
        );
        // ListViewにAdapterをセット
        mTodoListView.setAdapter(mTodoListAdapter);

        // 起動時にソフトウェアキーボードが表示されないようにする
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Controllerを作成
        mUserEventViewController = new UserEventViewController(this); // parentとしてこのActivityを設定(利用しなければnullでもよい)
        mUserEventViewController.onCreate(this); // ButterKnife
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUserEventViewController.onStart();
        // 画面の初期表示
        updateView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this); // EventBus
        EventBus.getDefault().register(mUserEventViewController); // EventBus
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this); // EventBus
        EventBus.getDefault().unregister(mUserEventViewController); // EventBus
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this); // ButterKnife
        mUserEventViewController.onDestroy(); // ButterKnife
    }

    /**
     * EventBusからの通知の購読（Realm上のTodoデータの変更）*Viewの操作を伴う為メインスレッドで受ける*
     *
     * @param event EventBus用のイベントクラス
     */
    @SuppressWarnings("unused")
    public void onEventMainThread(TodoModel.ChangedEvent event) {
        // 画面の表示を更新
        updateView();
    }

    /**
     * 画面の表示を更新するPrivateメソッド
     */
    @MainThread
    private void updateView() {
        // データセットの変更があった旨をAdapterへ通知
        mTodoListAdapter.notifyDataSetChanged();
        // Todoデータの件数を更新
        mCountTextView.setText(String.valueOf(TodoModel.getInstance().getSize()));
    }
}
