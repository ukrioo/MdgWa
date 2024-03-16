package its.madruga.wpp.xposed.models;

import androidx.annotation.NonNull;

import de.robv.android.xposed.XSharedPreferences;

public class XHookBase {

    public final ClassLoader loader;
    public final XSharedPreferences prefs;

    public XHookBase(@NonNull ClassLoader loader, @NonNull XSharedPreferences preferences) {
        this.loader = loader;
        this.prefs = preferences;
    }

    public void doHook() {

    }
}
