package com.senter.demo.uhf.modelD2.iso18k6b;

import android.os.Bundle;
import android.util.Log;

import com.senter.demo.uhf.common.Activity1InventoryCommonAbstract;
import com.senter.demo.uhf.util.DataTransfer;
import com.senter.iot.support.openapi.uhf.UhfD2;
import com.senter.iot.support.openapi.uhf.UhfD2.UmdErrorCode;
import com.senter.iot.support.openapi.uhf.UhfD2.UmdOnIso18k6bInventory;

public class Activity1Inventory extends Activity1InventoryCommonAbstract {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void uiOnInverntryButton() {
        startInventory();
    }


    private void startInventory() {
//		Log.e("", "Activity0Inventory: startInventory");
//		getViews().enableInventoryButton(false);
//		viewsSetAsStarted();

        UhfD2.getInstance().iso18k6bInventory(new UmdOnIso18k6bInventory() {

            @Override
            public void onTagInventory(Iso18k6bInventoryResult rslt, int antId) {
                Log.e("", "onTagInventory:" + DataTransfer.xGetString(rslt.getUid().getBytes()));
                addNewUiiMassageToListview("Uid:" + DataTransfer.xGetString(rslt.getUid().getBytes()));
            }

            @Override
            public void onFinishedWithError(UmdErrorCode error) {
//				Log.e("", "onFinishedWithError:"+error);
//				getViews().enableInventoryButton(true);
//				viewsSetAsStoped();
            }

            @Override
            public void onFinishedSuccessfully(int antId, int tagFound) {
//				Log.e("", "onFinishedSuccessfully:"+tagFound);
//				getViews().enableInventoryButton(true);
//				viewsSetAsStoped();
            }
        });
    }


}
