package its.madruga.wpp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.materialswitch.MaterialSwitch;

import its.madruga.wpp.R;
import its.madruga.wpp.activities.personalization.PersonalizationGeneralActivity;
import its.madruga.wpp.listeners.SwitchListener;
import its.madruga.wpp.views.SwitchButton;
import its.madruga.wpp.views.SwitchButtonTop;
import its.madruga.wpp.views.TextViewButton;

public class BaseActivity extends AppCompatActivity {
    public SharedPreferences mShared;
    public SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(android.R.id.content);
    }

    public void configureListeners(View view) {

        mShared = getSharedPreferences(getPackageName() + "_preferences", MODE_WORLD_READABLE);
        mEditor = mShared.edit();

        if (view instanceof SwitchButton || view instanceof SwitchButtonTop) {
            var switchButton = (MaterialSwitch) view.findViewById(R.id.switch_button);
            var key = (String) switchButton.getTag();
            switchButton.setChecked(mShared.getBoolean(key, false));
            var listener = new SwitchListener(mShared, mEditor);
            switchButton.setOnCheckedChangeListener(listener);
        } else if (view instanceof TextViewButton) {
            view.setOnClickListener(v -> {
                try {
                    startActivity(new Intent(getApplicationContext(), Class.forName((String) view.getTag())));
                } catch (ClassNotFoundException ignored) { }
            });
        } else if (view instanceof ViewGroup) {
            var childCount = ((ViewGroup) view).getChildCount();
            for (var i = 0; i < childCount; i++) {
                var thisView = ((ViewGroup) view).getChildAt(i);
                configureListeners(thisView);
            }
        }
    }


}
