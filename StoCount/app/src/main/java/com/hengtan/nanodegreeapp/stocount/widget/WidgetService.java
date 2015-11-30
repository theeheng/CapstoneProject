package com.hengtan.nanodegreeapp.stocount.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;


public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}
