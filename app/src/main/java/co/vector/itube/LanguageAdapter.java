package co.vector.itube;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class LanguageAdapter extends ArrayAdapter<ClipData.Item> {

    private Integer[] imgid = {
            R.drawable.farsi,
			R.drawable.arabic,
			R.drawable.hindi,
			R.drawable.turkish,
			R.drawable.spanish,
			R.drawable.english,
            R.drawable.german,
            R.drawable.italian,
            R.drawable.french,
            R.drawable.russian
    };
    private Context _context;
    int layoutId;
    ArrayList<ClipData.Item> Image_data = new ArrayList<ClipData.Item>();

    public LanguageAdapter(Context context,int image_layout,
                           ArrayList<ClipData.Item> data) {

        super(context, image_layout, data);
        this._context = context;
        this.Image_data = data;
        this.layoutId =image_layout;
    }

    static class ViewHolder {
        ImageView eventsImage;
        TextView txt_childText;

    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = ((Activity) _context).getLayoutInflater();
            convertView = inflater.inflate(layoutId, parent, false);

            holder.txt_childText = (TextView) convertView
                    .findViewById(R.id.language);

            holder.eventsImage = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(holder);

        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.eventsImage.setImageResource(imgid[position]);
        holder.txt_childText.setText(Image_data.get(position).getText());
        return convertView;
    }
}