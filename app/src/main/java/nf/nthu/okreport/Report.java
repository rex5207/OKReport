package nf.nthu.okreport;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.yalantis.phoenix.PullToRefreshView;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import nf.nthu.okreport.Fragments.CharInfo;
import nf.nthu.okreport.Fragments.ExpInfo;
import nf.nthu.okreport.Fragments.ItemInfo;

public class Report extends AppCompatActivity {
    public static String CharInfo_Report;
    public static String Exp_Report;
    public static String Item_Report;
    public static String account="";
    static ArrayList<String> accounts = new ArrayList<String>();
    public static PullToRefreshView mPullToRefreshView;
    ProgressDialog dialog;
    ViewPager viewPager;
    FragmentPagerItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        dialog = ProgressDialog.show(Report.this,
                "連線中", "取得資料中請稍候...", true);

        getConfig(Report.this);
        initAction();
        initTabs();
        initRefresh();
        initFirstView();

        Thread t1 = new Thread() {
            @Override
            public void run() {
                initAds();
            }
        };
        t1.start();
    }

    void getData() throws SQLException, ExecutionException, InterruptedException {
        if(account!= "" && account!=null) {
            String api_prev_url = "https://openkore-api.herokuapp.com/api/v1/all/report/";
            //*****************api start********************
            try {
                JSONObject j = new JSONObject(IOUtils.toString(new URL(api_prev_url + account), Charset.forName("UTF-8")));
                CharInfo_Report = DecodeBase64(j.getString("char_info"));
                Exp_Report = DecodeBase64(j.getString("exp_report"));
                Item_Report = DecodeBase64(j.getString("items_info"));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                CharInfo_Report = "目前沒有外掛資訊！";
                Exp_Report = "目前沒有外掛資訊！";
                Item_Report = "目前沒有外掛資訊！";
                e.printStackTrace();
            }
        }

        else{
            CharInfo_Report = "請新增外掛帳號！";
            Exp_Report = "請新增外掛帳號！";
            Item_Report = "請新增外掛帳號！";
        }

        //Update the View and Dismiss the loading dialog
        runOnUiThread(new Runnable() {
            public void run() {
                boolean temptop = ItemInfo.scview.isTop;
                int index = viewPager.getCurrentItem();
                viewPager.setAdapter(adapter);
                viewPager.setCurrentItem(index);
                ItemInfo.scview.isTop = temptop;
                dialog.dismiss();
                mPullToRefreshView.setRefreshing(false);
            }
        });

    }

        void initFirstView() {
            if(account=="" || account==null){
                CharInfo_Report = "請新增外掛帳號！";
                Exp_Report = "請新增外掛帳號！";
                Item_Report = "請新增外掛帳號！";
                dialog.dismiss();
            }
            else{
                Thread t1 = new Thread() {
                    @Override
                    public void run() {
                        try {
                            getData();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                t1.start();
            }
        }

        void initAction(){
        /**Add the Dropdown menu**/
        ArrayAdapter<String> barAdapter = new ArrayAdapter<String>(this, R.layout.myspinner,
                android.R.id.text1, accounts);
        barAdapter.setDropDownViewResource(R.layout.myspinner);
        // Callback
        ActionBar.OnNavigationListener callback = new ActionBar.OnNavigationListener() {
            @Override
            public boolean onNavigationItemSelected(int position, long id) {
                if(account == accounts.get(position))
                    return true;
                account = accounts.get(position);
                accounts.remove(position);
                accounts.add(0, account);
                initAction();
                //Show the Loading Dialog
                dialog = ProgressDialog.show(Report.this,
                        "連線中", "取得資料中請稍候...", true);

                Thread t1 = new Thread() {
                    @Override
                    public void run() {
                        try {
                            getData();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                t1.start();
                return true;
            }
        };

        // Action Bar
        ActionBar actions = getSupportActionBar();
        actions.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actions.setDisplayShowTitleEnabled(false);
        actions.setListNavigationCallbacks(barAdapter, callback);
        //Remove the shadow below actionbar
        actions.setElevation(0);
    }

    void initTabs() {
        //Add the fragment and Tab
        adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("角色資訊", CharInfo.class)
                .add("經驗值報告", ExpInfo.class)
                .add("道具列表", ItemInfo.class)
                .create());
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0 ) {
                    mPullToRefreshView.setEnabled(true);
                } else if (position == 1 && ExpInfo.scview.isTop == true) {
                    mPullToRefreshView.setEnabled(true);
                } else if (position == 1 && ExpInfo.scview.isTop == false) {
                    mPullToRefreshView.setEnabled(false);
                }else if (position == 2 && ItemInfo.scview.isTop == true) {
                    mPullToRefreshView.setEnabled(true);
                } else if (position == 2 && ItemInfo.scview.isTop == false) {
                    mPullToRefreshView.setEnabled(false);
                }
            }
        });
        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);
    }

    void initRefresh(){
        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Thread t1 = new Thread() {
                    @Override
                    public void run() {
                        try {
                            getData();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                t1.start();
            }
        });
    }

    void initAds(){
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3330018834665575~8779424840");
        final AdView mAdView = (AdView) findViewById(R.id.adView);
        final AdRequest adRequest = new AdRequest.Builder().build();
//        final AdRequest adRequest = new AdRequest.Builder().addTestDevice("4A92FF0F4551224FEFCB8C5316B360DD").build();
        runOnUiThread(new Runnable() {
            public void run() {
                mAdView.loadAd(adRequest);
            }
        });
    }

    String DecodeBase64(String base64) throws UnsupportedEncodingException {
        byte[] data = Base64.decode(base64, Base64.DEFAULT);
        String DCString = new String(data, "UTF-8");
        return DCString;
    }

    //設定檔更新
    public static void updateConfig(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        JSONArray jsonArray = new JSONArray();
        for(int i=0 ; i<accounts.size() ; i++) {
            jsonArray.put(accounts.get(i));
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("key", jsonArray.toString());
        editor.commit();
    }

    //設定檔寫入
    public static void setConfig(Context context,String account) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(account);
        for(int i=0 ; i<accounts.size() ; i++) {
            //帳號已存在
            if(account.equals(accounts.get(i))){
                Toast.makeText(context, "帳號已在清單內！", Toast.LENGTH_LONG).show();
                return;
            }
            jsonArray.put(accounts.get(i));
        }
        accounts.add(0, account);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("key", jsonArray.toString());
        editor.commit();
    }

    //設定檔讀取
    public static void getConfig(Context context){
        accounts.clear();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            JSONArray jsonArray2 = new JSONArray(prefs.getString("key", "[]"));
            for (int i = 0; i < jsonArray2.length(); i++) {
                accounts.add(jsonArray2.getString(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(accounts.isEmpty()!=true)
            account = accounts.get(0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_adding) {
            final View layout= LayoutInflater.from(Report.this).inflate(R.layout.add_account, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(Report.this)
                    .setTitle("請輸入帳號")
                    .setView(layout)
                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText editText = (EditText) layout.findViewById(R.id.edittext);
                            editText.requestFocus();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                            setConfig(Report.this, editText.getText().toString());
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    initAction();
                                }
                            });
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                public void onShow(DialogInterface dialog) {
                    EditText editText = (EditText) (layout.findViewById(R.id.edittext));
                    editText.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                }
            });
            alertDialog.show();
            return true;
        }

        else if (id == R.id.action_deleting) {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("移除帳號")
                    .setContentText("確定要從清單上移除帳號嗎？")
                    .setCancelText("取消")
                    .setConfirmText("確定")
                    .showCancelButton(true)
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            accounts.remove(getSupportActionBar().getSelectedNavigationIndex());
                            updateConfig(Report.this);
                            if (accounts.isEmpty() == true) {
                                CharInfo_Report = "請新增外掛帳號！";
                                Exp_Report = "請新增外掛帳號！";
                                Item_Report = "請新增外掛帳號！";
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        viewPager.setAdapter(adapter);
                                    }
                                });
                            }
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    initAction();
                                }
                            });
                        }
                    })
                    .show();
        }


        return super.onOptionsItemSelected(item);
    }
}
