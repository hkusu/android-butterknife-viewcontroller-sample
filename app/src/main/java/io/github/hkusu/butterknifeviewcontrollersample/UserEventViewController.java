package io.github.hkusu.butterknifeviewcontrollersample;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnTextChanged;

public class UserEventViewController extends ButterKnifeViewController<UserEventViewController.Listener> {
// NOTE：Listenerを設定しない場合は ButterKnifeViewController<Void> とする

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

    // Listenerを設定して呼び出し元にコールバックしたい場合のみ
    public interface Listener {
        void onCreateButtonClick();
        void onDeleteButtonClick();
    }

    @Override
    public void onStart(@Nullable Listener listener) {
        super.onStart(listener);
        mCreateButton.setEnabled(false); // 初期は[登録]ボタンを非活性に
    }

    /**
     * 入力エリアのテキスト変更
     */
    @OnTextChanged(R.id.todoEditText)
    public void onTodoEditTextChanged() {
        // [登録]ボタンを活性化
        mCreateButton.setEnabled(true);
    }

    /**
     * [登録]ボタン押下
     */
    @OnClick(R.id.createButton)
    public void onCreateButtonClick() {
        // 入力内容が空の場合は何もしない
        if (mTodoEditText.getText().toString().equals("")) {
            return;
        }
        // Todoデータを登録
        registerTodo();

        // Activityに[登録]ボタンが押下されたことを通知してみるテスト
        Listener listener = getListener();
        if (listener != null) {
            getListener().onCreateButtonClick();
        }
    }

    /**
     * 入力エリアでEnter
     *
     * @param event キーイベント
     * @return イベント処理結果(trueは消化済の意)
     */
    @OnEditorAction(R.id.todoEditText)
    public boolean onTodoEditTextEditorAction(KeyEvent event) {
        // 入力内容が空の場合は何もしない
        if (mTodoEditText.getText().toString().equals("")) {
            return true;
        }
        // 前半はソフトウェアキーボードのEnterキーの判定、後半は物理キーボードでの判定
        if (event == null || (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
            // Todoデータを登録
            registerTodo();
        }
        return true;
    }

    /**
     * 画面での入力内容をRealmへ登録するPrivateメソッド
     */
    @MainThread
    private void registerTodo() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        // Todoデータを作成
        TodoEntity todoEntity = new TodoEntity();
        todoEntity.setText(mTodoEditText.getText().toString());
        // データ操作モデルを通して登録
        TodoModel.getInstance().createOrUpdate(todoEntity);
        // 入力内容は空にする
        mTodoEditText.setText(null);
        // ソフトウェアキーボードを隠す
        ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mTodoEditText.getWindowToken(), 0);
        // [登録]ボタンを非活性に
        mCreateButton.setEnabled(false);
    }

    /**
     * EventBusからの通知の購読（削除ボタンの押下）
     *
     * @param event EventBus用のイベントクラス
     */
    @SuppressWarnings("unused")
    public void onEvent(TodoListAdapter.DeleteButtonClickedEvent event) {
        // データ操作モデルを通して削除
        TodoModel.getInstance().removeById(event.getId());

        // Activityに[削除]ボタンが押下されたことを通知してみるテスト
        Listener listener = getListener();
        if (listener != null) {
            getListener().onDeleteButtonClick();
        }
    }
}
