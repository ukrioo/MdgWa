package its.madruga.wpp.xposed.plugins.privacy;

import static its.madruga.wpp.ClassesReference.GhostMode.methodName;
import static its.madruga.wpp.ClassesReference.GhostMode.param1;
import static its.madruga.wpp.ClassesReference.GhostMode.param2;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import its.madruga.wpp.xposed.models.XHookBase;

public class XGhostMode extends XHookBase {

    public XGhostMode(ClassLoader loader, XSharedPreferences preferences) {
        super(loader, preferences);
    }

    @Override
    public void doHook() {
        Class<?> class1 = XposedHelpers.findClass(param1, loader);
        Class<?> class2 = XposedHelpers.findClass(param2, loader);

        var ghostmode_t = prefs.getBoolean("ghostmode_t", false);
        var ghostmode_r = prefs.getBoolean("ghostmode_r", false);


        XposedHelpers.findAndHookMethod(class1, methodName, class1, class2, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                var p1 = (int) param.args[2];
                if (p1 == 1 && ghostmode_r) {
                    param.setResult(null);
                    return;
                }
                if (p1 == 0 && ghostmode_t) {
                    param.setResult(null);
                    return;
                }
                super.beforeHookedMethod(param);
            }
        });
    }
}
