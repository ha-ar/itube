package co.vector.itube;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;

import java.util.ArrayList;
import java.util.Locale;

import Models.DurationModel;
import Models.GetAllByCategoryModel;

/**
 * Created by android on 12/3/14.
 */
public class SongListViewAdapter extends ArrayAdapter<ItemDetails> {
    private static ArrayList<ItemDetails> itemDetailsrrayList;
    private static ArrayList<ItemDetailsDuration> itemDetailsrrayListDuration;
    Context ctx;
    int layoutId;
    ArrayList<String> newArrayListId;
    private ArrayList<ItemDetails> arraylist;
    private ArrayList<ItemDetailsDuration> arraylistDuration;

    public SongListViewAdapter(Context context,int image_layout, ArrayList<ItemDetails> results,
                               ArrayList<ItemDetailsDuration> resultsDuration) {
        super(context,image_layout,  results);
        itemDetailsrrayList = results;
        itemDetailsrrayListDuration = resultsDuration;
        arraylist = new ArrayList<ItemDetails>();
        arraylistDuration = new ArrayList<ItemDetailsDuration>();
        newArrayListId = new ArrayList<String>();
        arraylist.addAll(itemDetailsrrayList);
        try {
            arraylistDuration.addAll(itemDetailsrrayListDuration);
        }catch (NullPointerException e){}
        layoutId = image_layout;
        this.ctx = context;
    }

    public int getCount() {
        return GetAllByCategoryModel.getInstance().items.size();
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) ctx).getLayoutInflater();
            convertView = inflater.inflate(layoutId, parent, false);
            holder = new ViewHolder();
            holder.txt_Name = (TextView) convertView.findViewById(R.id.title);
            holder.txt_duration = (TextView) convertView.findViewById(R.id.duration);
            holder.dropDown = (ImageView) convertView.findViewById(R.id.drop_menu);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        AQuery aq = new AQuery(convertView);
        try {
            String time = null;
//            time = itemDetailsrrayList.get(position).getduration();
//            String splitedtiem = CalculateTime(Long.parseLong(time) * 1000);
            try {
            holder.txt_Name.setText(itemDetailsrrayList.get(position).getName());
            holder.txt_duration.setText(itemDetailsrrayListDuration.get(position).getDuration());

        } catch (NullPointerException npe) {
        }
        ImageOptions options = new ImageOptions();
        options.targetWidth = 200;
        aq.id(R.id.image).image(itemDetailsrrayList.get(position).getImage(), options);
        }catch (IndexOutOfBoundsException e){}
        holder.dropDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity.baseClass.setVideoId(GetAllByCategoryModel.getInstance().items.get(position).videoId.vedioid);
                BaseActivity.baseClass.setVideoTitle(GetAllByCategoryModel.getInstance().items.get(position).snippet.VideoTitle);
                BaseActivity.baseClass.setVideoThumbnail(GetAllByCategoryModel.getInstance().items.get(position).snippet.thumbnails.aDefault.url);
                BaseActivity.baseClass.setVideoPlayerLink("https://www.youtube.com/v/" + GetAllByCategoryModel.getInstance().items.get(position).videoId.vedioid);
                BaseActivity.baseClass.setVideoDuraion(DurationModel.getInstance().items.get(position).contentDetails.duration);

                SongsListViewFragment.popupWindow.showAsDropDown(v, -5, 0);
            }
        });
        return convertView;
    }

    static class ViewHolder {
        TextView txt_Name;
        TextView txt_duration;
        ImageView dropDown;
    }

    public void filter(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());
        itemDetailsrrayList.clear();
        itemDetailsrrayListDuration.clear();

        if (charText.length() == 0) {
            itemDetailsrrayList.addAll(arraylist);
            itemDetailsrrayListDuration.addAll(arraylistDuration);
        } else {
            Log.e("TExt",charText);
            for (int loop=0;loop<arraylist.size() ; loop++) {
                if (arraylist.get(loop).getName().toLowerCase().contains(charText)) {
                    Log.e("Name", arraylist.get(loop).getName() + "/" + charText);
                    try {
                    itemDetailsrrayList.add(arraylist.get(loop));
                    itemDetailsrrayListDuration.add(arraylistDuration.get(loop));
                    }catch (IndexOutOfBoundsException e){}
                }
            }
        }
        notifyDataSetChanged();
    }

    public String CalculateTime(long millisUntilFinished) {
        long hours = millisUntilFinished / 3600000;
        long minutes = millisUntilFinished / 60000;
        minutes = minutes % 60;
        long seconds = millisUntilFinished / 1000;
        seconds = seconds % 60;
        StringBuilder stringBuilder = new StringBuilder();
        if (hours < 10 && hours > 0) {
            stringBuilder.append(0);
        }
        if (hours < 1) {
            stringBuilder.append(00);
        }
        stringBuilder.append(hours).append(":");
        if (minutes < 10 && minutes > 0) {
            stringBuilder.append(0);
        }
        if (minutes < 1) {
            stringBuilder.append(00);
        }
        stringBuilder.append(minutes).append(":");
        if (seconds < 10) {
            stringBuilder.append(0);
        }
        stringBuilder.append(seconds);
        return stringBuilder.toString();
    }

}
