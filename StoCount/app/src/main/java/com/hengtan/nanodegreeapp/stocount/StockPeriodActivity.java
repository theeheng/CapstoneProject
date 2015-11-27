package com.hengtan.nanodegreeapp.stocount;

<<<<<<< HEAD
<<<<<<< HEAD
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;

public class StockPeriodActivity extends AppCompatActivity {

    CalendarView calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_period);


        //initializes the calendarview

        initializeCalendar();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void initializeCalendar() {


        calendar = (CalendarView) findViewById(R.id.calendarView);



        // sets whether to show the week number.

        calendar.setShowWeekNumber(false);



        // sets the first day of week according to Calendar.

        // here we set Monday as the first day of the Calendar
        calendar.setFirstDayOfWeek(2);


        //The background color for the selected week.

        calendar.setSelectedWeekBackgroundColor(getResources().getColor(R.color.green));



        //sets the color for the dates of an unfocused month.

        calendar.setUnfocusedMonthDateColor(getResources().getColor(R.color.transparent));



        //sets the color for the separator line between weeks.

        calendar.setWeekSeparatorLineColor(getResources().getColor(R.color.transparent));



        //sets the color for the vertical bar shown at the beginning and at the end of the selected date.

        calendar.setSelectedDateVerticalBar(R.color.darkgreen);



        //sets the listener to be notified upon selected date change.

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

                    //show the selected date as a toast

            @Override

            public void onSelectedDayChange(CalendarView view, int year, int month, int day) {

                Toast.makeText(getApplicationContext(), day + "/" + month + "/" + year, Toast.LENGTH_LONG).show();

            }

        });



    }


}
=======
=======
>>>>>>> origin/master
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

        if (savedInstanceState != null && savedInstanceState.containsKey(USER_PARCELABLE)) {
            mUser = savedInstanceState.getParcelable(USER_PARCELABLE);
        }
        else
        {
            //Bundle arguments = getArguments();
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();

            if(bundle.get(USER_PARCELABLE) != null)
            {
                bundle = (Bundle) bundle.get(USER_PARCELABLE);
                mUser = bundle.getParcelable(USER_PARCELABLE);
            }
        }

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
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
<<<<<<< HEAD
}
>>>>>>> origin/master
=======
}
>>>>>>> origin/master
