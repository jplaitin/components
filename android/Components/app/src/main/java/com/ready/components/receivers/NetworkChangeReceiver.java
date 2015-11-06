package com.ready.components.receivers;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.ready.components.utils.Lg;
import com.ready.components.utils.NetworkUtils;

/**
 * Needs permissions to work
 *     <uses-permission android:name="android.permission.INTERNET" />
 *     <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 *
 * Needs Classes
 *      NetworkUtils - for checking connection and showing dialog
 *      Lg - for logging
 *
 * Created by jarnolaitinen on 05/11/15.
 */
public abstract class NetworkChangeReceiver extends BroadcastReceiver {

    /* Use this value onNetworkChange function to determine if device is connected to network or not*/
    private boolean isConnectedToNetwork;
    private AlertDialog dialog;


    @Override
    public void onReceive(final Context context, final Intent intent) {
        isConnectedToNetwork = NetworkUtils.isNetworkAvailable(context);
        onNetworkChange(context, intent);
        if(!isConnectedToNetwork) {
            Lg.e("not connected");
            showDialog(context);
        }
        else {
            Lg.i("connected");
            hideDialog();
        }
    }

    public final void register(Context context){
        context.registerReceiver(this, getNetworkChangeFilters());
        if(isConnectedToNetwork()){
            hideDialog();
        }
        else{
            showDialog(context);
        }
    }

    public final void unregister(Context context){
        context.unregisterReceiver(this);
        hideDialog();
    }

    public final IntentFilter getNetworkChangeFilters(){
        IntentFilter networkChangeFilter = new IntentFilter();
        networkChangeFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeFilter.addAction("android.net.conn.WIFI_STATE_CHANGED");
        return  networkChangeFilter;
    }

    public final boolean isConnectedToNetwork(){
        return isConnectedToNetwork;
    }

    public final void hideDialog(){
        if(dialog != null && dialog.isShowing()) {
            Lg.i("hide dialog");
            dialog.dismiss();
            dialog = null;
        }
    }

    public final void showDialog(Context context)
    {
        if(dialog == null) {//dialog == null because if multiple ways to connect (like wifi and mobile) causes multiple onReceive connect events
            dialog = NetworkUtils.createNoNetworkDialog(context);
            dialog.show();
        }
        else{
            dialog.show();
        }
        Lg.i("show dialog is showing:"+ dialog.isShowing());
    }

    public abstract void onNetworkChange(Context context, Intent intent);
}