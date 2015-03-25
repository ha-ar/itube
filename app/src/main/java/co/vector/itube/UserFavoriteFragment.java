package co.vector.itube;

import android.app.Fragment;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TextView;

import com.androidquery.AQuery;

import java.util.ArrayList;
import java.util.List;

import DB.DaoMaster;
import DB.DaoSession;
import DB.Favorite;
import DB.FavoriteDao;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by android on 11/20/14.
 */
public class UserFavoriteFragment extends Fragment {

    BaseClass baseClass;ArrayList<String> savelist  = new ArrayList<String>() ;
    AQuery aq; static  ArrayList<ItemDetails> image_details;
    public static ItemListBaseAdapter itemListBaseAdapter;TextView text;
    View rootView;String popUpContents[];

    public UserFavoriteFragment() {
    }

    public UserFavoriteFragment newinstance() {
        UserFavoriteFragment fragment = new UserFavoriteFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_songslistview,
                container, false);

        baseClass = ((BaseClass) getActivity().getApplicationContext());

        BaseActivity.language.setText("Browse " + baseClass.getCategory());
        BaseActivity.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length() != 0) {
                    UserFavoriteFragment.this.itemListBaseAdapter.filter(newText);
                }
                return false;
            }
        });
        aq = new AQuery(getActivity(), rootView);
        aq.id(R.id.footer).visibility(View.GONE);
        if(!baseClass.isTabletDevice(getActivity()))
        {
            aq.id(R.id.listView).getGridView().setNumColumns(2);
        }
        else
            aq.id(R.id.listView).getGridView().setNumColumns(5);
        aq.id(R.id.listView).itemClicked(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                baseClass.setIsFromFavorites("YES");
                baseClass.setPosition(position);
                baseClass.setVideoTitle(image_details.get(position).getName());
                baseClass.setVideoViewer(image_details.get(position).getViewer());
                Intent intent = new Intent(getActivity(),YoutubeBaseActivity.class);
                startActivity(intent);
            }
        });
        PrepareDropDown();
        GetFavoriteFromDB();
        return  rootView;
    }
    private  void PrepareDropDown()
    {
        ArrayList<String> list = new ArrayList<String>();
        list.add("Delete From Favorites");
        popUpContents = new String[list.size()];
        list.toArray(popUpContents);
        BaseActivity.popupWindow = popupWindow();
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
    public class DropdownOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
            BaseActivity.popupWindow.dismiss();
            if(position==0) {
                try {
                    DeletedSongFromFavorite();
                    aq.id(R.id.listView).clear();
                    baseClass.getArrayList().clear();
                    GetFavoriteFromDB();
                    Crouton.makeText(
                            getActivity(),
                            "Video Deleted from Favorites.",
                            Style.ALERT).show();
                } catch (Exception e) {
                }
            }
        }
    }
    public void DeletedSongFromFavorite(){
        DaoMaster.DevOpenHelper ex_database_helper_obj = new DaoMaster.DevOpenHelper(
                getActivity(), "javantube.sqlite", null);
        SQLiteDatabase ex_db = ex_database_helper_obj
                .getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(ex_db);
        DaoSession daoSession = daoMaster.newSession();
        FavoriteDao favoriteDao = daoSession.getFavoriteDao();
        Log.e("ok",baseClass.getVideoId());
        favoriteDao.deleteByKey(baseClass.getVideoId());
        daoSession.clear();
        ex_db.close();
        ex_database_helper_obj.close();
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
    private  void GetFavoriteFromDB()
    {
        DaoMaster.DevOpenHelper ex_database_helper_obj = new DaoMaster.DevOpenHelper(
                getActivity(), "javantube.sqlite", null);
        SQLiteDatabase ex_db = ex_database_helper_obj.getReadableDatabase();
        DaoMaster daoMaster = new DaoMaster(ex_db);
        DaoSession daoSession = daoMaster.newSession();

        FavoriteDao favoriteDao = daoSession.getFavoriteDao();
        List<Favorite> temp = favoriteDao.queryBuilder().list();
        image_details = new ArrayList<ItemDetails>();

        if(temp.size() !=0) {
            for (Favorite favorite : temp) {
                savelist.add(favorite.getVideoId());
                ItemDetails item_details = new ItemDetails();
                item_details.setId(favorite.getVideoId());
                item_details.setName(favorite.getVideoTitle());
                item_details.setViewer(favorite.getVideoViewer());
                item_details.setduration(favorite.getVideoDuration());
                item_details.setUploaddate(favorite.getVideoUploadDate());
                item_details.setAuthor(favorite.getVideoAuthor());
                item_details.setImage(favorite.getVideoThumbnail());
                image_details.add(item_details);
            }
            baseClass.setSaveList(image_details);
            baseClass.setArrayList(savelist);
            itemListBaseAdapter = new ItemListBaseAdapter(
                    getActivity(),R.layout.layout_songs_list_maker, image_details);
            aq.id(R.id.listView).adapter(itemListBaseAdapter);
        }
        else {
            aq.id(R.id.listView).visibility(View.GONE);
            aq.id(R.id.textView).visibility(View.VISIBLE).text("No Favorite record found.");
        }
        daoSession.clear();
        ex_db.close();
        ex_database_helper_obj.close();
    }
}
