package com.example.goldzen_1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class RankSqlDataBaseHelper extends SQLiteOpenHelper {
    private static final String DataBaseName = "RankDataBaseIt_2.db";
    private static final int DataBaseVersion = 1;

    public RankSqlDataBaseHelper(@Nullable Context context, @Nullable String name,
                                 @Nullable SQLiteDatabase.CursorFactory factory,
                                 int version, String TableName) {
        super(context, DataBaseName, null, DataBaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SqlTable = "CREATE TABLE IF NOT EXISTS Rank (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "playRound INTEGER not null," +
                "player text not null," +
                "round INTEGER not null," +
                "playTime INTEGER not null," +
                "second INTEGER not null," +
                "pass_fail_status text not null" +
                ")";
        sqLiteDatabase.execSQL(SqlTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        final String SQL = "DROP TABLE Rank";
        sqLiteDatabase.execSQL(SQL);
    }
}
