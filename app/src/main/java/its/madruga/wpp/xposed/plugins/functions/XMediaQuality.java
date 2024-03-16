package its.madruga.wpp.xposed.plugins.functions;

import static de.robv.android.xposed.XposedHelpers.findClass;
import static its.madruga.wpp.ClassesReference.MediaQuality.imainClass;
import static its.madruga.wpp.ClassesReference.MediaQuality.imethod;
import static its.madruga.wpp.ClassesReference.MediaQuality.iparam1;
import static its.madruga.wpp.ClassesReference.MediaQuality.vClassQuality;
import static its.madruga.wpp.ClassesReference.MediaQuality.vMethodResolution;
import static its.madruga.wpp.ClassesReference.MediaQuality.vParam1;
import static its.madruga.wpp.ClassesReference.MediaQuality.vParam2;
import static its.madruga.wpp.ClassesReference.MediaQuality.vmethod;
import static its.madruga.wpp.ClassesReference.MediaQuality.vmethod2;

import android.util.Pair;

import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import its.madruga.wpp.xposed.models.XHookBase;

public class XMediaQuality extends XHookBase {
    public XMediaQuality(ClassLoader loader, XSharedPreferences preferences) {
        super(loader, preferences);
    }

    @Override
    public void doHook() {
        var videoQuality = prefs.getBoolean("videoquality", false);
        var imageQuality = prefs.getBoolean("imagequality", false);

        if (videoQuality) {
            XposedHelpers.findAndHookMethod(vClassQuality, loader, vMethodResolution, int.class, int.class, int.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    var pair = new Pair<>(param.args[0], param.args[1]);
                    param.setResult(pair);
                }
            });

            XposedHelpers.findAndHookMethod(vClassQuality, loader, vmethod2, int.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult(1600000);
                }
            });

            XposedHelpers.findAndHookMethod(vClassQuality, loader, vmethod, findClass(vParam1, loader), findClass(vParam2, loader), int.class, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    return new Pair<>(true, new ArrayList<>());
                }
            });
        }

        if (imageQuality) {
            var iqClass = findClass(imainClass, loader);
            XposedHelpers.findAndHookMethod(iqClass, imethod, findClass(iparam1, loader), iqClass, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    int p1 = (int) param.args[2];
                    if (checkMedia(p1, 1)) {
                        param.setResult(1000);
                    }
                    if (checkMedia(p1, 2)) {
                        param.setResult(10000);
                    }
                    if (checkMedia(p1, 4)) {
                        param.setResult(100000);
                    }
                    super.beforeHookedMethod(param);
                }
            });
        }
    }

    private boolean checkMedia(int i, int i2) {
        int[] validValues = {1578, 1575, 1581, 1576, 1574, 1580, 596, 4155, 3659, 3660, 3658, 3306, 3656, 3185, 595, 3655, 3755, 3756, 3757, 3758, 3657};
        int index = (i2 - 1) * 3;
        return index >= 0 && index < validValues.length && validValues[index] == i;
    }


}