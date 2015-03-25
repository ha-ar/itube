package co.vector.itube;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;

import java.util.ArrayList;
import java.util.Locale;

public class ItemListBaseAdapter extends ArrayAdapter<ItemDetails> {
    private static ArrayList<ItemDetails> itemDetailsrrayList;
    Context ctx;
    int layoutId;
    ArrayList<String> newArrayListId;
    private ArrayList<ItemDetails> arraylist, newArrayListdetail;

    public ItemListBaseAdapter(Context context,int image_layout, ArrayList<ItemDetails> results) {
        super(context,image_layout,  results);
        itemDetailsrrayList = results;
        arraylist = new ArrayList<ItemDetails>();
        newArrayListdetail = new ArrayList<ItemDetails>();
        newArrayListId = new ArrayList<String>();
        arraylist.addAll(itemDetailsrrayList);
        layoutId = image_layout;
        this.ctx = context;
    }
    public int getCount() {
        return itemDetailsrrayList.size();
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
            time = itemDetailsrrayList.get(position).getduration();
            String splitedtiem = CalculateTime(Long.parseLong(time) * 1000);
            holder.txt_Name.setText(itemDetailsrrayList.get(position).getName());
            holder.txt_duration.setText(splitedtiem);
        }catch (NullPointerException npe){}
        ImageOptions options = new ImageOptions();
        options.targetWidth = 200;
        aq.id(R.id.image).image(itemDetailsrrayList.get(position).getImage(), options);
       holder.dropDown.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
                   newArrayListId = BaseActivity.baseClass.getArrayList();
                   newArrayListdetail = BaseActivity.baseClass.getSaveList();

                   BaseActivity.baseClass.setVideoId(newArrayListId.get(position));
                   BaseActivity.baseClass.setVideoTitle(newArrayListdetail.get(position).getName());
                   BaseActivity.baseClass.setVideoThumbnail(newArrayListdetail.get(position).getName());
                   BaseActivity.baseClass.setVideoDuraion(newArrayListdetail.get(position).getduration());
                   BaseActivity.baseClass.setVideoViewer(newArrayListdetail.get(position).getViewer());
               BaseActivity.baseClass.setVideoAuthor(newArrayListdetail.get(position).getAuthor());
               BaseActivity.baseClass.setVideoUploadDate(newArrayListdetail.get(position).getUploaddate());
               BaseActivity.popupWindow.showAsDropDown(v, -5, 0);
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
        if (charText.length() == 0) {
            itemDetailsrrayList.addAll(arraylist);
        }
        else
        {
            for (ItemDetails adapter : arraylist)
            {
                if (adapter.getName().toLowerCase().contains(charText))
                {
                    itemDetailsrrayList.add(adapter);
                }
            }
        }
        notifyDataSetChanged();
    }
    public  String CalculateTime(long millisUntilFinished)
    {
        long hours = millisUntilFinished/3600000;
        long minutes = millisUntilFinished/60000;
        minutes = minutes % 60;
        long seconds = millisUntilFinished/1000;
        seconds = seconds % 60;
        StringBuilder stringBuilder = new StringBuilder();
        if(hours<10 && hours >0)
        {
            stringBuilder.append(0);
        }
        if(hours < 1)
        {
            stringBuilder.append(00);
        }
        stringBuilder.append(hours).append(":");
        if(minutes<10 && minutes > 0)
        {
            stringBuilder.append(0);
        }
        if(minutes < 1)
        {
            stringBuilder.append(00);
        }
        stringBuilder.append(minutes).append(":");
        if(seconds<10)
        {
            stringBuilder.append(0);
        }
        stringBuilder.append(seconds);
        return  stringBuilder.toString();
    }
}