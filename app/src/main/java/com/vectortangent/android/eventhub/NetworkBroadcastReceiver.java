package com.vectortangent.android.eventhub;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkBroadcastReceiver extends BroadcastReceiver {

    private NetworkConnectivityListener connectivityListener;
//
    public NetworkBroadcastReceiver(NetworkConnectivityListener listener){
        this.connectivityListener = listener;
    }
//
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null) {
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                ConnectivityManager cm =
                        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                if (!isConnected) {
                    connectivityListener.onNetworkChanged("Not Connected");
                } else {
                    connectivityListener.onNetworkChanged("Connected");
                }
            }
        }
    }
}
