package co.vector.itube;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

/**
 * Created by android on 11/13/14.
 */
public class BaseActivity extends Activity {
    static  TextView language;AQuery aq;int open;static PopupWindow popupWindow;
    static  BaseClass baseClass;static SearchView searchView;LinearLayout layout;MenuDrawer mDrawerLeft;
    static BroadcastReceiver receiver = null;
    static Long minutes;

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

//        minutes = Long.valueOf(baseClass.getCheckDuration());
//        Long millisec = minutes *60*1000;
//        MyCount counter = new MyCount(millisec, 1000);
//        counter.start();
        if(baseClass.isTabletDevice(this))
        {
            aq.id(R.id.BaseLayout).background(R.drawable.background_tablet);
        }
        else
        {
            aq.id(R.id.BaseLayout).background(R.drawable.background_simple);
        }
        language = (TextView) findViewById(R.id.select_language);
        searchView = (SearchView) findViewById(R.id.search);
        open=0;
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
//    public class MyCount extends CountDownTimer {
//
//        public MyCount(long millisInFuture, long countDownInterval) {
//            super(millisInFuture, countDownInterval);
//        }
//
//        @Override
//        public void onFinish() {
//            baseClass.clearSharedPrefs();
//            startActivity(new Intent(BaseActivity.this, LoginActivity.class));
//            Intent broadcastIntent = new Intent();
//            broadcastIntent
//                    .setAction("co.vector.itube");
//            LocalBroadcastManager.getInstance(BaseActivity.this).sendBroadcast(broadcastIntent);
//            BaseActivity.this.finish();
//        }
//
//        @Override
//        public void onTick(long millisUntilFinished) {
//        }
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