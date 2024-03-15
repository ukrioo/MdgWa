package its.madruga.wpp.xposed.plugins.core;

import static its.madruga.wpp.ClassesReference.Databases.axolotlClass;
import static its.madruga.wpp.ClassesReference.Databases.msgstoreClass;

import de.robv.android.xposed.XSharedPreferences;
import its.madruga.wpp.core.databases.Axolotl;
import its.madruga.wpp.core.databases.MessageStore;

public class XDatabases {

    public static MessageStore msgstore;
    public static Axolotl axolotl;

    public static void Initialize(ClassLoader loader, XSharedPreferences pref) {
        msgstore = new MessageStore(msgstoreClass, loader);
        axolotl = new Axolotl(axolotlClass, loader);
    }
}
