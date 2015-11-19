package com.hengtan.nanodegreeapp.stocount;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;

/**
 * Created by Eric on 15/6/1.
 */
public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initToolbar();

      /*  FloatingActionMenu fam = (FloatingActionMenu) findViewById(R.id.menu_down);
        FloatingActionButton fab = (FloatingActionButton) fam.findViewById(fam.getMenuButtonId());

CoordinatorLayout cl = (CoordinatorLayout) findViewById(R.id.main_content);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) fam.getLayoutParams();
        params.setAnchorId(R.id.appbar);
        params.anchorGravity = Gravity.BOTTOM | Gravity.RIGHT | Gravity.END;
        params.setBehavior(new FloatingActionButtonBehavior(fab));
        fam.setLayoutParams(params);
*/

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("ScrollViewActivity");
    }

}
