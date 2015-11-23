package com.kopi.beaconfit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android. webkit.WebView;
import android. webkit.WebViewClient;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import java.util.Collection;

import org.altbeacon.beacon.AltBeacon;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {
    private BeaconManager beaconManager;
    protected static final String TAG = "RangingActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        try{
            beaconManager.setForegroundBetweenScanPeriod(0l);
            beaconManager.setForegroundScanPeriod(1100l);
            beaconManager.updateScanPeriods();
        }
        catch (RemoteException e){
            Log.e(TAG, "Cannot talk to BLE server");
        }
        String url = "http://www.beaconfit.net";
        WebView view = (WebView) this.findViewById(R.id.webView);
        view.setWebViewClient(new WebViewClient());
        view.getSettings().setJavaScriptEnabled(true);
        view.loadUrl(url);
        beaconManager.bind(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(false);
    }
    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    //EditText editText = (EditText)RangingActivity.this.findViewById(R.id.rangingText);
                    Beacon firstBeacon = beacons.iterator().next();
                    Log.i(TAG, "The first beacon I see is about "+beacons.iterator().next().getDistance()+" meters away.");
                }
            }

        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("beaconfit", Identifier.parse("C02EB7EB-C0BE-B04A-CA2B-CB0BA655820A"),null, null));
        } catch (RemoteException e) {
        }
    }

    private void logToDisplay(final String line) {
        runOnUiThread(new Runnable() {
            public void run() {

            }
        });
    }
}
