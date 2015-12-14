package io.github.hkusu.butterknifeviewcontrollersample;

import android.app.Activity;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;

public class ButterKnifeViewController<T> {

    private WeakReference<Activity> mActivityRef;
    private WeakReference<T> mListenerRef;

    @Nullable
    @CheckResult
    protected final Activity getActivity() {
        if (mActivityRef == null) {
            return null;
        }
        return mActivityRef.get();
    }

    @Nullable
    @CheckResult
    protected final T getListener() {
        if (mListenerRef == null) {
            return null;
        }
        return mListenerRef.get();
    }

    @CallSuper
    public void onCreate(@NonNull Activity activity) {
        ButterKnife.bind(this, activity);
        mActivityRef = new WeakReference<>(activity);
    }

    @CallSuper
    public void onStart(@Nullable T listener) {
        if (listener != null) {
            mListenerRef = new WeakReference<>(listener);
        }
    }

    protected void onResume() {}

    protected void onPause() {}

    protected void onStop() {}

    @CallSuper
    public void onDestroy() {
        ButterKnife.unbind(this);
        mActivityRef = null;
        mListenerRef = null; // onStopで処理したいがそのためにコールさせるのは冗長なので
    }
}