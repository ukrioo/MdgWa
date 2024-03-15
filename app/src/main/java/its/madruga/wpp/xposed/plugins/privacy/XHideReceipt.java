package its.madruga.wpp.xposed.plugins.privacy;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import its.madruga.wpp.ClassesReference;
import its.madruga.wpp.xposed.models.XHookBase;

public class XHideReceipt extends XHookBase {
    public XHideReceipt(ClassLoader loader, XSharedPreferences preferences) {
        super(loader, preferences);
    }

    @Override
    public void doHook() {
        if (!prefs.getBoolean("hidereceipt", false)) return;
        var mainClass = XposedHelpers.findClass(ClassesReference.HideReceipt.mainClass, loader);
        var param1Class = XposedHelpers.findClass(ClassesReference.HideReceipt.param1Class, loader);
        var param2Class = XposedHelpers.findClass("com.whatsapp.jid.DeviceJid", loader);
        var param3Class = XposedHelpers.findClass("com.whatsapp.jid.UserJid", loader);
        var param4Class = XposedHelpers.findClass(ClassesReference.HideReceipt.param4Class, loader);
        XposedHelpers.findAndHookMethod(mainClass, "A01", param1Class, param2Class, param3Class, param4Class, String.class, String[].class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                var jid = param.args[0] == null ? null : (String) XposedHelpers.callMethod(param.args[0], "getRawString");
                XposedBridge.log("[-] Jid: " + jid);
                XposedBridge.log("[-] Param: " + param.args[4]);
                XposedBridge.log("______________________");
                if (jid == null || jid.contains("@lid")) {
                    param.args[4] = "inactive";
                }
                super.beforeHookedMethod(param);
            }
        });
    }
}
