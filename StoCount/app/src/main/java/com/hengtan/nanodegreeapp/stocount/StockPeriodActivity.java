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
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import butterknife.InjectView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StockPeriodActivity extends AppCompatActivity implements OnDateSelectedListener, OnMonthChangedListener {

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

    public static final String USER_PARCELABLE = "USERPARCELABLE";

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_period);
        ButterKnife.inject(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        widget.setOnDateChangedListener(this);
        widget.setOnMonthChangedListener(this);

        //Setup initial text
        //textView.setText(getSelectedDatesString());

        mFabButton.setVisibility(View.GONE);

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

        StockPeriod stockPeriod = new StockPeriod();
        stockPeriod.setStartDate(widgetSelectedDate.getDate());
        stockPeriod.SaveStockPeriod(getContentResolver());

        Application.setCurrentStockPeriod(stockPeriod);

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

}


