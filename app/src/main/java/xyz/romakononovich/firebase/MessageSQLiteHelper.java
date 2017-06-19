package xyz.romakononovich.firebase;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by romank on 15.06.17.
 */

public class MessageSQLiteHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "Messages.db.v1";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "message";
    public static final String COLUMN_MESSAGE = "message";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_ID = "id";

    public MessageSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }
    public MessageSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public MessageSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createDbStatement = "CREATE TABLE "
                + TABLE_NAME + "(" + COLUMN_ID
                + " text not null, " + COLUMN_TIME
                + " text not null, " + COLUMN_MESSAGE
                + " text not null);";

        db.execSQL(createDbStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }
}
