package its.madruga.wpp.xposed.plugins.privacy;

import static its.madruga.wpp.ClassesReference.FreezeLastSeen.classSendAvailable;

import static its.madruga.wpp.ClassesReference.FreezeLastSeen.classSendAvailable;
import static its.madruga.wpp.ClassesReference.FreezeLastSeen.methodSendAvailable;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import its.madruga.wpp.ClassesReference;
import its.madruga.wpp.xposed.models.XHookBase;

public class XFreezeLastSeen extends XHookBase {
    public XFreezeLastSeen(ClassLoader loader, XSharedPreferences preferences) {
        super(loader, preferences);
    }

    @Override
    public void doHook() {
        var mainClass = XposedHelpers.findClass(classSendAvailable, loader);
        var freeze = prefs != null && prefs.getBoolean("freezelastseen", false);
        XposedHelpers.findAndHookMethod(
                mainClass.getName(),
                loader,
                ClassesReference.FreezeLastSeen.methodSendAvailable,
                ClassesReference.FreezeLastSeen.param1, boolean.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        if (freeze) param.setResult(null);
                        else super.beforeHookedMethod(param);
                    }
                });
    }


}
