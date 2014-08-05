package com.rosshambrick.rainorshine.core.model.events;

import retrofit.RetrofitError;

public class NetworkErrorOccurred {
    private RetrofitError mRetrofitError;

    public NetworkErrorOccurred(RetrofitError retrofitError) {

        mRetrofitError = retrofitError;
    }

    public RetrofitError getRetrofitError() {
        return mRetrofitError;
    }
}
