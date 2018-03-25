package app.test.kotlin.sampletango.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DiaryHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object {
        private const val DB_NAME = "diary.db"
        private const val DB_VERSION = 1
        private const val SQL_CREATE_TABLE_DIARY = "CREATE TABLE diary(" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                "`title` TEXT," +
                "`body` TEXT," +
                "`create_at` INTEGER," +
                "`update_at` INTEGER," +
                "`is_delete` INTEGER" +
                ")"
        private const val SQL_DROP_TABLE_DIARY = "DROP TABLE IF EXISTS diary"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_TABLE_DIARY)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
//        db?.execSQL(SQL_DROP_TABLE_DIARY)
    }
}