package its.madruga.wpp.xposed.models;

import de.robv.android.xposed.XSharedPreferences;

public class XHookBase {

    public final ClassLoader loader;
    public final XSharedPreferences prefs;

    public XHookBase(ClassLoader loader, XSharedPreferences preferences) {
        this.loader = loader;
        this.prefs = preferences;
    }

    public void doHook() {

    }
}
