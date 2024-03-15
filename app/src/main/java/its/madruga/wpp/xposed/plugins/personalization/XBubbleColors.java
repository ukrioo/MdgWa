package its.madruga.wpp.xposed.plugins.personalization;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static its.madruga.wpp.ClassesReference.BubbleColors.balloonIncomingNormal;
import static its.madruga.wpp.ClassesReference.BubbleColors.balloonIncomingNormalExt;
import static its.madruga.wpp.ClassesReference.BubbleColors.balloonOutgoingNormal;
import static its.madruga.wpp.ClassesReference.BubbleColors.balloonOutgoingNormalExt;
import static its.madruga.wpp.utils.colors.IColors.parseColor;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import its.madruga.wpp.ClassesReference;
import its.madruga.wpp.xposed.models.XHookBase;

public class XBubbleColors extends XHookBase {
    public XBubbleColors(ClassLoader loader, XSharedPreferences preferences) {
        super(loader, preferences);
    }

    @Override
    public void doHook() {
        var bubbleLeftColor = prefs.getString("bubble_left", "0");
        var bubbleRightColor = prefs.getString("bubble_right", "0");
        var cls = ClassesReference.BubbleColors.bubblesClass;

        if (!bubbleRightColor.equals("0")) {
            findAndHookMethod(cls, loader, balloonOutgoingNormal, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    var balloon = (Drawable) param.getResult();
                    balloon.setColorFilter(
                            new PorterDuffColorFilter(parseColor(bubbleRightColor), PorterDuff.Mode.SRC_IN)
                    );
                    super.afterHookedMethod(param);
                }
            });

            findAndHookMethod(cls, loader, balloonOutgoingNormalExt, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    var balloon = (Drawable) param.getResult();
                    balloon.setColorFilter(
                            new PorterDuffColorFilter(parseColor(bubbleRightColor), PorterDuff.Mode.SRC_IN)
                    );
                    super.afterHookedMethod(param);
                }
            });
        }

        if (!bubbleLeftColor.equals("0")) {
            findAndHookMethod(cls, loader, balloonIncomingNormal, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    var balloon = (Drawable) param.getResult();
                    balloon.setColorFilter(
                            new PorterDuffColorFilter(parseColor(bubbleLeftColor), PorterDuff.Mode.SRC_IN)
                    );
                    super.afterHookedMethod(param);
                }
            });

            findAndHookMethod(cls, loader, balloonIncomingNormalExt, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    var balloon = (Drawable) param.getResult();
                    balloon.setColorFilter(
                            new PorterDuffColorFilter(parseColor(bubbleLeftColor), PorterDuff.Mode.SRC_IN)
                    );
                    super.afterHookedMethod(param);
                }
            });
        }
    }
}
