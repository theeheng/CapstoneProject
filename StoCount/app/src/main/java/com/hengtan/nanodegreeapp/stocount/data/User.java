package com.hengtan.nanodegreeapp.stocount.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by htan on 27/11/2015.
 */
public class User implements Parcelable {

    private Integer mUserId;
    private String mDisplayName;
    private String mEmail;
    private String mPhotoUrl;
    private String mGoogleID;

    private List<String> mParcelableString;

    public User(Cursor cursor) {

        this.mUserId = cursor.getInt(cursor.getColumnIndex(StoCountContract.UserEntry._ID));
        this.mDisplayName = cursor.getString(cursor.getColumnIndex(StoCountContract.UserEntry.DISPLAY_NAME));
        this.mEmail = cursor.getString(cursor.getColumnIndex(StoCountContract.UserEntry.EMAIL));
        this.mPhotoUrl = cursor.getString(cursor.getColumnIndex(StoCountContract.UserEntry.PHOTO_URL));
        this.mGoogleID = cursor.getString(cursor.getColumnIndex(StoCountContract.UserEntry.GOOGLE_ID));
    }

    private enum UserIndex
    {
        USER_ID(0), DISPLAY_NAME(1), EMAIL(2), PHOTO_URL(3), GOOGLE_ID(4);

        private int value;

        private UserIndex(int value)
        {
            this.value = value;
        }
    };

    public User(GoogleSignInAccount account)
    {
        this.mUserId = null;
        this.mDisplayName = account.getDisplayName();
        this.mEmail = account.getEmail();
        this.mPhotoUrl = account.getPhotoUrl().toString();
        this.mGoogleID = account.getId();
    }

    public User(Parcel in)
    {
        mParcelableString = new ArrayList<String>();
        in.readStringList(this.mParcelableString);

        if(!mParcelableString.get(UserIndex.USER_ID.ordinal()).isEmpty())
        {
            this.mUserId = Integer.parseInt(mParcelableString.get(UserIndex.USER_ID.ordinal()));
        }
        else
        {
            this.mUserId = null;
        }

        this.mDisplayName = mParcelableString.get(UserIndex.DISPLAY_NAME.ordinal());
        this.mEmail = mParcelableString.get(UserIndex.EMAIL.ordinal());
        this.mPhotoUrl = mParcelableString.get(UserIndex.PHOTO_URL.ordinal());
        this.mGoogleID = mParcelableString.get(UserIndex.GOOGLE_ID.ordinal());
    }

    public String getDisplayName()
    {
        return this.mDisplayName;
    }

    public void setDisplayName(String displayName)
    {
        this.mDisplayName = displayName;
    }

    public String getEmail()
    {
        return this.mEmail;
    }

    public void setEmail(String email)
    {
        this.mEmail = email;
    }

    public String getPhotoUrl() { return this.mPhotoUrl; }

    public void setPhotoUrl(String photoUrl) { this.mPhotoUrl = photoUrl; }

    public String getGoogleID() { return this.mGoogleID; }

    public void setGoogleID(String googleId)    { this.mGoogleID = googleId; }

    public Integer getUserId()
    {
        return this.mUserId;
    }

    public void setUserId(Integer productId)
    {
        this.mUserId = productId;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        ArrayList<String> values = new ArrayList<String>();

        if(this.mUserId == null)
        {
            values.add(UserIndex.USER_ID.ordinal(),"");
        }
        else
        {
            values.add(UserIndex.USER_ID.ordinal(),this.mUserId.toString());
        }
        values.add(UserIndex.DISPLAY_NAME.ordinal(),this.mDisplayName);
        values.add(UserIndex.EMAIL.ordinal(),this.mEmail);
        values.add(UserIndex.PHOTO_URL.ordinal(),this.mPhotoUrl);
        values.add(UserIndex.GOOGLE_ID.ordinal(),this.mGoogleID);

        dest.writeStringList(values);
    }

    public static final Parcelable.Creator<User> CREATOR
            = new Parcelable.Creator<User>() {

        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) { return new User[size]; }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public void SaveUser(ContentResolver contentResolver) {
        ContentValues values = new ContentValues();

        values.put(StoCountContract.UserEntry.DISPLAY_NAME, this.mDisplayName);
        values.put(StoCountContract.UserEntry.EMAIL, this.mEmail);
        values.put(StoCountContract.UserEntry.PHOTO_URL, this.mPhotoUrl);
        values.put(StoCountContract.UserEntry.GOOGLE_ID, this.mGoogleID);

        if(this.mUserId != null)
        {
            contentResolver.update(StoCountContract.UserEntry.CONTENT_URI, values, StoCountContract.UserEntry._ID + " = ? ", new String[] { this.mUserId.toString() } );
        }
        else
        {
            Uri result = contentResolver.insert(StoCountContract.UserEntry.CONTENT_URI, values);
            mUserId = Integer.parseInt(result.getLastPathSegment());
        }

    }
}
