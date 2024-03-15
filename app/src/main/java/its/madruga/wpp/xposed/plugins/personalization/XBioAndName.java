package its.madruga.wpp.xposed.plugins.personalization;

import static its.madruga.wpp.ClassesReference.ShowBioAndName.setTitleMethod;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import its.madruga.wpp.ClassesReference;
import its.madruga.wpp.xposed.models.XHookBase;

public class XBioAndName extends XHookBase {
    public XBioAndName(ClassLoader loader, XSharedPreferences preferences) {
        super(loader, preferences);
    }

    public static String getBio(Activity activity) {
        return activity.getSharedPreferences(activity.getPackageName() + "_preferences_light", Context.MODE_PRIVATE).getString("my_current_status", ".");
    }

    public static String getName(Activity activity) {
        return activity.getSharedPreferences("startup_prefs", Context.MODE_PRIVATE).getString("push_name", "MdgWa");
    }

    @Override
    public void doHook() {
        var showName = prefs.getBoolean("shownamehome", false);
        var showBio = prefs.getBoolean("showbiohome", false);
        var methodHook = new MethodHook(showName, showBio);
        XposedHelpers.findAndHookMethod("com.whatsapp.HomeActivity", loader, "onCreate", Bundle.class, methodHook);
    }

    public static class MethodHook extends XC_MethodHook {
        private final boolean showName;
        private final boolean showBio;

        public MethodHook(boolean showName, boolean showBio) {
            this.showName = showName;
            this.showBio = showBio;
        }

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {

            var actionBar = XposedHelpers.callMethod(param.thisObject, "getSupportActionBar");
            var homeActivity = (Activity) param.thisObject;
            var bio = getBio(homeActivity);
            var name = getName(homeActivity);

            XposedHelpers.findAndHookMethod(actionBar.getClass().getName(), actionBar.getClass().getClassLoader(), setTitleMethod, CharSequence.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (showName) param.args[0] = name;
                    super.beforeHookedMethod(param);
                }
            });
            if (showName) {
                XposedHelpers.callMethod(actionBar, setTitleMethod, name);
            }
            if (showBio) {
                XposedHelpers.callMethod(actionBar, ClassesReference.ShowBioAndName.setSummaryMethod, bio);
            }

            super.afterHookedMethod(param);
        }
    }
}
