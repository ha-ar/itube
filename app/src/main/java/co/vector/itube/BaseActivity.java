package co.vector.itube;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by android on 11/13/14.
 */
public class BaseActivity extends Activity {
    static  TextView language;AQuery aq;int open;static PopupWindow popupWindow;
    static  BaseClass baseClass;static SearchView searchView;LinearLayout layout;MenuDrawer mDrawerLeft;
    static BroadcastReceiver receiver = null;
    static Long minutes;
    AlertDialog dialog;

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

        minutes = Long.valueOf(baseClass.getCheckDuration());
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
        aq.id(R.id.layout_sharing).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLeft.closeMenu();
                FragmentManager fm = getFragmentManager();
                for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    fm.popBackStack();
                }
                SelectFromMenu();
            }
        });


        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.frame_container, new BaseFragment()
                            .newinstance()).commit();
        }
    }
    public void SelectFromMenu()
    {
        final CharSequence[] options = { "Whats App", "Viber","Facebook","Twitter","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Share this app via");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            String urlToShare = "http://ivideo23.com/iVideo.apk";

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                PackageManager pm=getPackageManager();
                if (options[which].equals("Whats App")) {
                    try {
                        Intent waIntent = new Intent(Intent.ACTION_SEND);
                        waIntent.setType("text/plain");
                        pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                        waIntent.setPackage("com.whatsapp");
                        waIntent.putExtra(Intent.EXTRA_TEXT, urlToShare);
                        startActivity(Intent.createChooser(waIntent, "Share with"));

                    } catch (PackageManager.NameNotFoundException e) {
                        Crouton.makeText(BaseActivity.this, "WhatsApp not Installed", Style.ALERT).show();
                    }

                } else if (options[which].equals("Viber")) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.setClassName("com.viber.voip", "com.viber.voip.WelcomeActivity");
                        intent.putExtra(Intent.EXTRA_TEXT, urlToShare);
                        startActivity(intent);
                    } catch (Exception e) {
                        Crouton.makeText(BaseActivity.this, "Viber not Installed", Style.ALERT).show();
                    }
                } else if (options[which].equals("Facebook")) {
                    try {
                        Intent intent1 = new Intent();
                        intent1.setClassName("com.facebook.katana", "com.facebook.katana.activity.composer.ImplicitShareIntentHandler");
                        intent1.setAction("android.intent.action.SEND");
                        intent1.setType("text/plain");
                        intent1.putExtra("android.intent.extra.TEXT", urlToShare);
                        startActivity(intent1);
                    } catch (Exception e) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + urlToShare;
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
                        startActivity(intent);
                    }
                } else if (options[which].equals("Twitter")) {
                    try
                    {
                        getPackageManager().getPackageInfo("com.twitter.android", 0);
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setClassName("com.twitter.android", "com.twitter.android.composer.ComposerActivity");
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, urlToShare);
                        startActivity(intent);

                    }
                    catch (Exception e)
                    {
                        Crouton.makeText(BaseActivity.this,"Twitter is not installed on this device",Style.ALERT).show();

                    }
                } else if (options[which].equals("Cancel")) {
                    dialog.dismiss();

                }

            }
        });
        dialog = builder.create();
        dialog.show();

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