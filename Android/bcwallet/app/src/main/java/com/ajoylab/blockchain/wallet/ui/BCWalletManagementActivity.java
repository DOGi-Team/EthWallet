package com.ajoylab.blockchain.wallet.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ajoylab.blockchain.wallet.R;
import com.ajoylab.blockchain.wallet.common.BCConstants;
import com.ajoylab.blockchain.wallet.common.BCException;
import com.ajoylab.blockchain.wallet.model.BCViewPagerData;
import com.ajoylab.blockchain.wallet.model.BCWalletData;
import com.ajoylab.blockchain.wallet.utils.BCBalanceUtils;
import com.ajoylab.blockchain.wallet.viewmodel.BCImportWalletViewModel;
import com.ajoylab.blockchain.wallet.viewmodel.BCPaymentViewModel;
import com.ajoylab.blockchain.wallet.viewmodel.BCWalletManagementViewModel;

/**
 * Created by liuya on 2018/1/25.
 */

public class BCWalletManagementActivity extends BCBaseActivity implements BCWalletManagementAdapter.OnSetDefaultWalletListener
{
    private static final String TAG = "###BCWalletManageACT";

    private BCWalletManagementAdapter mAdapter;
    private BCWalletManagementViewModel mViewModel;
    private TextView mNoWalletText;
    private SwipeRefreshLayout mRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate 111");

        setContentView(R.layout.activity_wallet_management);
        setupToolbar();

        mNoWalletText = findViewById(R.id.noWalletText);
        mRefreshLayout = findViewById(R.id.refresh_layout);

        mAdapter = new BCWalletManagementAdapter(this);
        SwipeRefreshLayout refreshLayout = findViewById(R.id.refresh_layout);

        RecyclerView list = findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(mAdapter);

        mViewModel = ViewModelProviders.of(this).get(BCWalletManagementViewModel.class);
        mViewModel.walletList().observe(this, this::onGetWalletList);
        mViewModel.defaultWallet().observe(this, this::onGetDefaultWallet);
        mViewModel.isInProgress().observe(this, this::onIsInProgress);
        mViewModel.exception().observe(this, this::onException);
        refreshLayout.setOnRefreshListener(mViewModel::refreshWalletAccounts);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume 111");
        mViewModel.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_import, menu);
        //getMenuInflater().inflate(R.menu.menu_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int i = item.getItemId();
        Log.d(TAG, "onOptionsItemSelected 000 id: " + i);

        if (R.id.action_import == i) {
            Log.d(TAG, "onOptionsItemSelected 111");
            Intent intent = new Intent(this, BCImportWalletActivity.class);
            startActivityForResult(intent, BCConstants.REQUEST_CODE_IMPORT_WALLET);
        } else if (R.id.action_add == i) {
            Log.d(TAG, "onOptionsItemSelected 222");
        } else if (android.R.id.home == i) {
            Log.d(TAG, "onOptionsItemSelected 333");
            onBackPressed();
            return true;
        }

        Log.d(TAG, "onOptionsItemSelected 444");
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSetDefault(BCWalletData wallet) {
        Log.d(TAG, "onSetDefault 111");
        mViewModel.handleSelectDefaultWallet(wallet);
        mAdapter.setDefaultWallet(wallet);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult 111 requestCode: " + requestCode + " resultCode: " + resultCode);

        if (BCConstants.REQUEST_CODE_IMPORT_WALLET == requestCode) {

            Log.d(TAG, "onActivityResult 222 resultCode: " + resultCode);
            //showToolbar();
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "onActivityResult 333");
                //mViewModel.refreshWalletAccounts();
                new AlertDialog.Builder(this)
                        .setTitle(R.string.alert_dialog_title_wallet_imported)
                        .setPositiveButton(R.string.alert_dialog_button_ok, (dialog1, id) -> {
                        })
                        .create()
                        .show();
            }
        }
    }

    private void onGetWalletList(BCWalletData[] wallets) {
        Log.d(TAG, "onGetWalletList 111 count: " + wallets.length);
        if (wallets == null || wallets.length == 0) {
            Log.d(TAG, "onGetWalletList 222");
            mNoWalletText.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "onGetWalletList 333");
            mNoWalletText.setVisibility(View.GONE);
            mAdapter.setWallets(wallets);
        }

        mRefreshLayout.setRefreshing(false);
    }

    private void onGetDefaultWallet(BCWalletData wallet) {
        Log.d(TAG, "onGetDefaultWallet 111 addr: " + wallet.getAddress());
        mAdapter.setDefaultWallet(wallet);
    }

    private void onIsInProgress(boolean isInProgress) {
        Log.d(TAG, "onIsInProgress 111: " + isInProgress);
    }

    private void onException(BCException error) {
        Log.d(TAG, "onException 111");
        mRefreshLayout.setRefreshing(false);
    }
}
