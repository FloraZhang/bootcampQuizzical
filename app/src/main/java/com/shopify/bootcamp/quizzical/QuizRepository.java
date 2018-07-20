package com.shopify.bootcamp.quizzical;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.ResponseCache;
import java.util.List;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.Okio;

public class QuizRepository {
    private Context context;

    public QuizRepository(Context context) {
        this.context = context;
    }

    public Quiz getQuiz() {
        InputStream inputStream;
        try {
            inputStream = context.getAssets().open("quiz.json");
        } catch (IOException e) {
            Log.e("QuizRepo", "unable to open quiz.json", e);
            return null;
        }

        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Quiz> jsonAdapter = moshi.adapter(Quiz.class);
        try {
            Quiz quiz = jsonAdapter.fromJson(Okio.buffer(Okio.source(inputStream)));
            return quiz;
        } catch (IOException e) {
            Log.e("QuizRepo", "Could not parse json", e);
            return null;
        }

    }
    public void getRemoteQuiz(int id, final QuizCallback quizCallback) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new ResponseCacheInterceptor())
                .cache(new Cache(new File(
                        this.context.getCacheDir(),
                        "apiResponses"), 5 * 1024 * 1024))
                .build();
        Request request = new Request.Builder()
                .url("https://oolong.tahnok.me/cdn/quizzes/" + id + ".json")
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                quizCallback.onFailure();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    quizCallback.onFailure();
                }

                Moshi moshi = new Moshi.Builder().build();
                JsonAdapter<Quiz> jsonAdapter = moshi.adapter(Quiz.class);
                Quiz quiz = jsonAdapter.fromJson(response.body().source());
                quizCallback.onSuccess(quiz);
            }
        });

    }


    public interface QuizCallback {
        void onFailure();
        void onSuccess (Quiz quiz);
    }

    public void getRemoteQuizzes(final QuizzesCallback quizzesCallback) {
        Request request = new Request.Builder()
                .url("https://oolong.tahnok.me/cdn/quizzes.json")
                .build();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new ResponseCacheInterceptor())
                .cache(new Cache(new File(
                        this.context.getCacheDir(),
                        "apiResponses"), 5 * 1024 * 1024))
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                quizzesCallback.onFailure();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    quizzesCallback.onFailure();
                }

                Moshi moshi = new Moshi.Builder().build();
                Type type = Types.newParameterizedType(List.class, Quiz.class);
                JsonAdapter<List<Quiz>> jsonAdapter = moshi.adapter(type);
                List<Quiz> quizzes = jsonAdapter.fromJson(response.body().source());
                quizzesCallback.onSuccess(quizzes);
            }
        });

    }


    public interface QuizzesCallback {
        void onFailure();
        void onSuccess (List<Quiz> quizzes);
    }

    private static class ResponseCacheInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            okhttp3.Response originalResponse = chain.proceed(chain.request());
            return originalResponse.newBuilder()
                    .header("Cache-Control", "public, max-age=" + 60)
                    .build();
        }
    }

}
