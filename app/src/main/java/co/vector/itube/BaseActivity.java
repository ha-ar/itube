package co.vector.itube;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TextView;

import com.androidquery.AQuery;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

import java.util.Calendar;

/**
 * Created by android on 11/13/14.
 */
public class BaseActivity extends Activity {
static  TextView language;AQuery aq;int open;static PopupWindow popupWindow;
    static  BaseClass baseClass;static SearchView searchView;LinearLayout layout;MenuDrawer mDrawerLeft;
    static BroadcastReceiver receiver = null;
    Calendar newAlarmTime;
    AlarmManager alarmManager;Intent intentAlarm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDrawerLeft = MenuDrawer.attach(this, MenuDrawer.Type.OVERLAY, Position.START, MenuDrawer.MENU_DRAG_WINDOW);
        mDrawerLeft.setContentView(R.layout.activity_base);
        mDrawerLeft.setMenuView(R.layout.layout_drawer);
        mDrawerLeft.setDrawOverlay(true);
        mDrawerLeft.setDrawerIndicatorEnabled(true);
        mDrawerLeft.setupUpIndicator(this);
        baseClass  =((BaseClass) getApplicationContext());
        aq = new AQuery(this);
        if(baseClass.isTabletDevice(this))
        {
            aq.id(R.id.BaseLayout).background(R.drawable.background_tablet);
        }
        else
        {
            aq.id(R.id.BaseLayout).background(R.drawable.background_simple);
        }

//        if (alarmManager!= null) {
//            alarmManager.cancel(PendingIntent
//                    .getBroadcast(getApplicationContext(), 1, intentAlarm,
//                            PendingIntent.FLAG_UPDATE_CURRENT));
//        }
        newAlarmTime = Calendar.getInstance();
        alarmManager = (AlarmManager) getApplicationContext()
                .getSystemService(Context.ALARM_SERVICE);
        intentAlarm = new Intent(getApplicationContext(), AlarmReceiver.class);

        alarmManager.set(AlarmManager.RTC_WAKEUP, newAlarmTime
                .getTimeInMillis() + 5*60*60*24*1000,PendingIntent
                .getBroadcast(getApplicationContext(), 1, intentAlarm,
                        PendingIntent.FLAG_UPDATE_CURRENT));

        language = (TextView) findViewById(R.id.select_language);
        searchView = (SearchView) findViewById(R.id.search);
        open=0;
        //CheckDuration();
       searchView.setOnSearchClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               language.setVisibility(View.GONE);
               open = 1;
           }
       });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                language.setVisibility(View.VISIBLE);
                open = 0;
                return false;
            }
        });
        aq.id(R.id.menu).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLeft.toggleMenu();
            }
        });
        aq.id(R.id.layout_home).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLeft.closeMenu();
                FragmentManager fm = getFragmentManager();
                for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    fm.popBackStack();
                }
                getFragmentManager().beginTransaction()
                        .replace(R.id.frame_container, new BaseFragment()
                                .newinstance()).commit();
            }
        });
        aq.id(R.id.layout_category).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLeft.closeMenu();
                FragmentManager fm = getFragmentManager();
                for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    fm.popBackStack();
                }
                getFragmentManager().beginTransaction()
                        .replace(R.id.frame_container, new CategoryFragment()
                                .newinstance()).commit();
            }
        });
        aq.id(R.id.layout_playlist).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLeft.closeMenu();
                baseClass.setIsFromFavorites("Yes");
                FragmentManager fm = getFragmentManager();
                for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    fm.popBackStack();
                }
                getFragmentManager().beginTransaction()
                        .replace(R.id.frame_container, new UserPlaylistFragment()
                                .newinstance()).commit();
            }
        });
        aq.id(R.id.layout_favorite).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLeft.closeMenu();
                baseClass.setIsFromFavorites("Yes");
                FragmentManager fm = getFragmentManager();
                for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    fm.popBackStack();
                }
                getFragmentManager().beginTransaction()
                        .replace(R.id.frame_container, new UserFavoriteFragment()
                                .newinstance()).commit();
            }
        });
        aq.id(R.id.layout_logout).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLeft.closeMenu();
                FragmentManager fm = getFragmentManager();
                for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    fm.popBackStack();
                }
                baseClass.clearSharedPrefs();
                startActivity(new Intent(BaseActivity.this, LoginActivity.class));
                BaseActivity.this.finish();
            }
        });
        aq.id(R.id.layout_subscription).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLeft.closeMenu();
                FragmentManager fm = getFragmentManager();
                for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    fm.popBackStack();
                }
                if(BillingHelper.isBillingSupported()){
                    BillingHelper.requestPurchase(getApplicationContext(), "android.test.purchased");
                } else {
                    Log.i("TAG", "Can't purchase on this device");
                }
            }
        });


        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.frame_container, new BaseFragment()
                            .newinstance()).commit();
        }
    }
    int counter;Long Duration;
//    private void CheckDuration()
//    {
//       Long duration = baseClass.getCheckDuration();
//        Duration =  duration * 1000 * 60;
//        counter=0;
//        final Handler handler=new Handler();
//        final Runnable r = new Runnable()
//            {
//                public void run()
//                {
//                    if(counter==0) {
//                        try {
//                            counter++;
//                            FragmentManager fm = getFragmentManager();
//                            for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
//                                fm.popBackStack();
//                            }
//                            baseClass.clearSharedPrefs();
//                            startActivity(new Intent(BaseActivity.this, LoginActivity.class));
//                            Intent broadcastIntent = new Intent();
//                            broadcastIntent
//                                    .setAction("co.vector.javantube");
//                            LocalBroadcastManager.getInstance(BaseActivity.this).sendBroadcast(broadcastIntent);
//                            BaseActivity.this.finish();
//                        } catch (Exception e) {
//                        }
//                    }
//                    handler.postDelayed(this, Duration);
//            }
//        };
//        handler.postDelayed(r, Duration);
//
//    }
    @Override
    public void onBackPressed() {
        if(this.getFragmentManager().getBackStackEntryCount()==0) {
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    moveTaskToBack(true);

                                }
                            }).setNegativeButton("No", null).show();
        }
        else
            super.onBackPressed();
    }
    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        receiver = null;
        super.onDestroy();
    }
}