package Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by android on 11/11/14.
 */
public class GetAllByCategoryModel {

    private static GetAllByCategoryModel _obj = null;

    public GetAllByCategoryModel() {

    }

    public static GetAllByCategoryModel getInstance() {
        if (_obj == null) {
            _obj = new GetAllByCategoryModel();
        }
        return _obj;
    }

    public void setList(GetAllByCategoryModel obj) {
        _obj = obj;
    }

    public void appendList(GetAllByCategoryModel obj) {
        _obj.nextPageToken = obj.nextPageToken;
        for(Items data : obj.items)
            if(!_obj.items.contains(data))
                _obj.items.add(data);
    }
    @SerializedName("nextPageToken")
    public String nextPageToken;

    @SerializedName("pageInfo")
    public PageInfo pageInfo = new PageInfo();

    @SerializedName("items")
    public ArrayList<Items> items = new ArrayList<Items>();

    public static class Items {
        @SerializedName("id")
        public VideoId videoId = new VideoId();

        @SerializedName("snippet")
        public Snippet snippet = new Snippet();

    }
    public static class PageInfo {
        @SerializedName("totalResults")
        public String totalResults ;
    }
    public static class Snippet {
        @SerializedName("title")
        public String VideoTitle ;

        @SerializedName("description")
        public String description ;

        @SerializedName("thumbnails")
        public Thumbnails thumbnails = new Thumbnails();
    }
    public static class Thumbnails {
        @SerializedName("default")
        public Default aDefault = new Default();
    }
    public static class Default {
        @SerializedName("url")
        public String url ;
    }
    public static class VideoId {
        @SerializedName("videoId")
        public String vedioid ;
    }

}