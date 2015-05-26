package Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by android on 5/21/15.
 */
public class DurationModel {
    private static DurationModel _obj = null;

    public DurationModel() {

    }

    public static DurationModel getInstance() {
        if (_obj == null) {
            _obj = new DurationModel();
        }
        return _obj;
    }

    public void setList(DurationModel obj) {
        _obj = obj;
    }
    public void appendList(DurationModel obj) {
        for(Items data : obj.items)
            if(!_obj.items.contains(data))
                _obj.items.add(data);
    }


    @SerializedName("items")
    public ArrayList<Items> items = new ArrayList<Items>();

    public static class Items{

        @SerializedName("contentDetails")
        public ContentDetails contentDetails = new ContentDetails();
    }
    public static class ContentDetails{
        @SerializedName("duration")
        public String duration ;
    }
}

