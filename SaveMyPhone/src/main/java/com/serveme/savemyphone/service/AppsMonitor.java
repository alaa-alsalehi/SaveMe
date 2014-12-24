package com.serveme.savemyphone.service;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.serveme.savemyphone.model.DBOperations;
import com.serveme.savemyphone.model.Launcher;
import com.serveme.savemyphone.view.BaseActivity;
import com.serveme.savemyphone.view.utils.AlertUtility;
import com.serveme.savemyphone.view.utils.AnalyticsExceptionParser;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

//import android.widget.Toast;

public class AppsMonitor extends Service {
    private static final int UPDATE_INTERVAL = 39;
    private Timer timer = new Timer();
    private ActivityManager am;
    private DBOperations db;
    // private ComponentName lastallowedapp;
    int counter = 1;
    private Handler handler;

    private volatile boolean stopped;

    private ServiceUtils serviceUtils;

    private enum MobileState {
        START_APP, ALLOW_APP, UNALLOW_APP, ANDROID, USER_ACTIVITY, UNALLOW_APP_STARTED_BY_ALLOW_APP, START_ALERT_MESSAGE, END_ALERT_MESSAGE
    }

    private volatile MobileState currentState;
    private volatile MobileState previousState;
    private Launcher currentApp;
    private long startTime;
    private long sessionId;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        currentState = MobileState.USER_ACTIVITY;
        previousState = MobileState.START_APP;
        db = DBOperations.getInstance(this);
        sessionId = db.createAppLogSession(System.currentTimeMillis());
        am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        // view = LayoutInflater.from(AppsMonitor.this).inflate(
        // R.layout.password_request, null);
        final WindowManager.LayoutParams param = AlertUtility.getParam();
        final View view = AlertUtility.getView(AppsMonitor.this);
        serviceUtils = new ServiceUtils(AppsMonitor.this, am);
        handler = new Handler() {
            // Toast toast = Toast.makeText(AppsMonitor.this,
            // R.string.prevent_message, Toast.LENGTH_LONG);

            @Override
            public void handleMessage(Message msg) {
                final WindowManager wmgr = (WindowManager) getApplicationContext()
                        .getSystemService(Context.WINDOW_SERVICE);
                if (!stopped) {
                    if (msg.what == 0) {
                        synchronized (view) {
                            if (currentState == MobileState.UNALLOW_APP) {

                                try {
                                    wmgr.addView(view, param);
                                } catch (Exception e) {
                                    Tracker tracker = EasyTracker
                                            .getInstance(AppsMonitor.this);
                                    tracker.send(MapBuilder.createException(
                                            new AnalyticsExceptionParser()
                                                    .getDescription(Thread
                                                            .currentThread()
                                                            .toString()
                                                            + " "
                                                            + previousState
                                                            + " "
                                                            + currentState, e),
                                            false).build());
                                }
                                // toast.show();
                                setCurrentState(MobileState.START_ALERT_MESSAGE);
                            }
                        }
                    } else if (msg.what == 1) {
                        synchronized (view) {
                            if (currentState == MobileState.START_ALERT_MESSAGE) {
                                setCurrentState(MobileState.END_ALERT_MESSAGE);
                                try {
                                    wmgr.removeView(view);
                                } catch (Exception e) {
                                    Tracker tracker = EasyTracker
                                            .getInstance(AppsMonitor.this);
                                    tracker.send(MapBuilder.createException(
                                            new AnalyticsExceptionParser()
                                                    .getDescription(Thread
                                                            .currentThread()
                                                            .toString()
                                                            + " "
                                                            + previousState
                                                            + " "
                                                            + currentState, e),
                                            false).build());
                                }
                            }
                        }
                    } else if (msg.what == 2) {
                        Bundle bundle = msg.getData();
                        String packageName = bundle.getString("packageName");
                        long startTime = bundle.getLong("startTime");
                        long endTime = bundle.getLong("endTime");
                        DBOperations.getInstance(AppsMonitor.this)
                                .insertAppLog(packageName, startTime, endTime, sessionId);
                    }
                }

            }

        };
        doGetRunningApp();
        return START_STICKY; // continue running until it is explicitly stopped,
        // so return sticky
    }

    private void doGetRunningApp() {
        final View view = AlertUtility.getView(AppsMonitor.this);

        timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
                    preventAppFromRunningOnLollipop(view);
                } else {
                    preventAppFromRunning(view);
                }
            }
        }, 1, UPDATE_INTERVAL);
    }

    private void setCurrentState(MobileState newState) {
        previousState = currentState;
        currentState = newState;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
        stopped = true;
        sendBroadcast(new Intent("finish_user_activity"));
        final View view = AlertUtility.getView(AppsMonitor.this);
        handler.removeMessages(0);
        handler.removeMessages(1);
        // ����� ������ �������� ����� ��� ���� �����
        final WindowManager wmgr = (WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        synchronized (view) {
            try {
                if (view != null)
                    wmgr.removeView(view);
            } catch (Exception e) {
            }
        }
    }

    private void preventAppFromRunning(View view) {
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        ComponentName topActivity = taskInfo.get(0).topActivity;
        ComponentName baseActivity = taskInfo.get(0).baseActivity;// ��������
        // ������
        Launcher launcher = new Launcher(topActivity.getPackageName(),
                null);

        if (!db.getWhiteListPackages().contains(launcher)
                && !topActivity.getPackageName().equals("android")
                && !topActivity.getClassName().equals(
                "com.serveme.savemyphone.view.UserActivity")
                && !topActivity
                .getClassName()
                .equals("com.serveme.savemyphone.view.RecoveryActivity")
                && !topActivity.getClassName().equals(
                "com.serveme.savemyphone.view.WaitingActivity")
                && !topActivity
                .getClassName()
                .equals("com.haibison.android.lockpattern.LockPatternActivity")) {
            // ActivityManager manager =
            // (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
            // List<RunningAppProcessInfo> services =
            // manager.getRunningAppProcesses();
            // for(RunningAppProcessInfo rpi : services){
            // if(rpi.processName.startsWith(componentInfo.getPackageName())){
            // android.os.Process.killProcess(rpi.pid);
            // }
            // }
            // am.killBackgroundProcesses(componentInfo.getPackageName());
            /*
             * componentInfo = taskInfo.get(0).baseActivity; launcher =
			 * new Launcher( componentInfo.getPackageName(), null);
			 */

            launcher = new Launcher(baseActivity.getPackageName(), null);
            if (db.getWhiteListPackages().contains(launcher)) {
                // Intent intent = new Intent(Intent.ACTION_MAIN);
                // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                // Intent.FLAG_ACTIVITY_NEW_TASK);
                // intent.setComponent(new
                // ComponentName(lastallowedapp.getPackageName(),lastallowedapp.getClassName()));
                // startActivity(intent);
                // ��� ��� ���� ������� ������ ����� ��� ��� ������ ���
                // �������� ������
                Intent i = new Intent(AppsMonitor.this,
                        BaseActivity.class);
                i.putExtra("package", baseActivity.getPackageName());
                i.putExtra("activity", baseActivity.getClassName());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                synchronized (view) {
                    if (currentState == MobileState.START_ALERT_MESSAGE) {

                    } else if (currentState != MobileState.UNALLOW_APP_STARTED_BY_ALLOW_APP) {
                        handler.sendEmptyMessage(0);
                        setCurrentState(MobileState.UNALLOW_APP_STARTED_BY_ALLOW_APP);
                    }
                }
            } else {
                // Log.d("test", "test");
                synchronized (view) {
                    if (currentState == MobileState.START_ALERT_MESSAGE) {
                        // ��� ��� ����� ����� ����� �� ���� �������
                    } else if (currentState != MobileState.UNALLOW_APP) {
                        setCurrentState(MobileState.UNALLOW_APP);
                        handler.sendEmptyMessage(0);
                    }
                }
            }
            //
        } else if (topActivity.getPackageName().equals("android")) {
            // lastallowedapp = taskInfo.get(0).topActivity;
            synchronized (view) {
                if (currentState != MobileState.START_ALERT_MESSAGE) {
                    setCurrentState(MobileState.ANDROID);
                } else {
                    handler.sendEmptyMessage(1);
                }
            }
        } else if (topActivity.getClassName().equals(
                "com.serveme.savemyphone.view.UserActivity")) {
            synchronized (view) {
                if (currentState == MobileState.START_ALERT_MESSAGE) {
                    handler.sendEmptyMessage(1);
                } else {
                    setCurrentState(MobileState.USER_ACTIVITY);
                }
            }
        } else {
            synchronized (view) {
                if (currentState != MobileState.START_ALERT_MESSAGE) {
                    setCurrentState(MobileState.ALLOW_APP);
                } else {
                    handler.sendEmptyMessage(1);
                }
            }
            if (currentApp == null
                    || currentApp.getPackageName() == null
                    || !currentApp.equals(
                    launcher)) {
                if (currentApp != null
                        && currentApp.getPackageName() != null
                        && !currentApp.getPackageName().equals(
                        getPackageName())) {
                    Bundle bundle = new Bundle();
                    bundle.putString("packageName",
                            currentApp.getPackageName());
                    bundle.putLong("startTime", startTime);
                    bundle.putLong("endTime",
                            System.currentTimeMillis());
                    // ����� �������� ����� ������� ����� ��� ���� �����
                    // ��� ����� �������� ��� lock
                    Message message = handler.obtainMessage(2);
                    message.setData(bundle);
                    handler.sendMessage(message);

                    Log.d("change app", "replace "
                            + (currentApp == null ? " null "
                            : currentApp.toString())
                            + " by "
                            + (baseActivity == null ? " null "
                            : baseActivity.getPackageName()));
                    Log.d("package", currentApp.getPackageName() + " "
                            + getPackageName());
                }
                startTime = System.currentTimeMillis();
                currentApp = launcher;
            }
        }
    }

    private void preventAppFromRunningOnLollipop(View view) {
        List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();

        List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo process : processes) {
            // Take a look at the IMPORTANCE_VISIBLE property as well in the link provided at the bottom
            if (process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && !serviceUtils.isRunningService(process.processName)) {
                Launcher launcher = new Launcher(process.processName, null);
                List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
                ComponentName topActivity = taskInfo.get(0).topActivity;
                if (!db.getWhiteListPackages().contains(launcher)
                        && !launcher.getPackageName().equals("android")
                        && !topActivity.getClassName().equals("com.serveme.savemyphone.view.UserActivity")
                        && !topActivity.getClassName().equals("com.serveme.savemyphone.view.RecoveryActivity")
                        && !topActivity.getClassName().equals("com.serveme.savemyphone.view.WaitingActivity")
                        && !topActivity.getClassName().equals("com.haibison.android.lockpattern.LockPatternActivity")) {
                    // ActivityManager manager =
                    // (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
                    // List<RunningAppProcessInfo> services =
                    // manager.getRunningAppProcesses();
                    // for(RunningAppProcessInfo rpi : services){
                    // if(rpi.processName.startsWith(componentInfo.getPackageName())){
                    // android.os.Process.killProcess(rpi.pid);
                    // }
                    // }
                    // am.killBackgroundProcesses(componentInfo.getPackageName());
            /*
             * componentInfo = taskInfo.get(0).baseActivity; launcher =
			 * new Launcher( componentInfo.getPackageName(), null);
			 */
                    // Log.d("test", "test");
                    synchronized (view) {
                        if (currentState == MobileState.START_ALERT_MESSAGE) {
                            // ��� ��� ����� ����� ����� �� ���� �������
                        } else if (currentState != MobileState.UNALLOW_APP) {
                            setCurrentState(MobileState.UNALLOW_APP);
                            handler.sendEmptyMessage(0);
                        }
                    }
                    //
                } else if (topActivity.getPackageName().equals("android")) {
                    // lastallowedapp = taskInfo.get(0).topActivity;
                    synchronized (view) {
                        if (currentState != MobileState.START_ALERT_MESSAGE) {
                            setCurrentState(MobileState.ANDROID);
                        } else {
                            handler.sendEmptyMessage(1);
                        }
                    }
                } else if (topActivity.getClassName().equals(
                        "com.serveme.savemyphone.view.UserActivity")) {
                    synchronized (view) {
                        if (currentState == MobileState.START_ALERT_MESSAGE) {
                            handler.sendEmptyMessage(1);
                        } else {
                            setCurrentState(MobileState.USER_ACTIVITY);
                        }
                    }
                } else {
                    synchronized (view) {
                        if (currentState != MobileState.START_ALERT_MESSAGE) {
                            setCurrentState(MobileState.ALLOW_APP);
                        } else {
                            handler.sendEmptyMessage(1);
                        }
                    }
                    if (currentApp == null
                            || currentApp.getPackageName() == null
                            || !currentApp.equals(
                            launcher)) {
                        if (currentApp != null
                                && currentApp.getPackageName() != null
                                && !currentApp.getPackageName().equals(
                                getPackageName())) {
                            Bundle bundle = new Bundle();
                            bundle.putString("packageName",
                                    currentApp.getPackageName());
                            bundle.putLong("startTime", startTime);
                            bundle.putLong("endTime",
                                    System.currentTimeMillis());
                            // ����� �������� ����� ������� ����� ��� ���� �����
                            // ��� ����� �������� ��� lock
                            Message message = handler.obtainMessage(2);
                            message.setData(bundle);
                            handler.sendMessage(message);

                        }
                        startTime = System.currentTimeMillis();
                        currentApp = launcher;
                    }
                }
                break;
            }
        }
    }


}