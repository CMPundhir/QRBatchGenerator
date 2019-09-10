package com.cmpundhir.cm.qrbatchgenerator.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;

import androidx.appcompat.app.AlertDialog;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class CheckNetworkAvailability {
    static Context context;
    public static boolean isNetworkAvailable(Context con) {
        context = con;
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        boolean res = connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
        if(!res){
           // showNetworkNotAvailableDialog(context);
        }
        return res;
    }
    public static boolean isInternetAvailable() {
        try {
            final InetAddress address = InetAddress.getByName("www.google.com");
            boolean res = !address.equals("");
            if(!res){
                showNetworkNotAvailableDialog(context);
            }
            return res;
        } catch (UnknownHostException e) {
            // Log error
        }
        return false;
    }
    private static void showNetworkNotAvailableDialog(final Context context){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("No Internet !!").setMessage("Your Internet Connection is not working.");
        builder.setPositiveButton("Activate Internet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity"));
                context.startActivity(intent);
            }
        });

        builder.setCancelable(true);
        builder.show();
    }

    public void isNetWorking(Context con){
        context = con;
        new NetworkAsyncTask().execute();
    }
    class NetworkAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            isInternetAvailable();
            return null;
        }
    }
}
