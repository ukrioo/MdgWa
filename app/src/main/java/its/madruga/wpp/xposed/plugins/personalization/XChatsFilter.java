package its.madruga.wpp.xposed.plugins.personalization;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getIntField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setObjectField;
import static its.madruga.wpp.ClassesReference.ChatsFilter.classGetTab;
import static its.madruga.wpp.ClassesReference.ChatsFilter.classTabName;
import static its.madruga.wpp.ClassesReference.ChatsFilter.classTabsList;
import static its.madruga.wpp.ClassesReference.ChatsFilter.fieldTabsList;
import static its.madruga.wpp.ClassesReference.ChatsFilter.methodGetTab;
import static its.madruga.wpp.ClassesReference.ChatsFilter.methodTabIcon;
import static its.madruga.wpp.ClassesReference.ChatsFilter.methodTabInstance;
import static its.madruga.wpp.ClassesReference.ChatsFilter.methodTabName;
import static its.madruga.wpp.ClassesReference.ChatsFilter.nameId;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import its.madruga.wpp.ClassesReference;
import its.madruga.wpp.xposed.models.XHookBase;

public class XChatsFilter extends XHookBase {

    public final int CHATS = 200;
    public final int STATUS = 300;
    public final int CALLS = 400;
    public final int COMMUNITY = 600;
    public final int GROUPS = 800;
    public final ArrayList<Integer> tabs = new ArrayList<>();
    public int tabCount = 0;

    public XChatsFilter(ClassLoader loader, XSharedPreferences preferences) {
        super(loader, preferences);
        var newHome = prefs.getBoolean("novahome", false);
        if (newHome) {
            tabs.add(CHATS);
            tabs.add(GROUPS);
            tabs.add(STATUS);
            tabs.add(COMMUNITY);
            tabs.add(CALLS);
        } else {
            tabs.add(COMMUNITY);
            tabs.add(CHATS);
            tabs.add(GROUPS);
            tabs.add(STATUS);
            tabs.add(CALLS);
        }
    }

    public void doHook() {

        var separateGroups = prefs.getBoolean("separategroups", false);
        if (!separateGroups) return;

        var cFrag = XposedHelpers.findClass("com.whatsapp.conversationslist.ConversationsFragment", loader);
        var home = XposedHelpers.findClass("com.whatsapp.HomeActivity", loader);

        // Modifying tab list order
        hookTabList(home);
        // Setting up fragments
        hookTabInstance(cFrag);
        // Setting group tab name
        hookTabName();
        // Setting group icon
        hookTabIcon();
        // Setting tab count
        hookTabCount();
    }

    private void hookTabCount() {
        var home = findClass("com.whatsapp.HomeActivity", loader);
        findAndHookMethod(home, "A0p", home, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                var a1 = XposedHelpers.getObjectField(param.args[0], "A0i");
                var chatCount = 0;
                var groupCount = 0;
                // Fiz ele pegar direto da database, esse metodo que dei hook, e chamado sempre q vc muda de tab, entra/sai de um chat ->
                // ou quando a lista e atualizada, ent ele sempre vai atualizar
                var db = SQLiteDatabase.openDatabase("/data/data/com.whatsapp/databases/msgstore.db", null, SQLiteDatabase.OPEN_READONLY);
                // essa coluna que eu peguei, mostra a quantidade de mensagens n lidas (obvio ne).
                // nao coloquei apenas > 0 pq quando vc marca um chat como nao lido, esse valor fica -1
                // entao pra contar direitinho deixei != 0
                var sql = "SELECT * FROM chat WHERE unseen_message_count != 0";
                var cursor = db.rawQuery(sql, null);
                while (cursor.moveToNext()) {
                    // row da jid do chat
                    @SuppressLint("Range") int jid = cursor.getInt(cursor.getColumnIndex("jid_row_id"));
                    // verifica se esta arquivado ou n
                    @SuppressLint("Range") int hidden = cursor.getInt(cursor.getColumnIndex("hidden"));
                    if (hidden == 1) return;
                    // aqui eu fiz pra verificar se e grupo ou n, ai ele pega as infos da jid de acordo com a row da jid ali de cima
                    var sql2 = "SELECT * FROM jid WHERE _id == ?";
                    var cursor1 = db.rawQuery(sql2, new String[]{ String.valueOf(jid)});
                    while (cursor1.moveToNext()) {
                        // esse server armazena oq ele e, s.whatsapp.net, lid, ou g.us
                        @SuppressLint("Range") var server = cursor1.getString(cursor1.getColumnIndex("server"));
                        // separacao simples
                        if (server.equals("g.us")) {
                            groupCount++;
                        } else {
                            chatCount++;
                        }
                    }
                }
                // cada tab tem sua classe, ent eu percorro todas pra funcionar dboa
                for(int i = 0; i < tabs.size(); i++) {
                    var q = XposedHelpers.callMethod(a1, "A00", a1, i);

                    // deixei a de call pq a de gp nao aparece
                    if (tabs.get(i) == CHATS) {
                        setObjectField(q, "A01", groupCount);
                    } else if (tabs.get(i) == CALLS) {
                        setObjectField(q, "A01", chatCount);
                    }
                }
                super.afterHookedMethod(param);
            }
        });
        //Issaq meio q ativa o contador da tab de grupo, mas fica totalmente igual ao chats
        findAndHookMethod("X.1NI", loader, "A05", Context.class, findClass("X.920", loader), int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                var indexTab = (int) param.args[2];
                if (indexTab == 4) {
                    param.args[2] = 0;
                } else if (indexTab == 0) {
                    param.args[2] = 1;
                }
                super.beforeHookedMethod(param);
            }
        });
    }

    private void hookTabIcon() {
        findAndHookMethod(classTabsList, loader, methodTabIcon, new XC_MethodHook() {
            @SuppressLint("ResourceType")
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                var superClass = param.thisObject.getClass().getSuperclass();
                if (superClass != null && superClass.getName().equals(classTabsList)) {
                    var fields = superClass.getDeclaredFields();
                    for (var field : fields) {
                        XposedBridge.log("Field: " + superClass.getName() + ";->" + field.getName() + ":" + field.getType().getName());
                    }
                    var field1 = superClass.getDeclaredField("A09").get(param.thisObject);
//                    var field1 = getObjectField(superClass, "A09");
                    var field2 = getObjectField(field1, "A01");
                    var menu = getObjectField(field2, "A03");
                    if (menu != null) {
                        var menuItem = (MenuItem) callMethod(menu, "findItem", GROUPS);
                        if (menuItem != null) {
                            menuItem.setIcon(ClassesReference.ChatsFilter.tabIconId);
                        }
                    }
                }

                super.afterHookedMethod(param);
            }
        });
    }

    @SuppressLint("ResourceType")
    private void hookTabName() {
        findAndHookMethod(classTabName, loader, methodTabName, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                var tab = (int) param.args[0];
                var activity = (Activity) XposedHelpers.getObjectField(param.thisObject, "A02");
                if (tab == GROUPS) {
                    var name = activity.getResources().getString(nameId);
                    param.setResult(name);
                }
            }
        });
    }

    private void hookTabInstance(Class<?> cFrag) {
        XposedHelpers.findAndHookMethod(classGetTab, loader, methodGetTab, int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                var tabId = ((Number) tabs.get((int) param.args[0])).intValue();

                if (tabId == GROUPS || tabId == CHATS) {
                    var convFragment = XposedHelpers.findConstructorExact(cFrag.getName(), loader).newInstance();
                    var convFragmentClass = convFragment.getClass();
                    XposedHelpers.setAdditionalInstanceField(convFragment, "isGroup", tabId == GROUPS);
                    XposedHelpers.findAndHookMethod(convFragmentClass.getName(), convFragmentClass.getClassLoader(), methodTabInstance, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            var isGroup = false;
                            var isGroupField = XposedHelpers.getAdditionalInstanceField(param.thisObject, "isGroup");

                            // Temp fix for
                            if (isGroupField == null) {
                                XposedBridge.log("-----------------------------------");
                                XposedBridge.log("isGroupTabCount: " + tabCount);
                                XposedBridge.log("isGroupTabField: " + (isGroupField != null));
                                XposedBridge.log("isGroupTabCount >= 2: " + (tabCount >= 2));
                                XposedBridge.log("-----------------------------------");
                                isGroup = tabCount >= 2;
                                tabCount++;
                                if (tabCount == 4) tabCount = 0;
                            } else {
                                isGroup = (boolean) isGroupField;
                            }
                            XposedBridge.log("[•] isGroup: " + isGroup);

                            var chatsList = (List) param.getResult();
                            var editableChatList = new ArrayList<>();
                            var requiredServer = isGroup ? "g.us" : "s.whatsapp.net";
                            for (var chat : chatsList) {
                                var server = (String) callMethod(getObjectField(chat, "A00"), "getServer");
                                if (server.equals(requiredServer)) {
                                    editableChatList.add(chat);
                                }
                            }
                            param.setResult(editableChatList);
                            super.afterHookedMethod(param);
                        }
                    });

                    // corigindo a bct do fab
                    XposedHelpers.findAndHookMethod(convFragmentClass.getName(), convFragmentClass.getClassLoader(), "BHP", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            var isGroup = false;
                            var isGroupField = XposedHelpers.getAdditionalInstanceField(param.thisObject, "isGroup");
                            if (isGroupField != null) isGroup = (boolean) isGroupField;
                            if (isGroup) {
                                param.setResult(GROUPS);
                            }
                            super.afterHookedMethod(param);
                        }
                    });

                    param.setResult(convFragment);
                } else {
                    super.afterHookedMethod(param);
                }
            }
        });
    }

    private void hookTabList(Class<?> home) {
        if (!prefs.getBoolean("separategroups", false)) return;
        XposedHelpers.findAndHookMethod(classTabsList, loader, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                var newHome = prefs != null && prefs.getBoolean("novahome", false);
                tabs.clear();
                if (newHome) {
                    tabs.add(CHATS);
                    tabs.add(GROUPS);
                    tabs.add(STATUS);
                    tabs.add(COMMUNITY);
                    tabs.add(CALLS);
                } else {
                    tabs.add(COMMUNITY);
                    tabs.add(CHATS);
                    tabs.add(GROUPS);
                    tabs.add(STATUS);
                    tabs.add(CALLS);
                }
                XposedHelpers.setStaticObjectField(home, fieldTabsList, tabs);
            }
        });
    }
}
