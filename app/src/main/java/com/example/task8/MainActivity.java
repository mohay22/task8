package com.example.task8;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {
    private static final int STORAGE_PERMISSION_CODE = 100;
    private NetworkReceiver networkReceiver;
    private ViewPager2 viewPager;
    private View rootView;
    private DisplayFragment displayFragment;  // Reference to update

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupViewPager();
        setupNetworkMonitoring();
        checkNetworkStatus();
        checkStoragePermission();
    }

    private void initializeViews() {
        viewPager = findViewById(R.id.viewPager);
        rootView = findViewById(R.id.rootLayout);
    }

    private void setupViewPager() {
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(position == 0 ? "Upload" : "Display")
        ).attach();

        // Store reference to DisplayFragment
        displayFragment = (DisplayFragment) adapter.createFragment(1);
    }

    private void setupNetworkMonitoring() {
        networkReceiver = new NetworkReceiver(this);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);
    }

    private void checkNetworkStatus() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            showConnectedMessage();
        } else {
            showDisconnectedMessage();
        }
    }

    public void showConnectedMessage() {
        Toast.makeText(this, "Connected to the Internet!", Toast.LENGTH_SHORT).show();
    }

    public void showDisconnectedMessage() {
        Toast.makeText(this, "No Internet Connection. Local video features still available.",
                Toast.LENGTH_LONG).show();
        if (rootView != null) {
            Snackbar.make(rootView, "Offline: Local videos still accessible", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Retry", v -> checkNetworkStatus())
                    .show();
        }
        showNetworkSettingsDialog();
    }

    private void showNetworkSettingsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Network Unavailable")
                .setMessage("No internet detected. Enable WiFi or Mobile Data? Local video upload and display still work.")
                .setPositiveButton("Settings", (dialog, which) -> {
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                })
                .setNegativeButton("Continue Offline", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkReceiver != null) {
            unregisterReceiver(networkReceiver);
            networkReceiver = null;
        }
    }

    // New method to notify DisplayFragment
    public void notifyVideoAdded() {
        if (displayFragment != null) {
            displayFragment.refreshVideoList();
        }
    }
}