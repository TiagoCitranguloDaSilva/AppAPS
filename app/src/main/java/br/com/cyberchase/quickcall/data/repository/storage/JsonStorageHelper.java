package br.com.cyberchase.quickcall.data.repository.storage;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

public class JsonStorageHelper {

    private static final String PREFS_NAME = "quickcall_local_storage";

    private final SharedPreferences sharedPreferences;

    public JsonStorageHelper(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public JSONArray readArray(String key) {
        String json = sharedPreferences.getString(key, "[]");
        try {
            return new JSONArray(json);
        } catch (JSONException e) {
            return new JSONArray();
        }
    }

    public void writeArray(String key, JSONArray array) {
        sharedPreferences.edit().putString(key, array.toString()).apply();
    }

    public long nextId(String counterKey) {
        long nextValue = sharedPreferences.getLong(counterKey, 1L);
        sharedPreferences.edit().putLong(counterKey, nextValue + 1L).apply();
        return nextValue;
    }
}
