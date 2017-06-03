package com.yopie.movie.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.yopie.movie.model.MovieList;
import com.yopie.movie.model.MovieResponse;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Yopie on 6/3/2017.
 */

public class MovieDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movie.db";
    private static final int DATABASE_VERSION = 1;

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_DATABASE_SQL = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " ("
                + MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER, "
                + MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT, "
                + MovieContract.MovieEntry.COLUMN_BACKDROP_PATH + " TEXT, "
                + MovieContract.MovieEntry.COLUMN_TITLE + " TEXT, "
                + MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT, "
                + MovieContract.MovieEntry.COLUMN_VOTE_COUNT + " INTEGER, "
                + MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " REAL, "
                + MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT, "
                + MovieContract.MovieEntry.COLUMN_TIMESTAMP + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME')));";

        db.execSQL(CREATE_DATABASE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }

    public void setFavorite(MovieList movieList) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieList.getId());
        cv.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movieList.getPosterPath());
        cv.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, movieList.getBackdropPath());
        cv.put(MovieContract.MovieEntry.COLUMN_TITLE, movieList.getTitle());
        cv.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movieList.getOverview());
        cv.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, movieList.getVoteCount());
        cv.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movieList.getVoteAverage());
        cv.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movieList.getReleaseDate());

        long result = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, cv);
        Log.e(TAG, "saveMovie: " + result );
        db.close();
    }

    public void unsetFavorite(int movieID) {
        SQLiteDatabase db = this.getWritableDatabase();
        final String sql = "DELETE FROM "
                + MovieContract.MovieEntry.TABLE_NAME
                + " WHERE "
                + MovieContract.MovieEntry.COLUMN_MOVIE_ID
                + " LIKE '%" + movieID + "%';";

        db.execSQL(sql);
        Log.e(TAG, "unsetFavorite: " + sql );
        db.close();
    }

    public boolean isFavorite(int movieID) {
        SQLiteDatabase db = this.getReadableDatabase();

        final String sql = "SELECT * FROM "
                + MovieContract.MovieEntry.TABLE_NAME
                + " WHERE "
                + MovieContract.MovieEntry.COLUMN_MOVIE_ID
                + " = " + movieID + ";";

        Cursor cursor = db.rawQuery(sql,null);

        int total = cursor.getCount();
        Log.e(TAG, "isFavorite: " + total );
        cursor.close();
        db.close();
        return total > 0;
    }


    public MovieResponse getFavoriteMovie() {
        SQLiteDatabase db = this.getReadableDatabase();

        List<MovieList> movieList = new ArrayList<>();
        MovieResponse response = new MovieResponse();
        response.setResults(movieList);

        String sql = "SELECT  * FROM " + MovieContract.MovieEntry.TABLE_NAME;
        Cursor cursor      = db.rawQuery(sql, null);

        if(cursor.moveToFirst()) {
            do {
                MovieList listMovie = new MovieList();

                listMovie.setId(cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID)));
                listMovie.setBackdropPath(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH)));
                listMovie.setPosterPath(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH)));
                listMovie.setTitle(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
                listMovie.setOverview(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)));
                listMovie.setVoteCount(cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_COUNT)));
                listMovie.setVoteAverage(cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)));
                listMovie.setReleaseDate(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)));

                response.getResults().add(listMovie);
            } while (cursor.moveToNext());
        }else{
            Log.e(TAG, "getFavoriteMovie: no data");
        }

        cursor.close();
        db.close();

        Log.e(TAG, "getFavoriteMovie: " + response.toString() );
        return response;
    }
}
