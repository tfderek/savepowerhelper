package com.mfox.savepower.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by zhangyuanlu on 17/5/1.
 */

public class PackageInfoProvider extends ContentProvider {

    private DBOpenHelper dbOpenHelper;
    private static final UriMatcher MATCHER=new UriMatcher(UriMatcher.NO_MATCH);
    private static final int PACKAGEINFOS=1;
    private static final int PACKAGEINFO=2;

    static {
        MATCHER.addURI("cn.com.mfox.mpackageInfoProvider","packageinfo",PACKAGEINFOS);
        MATCHER.addURI("cn.com.mfox.mpackageInfoProvider","packageinfo/#",PACKAGEINFO);
    }

    @Override
    public boolean onCreate() {
        this.dbOpenHelper=new DBOpenHelper(this.getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db=dbOpenHelper.getReadableDatabase();
        switch (MATCHER.match(uri)){
            case PACKAGEINFOS:
                return db.query("packageInfo",projection,selection,selectionArgs,null,null,sortOrder);
            case PACKAGEINFO:
                long id= ContentUris.parseId(uri);
                String where="_id="+id;
                if(selection!=null&&!"".equals(selection))
                    where=selection+" and "+where;
                return db.query("packageInfo",projection,where,selectionArgs,null,null,sortOrder);
            default:
                throw new IllegalArgumentException("Unknow Uri:"+uri.toString());
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (MATCHER.match(uri)){
            case PACKAGEINFOS:
                return "vnd.android.cursor.dir/packageinfo";
            case PACKAGEINFO:
                return "vnd.android.cursor.item/packageinfo";
            default:
                throw new IllegalArgumentException("Unknow Uri:"+uri.toString());
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db=dbOpenHelper.getWritableDatabase();
        switch (MATCHER.match(uri)){
            case PACKAGEINFOS:
                long rowid=db.insert("packageInfo","packageName",values);
                Uri insertUri=ContentUris.withAppendedId(uri,rowid);
                this.getContext().getContentResolver().notifyChange(uri,null);
                return insertUri;
            default:
                throw new IllegalArgumentException("Unknow Uri:"+uri.toString());
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db=dbOpenHelper.getWritableDatabase();
        int count=0;
        switch (MATCHER.match(uri)){
            case PACKAGEINFOS:
                count=db.delete("packageInfo",selection,selectionArgs);
                return count;
            case PACKAGEINFO:
                long id=ContentUris.parseId(uri);
                String where="_id="+id;
                if(selection!=null&&!"".equals(selection))
                    where=selection+" and "+where;
                count=db.delete("packageInfo",where,selectionArgs);
                return count;
            default:
                throw new IllegalArgumentException("Unknow Uri:"+uri.toString());
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db=dbOpenHelper.getWritableDatabase();
        int count=0;
        switch (MATCHER.match(uri)){
            case PACKAGEINFOS:
                count=db.update("packageInfo",values,selection,selectionArgs);
                return count;
            case PACKAGEINFO:
                long id=ContentUris.parseId(uri);
                String where="_id="+id;
                if(selection!=null&&!"".equals(selection))
                    where=selection+" and "+where;
                count=db.update("packageInfo",values,where,selectionArgs);
                return count;
            default:
                throw new IllegalArgumentException("Unknow Uri:"+uri.toString());
        }
    }
}
