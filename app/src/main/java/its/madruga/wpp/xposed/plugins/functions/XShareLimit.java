package its.madruga.wpp.xposed.plugins.functions;

import static its.madruga.wpp.ClassesReference.LimitShare.booleanField;
import static its.madruga.wpp.ClassesReference.LimitShare.methodShareLimit;
import static its.madruga.wpp.ClassesReference.LimitShare.param1;

import android.view.View;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import its.madruga.wpp.xposed.models.XHookBase;

public class XShareLimit extends XHookBase {
    public XShareLimit(ClassLoader loader, XSharedPreferences preferences) {
        super(loader, preferences);
    }

    public void doHook() {
        var mainClass = XposedHelpers.findClass("com.whatsapp.contact.picker.ContactPickerFragment", loader);
        var removeForwardLimit = prefs.getBoolean("removeforwardlimit", false);
        XposedHelpers.findAndHookMethod(
                mainClass.getName(), loader,
                methodShareLimit,
                View.class,
                XposedHelpers.findClass(param1, loader),
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        if (removeForwardLimit) {
                            XposedHelpers.setBooleanField(param.thisObject, booleanField, true);
                        }
                        super.beforeHookedMethod(param);
                    }
                });
    }
}
