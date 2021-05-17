package com.senter.demo.uhf.modelD2;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.senter.demo.uhf.R;
import com.senter.demo.uhf.common.Activity_Abstract;
import com.senter.iot.support.openapi.uhf.UhfD2;

public class Activity4Settings extends Activity_Abstract {
    private EditText powerEditText;
    private Button powerGetButton;
    private Button powerSetButton;
    
    private TextView temporaryTextView;
    private Button temporaryGetButton;

    private Spinner profileSpinner;
    private Button profileGetButton;
    private Button profileSetButton;
    
    private Spinner sp_frequency_region;
    private Spinner sp_frequency_start;
    private Spinner sp_frequency_end;
    private Button btn_region_read;
    private Button btn_region_set;
    
    List<UhfD2.UmdFrequencyPoint> FCCPointList = new ArrayList<>();
    List<UhfD2.UmdFrequencyPoint> ETSIPointList = new ArrayList<>();
    List<UhfD2.UmdFrequencyPoint> CHNPointList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity4settingsactivity_d2);
        initData();
        
        powerEditText = findViewById(R.id.idE27SettingsActivityD2_Power_etShow);
        powerGetButton = findViewById(R.id.idE27SettingsActivityD2_Power_btnRead);
        powerSetButton = findViewById(R.id.idE27SettingsActivityD2_Power_btnSet);
        
        powerGetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnPowerGet();
            }
        });
        
        powerSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnPowerSet();
            }
        });
        
        onBtnPowerGet();
        
        LinearLayout ll = findViewById(R.id.idE27SettingsActivityD2_Temporary_ll);
        ll.setVisibility(View.VISIBLE);
        temporaryTextView = findViewById(R.id.idE27SettingsActivityD2_Temporary_tvShow);
        temporaryGetButton = findViewById(R.id.idE27SettingsActivityD2_Temporary_btnRead);
        temporaryGetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnTemporaryGet();
            }
        });
        
        onBtnTemporaryGet();
        
        profileSpinner = findViewById(R.id.idE27SettingsActivityD2_Profile_SpProfiles);
        profileGetButton = findViewById(R.id.idE27SettingsActivityD2_Profile_btnRead);
        profileSetButton = findViewById(R.id.idE27SettingsActivityD2_Profile_btnSet);
        
        profileGetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnProfileGet();
            }
        });
        profileSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnProfileSet();
            }
        });
        onBtnProfileGet();
        
        sp_frequency_region = findViewById(R.id.sp_frequency_region);
        sp_frequency_start = findViewById(R.id.sp_frequency_start);
        sp_frequency_end = findViewById(R.id.sp_frequency_end);
        btn_region_read = findViewById(R.id.btn_region_read);
        btn_region_set = findViewById(R.id.btn_region_set);
        btn_region_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnFrequencyRegionGet();
            }
        });
        btn_region_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnFrequencyRegionSet();
            }
        });
        
        sp_frequency_region.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sp_frequency_start.setAdapter(new ArrayAdapter(Activity4Settings.this, android.R.layout.simple_spinner_item, getCurrentPointList()));
                sp_frequency_end.setAdapter(new ArrayAdapter(Activity4Settings.this, android.R.layout.simple_spinner_item, getCurrentPointList()));
                sp_frequency_end.setSelection(getCurrentPointList().size() - 1);
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            
            }
        });
        
        onBtnFrequencyRegionGet();
        
    }
    
    protected void onBtnPowerGet() {
        Integer power = UhfD2.getInstance().getOutputPower();
        if (power == null) {
            showToast("power get failed");
        }
        powerEditText.setText("" + power);
    }
    
    protected void onBtnPowerSet() {
        Integer power = null;
        try {
            power = Integer.valueOf(powerEditText.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
            showToast("power format error");
            return;
        }
        
        if (power < 0 || power > 32) {
            showToast("power must be in [0,33]");
            return;
        }
        
        Boolean ret = UhfD2.getInstance().setOutputPower(power);
        if (ret == null || ret == false) {
            showToast("set power failed");
        } else {
            showToast("power set successfully");
        }
    }
    
    protected void onBtnTemporaryGet() {
        Integer t = UhfD2.getInstance().getReadersTemperature();
        
        if (t == null) {
            temporaryTextView.setText("");
        } else {
            temporaryTextView.setText("" + t + " ℃");
        }
    }
    
    protected void onBtnProfileGet() {
        UhfD2.UmdLinkProfile linkProfile = UhfD2.getInstance().getLinkProfile();
        if (linkProfile != null) {
            switch (linkProfile) {
                case LinkProfile0:
                    profileSpinner.setSelection(0);
                    break;
                case LinkProfile1:
                    profileSpinner.setSelection(1);
                    break;
                case LinkProfile2:
                    profileSpinner.setSelection(2);
                    break;
                case LinkProfile3:
                    profileSpinner.setSelection(3);
                    break;
            }
        } else {
            showToast("get profile failed");
        }
    }
    
    protected void onBtnProfileSet() {
        
        UhfD2.UmdLinkProfile linkProfile = UhfD2.UmdLinkProfile.LinkProfile0;
        
        switch (profileSpinner.getSelectedItemPosition()) {
            case 0:
                linkProfile = UhfD2.UmdLinkProfile.LinkProfile0;
                break;
            case 1:
                linkProfile = UhfD2.UmdLinkProfile.LinkProfile1;
                break;
            case 2:
                linkProfile = UhfD2.UmdLinkProfile.LinkProfile2;
                break;
            case 3:
                linkProfile = UhfD2.UmdLinkProfile.LinkProfile3;
                break;
        }
        
        Boolean isSuccess = UhfD2.getInstance().setLinkProfile(linkProfile);
        if (isSuccess != null && isSuccess) {
            showToast("set profile successfully");
        } else {
            showToast("set profile failed");
        }
        
    }
    
    protected void onBtnFrequencyRegionGet() {
        final UhfD2.UmdFrequencyRegionInfo regionInfo = UhfD2.getInstance().getFrequencyRegion();
        if (regionInfo != null) {
            final UhfD2.UmdFrequencyRegion region = regionInfo.getRegion();
            switch (region) {
                case FCC:
                    sp_frequency_region.setSelection(0);
                    break;
                case ETSI:
                    sp_frequency_region.setSelection(1);
                    break;
                case CHN:
                    sp_frequency_region.setSelection(2);
                    break;
            }
            
            new Thread(new Runnable() {
                @Override
                public void run() {
                    
                    SystemClock.sleep(200);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            UhfD2.UmdFrequencyPoint startPoint = regionInfo.getStart();
                            UhfD2.UmdFrequencyPoint endPoint = regionInfo.getEnd();
                            
                            if (startPoint != null) {
                                int p = 0;
                                List<UhfD2.UmdFrequencyPoint> list = getCurrentPointList();
                                for (int i = 0; i < list.size(); i++) {
                                    if (startPoint.getFrequencyByKHz() == list.get(i).getFrequencyByKHz()) {
                                        p = i;
                                        break;
                                    }
                                }
                                sp_frequency_start.setSelection(p);
                            }
                            if (endPoint != null) {
                                int p = 0;
                                List<UhfD2.UmdFrequencyPoint> list = getCurrentPointList();
                                for (int i = 0; i < list.size(); i++) {
                                    if (endPoint.getFrequencyByKHz() == list.get(i).getFrequencyByKHz()) {
                                        p = i;
                                        break;
                                    }
                                }
                                sp_frequency_end.setSelection(p);
                            } else {
                                sp_frequency_end.setSelection(getCurrentPointList().size() - 1);
                            }
                            
                            Log.d("mine", "region->" + region.name() + " start->" + startPoint.name() + " end->" + endPoint.name());
                        }
                    });
                    
                }
            }).start();
            
        } else {
            showToast("get region failed");
        }
    }
    
    protected void onBtnFrequencyRegionSet() {
        UhfD2.UmdFrequencyRegion region = null;
        switch (sp_frequency_region.getSelectedItemPosition()) {
            case 0:
                region = UhfD2.UmdFrequencyRegion.FCC;
                break;
            case 1:
                region = UhfD2.UmdFrequencyRegion.ETSI;
                break;
            case 2:
                region = UhfD2.UmdFrequencyRegion.CHN;
                break;
        }
        
        boolean isSuccess = UhfD2.getInstance().setFrequencyRegion(region,
                                                                   getCurrentPointList().get(sp_frequency_start.getSelectedItemPosition()),
                                                                   getCurrentPointList().get(sp_frequency_end.getSelectedItemPosition()));
        if (isSuccess) {
            showToast("set region success");
        } else {
            showToast("set region failed");
        }
        
    }
    
    /**
     * 初始化数据
     */
    private void initData() {
        UhfD2.UmdFrequencyPoint[] points = UhfD2.UmdFrequencyPoint.values();
        for (UhfD2.UmdFrequencyPoint point : points) {
            if (point.getFrequencyByKHz() >= 865000 && point.getFrequencyByKHz() <= 868000) {
                //欧
                ETSIPointList.add(point);
                continue;
            }
            if (point.getFrequencyByKHz() >= 902000 && point.getFrequencyByKHz() <= 928000) {
                //美
                if (point.getFrequencyByKHz() >= 920000 && point.getFrequencyByKHz() <= 925000) {
                    CHNPointList.add(point);
                }
                FCCPointList.add(point);
            }
        }
    }
    
    /**
     * 获取当前的列表
     *
     * @return
     */
    public List<UhfD2.UmdFrequencyPoint> getCurrentPointList() {
        int position = sp_frequency_region.getSelectedItemPosition();
        switch (position) {
            case 0:
                return FCCPointList;
            case 1:
                return ETSIPointList;
            case 2:
                return CHNPointList;
        }
        return null;
    }
    
}
