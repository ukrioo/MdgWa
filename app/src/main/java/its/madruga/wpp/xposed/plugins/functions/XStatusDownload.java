package its.madruga.wpp.xposed.plugins.functions;

import static its.madruga.wpp.ClassesReference.StatusDownload.classMedia;
import static its.madruga.wpp.ClassesReference.StatusDownload.classMenuStatus;
import static its.madruga.wpp.ClassesReference.StatusDownload.fieldFile;
import static its.madruga.wpp.ClassesReference.StatusDownload.fieldList;
import static its.madruga.wpp.ClassesReference.StatusDownload.setPageActiveMethod;
import static its.madruga.wpp.xposed.plugins.core.XMain.mApp;

import android.media.MediaScannerConnection;
import android.os.Environment;
import android.os.FileUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import its.madruga.wpp.ClassesReference;
import its.madruga.wpp.xposed.models.XHookBase;

public class XStatusDownload extends XHookBase {
    public XStatusDownload(ClassLoader loader, XSharedPreferences preferences) {
        super(loader, preferences);
    }

    public void doHook() {
        if (!prefs.getBoolean("downloadstatus", false)) return;
        var mediaClass = XposedHelpers.findClass(classMedia, loader);
        var statusPlaybackFragment = "com.whatsapp.status.playback.fragment.StatusPlaybackContactFragment";
        XposedHelpers.findAndHookMethod(statusPlaybackFragment, loader, setPageActiveMethod, statusPlaybackFragment, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                var position = (int) param.args[1];
                var list = (List<?>) XposedHelpers.getObjectField(param.args[0], fieldList);
                var message = list.get(position);
                if (message != null && mediaClass.isInstance(message)) {

                    var menuStatusClass = XposedHelpers.findClass(classMenuStatus, loader);
                    XposedHelpers.findAndHookMethod(menuStatusClass, "onClick", View.class, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            var fileData = XposedHelpers.getObjectField(message, "A01");
                            var file = (File) XposedHelpers.getObjectField(fileData, fieldFile);
                            var thisObject = param.thisObject;
                            var aClass = XposedHelpers.findClass(ClassesReference.StatusDownload.aClass, loader);
                            var mClass = XposedHelpers.getObjectField(aClass.cast(XposedHelpers.getObjectField(thisObject, "A05")), "A03");
                            var menu = (MenuItem) XposedHelpers.callMethod(mClass, "findItem", ClassesReference.StatusDownload.findItem);
                            if (menu != null) return;

                            menu = (MenuItem) XposedHelpers.callMethod(mClass, "add", 0, ClassesReference.StatusDownload.addItem, 0, mApp.getString(ClassesReference.StatusDownload.downloadStringId));
                            menu.setOnMenuItemClickListener(item -> {
                                if (copyFile(file)) {
                                    Toast.makeText(mApp, "Saved", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(mApp, "Error when saving, try again", Toast.LENGTH_SHORT).show();
                                }
                                return true;
                            });

                            super.afterHookedMethod(param);
                        }
                    });

                }
            }
        });
    }

    private static boolean copyFile(File p) {
        if (p == null) return false;

        var destination = getPathDestination(p);

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

    private static String getPathDestination(File f) {
        var filePath = f.getAbsolutePath();
        var isVideo = false;
        var isImage = false;
        var isAudio = false;

        String[] videoFormats = {
                "3gp", "mp4", "mkv", "avi", "wmv", "flv", "mov", "webm", "ts", "m4v", "divx", "xvid", "mpg", "mpeg", "mpg2", "ogv", "vob", "f4v", "asf"
        };

        String[] imageFormats = {
                "jpeg", "jpg", "png", "gif", "bmp", "webp", "heif", "tiff", "raw", "svg", "eps", "ai"
        };

        String[] audioFormats = {
                "mp3", "wav", "ogg", "m4a", "aac", "flac", "amr", "wma", "opus", "mid", "xmf", "rtttl", "rtx", "ota", "imy", "mpga", "ac3", "ec3", "eac3"
        };

        for (String format : videoFormats) {
            if (filePath.toLowerCase().endsWith("." + format)) {
                isVideo = true;
                break;
            }
        }

        for (String format : imageFormats) {
            if (filePath.toLowerCase().endsWith("." + format)) {
                isImage = true;
                break;
            }
        }

        for (String format : audioFormats) {
            if (filePath.toLowerCase().endsWith("." + format)) {
                isAudio = true;
                break;
            }
        }

        if (isVideo) {
            var folderPath = Environment.getExternalStorageDirectory() + "/Movies/WhatsApp/MdgWa Status/Status Videos/";
            var videoPath = new File(folderPath);
            if (!videoPath.exists()) videoPath.mkdirs();
            return videoPath.getAbsolutePath() + "/" + f.getName();
        } else if (isImage) {
            var folderPath = Environment.getExternalStorageDirectory() + "/Pictures/WhatsApp/MdgWa Status/Status Images/";
            var imagePath = new File(folderPath);
            if (!imagePath.exists()) imagePath.mkdirs();
            return imagePath.getAbsolutePath() + "/" + f.getName();
        } else if (isAudio) {
            var folderPath = Environment.getExternalStorageDirectory() + "/Music/WhatsApp/MdgWa Status/Status Sounds/";
            var audioPath = new File(folderPath);
            if (!audioPath.exists()) audioPath.mkdirs();
            return audioPath.getAbsolutePath() + "/" + f.getName();
        }
        return null;
    }

    public static String getMimeTypeFromExtension(String extension) {
        switch (extension) {
            case "3gp":
            case "mp4":
            case "mkv":
            case "avi":
            case "wmv":
            case "flv":
            case "mov":
            case "webm":
            case "ts":
            case "m4v":
            case "divx":
            case "xvid":
            case "mpg":
            case "mpeg":
            case "mpg2":
            case "ogv":
            case "vob":
            case "f4v":
            case "asf":
                return "video/*";
            case "jpeg":
            case "jpg":
            case "png":
            case "gif":
            case "bmp":
            case "webp":
            case "heif":
            case "tiff":
            case "raw":
            case "svg":
            case "eps":
            case "ai":
                return "image/*";
            case "mp3":
            case "wav":
            case "ogg":
            case "m4a":
            case "aac":
            case "flac":
            case "amr":
            case "wma":
            case "opus":
            case "mid":
            case "xmf":
            case "rtttl":
            case "rtx":
            case "ota":
            case "imy":
            case "mpga":
            case "ac3":
            case "ec3":
            case "eac3":
                return "audio/*";
            default:
                return "*/*";
        }
    }
}