package DB;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table PLAYLIST.
*/
public class PlaylistDao extends AbstractDao<Playlist, String> {

    public static final String TABLENAME = "PLAYLIST";

    /**
     * Properties of entity Playlist.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property VideoId = new Property(0, String.class, "VideoId", true, "VIDEO_ID");
        public final static Property VideoTitle = new Property(1, String.class, "VideoTitle", false, "VIDEO_TITLE");
        public final static Property VideoDuration = new Property(2, String.class, "VideoDuration", false, "VIDEO_DURATION");
        public final static Property VideoViewer = new Property(3, String.class, "VideoViewer", false, "VIDEO_VIEWER");
        public final static Property VideoUploadDate = new Property(4, String.class, "VideoUploadDate", false, "VIDEO_UPLOAD_DATE");
        public final static Property VideoAuthor = new Property(5, String.class, "VideoAuthor", false, "VIDEO_AUTHOR");
        public final static Property VideoThumbnail = new Property(6, String.class, "VideoThumbnail", false, "VIDEO_THUMBNAIL");
    };


    public PlaylistDao(DaoConfig config) {
        super(config);
    }
    
    public PlaylistDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'PLAYLIST' (" + //
                "'VIDEO_ID' TEXT PRIMARY KEY NOT NULL ," + // 0: VideoId
                "'VIDEO_TITLE' TEXT NOT NULL ," + // 1: VideoTitle
                "'VIDEO_DURATION' TEXT," + // 2: VideoDuration
                "'VIDEO_VIEWER' TEXT," + // 3: VideoViewer
                "'VIDEO_UPLOAD_DATE' TEXT," + // 4: VideoUploadDate
                "'VIDEO_AUTHOR' TEXT," + // 5: VideoAuthor
                "'VIDEO_THUMBNAIL' TEXT);"); // 6: VideoThumbnail
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'PLAYLIST'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Playlist entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getVideoId());
        stmt.bindString(2, entity.getVideoTitle());
 
        String VideoDuration = entity.getVideoDuration();
        if (VideoDuration != null) {
            stmt.bindString(3, VideoDuration);
        }
 
        String VideoViewer = entity.getVideoViewer();
        if (VideoViewer != null) {
            stmt.bindString(4, VideoViewer);
        }
 
        String VideoUploadDate = entity.getVideoUploadDate();
        if (VideoUploadDate != null) {
            stmt.bindString(5, VideoUploadDate);
        }
 
        String VideoAuthor = entity.getVideoAuthor();
        if (VideoAuthor != null) {
            stmt.bindString(6, VideoAuthor);
        }
 
        String VideoThumbnail = entity.getVideoThumbnail();
        if (VideoThumbnail != null) {
            stmt.bindString(7, VideoThumbnail);
        }
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Playlist readEntity(Cursor cursor, int offset) {
        Playlist entity = new Playlist( //
            cursor.getString(offset + 0), // VideoId
            cursor.getString(offset + 1), // VideoTitle
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // VideoDuration
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // VideoViewer
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // VideoUploadDate
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // VideoAuthor
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6) // VideoThumbnail
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Playlist entity, int offset) {
        entity.setVideoId(cursor.getString(offset + 0));
        entity.setVideoTitle(cursor.getString(offset + 1));
        entity.setVideoDuration(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setVideoViewer(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setVideoUploadDate(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setVideoAuthor(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setVideoThumbnail(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(Playlist entity, long rowId) {
        return entity.getVideoId();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(Playlist entity) {
        if(entity != null) {
            return entity.getVideoId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
