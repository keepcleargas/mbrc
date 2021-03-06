package com.kelsos.mbrc.data.dbdata;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import com.kelsos.mbrc.data.db.LibraryProvider;
import com.kelsos.mbrc.data.interfaces.TrackColumns;
import com.kelsos.mbrc.util.RemoteUtils;
import org.codehaus.jackson.JsonNode;

import java.util.Date;

public class Track extends DataItem implements TrackColumns {
    private long id;
    private String hash;
    private String title;
    private long albumId;
    private String album;
    private String albumArtist;
    private long genreId;
    private String genre;
    private long artistId;
    private String artist;
    private String year;
    private int trackNo;
    private Date updated;
    public static final String TABLE_NAME = "tracks";

    public static final String CREATE_TABLE =
            "create table " + TABLE_NAME + "(" + _ID
            + " integer primary key," + HASH + " text ,"
            + TITLE + " text," + ALBUM_ID + " integer, " + GENRE_ID + " integer,"
            + ARTIST_ID + " integer," + YEAR + " text," + TRACK_NO + " integer,"
            + COVER_ID + " integer, " + UPDATED + " datetime, "
            + "foreign key (" + ARTIST_ID + ") references "
            + Artist.TABLE_NAME + "("+ _ID + ") on delete cascade, "
            + "foreign key (" + ALBUM_ID + ") references "
            + Album.TABLE_NAME + "("+ _ID + ") on delete cascade, "
            + "foreign key (" + GENRE_ID + ") references "
            + Genre.TABLE_NAME + "("+ _ID + ") on delete cascade, "
            + "unique(" + HASH + ") on conflict ignore" + ")";

    public static final String DROP_TABLE = "drop table if exists " + TABLE_NAME;
    public static final String SELECT_TRACKS =
            "select t." + _ID + ", t." + HASH + ", t." + TITLE + ", t." + ALBUM_ID + ", al." +
                    Album.ALBUM_NAME + ", t." + GENRE_ID + ", g." + Genre.GENRE_NAME + ", t." + ARTIST_ID +
                    ", ar." + Artist.ARTIST_NAME + ", t." + YEAR + ", t." + TRACK_NO + ", t." + COVER_ID +
                    ", t." + UPDATED +
                    " from " + Album.TABLE_NAME + " al, " + Artist.TABLE_NAME + " ar,"
                    + Genre.TABLE_NAME + " g, " + TABLE_NAME + " t " +
                    " where al." + _ID + " = t." + ALBUM_ID + " and ar." + _ID + " = t." + ARTIST_ID +
                    " and g." + _ID + " = t." + GENRE_ID
                    + " order by " + " ar." + Artist.ARTIST_NAME + " ASC" + ", al." + Album.ALBUM_NAME
                    + " ASC" + ", t." + TRACK_NO + " ASC";

    public static final String SELECT_TRACK = SELECT_TRACKS + " and t." + _ID + " = ?";
    public static final String INSERT =
            String.format("insert into %s (%s, %s, %s, %s, %s, %s, %s) " +
                    "values (?, ?, (select _id from genres where genre_name = ?)," +
                    " (select _id from artists where artist_name = ?)," +
                    " (select _id from albums where album_name = ?), ?, ?)",
                    TABLE_NAME, HASH, TITLE, GENRE_ID, ARTIST_ID, ALBUM_ID, YEAR, TRACK_NO);

    public static Uri getContentUri() {
        return Uri.withAppendedPath(Uri.parse(LibraryProvider.SCHEME +
                LibraryProvider.AUTHORITY), TABLE_NAME);
    }

    public static final Uri CONTENT_ALBUM_URI = Uri.withAppendedPath(getContentUri(), "album");

    public static final int BASE_URI_CODE = 0x8ee72c3;
    public static final int BASE_ITEM_CODE =  0x63b3c5d;
    public static final int BASE_ALBUM_FILTER_CODE = 0x3821dd2e;

    public static void addMatcherUris(UriMatcher uriMatcher) {
        uriMatcher.addURI(LibraryProvider.AUTHORITY, TABLE_NAME, BASE_URI_CODE);
        uriMatcher.addURI(LibraryProvider.AUTHORITY, TABLE_NAME + "/#", BASE_ITEM_CODE);
        uriMatcher.addURI(LibraryProvider.AUTHORITY, TABLE_NAME + "/album/*", BASE_ALBUM_FILTER_CODE);
    }

    public static final String TYPE_DIR = "vnd.android.cursor.dir/vnd.com.kelsos.mbrc.provider." + TABLE_NAME;
    public static final String TYPE_ITEM = "vnd.android.cursor.item/vnd.com.kelsos.mbrc.provider." + TABLE_NAME;


    public Track(JsonNode node) {
        this.id = -1;
        this.hash = node.path("hash").asText();
        this.title = node.path("title").asText();
        this.albumId = -1;
        this.album = node.path("album").asText();
        this.genreId = -1;
        this.genre = node.path("genre").asText();
        this.artistId = -1;
        this.artist = node.path("artist").asText();
        this.year = node.path("year").asText();
        this.trackNo = node.path("track_no").asInt();
        this.albumArtist = node.path("album_artist").asText();
    }

    public Track(final Cursor cursor) {
        this.id = cursor.getLong(cursor.getColumnIndex(_ID));
        this.hash = cursor.getString(cursor.getColumnIndex(HASH));
        this.title = cursor.getString(cursor.getColumnIndex(TITLE));
        this.albumId = cursor.getLong(cursor.getColumnIndex(ALBUM_ID));
        this.album = cursor.getString(cursor.getColumnIndex(Album.ALBUM_NAME));
        this.genreId = cursor.getLong(cursor.getColumnIndex(GENRE_ID));
        this.genre = cursor.getString(cursor.getColumnIndex(Genre.GENRE_NAME));
        this.artistId = cursor.getLong(cursor.getColumnIndex(ARTIST_ID));
        this.artist = cursor.getString(cursor.getColumnIndex(Artist.ARTIST_NAME));
        this.year = cursor.getString(cursor.getColumnIndex(YEAR));
        this.trackNo = cursor.getInt(cursor.getColumnIndex(TRACK_NO));
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(HASH, hash);
        values.put(TITLE, title);
        values.put(ALBUM_ID, albumId);
        values.put(GENRE_ID, genreId);
        values.put(ARTIST_ID, artistId);
        values.put(YEAR, year);
        values.put(TRACK_NO, trackNo);
        values.put(UPDATED, RemoteUtils.currentTime());
        return values;
    }

    @Override public String getTableName() {
        return TABLE_NAME;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }
    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public long getGenreId() {
        return genreId;
    }

    public void setGenreId(long genreId) {
        this.genreId = genreId;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public long getArtistId() {
        return artistId;
    }

    public void setArtistId(long artistId) {
        this.artistId = artistId;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public int getTrackNo() {
        return trackNo;
    }

    public void setTrackNo(int trackNo) {
        this.trackNo = trackNo;
    }

    public String getAlbumArtist() {
        return albumArtist;
    }

    public void setAlbumArtist(String albumArtist) {
        this.albumArtist = albumArtist;
    }

}
