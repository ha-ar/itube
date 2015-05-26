package co.vector.itube;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import DB.DaoMaster;
import DB.DaoSession;
import DB.Favorite;
import DB.FavoriteDao;
import DB.Playlist;
import DB.PlaylistDao;
import Models.DurationModel;
import Models.GetAllByCategoryModel;
import Models.VideoItem;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import services.CallBack;
import services.GetByAllCategoryService;

/**
 * Created by android on 11/17/14.
 */
public class SongsListViewFragment extends Fragment {
    BaseClass baseClass;List<String> list;int total_count;
    AQuery aq;static PopupWindow popupWindow;
    public static SongListViewAdapter songListViewAdapter;
    View rootView;static  int SelectedId;  String popUpContents[];static String Query;
    static GridView list_songs;static int index;
    GetByAllCategoryService obj;
    ArrayList<ItemDetailsDuration> image_details_duration;
    ArrayList<ItemDetails> image_details;

    public SongsListViewFragment() {
    }

    public SongsListViewFragment newinstance() {
        SongsListViewFragment fragment = new SongsListViewFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        index =1;
        rootView = inflater.inflate(R.layout.layout_songslistview,
                container, false);
        list_songs = (GridView) rootView.findViewById(R.id.listView);
        aq = new AQuery(getActivity(),rootView);
        //TODO will implement it later when all things will be fixed
        list_songs.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {

                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                index=page;
                if(baseClass.getDataBy().equalsIgnoreCase("Category")) {
                    obj.getbycategory(baseClass.getNewCategory(), baseClass.getDuration(),
                            GetAllByCategoryModel.getInstance().nextPageToken,  true,
                            new CallBack(SongsListViewFragment.this, "GetAllBy" + baseClass.getCategory() + "More"));
                }
                else
                {
                    obj.getbysearch(Query, GetAllByCategoryModel.getInstance().nextPageToken, true,
                            new CallBack(SongsListViewFragment.this, "GetAllBySearchMore"));
                }
                // or customLoadMoreDataFromApi(totalItemsCount);
            }
        });
        //footerView = (LinearLayout) rootView.findViewById(R.id.footer);
        if(!BaseClass.isTabletDevice(getActivity()))
        {
            list_songs.setNumColumns(2);
        }
        else
            list_songs.setNumColumns(5);
        baseClass = ((BaseClass)getActivity().getApplicationContext());
        BaseActivity.language.setText("Browse "+baseClass.getCategory());

        BaseActivity.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                index=1;
                baseClass.setDataBy("Search");
                try {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(BaseActivity.searchView.getWindowToken(), 0);
                Query = query;
                obj = new GetByAllCategoryService(getActivity());
                obj.getbysearch(query,  GetAllByCategoryModel.getInstance().nextPageToken, true,
                        new CallBack(SongsListViewFragment.this, "GetAllBySearch"));
                }catch (NullPointerException e){}
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() != 0) {
                    try{
                        songListViewAdapter.filter(newText);
                    }catch (NullPointerException npe){}
                }
                else
                {
                    try {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(BaseActivity.searchView.getWindowToken(), 0);
                    }catch (NullPointerException e){}
                }
                return false;
            }
        });
        PrepareDropDown();

        obj = new GetByAllCategoryService(getActivity());
        obj.getbycategory(baseClass.getNewCategory(),baseClass.getDuration(), "",true,
                new CallBack(SongsListViewFragment.this, "GetAllBy" + baseClass.getCategory()));

//        footerView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                index++;
//                try{
//                    footerView.setVisibility(View.GONE);
//                }catch (NullPointerException e){}
//                obj = new GetByAllCategoryService(getActivity());
//                if(baseClass.getDataBy().equalsIgnoreCase("Category")) {
//                    obj.getbycategory(baseClass.getNewCategory(), baseClass.getDuration(), baseClass.getAUTH_TOKEN(), index, true,
//                            new CallBack(SongsListViewFragment.this, "GetAllBy" + baseClass.getCategory() + "More"));
//                }
//                else
//                {
//                    obj.getbysearch(Query, baseClass.getAUTH_TOKEN(), index, true,
//                            new CallBack(SongsListViewFragment.this, "GetAllBySearchMore"));
//                }
//
//            }
//        });
        list_songs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getActivity(),YoutubeBaseActivity.class));
                baseClass.setVideoId(GetAllByCategoryModel.getInstance().items.get(position).videoId.vedioid);
                baseClass.setVideoTitle(GetAllByCategoryModel.getInstance().items.get(position).snippet.VideoTitle);
                baseClass.setVideoThumbnail(GetAllByCategoryModel.getInstance().items.get(position).snippet.thumbnails.aDefault.url);
                baseClass.setVideoPlayerLink("https://www.youtube.com/v/"+GetAllByCategoryModel.getInstance().items.get(position).videoId.vedioid);
                baseClass.setVideoDuraion(DurationModel.getInstance().items.get(position).contentDetails.duration);
                SelectedId = position;
            }
        });
        return rootView;
    }
    public void GetAllBySearch(Object caller, Object model) {
        GetAllByCategoryModel.getInstance().setList((GetAllByCategoryModel) model);
        GetDurationDetail();
        if (GetAllByCategoryModel.getInstance().items.size()==0) {
            Log.e("Index",String.valueOf(index));
            image_details = GetSearchResults();
//                songListViewAdapter = new SongListViewAdapter(
//                        getActivity(),R.layout.layout_songs_list_maker, image_details,image_details_duration);
            list_songs.setAdapter(songListViewAdapter);
            if(GetAllByCategoryModel.getInstance().pageInfo.totalResults.equals("1000000"))
            aq.id(R.id.total_results).text("Total Results: More Than 5 Mil");
            else
            aq.id(R.id.total_results).text("Total Results: Less Than 5 Mil");

        } else {
            aq.id(R.id.textView).visibility(View.VISIBLE).text("No "+baseClass.getCategory()+" record found.");
            Crouton.makeText(getActivity(), "Check internet settings or server not responding.", Style.ALERT).show();
        }
    }
    public void GetAllBySearchMore(Object caller, Object model) {
        GetAllByCategoryModel.getInstance().appendList((GetAllByCategoryModel) model);
        GetDurationDetail();
            Log.e("Index",String.valueOf(index));
            image_details = GetSearchResults();
                songListViewAdapter = new SongListViewAdapter(
                        getActivity(),R.layout.layout_songs_list_maker, image_details,image_details_duration);
            songListViewAdapter.notifyDataSetChanged();
            if(GetAllByCategoryModel.getInstance().pageInfo.totalResults.equals("1000000"))
                aq.id(R.id.total_results).text("Total Results: More Than 5 Mil");
            else
                aq.id(R.id.total_results).text("Total Results: Less Than 5 Mil");
    }
    public void GetAllByMovies(Object caller, Object model) {
        GetAllByCategoryModel.getInstance().setList((GetAllByCategoryModel) model);

        //if (GetAllByCategoryModel.getInstance().items.size()==0) {
                Log.e("Index",String.valueOf(index));
                image_details = GetSearchResults();
                songListViewAdapter = new SongListViewAdapter(
                        getActivity(),R.layout.layout_songs_list_maker, image_details,image_details_duration);
                list_songs.setAdapter(songListViewAdapter);
            GetDurationDetail();
            if(GetAllByCategoryModel.getInstance().pageInfo.totalResults.equals("1000000"))
                aq.id(R.id.total_results).text("Total Results: More Than 5 Mil");
            else
                aq.id(R.id.total_results).text("Total Results: Less Than 5 Mil");
//        } else {
//            aq.id(R.id.textView).visibility(View.VISIBLE).text("No "+baseClass.getCategory()+" record found.");
//            Crouton.makeText(getActivity(), "Check internet settings or server not responding.", Style.ALERT).show();
//        }
    }
    public void GetAllByMoviesMore(Object caller, Object model) {
        GetAllByCategoryModel.getInstance().appendList((GetAllByCategoryModel) model);
        GetDurationDetail();
                Log.e("Index",String.valueOf(index));
                image_details = GetSearchResults();
                songListViewAdapter = new SongListViewAdapter(
                        getActivity(),R.layout.layout_songs_list_maker, image_details,image_details_duration);
                songListViewAdapter.notifyDataSetChanged();

            if(GetAllByCategoryModel.getInstance().pageInfo.totalResults.equals("1000000"))
                aq.id(R.id.total_results).text("Total Results: More Than 5 Mil");
            else
                aq.id(R.id.total_results).text("Total Results: Less Than 5 Mil");

    }
    private  void PrepareDropDown()
    {
        list = new ArrayList<String>();
        list.add("Add To playlist");
        list.add("Add To Favorites");
        popUpContents = new String[list.size()];
        list.toArray(popUpContents);
        popupWindow = popupWindow();
    }
    public PopupWindow popupWindow() {
        PopupWindow popupWindow = new PopupWindow(getActivity());
        ListView listView = new ListView(getActivity());
        listView.setAdapter(Adapter(popUpContents));
        listView.setOnItemClickListener(new DropdownOnItemClickListener());
        popupWindow.setFocusable(true);
        popupWindow.setWidth( baseClass.getDpValue(170,getActivity()));
        popupWindow.setBackgroundDrawable(getResources().getDrawable(
                android.R.drawable.dialog_holo_light_frame));
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(listView);

        return popupWindow;
    }
    private ArrayAdapter<String> Adapter(String Array[]) {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, Array) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                String text = getItem(position);
                TextView listItem = new TextView(getActivity());
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
    public void GetAllByCartoons(Object caller, Object model) {
        GetAllByCategoryModel.getInstance().setList((GetAllByCategoryModel) model);
        GetDurationDetail();
            image_details = GetSearchResults();
                songListViewAdapter = new SongListViewAdapter(
                        getActivity(),R.layout.layout_songs_list_maker, image_details,image_details_duration);
            list_songs.setAdapter(songListViewAdapter);
            if(GetAllByCategoryModel.getInstance().pageInfo.totalResults.equals("1000000"))
                aq.id(R.id.total_results).text("Total Results: More Than 5 Mil");
            else
                aq.id(R.id.total_results).text("Total Results: Less Than 5 Mil");

    }
    public void GetAllByCartoonsMore(Object caller, Object model) {
        GetAllByCategoryModel.getInstance().appendList((GetAllByCategoryModel) model);
        GetDurationDetail();
            Log.e("Index",String.valueOf(index));
        image_details = GetSearchResults();
        songListViewAdapter = new SongListViewAdapter(
                getActivity(),R.layout.layout_songs_list_maker, image_details,image_details_duration);
            songListViewAdapter.notifyDataSetChanged();
            if(GetAllByCategoryModel.getInstance().pageInfo.totalResults.equals("1000000"))
                aq.id(R.id.total_results).text("Total Results: More Than 5 Mil");
            else
                aq.id(R.id.total_results).text("Total Results: Less Than 5 Mil");

    }
    public void GetAllByMusic(Object caller, Object model) {
        GetAllByCategoryModel.getInstance().setList((GetAllByCategoryModel) model);
        GetDurationDetail();
            image_details = GetSearchResults();
            songListViewAdapter = new SongListViewAdapter(
                    getActivity(),R.layout.layout_songs_list_maker, image_details,image_details_duration);
            list_songs.setAdapter(songListViewAdapter);
            if(GetAllByCategoryModel.getInstance().pageInfo.totalResults.equals("1000000"))
                aq.id(R.id.total_results).text("Total Results: More Than 5 Mil");
            else
                aq.id(R.id.total_results).text("Total Results: Less Than 5 Mil");

    }
    public void GetAllByMusicMore(Object caller, Object model) {
        GetAllByCategoryModel.getInstance().appendList((GetAllByCategoryModel) model);
        GetDurationDetail();
        image_details = GetSearchResults();
        songListViewAdapter = new SongListViewAdapter(
                getActivity(),R.layout.layout_songs_list_maker, image_details,image_details_duration);
            songListViewAdapter.notifyDataSetChanged();
            if(GetAllByCategoryModel.getInstance().pageInfo.totalResults.equals("1000000"))
                aq.id(R.id.total_results).text("Total Results: More Than 5 Mil");
            else
                aq.id(R.id.total_results).text("Total Results: Less Than 5 Mil");

    }
    public void GetAllByDocumentaries(Object caller, Object model) {
        GetAllByCategoryModel.getInstance().setList((GetAllByCategoryModel) model);
        GetDurationDetail();
            image_details = GetSearchResults();
            songListViewAdapter = new SongListViewAdapter(
                    getActivity(),R.layout.layout_songs_list_maker, image_details,image_details_duration);
            list_songs.setAdapter(songListViewAdapter);
            if(GetAllByCategoryModel.getInstance().pageInfo.totalResults.equals("1000000"))
                aq.id(R.id.total_results).text("Total Results: More Than 5 Mil");
            else
                aq.id(R.id.total_results).text("Total Results: Less Than 5 Mil");

    }
    public void GetAllByDocumentariesMore(Object caller, Object model) {
        GetAllByCategoryModel.getInstance().appendList((GetAllByCategoryModel) model);
        GetDurationDetail();
        image_details = GetSearchResults();
        songListViewAdapter = new SongListViewAdapter(
                getActivity(),R.layout.layout_songs_list_maker, image_details,image_details_duration);
            songListViewAdapter.notifyDataSetChanged();
            if(GetAllByCategoryModel.getInstance().pageInfo.totalResults.equals("1000000"))
                aq.id(R.id.total_results).text("Total Results: More Than 5 Mil");
            else
                aq.id(R.id.total_results).text("Total Results: Less Than 5 Mil");

    }
    public void GetDurationDetail ()
    {
        int count = baseClass.getDurationCounter();

            for (int loop=count;loop<count + 50;loop++) {
                    obj.getduration(GetAllByCategoryModel.getInstance().items.get(loop).videoId.vedioid, false,
                            new CallBack(SongsListViewFragment.this, "GetDuration"));
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
        songListViewAdapter = new SongListViewAdapter(
                getActivity(),R.layout.layout_songs_list_maker, image_details,image_details_duration);
        songListViewAdapter.notifyDataSetInvalidated();
    }
    static int p1;
    private static ArrayList<ItemDetailsDuration> GetSearchResultsDuration() {

        ArrayList<ItemDetailsDuration> results = new ArrayList<ItemDetailsDuration>();
        for (p1 = 0; p1 < DurationModel.getInstance().items.size(); p1++) {
            ItemDetailsDuration item_details = new ItemDetailsDuration();
            item_details.setDuration(getTimeFromString(DurationModel.getInstance().items.get(p1).contentDetails.duration));
            Log.e("Time"+p1,getTimeFromString(DurationModel.getInstance().items.get(p1).contentDetails.duration));
            results.add(item_details);
        }
        return results;
    }
    static int p;
    private static ArrayList<ItemDetails> GetSearchResults() {
        ArrayList<ItemDetails> results = new ArrayList<ItemDetails>();
        for (p = 0; p < GetAllByCategoryModel.getInstance().items.size(); p++) {
            ItemDetails item_details = new ItemDetails();
                item_details.setId(GetAllByCategoryModel.getInstance().items.get(p).videoId.vedioid);

            item_details.setName(GetAllByCategoryModel.getInstance().items.get(p).snippet.VideoTitle);
            try {
                item_details.setduration(getTimeFromString(DurationModel.getInstance().items.get(p).contentDetails.duration));
            }catch (IndexOutOfBoundsException e){}
            item_details.setImage(GetAllByCategoryModel.getInstance().items.get(p).snippet.thumbnails.aDefault.url);
            results.add(item_details);
        }
        return results;
    }
    public class DropdownOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
           popupWindow.dismiss();
            if(position==0) {
                try {
                    AddedSongToPlaylist();
                    Crouton.makeText(
                            getActivity(),
                            "Video Added to Playlist.",
                            Style.CONFIRM).show();
                } catch (Exception e) {
                    Crouton.makeText(
                            getActivity(),
                            "Video Already Added To Playlist.",
                            Style.ALERT).show();
                }
            }
               if(position==1)
                {
                    try {
                        AddedSongToFavorite();
                        Crouton.makeText(
                                getActivity(),
                                "Video Added to Favorites.",
                                Style.CONFIRM).show();
                    }catch (Exception e){
                        Crouton.makeText(
                                getActivity(),
                                "Video Already Added To Favorites.",
                                Style.ALERT).show();
                    }
                }
        }
    }
    public void AddedSongToPlaylist(){
        DaoMaster.DevOpenHelper ex_database_helper_obj = new DaoMaster.DevOpenHelper(
                getActivity(), "javantube.sqlite", null);
        SQLiteDatabase ex_db = ex_database_helper_obj
                .getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(ex_db);
        DaoSession daoSession = daoMaster.newSession();

        PlaylistDao playlistDao = daoSession.getPlaylistDao();
        Playlist playlist = new Playlist(baseClass.getVideoId(),baseClass.getVideoTitle(),baseClass.getVideoDurtion(),
                baseClass.getVideoViewer(),baseClass.getVideoUploadDate(),baseClass.getVideoAuthor(),baseClass.getVideoThumbnail());
        playlistDao.insert(playlist);
        daoSession.clear();
        ex_db.close();
        ex_database_helper_obj.close();
    }
    public void AddedSongToFavorite(){
        DaoMaster.DevOpenHelper ex_database_helper_obj = new DaoMaster.DevOpenHelper(
                getActivity(), "javantube.sqlite", null);
        SQLiteDatabase ex_db = ex_database_helper_obj
                .getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(ex_db);
        DaoSession daoSession = daoMaster.newSession();
        FavoriteDao favoriteDao = daoSession.getFavoriteDao();
        Favorite favorite = new Favorite(baseClass.getVideoId(),baseClass.getVideoTitle(),baseClass.getVideoDurtion(),
                baseClass.getVideoViewer(),baseClass.getVideoUploadDate(),baseClass.getVideoAuthor(),baseClass.getVideoThumbnail());
        favoriteDao.insert(favorite);
        daoSession.clear();
        ex_db.close();
        ex_database_helper_obj.close();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        BaseActivity.language.setText("Select Category");
    }

//    public void search(String keywords){
//
//        query.setQ(keywords);
//        try{
//            SearchListResponse response = query.execute();
//            Log.e("response", response.toPrettyString());
//            final List<SearchResult> results = response.getItems();
//
//            List<VideoItem> items = new ArrayList<VideoItem>();
//            objModel = GetAllByCategoryModel.getInstance();
//            objModel.category.max_result_count = response.getPageInfo().getTotalResults();
//
//            for(int loop=0;loop< results.size();loop++){
//                video = new GetAllByCategoryModel.Videos();
//                video.title = results.get(loop).getSnippet().getTitle();
//                video.description = results.get(loop).getSnippet().getDescription();
//                video.thumbnails.add(results.get(loop).getSnippet().getThumbnails().getDefault().getUrl());
//                video.player_url = results.get(loop).getId().getVideoId();
//                video.uploaded_at = results.get(loop).getSnippet().getPublishedAt().toStringRfc3339();
//
//                String url = "https://www.googleapis.com/youtube/v3/videos?id="+video.player_url+"&part=contentDetails&key=AIzaSyBTarcxurznZvnN9kZW9ekWe7JyQsLSLCo";
//                aq.ajax(url, String.class,
//                        new AjaxCallback<String>() {
//                            @Override
//                            public void callback(String url, String json,
//                                                 AjaxStatus status) {
//                                if (json != null) {
//                                    Gson gson = new Gson();
//                                    DurationModel obj1 = new DurationModel();
//                                    obj1 = gson.fromJson(json.toString(),
//                                            DurationModel.class);
//                                    DurationModel.getInstance().setList(
//                                            obj1);
//                                    duration.add(getTimeFromString(DurationModel.getInstance().items.get(0).contentDetails.duration));
//                                    ArrayList<ItemDetails> image_details = GetSearchResults();
//                                    songListViewAdapter = new SongListViewAdapter(
//                                            getActivity(),R.layout.layout_songs_list_maker, image_details);
//                                    list_songs.setAdapter(songListViewAdapter);
//                                    songListViewAdapter.notifyDataSetChanged();
//                                    progressDialog.dismiss();
//                                    Log.e("Time",video.duration);
//                                }
//                            }
//                        });
//
//                objModel.category.videos.add(video);
//
//
////				VideoItem item = new VideoItem();
////				item.setTitle(result.getSnippet().getTitle());
////				item.setDescription(result.getSnippet().getDescription());
////				item.setThumbnailURL(result.getSnippet().getThumbnails().getDefault().getUrl());
////				item.setId(result.getId().getVideoId());
////				items.add(item);
//            }
//            Log.e("Youtube Connector size", GetAllByCategoryModel.getInstance().category.videos.size()+"");
//        }catch(IOException e){
//            Log.d("YC", "Could not search: "+e);
//        }
//    }
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
//    public String CalculateTotalResults()
//    {
//        StringBuilder stringBuilder= new StringBuilder();
//        String total_count =  GetAllByCategoryModel.getInstance().category.total_result_count+"00";
//        char[] arr = total_count.toCharArray();
//        if(total_count.length() == 8) {
//            stringBuilder.append(arr[0]);
//            stringBuilder.append(arr[1]);
//            stringBuilder.append(" Mil");
//            return stringBuilder.toString();
//        }
//        else if(total_count.length() == 7) {
//            stringBuilder.append(arr[0]);
//            stringBuilder.append(" Mil");
//            return stringBuilder.toString();
//        }
//        else
//            return total_count;
//
//    }
}