package xyz.romakononovich.firebase;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import xyz.romakononovich.firebase.Models.Message;

import static xyz.romakononovich.firebase.MessageSQLiteHelper.COLUMN_ID;
import static xyz.romakononovich.firebase.MessageSQLiteHelper.COLUMN_MESSAGE;
import static xyz.romakononovich.firebase.MessageSQLiteHelper.COLUMN_TIME;

public class MessageDataSource {
    private MessageSQLiteHelper messageSQLiteHelper;
    private SQLiteDatabase sqLiteDatabase;



    public MessageDataSource(Context context){
        messageSQLiteHelper = new MessageSQLiteHelper(context);
    }

    void open() {
        sqLiteDatabase = messageSQLiteHelper.getWritableDatabase();
    }
    void close() {
        messageSQLiteHelper.close();
    }
    void addMessage(Message message){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID,message.getId());
        contentValues.put(COLUMN_TIME,message.getTime());
        contentValues.put(COLUMN_MESSAGE,message.getMessage());
        //contentValues.put(COLUMN_TITLE,message.getTitle());
        sqLiteDatabase.insert(COLUMN_MESSAGE,null,contentValues);
    }
    private void delMessage(){

    }
    List<Message> getAllMessage(){
        List<Message> messageList = new ArrayList<>();
        if (sqLiteDatabase!=null) {
            Cursor cursor = sqLiteDatabase.query(MessageSQLiteHelper.TABLE_NAME,
                    new String[]{MessageSQLiteHelper.COLUMN_ID,MessageSQLiteHelper.COLUMN_TIME, MessageSQLiteHelper.COLUMN_MESSAGE}, null, null, null, null, null);

            cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Message message = new Message();
            message.setId(cursor.getString(0));
            message.setTime(cursor.getString(1));
            message.setMessage(cursor.getString(2));
           // message.setTitle(cursor.getString(3));
            messageList.add(message);
            cursor.moveToNext();
        }
        cursor.close();
        }
        return messageList;
    }
}
