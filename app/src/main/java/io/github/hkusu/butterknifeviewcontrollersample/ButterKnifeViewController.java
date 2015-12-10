package io.github.hkusu.butterknifeviewcontrollersample;

import android.app.Activity;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;

public class ButterKnifeViewController<T> {

    private WeakReference<T> mParentRef;

    protected ButterKnifeViewController(@Nullable T parent) {
        if (parent != null) {
            mParentRef = new WeakReference<>(parent);
        }
    }

    @Nullable
    protected T getParent() {
        if (mParentRef == null) {
            return null;
        }
        return mParentRef.get();
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
        mParentRef = null;
    }
}