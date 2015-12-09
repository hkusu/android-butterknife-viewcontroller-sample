package io.github.hkusu.butterknifeviewcontrollersample;

import android.app.Activity;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import butterknife.ButterKnife;

public class ButterKnifeViewController<T> {

    private T mParent;

    protected ButterKnifeViewController(@Nullable T parent) {
        mParent = parent;
    }

    @Nullable
    protected T getParent() {
        return mParent;
    }

    @CallSuper
    public void onCreate(@NonNull Activity activity) {
        ButterKnife.bind(this, activity);
    }

    protected void onStart() {}

    protected void onResume() {}

    protected void onPause() {}

    protected void onStop() {}

    @CallSuper
    public void onDestroy() {
        ButterKnife.unbind(this);
        mParent = null;
    }
}