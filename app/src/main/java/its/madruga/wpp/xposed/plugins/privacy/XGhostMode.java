package its.madruga.wpp.xposed.plugins.privacy;

import static its.madruga.wpp.ClassesReference.GhostMode.param1;
import static its.madruga.wpp.ClassesReference.GhostMode.param2;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import its.madruga.wpp.ClassesReference;
import its.madruga.wpp.xposed.models.XHookBase;

public class XGhostMode extends XHookBase {

    public XGhostMode(ClassLoader loader, XSharedPreferences preferences) {
        super(loader, preferences);
    }

    @Override
    public void doHook() {
        Class<?> class1 = XposedHelpers.findClass(param1, loader);
        Class<?> class2 = XposedHelpers.findClass(param2, loader);
        boolean ghostmode = prefs != null ? prefs.getBoolean("ghostmode", false) : false;
        if (!ghostmode) return;
        XposedHelpers.findAndHookMethod(class1, ClassesReference.GhostMode.methodName, class1, class2, int.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) {
                return null;
            }
        });
    }
}
