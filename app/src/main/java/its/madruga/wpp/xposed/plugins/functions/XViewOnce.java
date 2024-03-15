package its.madruga.wpp.xposed.plugins.functions;

import static its.madruga.wpp.ClassesReference.StatusDownload.downloadStringId;
import static its.madruga.wpp.ClassesReference.StatusDownload.fieldFile;
import static its.madruga.wpp.ClassesReference.ViewOnce.downloadDrawable;
import static its.madruga.wpp.ClassesReference.ViewOnce.initIntField;
import static its.madruga.wpp.ClassesReference.ViewOnce.menuIntField;
import static its.madruga.wpp.ClassesReference.ViewOnce.menuMethod;
import static its.madruga.wpp.ClassesReference.ViewOnce.slaMethod;
import static its.madruga.wpp.xposed.plugins.functions.XStatusDownload.getMimeTypeFromExtension;
import static its.madruga.wpp.xposed.plugins.core.XMain.mApp;

import android.media.MediaScannerConnection;
import android.os.Environment;
import android.os.FileUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import its.madruga.wpp.ClassesReference;
import its.madruga.wpp.xposed.models.XHookBase;

public class XViewOnce extends XHookBase {
    public XViewOnce(ClassLoader loader, XSharedPreferences preferences) {
        super(loader, preferences);
    }

    @Override
    public void doHook() {
        if (prefs.getBoolean("viewonce", false)) {

//            XposedHelpers.findAndHookMethod(ClassesReference.ViewOnce.sendReadClass, loader, "run", new XC_MethodHook() {
//                @Override
//                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                    var type = XposedHelpers.getIntField(param.thisObject, "A02");
//                    XposedBridge.log(type + " Int Run ViewOnce");
//                    if (type == 44) {
//                        param.setResult(null);
//                    }
//                    super.beforeHookedMethod(param);
//                }
//            });

            for (String i : ClassesReference.ViewOnce.vClasses) {
                XposedHelpers.findAndHookMethod(i, loader, ClassesReference.ViewOnce.methodName, int.class, new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        return null;
                    }
                });
            }
        }
        if (prefs.getBoolean("downloadviewonce", false)) {
            XposedHelpers.findAndHookMethod("com.whatsapp.mediaview.MediaViewFragment", loader, menuMethod, Menu.class, MenuInflater.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);

                    if (XposedHelpers.getIntField(param.thisObject, menuIntField) == 3) {
                        Menu menu = (Menu) param.args[0];
                        MenuItem item = menu.add(0, 0, 0, mApp.getString(downloadStringId)).setIcon(downloadDrawable);
                        item.setShowAsAction(2);
                        item.setOnMenuItemClickListener(item1 -> {
                            var i = XposedHelpers.getIntField(param.thisObject, initIntField);
                            var message = XposedHelpers.callMethod(param.thisObject, slaMethod, param.thisObject,i);
                            if (message != null) {
                                var fileData = XposedHelpers.getObjectField(message, "A01");
                                var file = (File) XposedHelpers.getObjectField(fileData, fieldFile);
                                if (copyFile(file)) {
                                    Toast.makeText(mApp, "Saved", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(mApp, "Error when saving, try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                            return true;
                        });
                    }

                }
            });
        }

    }

    private static boolean copyFile(File p) {
        if (p == null) return false;

        var folderPath = Environment.getExternalStorageDirectory() + "/Pictures/WhatsApp/MdgWa ViewOnce/";
        var filePath = new File(folderPath);
        if (!filePath.exists()) filePath.mkdirs();
        var destination = filePath.getAbsolutePath() + "/" + p.getName();

        try (FileInputStream in = new FileInputStream(p);
             FileOutputStream out = new FileOutputStream(destination)) {
            byte[] bArr = new byte[1024];
            while (true) {
                int read = in.read(bArr);
                if (read <= 0) {
                    in.close();
                    out.close();

                    String[] parts = destination.split("\\.");
                    String ext = parts[parts.length - 1].toLowerCase();

                    MediaScannerConnection.scanFile(mApp,
                            new String[]{destination},
                            new String[]{getMimeTypeFromExtension(ext)},
                            (path, uri) -> {});

                    return true;
                }
                out.write(bArr, 0, read);
            }
        } catch (IOException e) {
            XposedBridge.log(e.getMessage());
            return false;
        }
    }
}