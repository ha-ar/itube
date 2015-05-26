package co.vector.itube;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.ArrayList;
import java.util.List;

import DB.DaoMaster;
import DB.DaoSession;
import DB.Favorite;
import DB.FavoriteDao;
import DB.Playlist;
import DB.PlaylistDao;
import Models.AdsMessage;
import Models.DurationModel;
import Models.GetAllByCategoryModel;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import services.CallBack;
import services.GetAddService;
import services.GetByAllCategoryService;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by android on 11/10/14.
 */
public class YoutubeBaseActivity extends YouTubeFailureRecoveryActivity implements YouTubePlayer.OnFullscreenListener {
    BaseClass baseClass;
    LinearLayout baseLayout;
    RelativeLayout TopLayout;
    AQuery aq;
    int status;
    YouTubePlayer mPlayer;
    YouTubePlayerView youTubePlayerView;
    static String YouTubeKey = "AIzaSyASrjSqUPbo3LNV34A8CWQ8mmQB9EESJyI";
    static GridView list;
    LeftItemListBaseAdapter leftItemListBaseAdapter;
    private boolean fullscreen;
    String popUpContents[];
    static PopupWindow popupWindow;
    private View otherView;
    int index;
    String videoId, videoTitle, videoPlayerlink, videoDuration, videoViewer, videothumbnail;
    GetByAllCategoryService obj;
    LinearLayout listLinear;RelativeLayout listRelative;Activity activity;

    ArrayList<ItemDetailsDuration> image_details_duration;
    ArrayList<ItemDetails> image_details;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (baseClass.isTabletDevice(this)) {
            setContentView(R.layout.activity_youtubeplayer_tablet);
            activity= YoutubeBaseActivity.this;
            aq = new AQuery(activity);
            aq.id(R.id.suggestion_listView).getGridView().setNumColumns(3);
            aq.id(R.id.baselayout).background(R.drawable.background_tablet);
            listLinear = (LinearLayout) findViewById(R.id.listsection);
        } else {
            setContentView(R.layout.activity_youtubeplayer);
            activity= YoutubeBaseActivity.this;
            aq = new AQuery(activity);
            aq.id(R.id.suggestion_listView).getGridView().setNumColumns(2);
            listRelative= (RelativeLayout) findViewById(R.id.listsection1);
            aq.id(R.id.baselayout).background(R.drawable.background_simple);
        }

        // footerView = (LinearLayout) findViewById(R.id.footer);
        Animation fadeOutAnimation = AnimationUtils.loadAnimation(activity, R.anim.fade_out);
        aq.id(R.id.animation).animate(fadeOutAnimation);
        MyCount counter = new MyCount(3000, 1000);
        counter.start();
        baseClass = ((BaseClass) getApplicationContext());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("co.vector.itube");
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("onReceive", "Logout in progress");
                //At this point we should start the login activity and finish this one.
                YoutubeBaseActivity.this.finish();
            }
        }, intentFilter);


        // check for ads
        GetAddService ads = new GetAddService(activity);
        ads.getAdd(baseClass.getAUTH_TOKEN(), new CallBack(activity, "adsCallback"));

        otherView = findViewById(R.id.otherview);
        youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube);
        baseLayout = (LinearLayout) findViewById(R.id.baselayout);

        TopLayout = (RelativeLayout) findViewById(R.id.layout);
        SetOnCreate();
        if(baseClass.getIsFromFavorites().equalsIgnoreCase("YES"))
        {
            aq.id(R.id.animation_layout).getView().getLayoutParams().height =MATCH_PARENT;
        }
        if (isPlaylistVideoIdExist()) {
            aq.id(R.id.add_playlist).background(R.drawable.minus);
        } else {
            aq.id(R.id.add_playlist).background(R.drawable.add);
        }
        if (isFavoriteVideoIdExist()) {
            aq.id(R.id.favorite).background(R.drawable.favorited_u);
        } else {
            aq.id(R.id.favorite).background(R.drawable.favorite);
        }

        aq.id(R.id.add_playlist).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AddedSongToPlaylist();
                    aq.id(R.id.add_playlist).background(R.drawable.minus);
                    Crouton.makeText(
                            activity,
                            "Video Added to Playlist.",
                            Style.CONFIRM).show();
                } catch (Exception e) {
                    DeletedSongFromPlaylist();
                    aq.id(R.id.add_playlist).background(R.drawable.add);
                    Crouton.makeText(
                            activity,
                            "Video Deleted from Playlist.",
                            Style.ALERT).show();
                }
            }
        });
        aq.id(R.id.favorite).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AddedSongToFavorite();
                    aq.id(R.id.favorite).background(R.drawable.favorited_u);
                    Crouton.makeText(
                            activity,
                            "Video Added to Favorites.",
                            Style.CONFIRM).show();
                } catch (Exception e) {
                    DeletedSongFromFavorite();
                    aq.id(R.id.favorite).background(R.drawable.favorite);
                    Crouton.makeText(
                            activity,
                            "Video Deleted from Favorites.",
                            Style.ALERT).show();
                }
            }
        });
        list = (GridView) findViewById(R.id.suggestion_listView);
        list.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                index=page;
                obj = new GetByAllCategoryService(activity);
                if (baseClass.getDataBy().equalsIgnoreCase("Category")) {
                    obj.getbycategory(baseClass.getNewCategory(), baseClass.getDuration(),
                            GetAllByCategoryModel.getInstance().nextPageToken, true,
                            new CallBack(activity, "GetAllBy" + baseClass.getCategory() + "More"));
                } else {
                    obj.getbysearch(SongsListViewFragment.Query,
                            GetAllByCategoryModel.getInstance().nextPageToken, true,
                            new CallBack(activity, "GetAllBySearchMore"));
                }
            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPlayer = baseClass.getYOUTUBE();
                if (!baseClass.getIsFromFavorites().equalsIgnoreCase("YES")) {
                    try {
                        if(SongsListViewFragment.SelectedId  < position+1) {
                            position = position + 1;
                        }
                        videoId = GetAllByCategoryModel.getInstance().items.get(position).videoId.vedioid;
                        videoTitle = GetAllByCategoryModel.getInstance().items.get(position).snippet.VideoTitle;
                        videoPlayerlink = "https://www.youtube.com/v/"+GetAllByCategoryModel.getInstance().items.get(position).videoId.vedioid;
                        videothumbnail = GetAllByCategoryModel.getInstance().items.get(position).snippet.thumbnails.aDefault.url;
                        videoDuration = DurationModel.getInstance().items.get(position).contentDetails.duration;
                        baseClass.setVideoId(videoId);
                        baseClass.setVideoTitle(videoTitle);
                        baseClass.setVideoPlayerLink(videoPlayerlink);
                        baseClass.setVideoDuraion(videoDuration);
                        baseClass.setVideoThumbnail(videothumbnail);
                        mPlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
                        baseClass.setYOUTUBE(mPlayer);
                        mPlayer.loadVideo(baseClass.getVideoId());
                        mPlayer.play();
                        SetOnCreate();
                    } catch (NullPointerException e) {
                    }
                }
            }
        });
        try {
            index = 0;
            image_details = GetSearchResults();
            image_details_duration = GetSearchResultsDuration();
            leftItemListBaseAdapter = new LeftItemListBaseAdapter(
                    this,R.layout.layout_songs_list_maker, image_details,image_details_duration);
            list.setAdapter(leftItemListBaseAdapter);
            index = SongsListViewFragment.index;
//            footerView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    index++;
//                    try {
//                        footerView.setVisibility(View.GONE);
//                    } catch (NullPointerException e) {
//                    }
//                    obj = new GetByAllCategoryService(activity);
//                    if (baseClass.getDataBy().equalsIgnoreCase("Category")) {
//                        obj.getbycategory(baseClass.getNewCategory(), baseClass.getDuration(), baseClass.getAUTH_TOKEN(), index, true,
//                                new CallBack(activity, "GetAllBy" + baseClass.getCategory() + "More"));
//                    } else {
//                        obj.getbysearch(SongsListViewFragment.Query, baseClass.getAUTH_TOKEN(), index, true,
//                                new CallBack(activity, "GetAllBySearchMore"));
//                    }
//                }
//            });

        } catch (NullPointerException e) {
        }
        youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube);
        try {
            youTubePlayerView.initialize(YouTubeKey, YoutubeBaseActivity.this);
        } catch (ExceptionInInitializerError e) {
        }
        PrepareDropDown();
        if (baseClass.isTabletDevice(this)) {
            doLayoutTablet();
        } else {
            doLayout();
        }
    }

    public void GetAllBySearchMore(Object caller, Object model) {
        GetAllByCategoryModel.getInstance().appendList((GetAllByCategoryModel) model);
        GetDurationDetail();
            image_details = GetSearchResults();
        leftItemListBaseAdapter = new LeftItemListBaseAdapter(
                this,R.layout.layout_songs_list_maker, image_details,image_details_duration);
            leftItemListBaseAdapter.notifyDataSetChanged();
            aq.id(R.id.total_results).text("Total Results: " + GetAllByCategoryModel.getInstance().pageInfo.totalResults);

    }

    public void GetAllByMoviesMore(Object caller, Object model) {
        GetAllByCategoryModel.getInstance().appendList((GetAllByCategoryModel) model);
        GetDurationDetail();
            image_details = GetSearchResults();
        leftItemListBaseAdapter = new LeftItemListBaseAdapter(
                this,R.layout.layout_songs_list_maker, image_details,image_details_duration);
            leftItemListBaseAdapter.notifyDataSetChanged();
            aq.id(R.id.total_results).text("Total Results: " + GetAllByCategoryModel.getInstance().pageInfo.totalResults);

    }

    public void GetAllByCartoonsMore(Object caller, Object model) {
        GetAllByCategoryModel.getInstance().appendList((GetAllByCategoryModel) model);
        GetDurationDetail();
            image_details = GetSearchResults();
        leftItemListBaseAdapter = new LeftItemListBaseAdapter(
                this,R.layout.layout_songs_list_maker, image_details,image_details_duration);
            leftItemListBaseAdapter.notifyDataSetChanged();
            aq.id(R.id.total_results).text("Total Results: " + GetAllByCategoryModel.getInstance().pageInfo.totalResults);

    }

    public void GetAllByMusicMore(Object caller, Object model) {
        GetAllByCategoryModel.getInstance().appendList((GetAllByCategoryModel) model);
        GetDurationDetail();
            image_details = GetSearchResults();
        leftItemListBaseAdapter = new LeftItemListBaseAdapter(
                this,R.layout.layout_songs_list_maker, image_details,image_details_duration);
            leftItemListBaseAdapter.notifyDataSetChanged();
            aq.id(R.id.total_results).text("Total Results: " + GetAllByCategoryModel.getInstance().pageInfo.totalResults);

    }

    public void GetAllByDocumentariesMore(Object caller, Object model) {
        GetAllByCategoryModel.getInstance().appendList((GetAllByCategoryModel) model);
        GetDurationDetail();
            image_details = GetSearchResults();
        leftItemListBaseAdapter = new LeftItemListBaseAdapter(
                this,R.layout.layout_songs_list_maker, image_details,image_details_duration);
            leftItemListBaseAdapter.notifyDataSetChanged();
            aq.id(R.id.total_results).text("Total Results: " + GetAllByCategoryModel.getInstance().pageInfo.totalResults);

    }

    public void adsCallback(Object caller, Object model){
        AdsMessage.getInstance().setList((AdsMessage) model);
        if(!AdsMessage.getInstance().message.isEmpty()){
            aq.id(R.id.ads).visible().text(AdsMessage.getInstance().message);
        }
    }
    public void GetDurationDetail ()
    {
        int count = baseClass.getDurationCounter();

        for (int loop=count;loop<count + 50;loop++) {
                obj.getduration(GetAllByCategoryModel.getInstance().items.get(loop).videoId.vedioid, false,
                        new CallBack(YoutubeBaseActivity.this, "GetDuration"));
            }
        baseClass.setDurationCounter(count + 50);

    }
    public void GetDuration(Object caller, Object model) {

        if( DurationModel.getInstance().items.size()==0) {
            DurationModel.getInstance().setList((DurationModel) model);
        }else
        {
            DurationModel.getInstance().appendList((DurationModel) model);
        }
        image_details_duration = GetSearchResultsDuration();
        leftItemListBaseAdapter = new LeftItemListBaseAdapter(
                this,R.layout.layout_songs_list_maker, image_details,image_details_duration);
        leftItemListBaseAdapter.notifyDataSetInvalidated();
    }
    static int p1;
    private static ArrayList<ItemDetailsDuration> GetSearchResultsDuration() {

        ArrayList<ItemDetailsDuration> results = new ArrayList<ItemDetailsDuration>();
        for (p1 = 0; p1 < DurationModel.getInstance().items.size(); p1++) {
            if (SongsListViewFragment.SelectedId != p1) {
                    ItemDetailsDuration item_details = new ItemDetailsDuration();
                    item_details.setDuration(getTimeFromString(DurationModel.getInstance().items.get(p1).contentDetails.duration));
                    results.add(item_details);
            }
        }
        return results;
    }
    static int p;
    private static ArrayList<ItemDetails> GetSearchResults() {

        ArrayList<ItemDetails> results = new ArrayList<ItemDetails>();
        for (p = 0; p < GetAllByCategoryModel.getInstance().items.size(); p++) {
            if (SongsListViewFragment.SelectedId != p) {
                ItemDetails item_details = new ItemDetails();
                item_details.setName(GetAllByCategoryModel.getInstance().items.get(p).snippet.VideoTitle);
                item_details.setImage(GetAllByCategoryModel.getInstance().items.get(p).snippet.thumbnails.aDefault.url);
                results.add(item_details);
            }
        }
        return results;
    }

    private void PrepareDropDown() {
        List<String> list = new ArrayList<String>();
        list.clear();
        list.add("Add To playlist");
        list.add("Add To Favorites");
        popUpContents = new String[list.size()];
        list.toArray(popUpContents);
        popupWindow = popupWindow();

    }
    private static String getTimeFromString(String duration) {
        // TODO Auto-generated method stub
        String time = "";
        boolean hourexists = false, minutesexists = false, secondsexists = false;
        if (duration.contains("H"))
            hourexists = true;
        if (duration.contains("M"))
            minutesexists = true;
        if (duration.contains("S"))
            secondsexists = true;
        if (hourexists) {
            String hour = "";
            hour = duration.substring(duration.indexOf("T") + 1,
                    duration.indexOf("H"));
            if (hour.length() == 1)
                hour = "0" + hour;
            time += hour + ":";
        }
        if (minutesexists) {
            String minutes = "";
            if (hourexists)
                minutes = duration.substring(duration.indexOf("H") + 1,
                        duration.indexOf("M"));
            else
                minutes = duration.substring(duration.indexOf("T") + 1,
                        duration.indexOf("M"));
            if (minutes.length() == 1)
                minutes = "0" + minutes;
            time += minutes + ":";
        } else {
            time += "00:";
        }
        if (secondsexists) {
            String seconds = "";
            if (hourexists) {
                if (minutesexists)
                    seconds = duration.substring(duration.indexOf("M") + 1,
                            duration.indexOf("S"));
                else
                    seconds = duration.substring(duration.indexOf("H") + 1,
                            duration.indexOf("S"));
            } else if (minutesexists)
                seconds = duration.substring(duration.indexOf("M") + 1,
                        duration.indexOf("S"));
            else
                seconds = duration.substring(duration.indexOf("T") + 1,
                        duration.indexOf("S"));
            if (seconds.length() == 1)
                seconds = "0" + seconds;
            time += seconds;
        }
        return time;
    }
    private PopupWindow popupWindow() {
        PopupWindow popupWindow = new PopupWindow(YoutubeBaseActivity.this);
        ListView listView = new ListView(YoutubeBaseActivity.this);
        listView.setAdapter(Adapter(popUpContents));
        listView.setOnItemClickListener(new DropdownOnItemClickListenerActivity());
        popupWindow.setFocusable(true);
        popupWindow.setWidth(baseClass.getDpValue(170, YoutubeBaseActivity.this));
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(
                android.R.drawable.dialog_holo_light_frame));
        popupWindow.setContentView(listView);
        return popupWindow;
    }

    private class DropdownOnItemClickListenerActivity implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
            popupWindow.dismiss();
            if(position==0) {
                try {
                    AddedSongToPlaylist();
                    Crouton.makeText(activity,
                            "Video Added to Playlist.",
                            Style.CONFIRM).show();
                    Log.e("ok1","ok");
                } catch (Exception e) {
                    Crouton.makeText(
                            activity,
                            "Video Already Added To Playlist.",
                            Style.ALERT).show();
                    Log.e("ok2","ok");
                }
            }
            if(position==1)
                try {
                    AddedSongToFavorite();
                    Crouton.makeText(
                            activity,
                            "Video Added to Favorites.",
                            Style.CONFIRM).show();
                    Log.e("ok3", "ok");
                } catch (Exception e) {
                    Crouton.makeText(
                            activity,
                            "Video Already Added To Favorites.",
                            Style.ALERT).show();
                    Log.e("ok4", "ok");
                }
        }
    }

    private ArrayAdapter<String> Adapter(String Array[]) {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, Array) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                String text = getItem(position);
                TextView listItem = new TextView(activity);
                listItem.setText(text);
                listItem.setTag(position);
                listItem.setBackgroundColor(Color.GRAY);
                listItem.setTextColor(Color.WHITE);
                listItem.setPadding(10, 10, 10, 10);
                return listItem;
            }
        };
        return adapter;
    }

    private void SetOnCreate() {
        aq.id(R.id.playing_title).text(baseClass.getVideoTitle());
    }

    private boolean isPlaylistVideoIdExist() {
        status = 0;
        List<Playlist> temp = new ArrayList<Playlist>();
        try{
            DaoMaster.DevOpenHelper ex_database_helper_obj = new DaoMaster.DevOpenHelper(
                    activity, "javantube.sqlite", null);
            SQLiteDatabase ex_db = ex_database_helper_obj.getReadableDatabase();
            DaoMaster daoMaster = new DaoMaster(ex_db);
            DaoSession daoSession = daoMaster.newSession();

            PlaylistDao playlistDao = daoSession.getPlaylistDao();
            temp = playlistDao.queryBuilder().list();
            daoSession.clear();
            ex_db.close();
            ex_database_helper_obj.close();
        }catch (Exception e){
        }

        for (int loop = 0; loop < temp.size(); loop++) {
            if (temp.get(loop).getVideoId().equalsIgnoreCase(baseClass.getVideoId())) {
                status = 1;
                break;
            } else {
                status = 0;
            }
        }
        if (status == 1)
            return true;
        else
            return false;
    }

    private boolean isFavoriteVideoIdExist() {
        int status = 0;
        List<Favorite> temp = new ArrayList<Favorite>();
        try {
            DaoMaster.DevOpenHelper ex_database_helper_obj = new DaoMaster.DevOpenHelper(
                    activity, "javantube.sqlite", null);
            SQLiteDatabase ex_db = ex_database_helper_obj.getReadableDatabase();
            DaoMaster daoMaster = new DaoMaster(ex_db);
            DaoSession daoSession = daoMaster.newSession();

            FavoriteDao favoriteDao = daoSession.getFavoriteDao();
            temp = favoriteDao.queryBuilder().list();
            daoSession.clear();
            ex_db.close();
            ex_database_helper_obj.close();
        }catch (Exception e){
        }

        for (int loop = 0; loop < temp.size(); loop++) {
            if (temp.get(loop).getVideoId().equalsIgnoreCase(baseClass.getVideoId())) {
                status = 1;
                break;
            } else {
                status = 0;
            }
        }
        if (status == 1)
            return true;
        else
            return false;
    }

    public void AddedSongToPlaylist() {
      //  try {
            DaoMaster.DevOpenHelper ex_database_helper_obj = new DaoMaster.DevOpenHelper(
                    activity, "javantube.sqlite", null);
            SQLiteDatabase ex_db = ex_database_helper_obj
                    .getWritableDatabase();
            DaoMaster daoMaster = new DaoMaster(ex_db);
            DaoSession daoSession = daoMaster.newSession();

            PlaylistDao playlistDao = daoSession.getPlaylistDao();
            Playlist playlist = new Playlist(baseClass.getVideoId(), baseClass.getVideoTitle(), baseClass.getVideoDurtion(),
                    baseClass.getVideoViewer(), baseClass.getVideoUploadDate(), baseClass.getVideoAuthor(), baseClass.getVideoThumbnail());
            playlistDao.insert(playlist);
            daoSession.clear();
            ex_db.close();
            ex_database_helper_obj.close();
//        }catch (Exception e){
//        }
    }

    public void DeletedSongFromPlaylist(){
        DaoMaster.DevOpenHelper ex_database_helper_obj = new DaoMaster.DevOpenHelper(
                activity, "javantube.sqlite", null);
        SQLiteDatabase ex_db = ex_database_helper_obj
                .getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(ex_db);
        DaoSession daoSession = daoMaster.newSession();
        PlaylistDao playlistDao = daoSession.getPlaylistDao();
        playlistDao.deleteByKey(baseClass.getVideoId());
        daoSession.clear();
        ex_db.close();
        ex_database_helper_obj.close();
    }

    public void AddedSongToFavorite() {
       // try {
            DaoMaster.DevOpenHelper ex_database_helper_obj = new DaoMaster.DevOpenHelper(
                    activity, "javantube.sqlite", null);
            SQLiteDatabase ex_db = ex_database_helper_obj
                    .getWritableDatabase();
            DaoMaster daoMaster = new DaoMaster(ex_db);
            DaoSession daoSession = daoMaster.newSession();
            FavoriteDao favoriteDao = daoSession.getFavoriteDao();
            Favorite favorite = new Favorite(baseClass.getVideoId(), baseClass.getVideoTitle(), baseClass.getVideoDurtion(),
                    baseClass.getVideoViewer(), baseClass.getVideoUploadDate(), baseClass.getVideoAuthor(), baseClass.getVideoThumbnail());
            favoriteDao.insert(favorite);
            daoSession.clear();
            Log.e("id", baseClass.getVideoId());
            ex_db.close();
            ex_database_helper_obj.close();
//        }catch (Exception e){
//            Crouton.makeText(this, "Something went wrong. Try again!", Style.INFO).show();
//        }
    }

    public void DeletedSongFromFavorite() {
        Log.e("id",baseClass.getVideoId());
       // try {
            DaoMaster.DevOpenHelper ex_database_helper_obj = new DaoMaster.DevOpenHelper(
                    activity, "javantube.sqlite", null);
            SQLiteDatabase ex_db = ex_database_helper_obj
                    .getWritableDatabase();
            DaoMaster daoMaster = new DaoMaster(ex_db);
            DaoSession daoSession = daoMaster.newSession();
            FavoriteDao favoriteDao = daoSession.getFavoriteDao();
            favoriteDao.deleteByKey(baseClass.getVideoId());

            daoSession.clear();
            ex_db.close();
            ex_database_helper_obj.close();
//        }catch (Exception e){
//            Crouton.makeText(this, "Something went wrong. Try again!", Style.INFO).show();
//        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (baseClass.isTabletDevice(this)) {
            doLayoutTablet();
        } else {
            doLayout();
        }
        baseClass.setYOUTUBE(mPlayer);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPlayer = null;
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(BaseActivity.receiver);
        BaseActivity.receiver = null;
        super.onDestroy();
        mPlayer = null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPlayer = baseClass.getYOUTUBE();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mPlayer = baseClass.getYOUTUBE();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);
        youTubePlayer.setPlaybackEventListener(playbackEventListener);
        youTubePlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
        youTubePlayer.setOnFullscreenListener(this);
        if (!wasRestored) {
            if (youTubePlayer != null) {
                MyPlaylistEventListener myPlaylistEventListener = new MyPlaylistEventListener();
                if (baseClass.getIsFromFavorites().equalsIgnoreCase("YES")) {
                    try {
                        youTubePlayer.setPlaylistEventListener(myPlaylistEventListener);
                        youTubePlayer.loadVideos(baseClass.getArrayList(),baseClass.getPosition(),1);
                        youTubePlayer.play();
                        baseClass.setYOUTUBE(youTubePlayer);
                    } catch (ExceptionInInitializerError e) {
                    }
                } else {
                    try {
                        youTubePlayer.loadVideo(baseClass.getVideoId());
                        youTubePlayer.play();
                        baseClass.setYOUTUBE(youTubePlayer);
                    } catch (ExceptionInInitializerError e) {
                    }
                }
            }
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
        if (result.isUserRecoverableError()) {
            result.getErrorDialog(this, 1).show();
            Toast.makeText(getApplicationContext(),
                    YouTubePlayer.ErrorReason.INTERNAL_ERROR.toString(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return youTubePlayerView;
    }


    @Override
    public void onFullscreen(boolean isFullscreen) {
        fullscreen = isFullscreen;
        if (baseClass.isTabletDevice(this)) {
            doLayoutTablet();
        } else {
            doLayout();
        }
    }

    private void doLayout() {
        LinearLayout.LayoutParams playerParams =
                (LinearLayout.LayoutParams) youTubePlayerView.getLayoutParams();
        if (fullscreen) {
            playerParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            playerParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            otherView.setVisibility(View.GONE);
            aq.id(R.id.layout).visibility(View.GONE);
            listRelative.setVisibility(View.GONE);
        } else {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                playerParams.width = MATCH_PARENT;
                playerParams.height = MATCH_PARENT;
                aq.id(R.id.layout).visibility(View.GONE);
                otherView.setVisibility(View.GONE);
                listRelative.setVisibility(View.GONE);
            } else {
                aq.id(R.id.layout).visibility(View.VISIBLE);
                if (baseClass.getIsFromFavorites().equalsIgnoreCase("YES")) {
                    aq.id(R.id.add_playlist).visibility(View.INVISIBLE);
                    aq.id(R.id.favorite).visibility(View.INVISIBLE);
                    playerParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    playerParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    otherView.setVisibility(View.GONE);
                    listRelative.setVisibility(View.GONE);
                } else {
                    aq.id(R.id.add_playlist).visibility(View.VISIBLE);
                    aq.id(R.id.favorite).visibility(View.VISIBLE);
                    playerParams.width = MATCH_PARENT;
                    playerParams.height = WRAP_CONTENT;
                    otherView.setVisibility(View.VISIBLE);
                    listRelative.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void doLayoutTablet() {
        LinearLayout.LayoutParams playerParams =
                (LinearLayout.LayoutParams) youTubePlayerView.getLayoutParams();
        LinearLayout.LayoutParams layoutParams =
                (LinearLayout.LayoutParams) listLinear.getLayoutParams();
        if (fullscreen) {
            layoutParams.weight = 0;
            playerParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            playerParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            otherView.setVisibility(View.GONE);
            aq.id(R.id.layout).visibility(View.GONE);
            listLinear.setVisibility(View.GONE);

        } else {
            aq.id(R.id.layout).visibility(View.VISIBLE);
            if (baseClass.getIsFromFavorites().equalsIgnoreCase("YES")) {
                aq.id(R.id.add_playlist).visibility(View.INVISIBLE);
                aq.id(R.id.favorite).visibility(View.INVISIBLE);
                playerParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                playerParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                layoutParams.weight = 0;
                otherView.setVisibility(View.GONE);
                listLinear.setVisibility(View.GONE);
            } else {
                aq.id(R.id.add_playlist).visibility(View.VISIBLE);
                aq.id(R.id.favorite).visibility(View.VISIBLE);
                playerParams.width = MATCH_PARENT;
                playerParams.height = WRAP_CONTENT;
                playerParams.weight = 1;
                layoutParams.width = MATCH_PARENT;
                layoutParams.height = MATCH_PARENT;
                layoutParams.weight = 1;
                otherView.setVisibility(View.VISIBLE);
                listLinear.setVisibility(View.VISIBLE);
            }
        }
    }

    private class MyPlaylistEventListener implements YouTubePlayer.PlaylistEventListener {

        @Override
        public void onNext() {
            // TODO Auto-generated method stub
        }

        @Override
        public void onPlaylistEnded() {

        }

        @Override
        public void onPrevious() {
            // TODO Auto-generated method stub

        }
    }

    private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {

        @Override
        public void onBuffering(boolean arg0) {

        }

        @Override
        public void onPaused() {

        }

        @Override
        public void onPlaying() {

        }

        @Override
        public void onSeekTo(int arg0) {

        }

        @Override
        public void onStopped() {
        }


    };

    private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {

        @Override
        public void onAdStarted() {

        }

        @Override
        public void onError(YouTubePlayer.ErrorReason arg0) {

        }

        @Override
        public void onLoaded(String arg0) {

        }

        @Override
        public void onLoading() {
        }

        @Override
        public void onVideoEnded() {

        }

        @Override
        public void onVideoStarted() {

        }
    };

    @Override
    public void onBackPressed() {
        if (baseClass.isTabletDevice(this) && fullscreen == true) {
            fullscreen = false;
            doLayoutTablet();
        }
        if (baseClass.isTabletDevice(this) && fullscreen == false) {
            super.onBackPressed();
        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (!baseClass.isTabletDevice(this)) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                doLayout();
            }
        }else {
            super.onBackPressed();
            baseClass.setYOUTUBE(null);
        }
    }
    public class MyCount extends CountDownTimer {

        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            aq.id(R.id.animation_layout).visibility(View.GONE);
            aq.id(R.id.youtube).visibility(View.VISIBLE);
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }
    }
}