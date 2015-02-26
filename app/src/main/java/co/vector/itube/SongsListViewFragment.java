package co.vector.itube;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;

import java.util.ArrayList;
import java.util.List;

import DB.DaoMaster;
import DB.DaoSession;
import DB.Favorite;
import DB.FavoriteDao;
import DB.Playlist;
import DB.PlaylistDao;
import Models.GetAllByCategoryModel;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import services.CallBack;
import services.GetByAllCategoryService;

/**
 * Created by android on 11/17/14.
 */
public class SongsListViewFragment extends Fragment {
    BaseClass baseClass;List<String> list;
    AQuery aq;static PopupWindow popupWindow;
    public static SongListViewAdapter songListViewAdapter;
    View rootView,footerView;static  int SelectedId;  String popUpContents[];static String Query;
    static GridView list_songs;static int index;
    GetByAllCategoryService obj;

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
        footerView = (LinearLayout) rootView.findViewById(R.id.footer);
        if(!baseClass.isTabletDevice(getActivity()))
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
                    footerView.setVisibility(View.GONE);
                Query = query;
                obj = new GetByAllCategoryService(getActivity());
                obj.getbysearch(query, baseClass.getAUTH_TOKEN(), index, true,
                        new CallBack(SongsListViewFragment.this, "GetAllBySearch"));
                }catch (NullPointerException e){}
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() != 0) {
                    SongsListViewFragment.this.songListViewAdapter.filter(newText);
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
        aq = new AQuery(getActivity(),rootView);
        obj = new GetByAllCategoryService(getActivity());
        obj.getbycategory(baseClass.getNewCategory(),baseClass.getDuration(), baseClass.getAUTH_TOKEN(),index,true,
                new CallBack(this, "GetAllBy" + baseClass.getCategory()));

        footerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index++;
                try{
                    footerView.setVisibility(View.GONE);
                }catch (NullPointerException e){}
                obj = new GetByAllCategoryService(getActivity());
                if(baseClass.getDataBy().equalsIgnoreCase("Category")) {
                    obj.getbycategory(baseClass.getNewCategory(), baseClass.getDuration(), baseClass.getAUTH_TOKEN(), index, true,
                            new CallBack(SongsListViewFragment.this, "GetAllBy" + baseClass.getCategory() + "More"));
                }
                else
                {
                    obj.getbysearch(Query, baseClass.getAUTH_TOKEN(), index, true,
                            new CallBack(SongsListViewFragment.this, "GetAllBySearchMore"));
                }

            }
        });
        list_songs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getActivity(),YoutubeBaseActivity.class));
                baseClass.setVideoId(GetAllByCategoryModel.getInstance().category.videos.get(position).unique_id);
                baseClass.setVideoTitle(GetAllByCategoryModel.getInstance().category.videos.get(position).title);
                baseClass.setVideoThumbnail(GetAllByCategoryModel.getInstance().category.videos.get(position).thumbnails.get(0).url);
                baseClass.setVideoPlayerLink(GetAllByCategoryModel.getInstance().category.videos.get(position).player_url);
                baseClass.setVideoDuraion(GetAllByCategoryModel.getInstance().category.videos.get(position).duration);
                baseClass.setVideoUploadDate(GetAllByCategoryModel.getInstance().category.videos.get(position).uploaded_at);
                baseClass.setVideoAuthor(GetAllByCategoryModel.getInstance().category.videos.get(position).author.name);
                baseClass.setVideoViewer(GetAllByCategoryModel.getInstance().category.videos.get(position).view_count);
                SelectedId = position;
            }
        });
        return rootView;
    }
    public void GetAllBySearch(Object caller, Object model) {
        GetAllByCategoryModel.getInstance().setList((GetAllByCategoryModel) model);
        Log.e("s",GetAllByCategoryModel.getInstance().success);
        if (GetAllByCategoryModel.getInstance().success.equalsIgnoreCase("true")) {
            Log.e("Index",String.valueOf(index));
            ArrayList<ItemDetails> image_details = GetSearchResults();
                songListViewAdapter = new SongListViewAdapter(
                        getActivity(),R.layout.layout_songs_list_maker, image_details);
            footerView.setVisibility(View.VISIBLE);
            list_songs.setAdapter(songListViewAdapter);
            if(GetAllByCategoryModel.getInstance().category.total_result_count.equals("1000000"))
            aq.id(R.id.total_results).text("Total Results: Over 1M");
            else
            aq.id(R.id.total_results).text("Total Results: " + GetAllByCategoryModel.getInstance().category.total_result_count);

        } else {
            aq.id(R.id.textView).visibility(View.VISIBLE).text("No "+baseClass.getCategory()+" record found.");
            Toast.makeText(getActivity(), "Check internet settings or server not responding.", Toast.LENGTH_LONG).show();
        }
    }
    public void GetAllBySearchMore(Object caller, Object model) {
        GetAllByCategoryModel.getInstance().appendList((GetAllByCategoryModel) model);
        Log.e("s",GetAllByCategoryModel.getInstance().success);
        if (GetAllByCategoryModel.getInstance().success.equalsIgnoreCase("true")) {
            Log.e("Index",String.valueOf(index));
            ArrayList<ItemDetails> image_details = GetSearchResults();
                songListViewAdapter = new SongListViewAdapter(
                        getActivity(),R.layout.layout_songs_list_maker, image_details);
            footerView.setVisibility(View.VISIBLE);
            songListViewAdapter.notifyDataSetChanged();
            if(GetAllByCategoryModel.getInstance().category.total_result_count.equals("1000000"))
                aq.id(R.id.total_results).text("Total Results: Over 1Mil");
            else
                aq.id(R.id.total_results).text("Total Results: " + GetAllByCategoryModel.getInstance().category.total_result_count);
        } else {
            aq.id(R.id.textView).visibility(View.VISIBLE).text("No "+baseClass.getCategory()+" record found.");
            Toast.makeText(getActivity(), "Check internet settings or server not responding.", Toast.LENGTH_LONG).show();
        }
    }
    public void GetAllByMovies(Object caller, Object model) {
        GetAllByCategoryModel.getInstance().setList((GetAllByCategoryModel) model);
        Log.e("s",GetAllByCategoryModel.getInstance().success);
        if (GetAllByCategoryModel.getInstance().success.equalsIgnoreCase("true")) {
                Log.e("Index",String.valueOf(index));
                ArrayList<ItemDetails> image_details = GetSearchResults();
                songListViewAdapter = new SongListViewAdapter(
                        getActivity(),R.layout.layout_songs_list_maker, image_details);
            footerView.setVisibility(View.VISIBLE);
                list_songs.setAdapter(songListViewAdapter);
            if(GetAllByCategoryModel.getInstance().category.total_result_count.equals("1000000"))
                aq.id(R.id.total_results).text("Total Results: Over 1Mil");
            else
                aq.id(R.id.total_results).text("Total Results: " + GetAllByCategoryModel.getInstance().category.total_result_count);
        } else {
            aq.id(R.id.textView).visibility(View.VISIBLE).text("No "+baseClass.getCategory()+" record found.");
            Toast.makeText(getActivity(), "Check internet settings or server not responding.", Toast.LENGTH_LONG).show();
        }
    }
    public void GetAllByMoviesMore(Object caller, Object model) {
        GetAllByCategoryModel.getInstance().appendList((GetAllByCategoryModel) model);
        Log.e("s",GetAllByCategoryModel.getInstance().success);
        if (GetAllByCategoryModel.getInstance().success.equalsIgnoreCase("true")) {
                Log.e("Index",String.valueOf(index));
                ArrayList<ItemDetails> image_details = GetSearchResults();
                songListViewAdapter = new SongListViewAdapter(
                        getActivity(),R.layout.layout_songs_list_maker, image_details);
            footerView.setVisibility(View.VISIBLE);
                songListViewAdapter.notifyDataSetChanged();
            if(GetAllByCategoryModel.getInstance().category.total_result_count.equals("1000000"))
                aq.id(R.id.total_results).text("Total Results: Over 1Mil");
            else
                aq.id(R.id.total_results).text("Total Results: " + GetAllByCategoryModel.getInstance().category.total_result_count);
        } else {
            aq.id(R.id.textView).visibility(View.VISIBLE).text("No "+baseClass.getCategory()+" record found.");
            Toast.makeText(getActivity(), "Check internet settings or server not responding.", Toast.LENGTH_LONG).show();
        }
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
        if (GetAllByCategoryModel.getInstance().success.equalsIgnoreCase("true")) {
            ArrayList<ItemDetails> image_details = GetSearchResults();

                songListViewAdapter = new SongListViewAdapter(
                        getActivity(),R.layout.layout_songs_list_maker, image_details);
            footerView.setVisibility(View.VISIBLE);
            list_songs.setAdapter(songListViewAdapter);
            if(GetAllByCategoryModel.getInstance().category.total_result_count.equals("1000000"))
                aq.id(R.id.total_results).text("Total Results: Over 1Mil");
            else
                aq.id(R.id.total_results).text("Total Results: " + GetAllByCategoryModel.getInstance().category.total_result_count);
        } else {
            aq.id(R.id.textView).visibility(View.VISIBLE).text("No "+baseClass.getCategory()+" record found.");
            Toast.makeText(getActivity(), "Check internet settings or server not responding.", Toast.LENGTH_LONG).show();
        }
    }
    public void GetAllByCartoonsMore(Object caller, Object model) {
        GetAllByCategoryModel.getInstance().appendList((GetAllByCategoryModel) model);
        Log.e("s",GetAllByCategoryModel.getInstance().success);
        if (GetAllByCategoryModel.getInstance().success.equalsIgnoreCase("true")) {
            Log.e("Index",String.valueOf(index));
            ArrayList<ItemDetails> image_details = GetSearchResults();
                songListViewAdapter = new SongListViewAdapter(
                        getActivity(),R.layout.layout_songs_list_maker, image_details);
            footerView.setVisibility(View.VISIBLE);
            songListViewAdapter.notifyDataSetChanged();
            if(GetAllByCategoryModel.getInstance().category.total_result_count.equals("1000000"))
                aq.id(R.id.total_results).text("Total Results: Over 1Mil");
            else
                aq.id(R.id.total_results).text("Total Results: " + GetAllByCategoryModel.getInstance().category.total_result_count);
        } else {
            aq.id(R.id.textView).visibility(View.VISIBLE).text("No "+baseClass.getCategory()+" record found.");
            Toast.makeText(getActivity(), "Check internet settings or server not responding.", Toast.LENGTH_LONG).show();
        }
    }
    public void GetAllByMusic(Object caller, Object model) {
        GetAllByCategoryModel.getInstance().setList((GetAllByCategoryModel) model);
        if (GetAllByCategoryModel.getInstance().success.equalsIgnoreCase("true")) {
            ArrayList<ItemDetails> image_details = GetSearchResults();
                songListViewAdapter = new SongListViewAdapter(
                        getActivity(),R.layout.layout_songs_list_maker, image_details);
            footerView.setVisibility(View.VISIBLE);
            list_songs.setAdapter(songListViewAdapter);
            if(GetAllByCategoryModel.getInstance().category.total_result_count.equals("1000000"))
                aq.id(R.id.total_results).text("Total Results: Over 1Mil");
            else
                aq.id(R.id.total_results).text("Total Results: " + GetAllByCategoryModel.getInstance().category.total_result_count);
        } else {
            aq.id(R.id.textView).visibility(View.VISIBLE).text("No "+baseClass.getCategory()+" record found.");
            Toast.makeText(getActivity(), "Check internet settings or server not responding.", Toast.LENGTH_LONG).show();
        }
    }
    public void GetAllByMusicMore(Object caller, Object model) {
        GetAllByCategoryModel.getInstance().appendList((GetAllByCategoryModel) model);
        Log.e("s",GetAllByCategoryModel.getInstance().success);
        if (GetAllByCategoryModel.getInstance().success.equalsIgnoreCase("true")) {
            Log.e("Index",String.valueOf(index));
            ArrayList<ItemDetails> image_details = GetSearchResults();
                songListViewAdapter = new SongListViewAdapter(
                        getActivity(),R.layout.layout_songs_list_maker, image_details);
            footerView.setVisibility(View.VISIBLE);
            songListViewAdapter.notifyDataSetChanged();
            if(GetAllByCategoryModel.getInstance().category.total_result_count.equals("1000000"))
                aq.id(R.id.total_results).text("Total Results: Over 1Mil");
            else
                aq.id(R.id.total_results).text("Total Results: " + GetAllByCategoryModel.getInstance().category.total_result_count);
        } else {
            aq.id(R.id.textView).visibility(View.VISIBLE).text("No "+baseClass.getCategory()+" record found.");
            Toast.makeText(getActivity(), "Check internet settings or server not responding.", Toast.LENGTH_LONG).show();
        }
    }
    public void GetAllByDocumentaries(Object caller, Object model) {
        GetAllByCategoryModel.getInstance().setList((GetAllByCategoryModel) model);

        if (GetAllByCategoryModel.getInstance().success.equalsIgnoreCase("true")) {
            ArrayList<ItemDetails> image_details = GetSearchResults();
                songListViewAdapter = new SongListViewAdapter(
                        getActivity(),R.layout.layout_songs_list_maker, image_details);
            footerView.setVisibility(View.VISIBLE);
            list_songs.setAdapter(songListViewAdapter);
            if(GetAllByCategoryModel.getInstance().category.total_result_count.equals("1000000"))
                aq.id(R.id.total_results).text("Total Results: Over 1Mil");
            else
                aq.id(R.id.total_results).text("Total Results: " + GetAllByCategoryModel.getInstance().category.total_result_count);
        } else {
            aq.id(R.id.textView).visibility(View.VISIBLE).text("No "+baseClass.getCategory()+" record found.");
            Toast.makeText(getActivity(), "Check internet settings or server not responding.", Toast.LENGTH_LONG).show();
        }
    }
    public void GetAllByDocumentariesMore(Object caller, Object model) {
        GetAllByCategoryModel.getInstance().appendList((GetAllByCategoryModel) model);
        Log.e("s",GetAllByCategoryModel.getInstance().success);
        if (GetAllByCategoryModel.getInstance().success.equalsIgnoreCase("true")) {
            Log.e("Index",String.valueOf(index));
            ArrayList<ItemDetails> image_details = GetSearchResults();
                songListViewAdapter = new SongListViewAdapter(
                        getActivity(),R.layout.layout_songs_list_maker, image_details);
            footerView.setVisibility(View.VISIBLE);
            songListViewAdapter.notifyDataSetChanged();
            if(GetAllByCategoryModel.getInstance().category.total_result_count.equals("1000000"))
                aq.id(R.id.total_results).text("Total Results: Over 1Mil");
            else
                aq.id(R.id.total_results).text("Total Results: " + GetAllByCategoryModel.getInstance().category.total_result_count);
        } else {
            aq.id(R.id.textView).visibility(View.VISIBLE).text("No "+baseClass.getCategory()+" record found.");
            Toast.makeText(getActivity(), "Check internet settings or server not responding.", Toast.LENGTH_LONG).show();
        }
    }
    static int p;

    private static ArrayList<ItemDetails> GetSearchResults() {
Log.e("Size",String.valueOf(GetAllByCategoryModel.getInstance().category.videos.size()));
        ArrayList<ItemDetails> results = new ArrayList<ItemDetails>();
        for (p = 0; p < GetAllByCategoryModel.getInstance().category.videos.size(); p++) {
            ItemDetails item_details = new ItemDetails();
            item_details.setName(GetAllByCategoryModel.getInstance().category.videos.get(p).title);
            item_details.setAuthor(GetAllByCategoryModel.getInstance().category.videos.get(p).author.name);
            item_details.setViewer(GetAllByCategoryModel.getInstance().category.videos.get(p).view_count);
            item_details.setduration(GetAllByCategoryModel.getInstance().category.videos.get(p).duration);
            item_details.setUploaddate(GetAllByCategoryModel.getInstance().category.videos.get(p).uploaded_at);
            item_details.setImage(GetAllByCategoryModel.getInstance().category.videos.get(p).thumbnails.get(0).url);
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
}