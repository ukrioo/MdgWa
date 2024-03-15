package its.madruga.wpp.listeners;

import android.app.Activity;
import android.content.Intent;
import android.os.Process;
import android.util.AndroidRuntimeException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class RestartListener {

    public static int counter = 0;

    public static void start(Class<?> aClass) {

        XposedHelpers.findAndHookMethod(aClass, "onActivityStarted", Activity.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                var activity = (Activity) param.args[0];
                if (++counter == 1 && !activity.isActivityTransitionRunning()) {
                    var process = Runtime.getRuntime().exec("getprop mdgwa.pids");
                    var stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    var pids = stdInput.readLine().split(",");
                    var myPid = (int) XposedHelpers.callStaticMethod(Process.class, "myPid");
                    if (Arrays.asList(pids).contains(String.valueOf(myPid))) {
                        var app = activity.getApplication();
                        if (app != null) {
                            var intent = activity.getIntent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            activity.finishAffinity();
                            try {
                                app.startActivity(intent);
                            } catch (AndroidRuntimeException e) {
                                e.printStackTrace();
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                app.startActivity(intent);
                            }
                        }
                        Runtime runtime = (Runtime) XposedHelpers.callStaticMethod(Runtime.class, "getRuntime");
                        runtime.exit(0);
                    }
                }
            }
        });

        XposedHelpers.findAndHookMethod(aClass, "onActivityStopped", Activity.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                var activity = (Activity) param.args[0];
                if (--counter == 0 && !activity.isActivityTransitionRunning()) {
                    //TODO
                }
            }
        });

    }
}
