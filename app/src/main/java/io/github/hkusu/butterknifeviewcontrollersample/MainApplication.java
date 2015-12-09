package io.github.hkusu.butterknifeviewcontrollersample;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Realm の初期設定
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(realmConfiguration);
        // 起動時に Realm のデータを削除(開発時)
        //if (BuildConfig.DEBUG) {
        //    Realm.deleteRealm(realmConfiguration);
        //}
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        // Realm の接続を閉じる（どのみちアプリケーションが終了するが念のため）
        TodoModel.getInstance().closeRealm();
    }
}
