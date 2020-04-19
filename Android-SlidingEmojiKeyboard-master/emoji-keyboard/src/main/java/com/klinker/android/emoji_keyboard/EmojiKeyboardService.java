package com.klinker.android.emoji_keyboard;

import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.klinker.android.emoji_keyboard.adapter.EmojiPagerAdapter;
import com.klinker.android.emoji_keyboard.adapter.StaticEmojiAdapter;
import com.klinker.android.emoji_keyboard.constants.EmojiTexts;
import com.klinker.android.emoji_keyboard.constants.Google_EmojiDict;
import com.klinker.android.emoji_keyboard.view.EmojiKeyboardView;
import com.klinker.android.emoji_keyboard.view.KeyboardSinglePageView;
import com.klinker.android.emoji_keyboard_trial.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EmojiKeyboardService extends InputMethodService {

    private EmojiKeyboardView emojiKeyboardView;

    private InputConnection inputConnection;

    private String userInput;

    private InputMethodManager previousInputMethodManager;
    private IBinder iBinder;

    private static Context staticApplicationContext;

    public static Context getStaticApplicationContext() {
        return staticApplicationContext;
    }

    public EmojiKeyboardService() {
        super();

        if (Build.VERSION.SDK_INT >= 17) {
            enableHardwareAcceleration();
        }
    }

    @Override
    public View onCreateInputView() {

        staticApplicationContext = getApplicationContext();


        // Grab all input text before the cursor and make JSON request
        inputConnection = getCurrentInputConnection();
        CharSequence charSeq = inputConnection.getTextBeforeCursor(1000, 0);
        userInput = charSeq.toString();
        // Log.d("STATE", userInput);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonEmojiAIAPI jsonEmojiAIAPI = retrofit.create(JsonEmojiAIAPI.class);
        Call<List<Prediction>> call = jsonEmojiAIAPI.getPredictions(userInput);
        // call the RESTAPI and check the response, after handed response, populate suggested view
        call.enqueue(new Callback<List<Prediction>>() {
            @Override
            public void onResponse(Call<List<Prediction>> call, Response<List<Prediction>> response) {
                if (!response.isSuccessful()){
                    // do nothing
                    return;
                }

                List<Prediction> predictions = response.body();
                List<String> emojis = new ArrayList<String>();
                ArrayList<Integer> iconIds = new ArrayList<Integer>();
                Google_EmojiDict google_emojiDict = new Google_EmojiDict();
                Map<String, Integer> emojiDict = google_emojiDict.get_dict();
                for (Prediction prediction : predictions) {
                    // loop over each prediction and populate suggested view
                    for(String emoji : prediction.getEmojis()) {
                        emojis.add(emoji);
                    }
                }

                for(String emoji : emojis){
                    iconIds.add(emojiDict.get(emoji));
                }
                emojiKeyboardView.addPageEmojiPagerAdapter(emojis, iconIds);
            }

            @Override
            public void onFailure(Call<List<Prediction>> call, Throwable t) {
                // do nothing
            }
        });

        previousInputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        iBinder = this.getWindow().getWindow().getAttributes().token;

        emojiKeyboardView = (EmojiKeyboardView) getLayoutInflater()
                .inflate(R.layout.emoji_keyboard_layout, null);

        return emojiKeyboardView.getView();
    }

    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInputView(attribute, restarting);
        inputConnection = getCurrentInputConnection();
    }

    public void sendText(String text) {
        inputConnection.commitText(text, 1);
    }

    public void sendDownKeyEvent(int keyEventCode, int flags) {
        inputConnection.sendKeyEvent(
                new KeyEvent(
                        SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(),
                        KeyEvent.ACTION_DOWN,
                        keyEventCode,
                        0,
                        flags
                )
        );
    }

    public void sendUpKeyEvent(int keyEventCode, int flags) {
        inputConnection.sendKeyEvent(
                new KeyEvent(
                        SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(),
                        KeyEvent.ACTION_UP,
                        keyEventCode,
                        0,
                        flags
                )
        );
    }
    public void sendDownAndUpKeyEvent(int keyEventCode, int flags){
        sendDownKeyEvent(keyEventCode, flags);
        sendUpKeyEvent(keyEventCode, flags);
    }

/*
    public void switchToPreviousInputMethod() {

        Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vib.vibrate(25);

        try {
            previousInputMethodManager.switchToLastInputMethod(iBinder);
        } catch (Throwable t) { // java.lang.NoSuchMethodError if API_level<11
            Context context = getApplicationContext();
            CharSequence text = "Unfortunately input method switching isn't supported in your version of Android! You will have to do it manually :(";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }
*/
}
