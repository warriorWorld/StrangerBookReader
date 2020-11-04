package com.warrior.hangsu.administrator.strangerbookreader.base;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import io.reactivex.disposables.CompositeDisposable;

public class BaseViewModel extends ViewModel {
    protected CompositeDisposable mObserver = new CompositeDisposable();
    protected MutableLiveData<Boolean> isUpdating = new MutableLiveData<>();
    protected MutableLiveData<String> message = new MutableLiveData<>();

    public LiveData<Boolean> getIsUpdating() {
        return isUpdating;
    }

    public LiveData<String> getMessage() {
        return message;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mObserver.dispose();
    }
}
