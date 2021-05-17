package com.senter.demo.uhf.modelD2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.senter.demo.common.misc.ActivityHelper;
import com.senter.demo.uhf.R;
import com.senter.demo.uhf.modelD2.ConfigurationSettingsOfModelD2.Protocol;
import com.senter.iot.support.openapi.uhf.UhfD2;


public class Activity_FunctionSelection extends Activity {
    
    private final ConfigurationSettingsOfModelD2 mConfigurationSettingsOfModelD2 = ConfigurationSettingsOfModelD2.getInstance();
    
    private Protocol mCurrentProtocolOfModelD2 = null;
    
    private final ActivityHelper<Activity_FunctionSelection> ah = new ActivityHelper<Activity_FunctionSelection>(this);
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isSuccess = UhfD2.getInstance().init();
        if (!isSuccess) {
            ah.showToastLong("init failed");
            finish();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        UhfD2.getInstance().uninit();
    }
    
    @Override
    protected void onResume() {
        onCreateInitViews();
        super.onResume();
    }
    
    private class Views {
        public Views() {
            if ((mConfigurationSettingsOfModelD2.usingProtocol() == Protocol.I8k6c) && (mCurrentProtocolOfModelD2 == Protocol.I8k6c)) {
                return;
            } else if ((mConfigurationSettingsOfModelD2.usingProtocol() == Protocol.I8k6b) && (mCurrentProtocolOfModelD2 == Protocol.I8k6b)) {
                return;
            } else {
                //through down
            }
            
            setContentView(R.layout.activity0_function_selection_d2);


            if (mConfigurationSettingsOfModelD2.usingProtocol() == Protocol.I8k6c) {// 6C
                mCurrentProtocolOfModelD2 = Protocol.I8k6c;
                Button inventoryBtn=findViewById(R.id.inventoryButton);
                inventoryBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onIso6C_0Inventory();
                    }
                });
                
            }  else {
                throw new IllegalStateException();
            }
            
            Button settingsBtn=findViewById(R.id.settingsButton);
            settingsBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onActivitySettings();
                }
            });
        }
    }
    
    private void onCreateInitViews() {
        new Views();
    }
    
    private void onIso6C_0Inventory() {
        ah.startActivity(Activity1Inventory.class);
    }
    
    private void onActivitySettings() {
        ah.startActivity(Activity4Settings.class);
    }
    

    
}
