package its.madruga.wpp.listeners;

import static its.madruga.wpp.MainActivity.isRootGranted;
import static its.madruga.wpp.MainActivity.shell;

import android.content.SharedPreferences;
import android.widget.CompoundButton;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.DataOutputStream;
import java.io.IOException;

import its.madruga.wpp.R;

public class SwitchListener implements CompoundButton.OnCheckedChangeListener {
    private final SharedPreferences.Editor mEditor;
    private final SharedPreferences mShared;

    public SwitchListener(SharedPreferences mShared, SharedPreferences.Editor mEditor) {
        this.mEditor = mEditor;
        this.mShared = mShared;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getTag().equals("autoreboot")) {
            if (!isRootGranted) {
                new MaterialAlertDialogBuilder(buttonView.getContext())
                        .setTitle(R.string.root_needed)
                        .setMessage(R.string.root_needed_message)
                        .setCancelable(true)
                        .setNegativeButton("Ok", (dialogInterface, i) -> dialogInterface.dismiss())
                        .create()
                        .show();
                buttonView.setChecked(false);
                isChecked = false;
            }
        }

        mEditor.putBoolean((String) buttonView.getTag(), isChecked).apply();

        if (mShared.getBoolean("autoreboot", false) && isRootGranted) {
            try {
                var context = buttonView.getContext();
                var command = context.getString(R.string.get_pid);
                var stdin = new DataOutputStream(shell.getOutputStream());
                stdin.writeBytes(command + "\n");
                stdin.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}