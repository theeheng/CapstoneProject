package com.hengtan.nanodegreeapp.stocount;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.hengtan.nanodegreeapp.stocount.data.DBAsyncCallBack;
import com.hengtan.nanodegreeapp.stocount.data.DBAsyncTask;
import com.hengtan.nanodegreeapp.stocount.data.ProductCount;
import com.hengtan.nanodegreeapp.stocount.data.StockPeriod;
import com.hengtan.nanodegreeapp.stocount.data.User;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.InjectView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StockPeriodActivity extends AppCompatActivity implements OnDateSelectedListener, OnMonthChangedListener, DBAsyncCallBack {

    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();

    @InjectView(R.id.calendarView)
    protected MaterialCalendarView widget;

    @InjectView(R.id.user_avatar)
    protected ImageView userAvatar;

    @InjectView(R.id.user_name)
    protected TextView userName;

    @InjectView(R.id.user_email)
    protected TextView userEmail;

    @InjectView(R.id.fab)
    protected FloatingActionButton mFabButton;

    public static final String IS_CLOSE_STOCK_EXTRA = "ISCLOSESTOCKEXTRA";

    private User mUser;

    private StockPeriod mStockPeriod;
    private StockPeriod mNewStockPeriod;

    private boolean mIsClosingStockCount = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_period);
        ButterKnife.inject(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        widget.setOnDateChangedListener(this);
        widget.setOnMonthChangedListener(this);


        mFabButton.setVisibility(View.GONE);

        if (savedInstanceState != null && savedInstanceState.containsKey(IS_CLOSE_STOCK_EXTRA)) {
            mIsClosingStockCount = savedInstanceState.getBoolean(IS_CLOSE_STOCK_EXTRA, false);
        }
        else
        {
            //Bundle arguments = getArguments();
            Intent intent = getIntent();

            mIsClosingStockCount = intent.getBooleanExtra(IS_CLOSE_STOCK_EXTRA, false);
        }

        if(mIsClosingStockCount)
        {
            mStockPeriod = Application.getCurrentStockPeriod();

            if(mStockPeriod != null)
            {
                userName.setText("Close Stock Period");
                userEmail.setText("Stock Period Starting : "+mStockPeriod.DateFormat.format(mStockPeriod.getStartDate()));
                userAvatar.setVisibility(View.GONE);
            }
        }
        else
        {
            mUser = Application.getCurrentLoginUser();

            if(mUser != null) {
                userName.setText(mUser.getDisplayName());
                userEmail.setText(mUser.getEmail());
                Glide.with(this).load(mUser.getPhotoUrl()).asBitmap().into(new BitmapImageViewTarget(userAvatar) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(StockPeriodActivity.this.getResources(), resource);

                        circularBitmapDrawable.setCornerRadius(Math.max(resource.getWidth(), resource.getHeight()) / 2.0f);
                        //circularBitmapDrawable.setCircular(true);
                        userAvatar.setImageDrawable(circularBitmapDrawable);
                    }
                });
            }
        }
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @Nullable CalendarDay date, boolean selected) {
        //textView.setText(getSelectedDatesString());
        mFabButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        //noinspection ConstantConditions
        //getSupportActionBar().setTitle(FORMATTER.format(date.getDate()));
    }

    private String getSelectedDatesString() {
        CalendarDay date = widget.getSelectedDate();
        if (date == null) {
            return "No Selection";
        }
        return FORMATTER.format(date.getDate());
    }

    @OnClick(R.id.fab)
    protected void GoToHome()
    {
        CalendarDay widgetSelectedDate = widget.getSelectedDate();

        DBAsyncTask dbAsyncTask = new DBAsyncTask(getContentResolver(), DBAsyncTask.ObjectType.STOCK_PERIOD, DBAsyncTask.OperationType.SAVE, this);

        if(mIsClosingStockCount && mStockPeriod != null)
        {
            dbAsyncTask.setObjectType(DBAsyncTask.ObjectType.CLOSE_STOCK_PERIOD);

            mStockPeriod.setEndDate(widgetSelectedDate.getDate());

            mNewStockPeriod = new StockPeriod();

            Calendar c = Calendar.getInstance();
            c.setTime(widgetSelectedDate.getDate());
            c.add(Calendar.DATE, 1);

            mNewStockPeriod.setStartDate(c.getTime());

            dbAsyncTask.execute(mStockPeriod, mNewStockPeriod);
        }
        else if(!mIsClosingStockCount && mStockPeriod == null) {

            mStockPeriod = new StockPeriod();
            mStockPeriod.setStartDate(widgetSelectedDate.getDate());

            dbAsyncTask.execute(mStockPeriod);
        }
    }

    @Override
    public void CallBackOnSuccessfull() {

        if(mIsClosingStockCount && mNewStockPeriod != null) {
            Application.setCurrentStockPeriod(mNewStockPeriod);
        }
        else if((!mIsClosingStockCount)&&(mStockPeriod != null))
        {
            Application.setCurrentStockPeriod(mStockPeriod);
        }

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    public void CallBackOnFail() {

    }
}


