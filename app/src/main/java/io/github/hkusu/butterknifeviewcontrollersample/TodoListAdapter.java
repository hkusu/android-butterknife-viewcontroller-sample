package io.github.hkusu.butterknifeviewcontrollersample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class TodoListAdapter extends ArrayAdapter<TodoEntity> {

    /** Contextを持ったLayoutInflater */
    private final LayoutInflater mLayoutInflater;
    /** レイアウトXMLファイルのid */
    private final int mResource;

    public TodoListAdapter(Context context, int resource, List<TodoEntity> objects) {
        super(context, resource, objects);
        mLayoutInflater = LayoutInflater.from(context);
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(mResource, parent, false);
        }

        // 今回はViewHolderに状態を持つので毎回作成する
        viewHolder = new ViewHolder(convertView);
        // この行のTodoデータを取得
        TodoEntity todoEntity = getItem(position);
        // Todoのテキストを表示
        viewHolder.mTodoTextView.setText(todoEntity.getText());
        // [削除]ボタン用にidを保持
        viewHolder.id = todoEntity.getId();

        return convertView;
    }

    /**
     * ViewHolder
     */
    static class ViewHolder {

        @Bind(R.id.todoTextView)
        TextView mTodoTextView;
        @Bind(R.id.deleteButton)
        Button mDeleteButton;

        /** Todoデータのid */
        private int id;

        ViewHolder(View view) {
            ButterKnife.bind(this, view); // ButterKnife
        }

        /**
         * [削除]ボタン押下
         */
        @OnClick(R.id.deleteButton)
        public void onDeleteButtonClick() {
            // EventBus経由でボタンが押下された旨を通知
            EventBus.getDefault().post(new DeleteButtonClickedEvent(id));
        }
    }

    /**
     * EventBus用のイベントクラス
     */
    public static class DeleteButtonClickedEvent {

        /** Todoデータのid */
        private int id;

        /**
         * コンストラクタ
         *
         * @param id 削除対象のTodoデータのid
         */
        private DeleteButtonClickedEvent(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }
}
