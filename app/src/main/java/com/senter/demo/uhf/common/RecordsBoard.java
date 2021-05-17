package com.senter.demo.uhf.common;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;


import com.senter.demo.uhf.R;
import com.senter.demo.uhf.util.AlternateRowAdapter;

public class RecordsBoard {
    private final Activity owner;

    private final ListView lv;
    private final ArrayList<HashMap<String, String>> dataMerged = new ArrayList<HashMap<String, String>>();
    //private final SimpleAdapter adapterMergedData;
    public AlternateRowAdapter adapterMergedData;
    private final TextView tvCount;

    public RecordsBoard(Activity context, View ll) {
        owner = context;

        //adapterMergedData = new SimpleAdapter(owner, dataMerged, R.layout.common_lv2overlap_tv2, new String[]{ "1", "2", "3" }, new int[]{R.id.idE2CommenLv2OverlapTv1_tv0, R.id.idE2CommenLv2OverlapTv1_tv1, R.id.idE2CommenLv2OverlapTv1_tv2});
        adapterMergedData = new AlternateRowAdapter(owner, dataMerged, R.layout.common_lv2overlap_tv2, new String[]{ "1", "2", "3" }, new int[]{R.id.idE2CommenLv2OverlapTv1_tv0, R.id.idE2CommenLv2OverlapTv1_tv1, R.id.idE2CommenLv2OverlapTv1_tv2});
        lv = ll.findViewById(R.id.idE2CommenLv2Overlap_lvShow);
        lv.setAdapter(adapterMergedData);

        tvCount = ll.findViewById(R.id.idE2CommenLv2Overlap_tvTagsCount);

    }


    public void addMassage(final String string) {
        owner.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // update the merged data list
                int index = 0;
                for (; index < dataMerged.size(); index++) {
                    HashMap<String, String> omap = dataMerged.get(index);
                    if (omap.get("0").equals(string)) {// found it
                        int num = 1;

                        String numString = omap.get("1");
                        if (numString != null) {
                            try {
                                num = Integer.valueOf(numString);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        num++;
                        omap.put("1", "" + num);
                        break;
                    }
                }
                if (index == dataMerged.size()) {// not found in merged data list
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("0", string);
                    map.put("1", "" + 1);//tag count total
                    dataMerged.add(map);
                }
                tvCount.setText("" + dataMerged.size());

                adapterMergedData.notifyDataSetChanged();
            }
        });
    }

    public void addMassageNewTagInventoryDetected(final String uii, final String name, final String rssi, final String room, final String workplace) {
        owner.runOnUiThread(new Runnable() {
            public void run() {
                int i;
                for (i = 0; i < dataMerged.size(); i++) {
                    HashMap<String, String> hashMap = dataMerged.get(i);
                    if (hashMap.get("0").equals(uii)) {// found it
                        byte b = 1;
                        String str = hashMap.get("2");
                        int j = b;
                        if (str != null)
                            try {
                                j = Integer.valueOf(str).intValue();
                            } catch (Exception exception) {
                                exception.printStackTrace();
                                j = b;
                            }
                        StringBuilder stringBuilder1 = new StringBuilder();
                        stringBuilder1.append("");
                        stringBuilder1.append(j + 1);
                        hashMap.put("2", stringBuilder1.toString());
                        hashMap.put("3", rssi);
                        break;
                    }
                }
                if (i == dataMerged.size()) {// not found in merged data list
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put("0", uii);
                    if (name == "") {
                        hashMap.put("1", uii);
                    } else {
                        String str2 = name;
                        String str1 = str2;
                        if (room != "") {
                            StringBuilder stringBuilder1 = new StringBuilder();
                            stringBuilder1.append(str2);
                            stringBuilder1.append(" | ");
                            stringBuilder1.append(room);
                            str1 = stringBuilder1.toString();
                        }
                        str2 = str1;
                        hashMap.put("1", str2);
                    }
                    hashMap.put("2", "1");
                    hashMap.put("3", rssi);
                    dataMerged.add(hashMap);
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("");
                stringBuilder.append(dataMerged.size());
                tvCount.setText(stringBuilder.toString());
                adapterMergedData.notifyDataSetChanged();
            }
        });
    }
    public void resetMassages(final ArrayList<HashMap<String, String>> msgs) {
        owner.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dataMerged.clear();
                dataMerged.addAll(msgs);

                tvCount.setText("" + dataMerged.size());

                adapterMergedData.notifyDataSetChanged();
            }
        });
    }


    public void clearMsg() {// clear data
        owner.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dataMerged.clear();
                tvCount.setText("" + dataMerged.size());
                adapterMergedData.notifyDataSetChanged();
            }
        });
    }

    public ArrayList<HashMap<String, String>> getData() {
        ArrayList<HashMap<String, String>> ret = new ArrayList<HashMap<String, String>>();
        ret.addAll(dataMerged);
        return ret;
    }
}
