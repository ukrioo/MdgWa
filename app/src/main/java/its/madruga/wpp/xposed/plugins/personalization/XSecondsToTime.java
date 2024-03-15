package its.madruga.wpp.xposed.plugins.personalization;

import static its.madruga.wpp.ClassesReference.TimeModification.classFormat;
import static its.madruga.wpp.ClassesReference.TimeModification.methodFormat;
import static its.madruga.wpp.ClassesReference.TimeModification.paramFormat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import its.madruga.wpp.ClassesReference;
import its.madruga.wpp.xposed.models.XHookBase;

public class XSecondsToTime extends XHookBase {

    public XSecondsToTime(ClassLoader loader, XSharedPreferences preferences) {
        super(loader, preferences);
    }

    @Override
    public void doHook() {
        var myClass = XposedHelpers.findClass(classFormat, loader);
        var classParam1 = XposedHelpers.findClass(paramFormat, loader);
        var secondsToTime = prefs.getBoolean("segundos", false);
        var ampm = prefs.getBoolean("ampm", false);

        XposedHelpers.findAndHookMethod(myClass, methodFormat, classParam1, long.class, new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                var timestamp = (long) param.args[1];
                var date = new Date(timestamp);
                var patternDefault = "HH:mm";
                var patternSeconds = "HH:mm:ss";
                if (ampm) {
                    patternDefault = "hh:mm a";
                    patternSeconds = "hh:mm:ss a";
                }
                var pattern = secondsToTime ? patternSeconds : patternDefault;
                var formattedDate = new SimpleDateFormat(pattern, Locale.US).format(date);

                param.setResult(getTextInHour(formattedDate));
            }
        });
    }

    private String getTextInHour(String date) {
        var summary = prefs.getString("secondstotime", "");
        if (summary == null) return date;
        else return date + " " + summary;
    }
}
