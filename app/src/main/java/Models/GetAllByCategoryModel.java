package Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by android on 11/11/14.
 */
public class GetAllByCategoryModel {

    private static GetAllByCategoryModel _obj = null;

    private GetAllByCategoryModel() {

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
        for(Videos data : obj.category.videos)
            if(!_obj.category.videos.contains(data))
                _obj.category.videos.add(data);
    }
    @SerializedName("success")
    public String success;

    @SerializedName("category")
    public Category category = new Category();


    public class Category {
        public String feed_id ;
        public String updated_at;
        public String total_result_count;
        public String offset;
        public int max_result_count;
        @SerializedName("videos")
        public ArrayList<Videos> videos = new ArrayList<Videos>();
    }
    public static class Videos {
        public String video_id ;
        public String published_at;
        public String updated_at;
        public String uploaded_at;
        public String recorded_at;
        public  String title;
        public String description;
        public String duration;
        public String player_url;
        public String view_count;
        public String favorite_count;
        public String comment_count;
        public String widescreen;
        public String noembed;
        public String safe_search;
        public String position;
        public String video_position;
        public String latitude;
        public String longitude;
        public String insight_uri;
        public String unique_id;
        public String perm_private;
        @SerializedName("thumbnails")
        public ArrayList<String> thumbnails = new ArrayList<String>();
        @SerializedName("author")
        public Author author = new Author();
    }
    public class Thumbnails {
        @SerializedName("url")
        public String url ;
    }
    public static class Author {
        @SerializedName("name")
        public String name ;
    }

}