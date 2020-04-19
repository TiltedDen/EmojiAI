package com.klinker.android.emoji_keyboard;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface JsonEmojiAIAPI {

    @GET("{text}")
    Call<List<Prediction>> getPredictions(@Path("text") String text);

}
