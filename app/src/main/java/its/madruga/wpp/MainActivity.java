package its.madruga.wpp;

import static android.os.Process.killProcess;
import static android.os.Process.myPid;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import its.madruga.wpp.activities.BaseActivity;
import its.madruga.wpp.adapters.AppListAdapter;
import its.madruga.wpp.models.AppInfoModel;
import its.madruga.wpp.xposed.XposedChecker;

public class MainActivity extends BaseActivity {

    public final static String TAG = "Debug-Main";
    public static Process shell;
    public static boolean isRootGranted = false;
    public static boolean isLSPatched = false;
    public List<PackageInfo> lspatchPkgs = new ArrayList<>();
    public List<PackageInfo> wppPkgs = new ArrayList<>();
    public HashSet<String> wppPkgNames = new HashSet<>();
    private boolean requestingLSPosedScope = false;

    private void requestRootAccess() {
        Process su;
        DataOutputStream outputStream;
        BufferedReader inputStream;
        try {
            su = Runtime.getRuntime().exec("su");
            outputStream = new DataOutputStream(su.getOutputStream());
            outputStream.writeBytes("whoami\n");
            outputStream.flush();
            inputStream = new BufferedReader(new InputStreamReader(su.getInputStream()));
            isRootGranted = inputStream.readLine() != null;
            shell = su;
        } catch (IOException e) {
            Toast.makeText(this, "Error while executing 'su'", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (requestingLSPosedScope) {
            try {
                var stdin = new DataOutputStream(shell.getOutputStream());
                stdin.writeBytes("kill " + myPid() + "\n");
                stdin.flush();
            } catch (IOException e) {
                killProcess(myPid());
            }
        }
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setWppPkgs();
        requestRootAccess();

        if (!XposedChecker.isActive()) {
            requestLSPatchAccess();
            if (isLSPatched) {
                setContentViewMain();
            } else {
                setContentViewError();
                if (isRootGranted) {
                    requestModuleScope();
                }
            }
        } else {
            setContentViewMain();
        }
    }

    private void setContentViewError() {
        setContentView(R.layout.no_active_module);
        findViewById(R.id.lsposed_download).setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/mywalkb/LSPosed_mod"))));
        findViewById(R.id.lspatch_download).setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/LSPosed/LSPatch"))));
    }

    @SuppressLint({"WorldReadableFiles", "WrongConstant"})
    private void setContentViewMain() {
        setContentView(R.layout.activity_main);

        int mode = isLSPatched ? MODE_APPEND : MODE_WORLD_READABLE;
        mShared = getSharedPreferences(getPackageName() + "_preferences", mode);
        mEditor = mShared.edit();

        mEditor.putStringSet("whatsapp_packages", wppPkgNames).apply();

        var container = (ViewGroup) findViewById(R.id.container);
        var img = (ImageView) findViewById(R.id.whatsapp_version_status_img);
        var text = (TextView) findViewById(R.id.module_status_text);

        var workingText = getResources().getString(R.string.module_working_text);
        if (isLSPatched) {
            text.setText(String.format(workingText, "LSPatch"));
            img.setImageResource(R.drawable.lspatch_icon);
        } else {
            text.setText(String.format(workingText, "LSPosed"));
            img.setImageResource(R.drawable.lsposed_icon);
        }


        Log.i(TAG, "setContentViewMain: mode » " + mode);
        Log.i(TAG, "setContentViewMain: LSPatch » " + isLSPatched);

        findViewById(R.id.reset_preferences).setOnClickListener(v -> new MaterialAlertDialogBuilder(container.getContext()).setTitle(R.string.reset_preferences).setMessage(R.string.reset_preferences_message).setPositiveButton(android.R.string.ok, (dialog, which) -> {
            mEditor.clear().apply();
            dialog.dismiss();
            recreate();
        }).setNegativeButton(android.R.string.cancel, ((dialog, which) -> dialog.dismiss())).create().show());

        findViewById(R.id.restart_whatsapp).setOnClickListener(v -> {
            try {
                if (isRootGranted) {
                    var stdin = new DataOutputStream(shell.getOutputStream());
                    stdin.writeBytes("pidof com.whatsapp;echo null\n");
                    stdin.flush();
                    var stdout = new BufferedReader(new InputStreamReader(shell.getInputStream()));
                    var line = stdout.readLine();
                    if (line.equals("null")) {
                        Toast.makeText(this, "No proccesses", Toast.LENGTH_SHORT).show();
                    } else {
                        var pids = line.split(" ");
                        for (var pid : pids) {
                            stdin.writeBytes("kill " + pid + "\n");
                            stdin.flush();
                        }
                        Toast.makeText(this, "Killed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showRootNeededDialog();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        configureListeners(container);
        setWhatsappVersionMessage();

        findViewById(R.id.mdgwa_github_onclick).setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ItsMadruga/MdgWa"))));
        findViewById(R.id.mdgwa_telegram_channel).setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/mdgwamodule"))));
        findViewById(R.id.github_darker_onclick).setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Darker935"))));
        findViewById(R.id.madruga_github_onclick).setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ItsMadruga"))));

        var listView = (RecyclerView) findViewById(R.id.installed_app_list);
        var showApps = findViewById(R.id.show_supported_installed_apps);

        var dataModels = new ArrayList<AppInfoModel>();
        var pm = getPackageManager();
        for (var appInfo : isLSPatched ? lspatchPkgs : wppPkgs) {
            dataModels.add(new AppInfoModel(appInfo.packageName, appInfo.versionName, appInfo.applicationInfo.loadIcon(pm)));
        }
        var adapter = new AppListAdapter(dataModels, getApplicationContext());
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(adapter);
//        listView.addItemDecoration(new RecyclerView.ItemDecoration() {
//            @Override
//            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
//                outRect.setEmpty();
//            }
//        });

        showApps.setOnClickListener(v -> {
            if (listView.getVisibility() == View.GONE) {
                Log.i(TAG, "setContentViewMain: showing");
                Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
                listView.startAnimation(animation);
                listView.setVisibility(View.VISIBLE);
            } else {
                Log.i(TAG, "setContentViewMain: hidding");
                listView.setVisibility(View.GONE);
            }
        });
    }

    private void showRootNeededDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.root_needed)
                .setMessage(R.string.root_needed_message)
                .setCancelable(true)
                .setNegativeButton("Ok", (dialogInterface, i) -> dialogInterface.dismiss())
                .create()
                .show();
    }

    private void requestModuleScope() {
        try {
            var command = String.join(" ", new String[]{"am", "start", "-p", "com.android.shell", "-n", "com.android.shell/.BugreportWarningActivity", "-a", "android.intent.action.MAIN", "-f", "0x10000000", "-c", "org.lsposed.manager.LAUNCH_MANAGER", "-d", "module://" + getPackageName() + ":0/...\n"});
            var stdin = new DataOutputStream(shell.getOutputStream());
            stdin.writeBytes(command);
            stdin.flush();
            requestingLSPosedScope = true;
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), R.string.xposedinit_error, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void setWppPkgs() {
        var pm = getPackageManager();
        var pkgs = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        for (var pkg : pkgs) {
            if (pkg.permissions != null) {
                for (var permission : pkg.permissions) {
                    if (permission.name.equals("com.whatsapp.sticker.READ")) {
                        wppPkgs.add(pkg);
                        wppPkgNames.add(pkg.packageName);
                    }
                }
            }
        }
    }

    private void requestLSPatchAccess() {
        for (var pkg : wppPkgs) {
            if (pkg.applicationInfo.metaData != null && pkg.applicationInfo.metaData.containsKey("lspatch")) {
                lspatchPkgs.add(pkg);
            }
        }
    }

    private void setWhatsappVersionMessage() {
        var msg = (MaterialTextView) findViewById(R.id.show_supported_installed_apps);
        var required = (MaterialTextView) findViewById(R.id.module_status_text);
        var requiredText = required.getText().toString();
        var installed = (MaterialTextView) findViewById(R.id.module_status_sum);
        var installedText = installed.getText().toString();
        var resources = getResources();
        var moduleInfo = BuildConfig.VERSION_NAME;
        required.setText(String.format(requiredText, moduleInfo));
        try {
            var pm = getPackageManager();
            var wppInfo = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);


        } catch (PackageManager.NameNotFoundException e) {
            installed.setText(String.format(installedText, "--------"));
            installed.setTextColor(resources.getColor(R.color.default_red, getTheme()));
            if (msg.getVisibility() == View.GONE) {
                msg.setVisibility(View.VISIBLE);
            }
            msg.setText(R.string.whatsapp_not_installed);
            e.printStackTrace();
        }
    }
}