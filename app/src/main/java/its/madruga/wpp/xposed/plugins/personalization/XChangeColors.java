package its.madruga.wpp.xposed.plugins;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static its.madruga.wpp.ClassesReference.ChangeColors.customDrawable1;
import static its.madruga.wpp.ClassesReference.ChangeColors.customDrawable2;
import static its.madruga.wpp.ClassesReference.ChangeColors.customDrawable3;
import static its.madruga.wpp.utils.colors.ColorReplacement.replaceColors;
import static its.madruga.wpp.utils.colors.DrawableColors.replaceColor;
import static its.madruga.wpp.utils.colors.IColors.colors;

import android.app.Activity;
import android.app.Notification;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.xmlpull.v1.XmlPullParser;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import its.madruga.wpp.ClassesReference;
import its.madruga.wpp.utils.colors.IColors;
import its.madruga.wpp.xposed.models.XHookBase;

public class XChangeColors extends XHookBase {

    public static ClassLoader classLoader;

    public XChangeColors(ClassLoader loader, XSharedPreferences preferences) {
        super(loader, preferences);
        classLoader = loader;
    }

    @Override
    public void doHook() {
        if (!prefs.getBoolean("changecolor", false)) return;

        var primaryColor = prefs.getString("primary_color", "0");
        var secondaryColor = prefs.getString("secondary_color", "0");
        var backgroundColor = prefs.getString("background_color", "0");

        for (var c : colors.keySet()) {
            if (!primaryColor.equals("0")) {
                switch (c) {
                    case "00a884", "1da457", "21c063", "d9fdd3" ->
                            colors.put(c, primaryColor.substring(3));
                    case "#ff00a884", "#ff1da457", "#ff21c063", "#ff1daa61" ->
                            colors.put(c, primaryColor);
                    case "#ff103529" -> colors.put(c, "#66" + primaryColor.substring(3));
                }
            }

            if (!backgroundColor.equals("0")) {
                switch (c) {
                    case "0b141a" -> colors.put(c, backgroundColor.substring(3));
                    case "#ff0b141a" -> colors.put(c, backgroundColor);
                    case "#ff111b21" -> colors.put(c, backgroundColor);
                }
            }

            if (!secondaryColor.equals("0")) {
                if (c.equals("#ff202c33")) {
                    colors.put(c, secondaryColor);
                }
            }

        }

        findAndHookMethod(Activity.class.getName(), loader, "onCreate", Bundle.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        var activity = (Activity) param.thisObject;
                        var view = activity.findViewById(android.R.id.content).getRootView();
                        replaceColors(view);
                    }
                });

        var intBgHook = new IntBgColorHook();
        findAndHookMethod(Paint.class.getName(), loader, "setColor", int.class, intBgHook);
        findAndHookMethod(View.class.getName(), loader, "setBackgroundColor", int.class, intBgHook);
        findAndHookMethod(GradientDrawable.class.getName(), loader, "setColor", int.class, intBgHook);
        findAndHookMethod(ColorDrawable.class.getName(), loader, "setColor", int.class, intBgHook);
        findAndHookMethod(Notification.Builder.class.getName(), loader, "setColor", int.class, intBgHook);
        findAndHookMethod(Drawable.class.getName(), loader, "setTint", int.class, intBgHook);
        findAndHookMethod("com.whatsapp.CircularProgressBar", loader, "setProgressBarColor", int.class, intBgHook);
        findAndHookMethod("com.whatsapp.CircularProgressBar", loader, "setProgressBarBackgroundColor", int.class, intBgHook);

        var colorStateListHook = new ColorStateListHook();
        findAndHookMethod(Drawable.class.getName(), loader, "setTintList", ColorStateList.class, colorStateListHook);
        findAndHookMethod(customDrawable1, loader, "setBackgroundTintList", ColorStateList.class, colorStateListHook);
        findAndHookMethod(customDrawable1, loader, "setRippleColor", ColorStateList.class, colorStateListHook);
        findAndHookMethod(customDrawable1, loader, "setSupportImageTintList", ColorStateList.class, colorStateListHook);

        findAndHookMethod(customDrawable2, loader, "setTintList", ColorStateList.class, colorStateListHook);
        findAndHookMethod(customDrawable2, loader, "setTint", int.class, intBgHook);

        findAndHookMethod(customDrawable3, loader, "setTintList", ColorStateList.class, colorStateListHook);

        var inflaterHook = (XC_MethodHook) new LayoutInflaterHook();
        findAndHookMethod(LayoutInflater.class.getName(), loader, "inflate", int.class, ViewGroup.class, inflaterHook);
        findAndHookMethod(LayoutInflater.class.getName(), loader, "inflate", XmlPullParser.class, ViewGroup.class, inflaterHook);
        findAndHookMethod(LayoutInflater.class.getName(), loader, "inflate", int.class, ViewGroup.class, boolean.class, inflaterHook);
        findAndHookMethod(LayoutInflater.class.getName(), loader, "inflate", XmlPullParser.class, ViewGroup.class, boolean.class, inflaterHook);

        findAndHookMethod(View.class.getName(), loader, "setBackground", Drawable.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                var drawable = (Drawable) param.args[0];
                replaceColor(drawable);
                super.beforeHookedMethod(param);
            }
        });

    }

    public static class LayoutInflaterHook extends XC_MethodHook {
        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            super.afterHookedMethod(param);
            var view = (View) param.getResult();
            if (view == null) return;
            replaceColors(view);
        }
    }

    public static class ColorStateListHook extends XC_MethodHook {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            var colorStateList = param.args[0];
            if (colorStateList != null) {
                var mColors = (int[]) XposedHelpers.getObjectField(colorStateList, "mColors");

//            XposedBridge.log("mColors: " + Arrays.toString(mColors));
                for (int i = 0; i < mColors.length; i++) {
                    var sColor = IColors.toString(mColors[i]);
                    var newColor = colors.get(sColor);
                    if (newColor != null && newColor.length() == 9) {
                        mColors[i] = IColors.parseColor(newColor);
                    } else {
                        if (!sColor.equals("#0") && !sColor.startsWith("#ff")) {
                            var sColorSub = sColor.substring(0, 3);
                            newColor = colors.get(sColor.substring(3));
                            if (newColor != null) {
                                mColors[i] = IColors.parseColor(sColorSub + newColor);
                            }
                        }
                    }
                }
                XposedHelpers.setObjectField(colorStateList, "mColors", mColors);
                param.args[0] = colorStateList;
            }

            super.beforeHookedMethod(param);
        }
    }

    public static class IntBgColorHook extends XC_MethodHook {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            var color = (int) param.args[0];
            var sColor = IColors.toString(color);
            var newColor = colors.get(sColor);
//            XposedBridge.log("-> New Color: " + newColor);
            if (newColor != null && newColor.length() == 9) {
                param.args[0] = IColors.parseColor(newColor);
            } else {
                if (!sColor.equals("#0") && !sColor.startsWith("#ff")) {
                    var sColorSub = sColor.substring(0, 3);
                    newColor = colors.get(sColor.substring(3));
                    if (newColor != null) {
                        param.args[0] = IColors.parseColor(sColorSub + newColor);
                    }
                }
            }
            super.beforeHookedMethod(param);
        }
    }
}
