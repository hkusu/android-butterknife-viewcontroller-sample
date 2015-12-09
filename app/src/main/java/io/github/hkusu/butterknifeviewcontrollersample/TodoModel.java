package io.github.hkusu.butterknifeviewcontrollersample;

import android.os.Handler;
import android.os.HandlerThread;

import java.util.List;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmResults;

public class TodoModel {

    /** シングルトンインスタンス */
    private static final TodoModel INSTANCE = new TodoModel();
    /** 更新用スレッド名 */
    private static final String WORKER_THREAD_NAME = "realm";

    /** Realmのインスタンス(参照用) */
    private final Realm mRealmForSelect = Realm.getDefaultInstance();
    /** Realmのインスタンス(更新用) */
    private Realm mRealmForUpdate;
    /** 更新(登録・削除)用スレッドへメッセージを送るためのハンドラ */
    private final Handler mWorkerHandler;

    /**
     * シングルトンインスタンスを返す
     *
     * @return Todoデータ操作Modelのシングルトンインスタンス
     */
    public static TodoModel getInstance() {
        return INSTANCE;
    }

    /**
     * コンストラクタ *外部からのインスタンス作成は禁止*
     */
    private TodoModel() {
        // 更新用スレッドを起動
        HandlerThread handlerThread = new HandlerThread(WORKER_THREAD_NAME);
        handlerThread.start();
        // 更新用スレッドのハンドラを取得
        mWorkerHandler = new Handler(handlerThread.getLooper());
        // 更新用スレッドでRealmのインスタンス(更新用)を作成
        mWorkerHandler.post(new Runnable() {
            @Override
            public void run() {
                mRealmForUpdate = Realm.getDefaultInstance();
            }
        });
    }

    /**
     * Todoデータ全件を取得(降順)
     *
     * @return TodoデータのList *RealmResult型であるためgetし直さなくても変更内容は動的に反映されることに注意*
     */
    public List<TodoEntity> get() {
        return mRealmForSelect.allObjectsSorted(TodoEntity.class, TodoEntity.SORT_KEY, RealmResults.SORT_ORDER_DESCENDING);
    }

    /**
     * idをキーにTodoデータを取得 ※現状は未使用*
     *
     * @param id 検索対象のTodoデータのid
     * @return Todoデータ(1件)
     */
    public TodoEntity getById(int id) {
        return mRealmForSelect.where(TodoEntity.class)
                .equalTo(TodoEntity.PRIMARY_KEY, id)
                .findFirst();
    }

    /**
     * Todoデータを登録
     *
     * @param todoEntity 登録するTodoデータ
     * @return 成否
     */
    public boolean createOrUpdate(final TodoEntity todoEntity) {
        if (todoEntity.getId() == 0) {
            // 登録されているTodoデータの最大idを取得し、+1 したものをidとする(つまり連番)
            todoEntity.setId(getMaxId() + 1);
        }

        // 更新用スレッドで実行
        mWorkerHandler.post(new Runnable() {
            @Override
            public void run() {
                // トランザクション開始
                mRealmForUpdate.beginTransaction();
                try {
                    // idにプライマリキーを張ってあるため既に同一idのデータが存在していれば更新となる
                    mRealmForUpdate.copyToRealmOrUpdate(todoEntity);
                    // コミット
                    mRealmForUpdate.commitTransaction();
                    // データが変更された旨をEventBusで通知
                    EventBus.getDefault().post(new ChangedEvent());
                } catch (Exception e) {
                    // ロールバック
                    mRealmForUpdate.cancelTransaction();
                }
            }
        });
        return true;
    }

    /**
     * idをキーにTodoデータを削除
     *
     * @param id 削除対象のTodoデータのid
     * @return 成否
     */
    public boolean removeById(final int id) {
        // 更新用スレッドで実行
        mWorkerHandler.post(new Runnable() {
            @Override
            public void run() {
                // トランザクション開始
                mRealmForUpdate.beginTransaction();
                try {
                    // idに一致するレコードを削除
                    mRealmForUpdate.where(TodoEntity.class).equalTo(TodoEntity.PRIMARY_KEY, id).findAll().clear();
                    // コミット
                    mRealmForUpdate.commitTransaction();
                    // データが変更された旨をEventBusで通知
                    EventBus.getDefault().post(new ChangedEvent());
                } catch (Exception e) {
                    // ロールバック
                    mRealmForUpdate.cancelTransaction();
                }
            }
        });
        return true;
    }

    /**
     * Todoデータの件数を取得
     *
     * @return 件数
     */
    public int getSize() {
        return mRealmForSelect.allObjects(TodoEntity.class).size();
    }

    /**
     * 登録されているTodoデータの最大idを取得
     *
     * @return 最大id
     */
    private int getMaxId() {
        return mRealmForSelect.where(TodoEntity.class).findAll().max(TodoEntity.PRIMARY_KEY).intValue();
    }

    /**
     * Realmの接続を切断 *以降は利用できなくなるので注意*
     */
    public void closeRealm() {
        mRealmForSelect.close();
        mRealmForUpdate.close();
    }

    /**
     * EventBus用のイベントクラス
     */
    public static class ChangedEvent {
        // 特に渡すデータは無し
        private ChangedEvent() {
        }
    }
}
