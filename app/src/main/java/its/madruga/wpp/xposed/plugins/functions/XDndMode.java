package its.madruga.wpp.xposed.plugins.functions;

import static its.madruga.wpp.ClassesReference.DndMode.mainClass;
import static its.madruga.wpp.ClassesReference.DndMode.mainMethod;
import static its.madruga.wpp.xposed.plugins.core.XMain.mApp;


import android.content.Context;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import its.madruga.wpp.xposed.models.XHookBase;

public class XDndMode extends XHookBase {
    public XDndMode(ClassLoader loader, XSharedPreferences preferences) {
        super(loader, preferences);
    }

    @Override
    public void doHook() {
        super.doHook();
        var shared = mApp.getSharedPreferences(mApp.getPackageName() + "_mdgwa_preferences", Context.MODE_PRIVATE);
        if (!shared.getBoolean("dndmode", false)) return;
        XposedHelpers.findAndHookMethod(mainClass, loader, mainMethod, XposedHelpers.findClass(mainClass, loader), new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                return null;
            }
        });
    }
}
