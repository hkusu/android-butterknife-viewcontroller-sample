package io.github.hkusu.butterknifeviewcontrollersample;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TodoEntity extends RealmObject {

    /** プライマリキーの物理名(Todoデータ操作モデルで利用) */
    public static final String PRIMARY_KEY = "id";
    /** ソートキーの物理名(Todoデータ操作モデルで利用) */
    public static final String SORT_KEY = "id";

    // RealmObjectのカラム定義
    @PrimaryKey
    private int id;      // id
    private String text; // text

    // RealmObject では引数なしのデフォルトコンストラクタをpublicで定義する必要がある
    public TodoEntity() {
    }

    // 以降 Getter/Setter

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
