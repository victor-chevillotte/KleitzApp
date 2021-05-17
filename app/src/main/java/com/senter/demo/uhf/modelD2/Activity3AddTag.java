
package com.senter.demo.uhf.modelD2;
import android.content.Intent;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.senter.demo.uhf.R;
import com.senter.demo.uhf.common.Activity3AddTagCommonAbstract;

public class Activity3AddTag extends Activity3AddTagCommonAbstract {

    public String uiiOfFocus;
    public String name="";
    public String room="";
    public String workplace="";
    public Boolean newTag=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        uiiOfFocus = intent.getStringExtra("uii");
        TextView uiiDisplay = findViewById(R.id.AddTagUii);
        uiiDisplay.setText(uiiOfFocus);
        name = intent.getStringExtra("name");
        AutoCompleteTextView AddTagName = findViewById(R.id.AddTagName);
        AddTagName.setText(name);
        room = intent.getStringExtra("room");
        AutoCompleteTextView AddTagRoom = findViewById(R.id.AddTagRoom);
        AddTagRoom.setText(room);
        workplace = intent.getStringExtra("workplace");
        AutoCompleteTextView AddTagWorkplace = findViewById(R.id.AddTagWorkplace);
        AddTagWorkplace.setText(workplace);
        newTag = intent.getBooleanExtra("newTag", Boolean.parseBoolean("true"));
        if(!newTag){
            Button addModifyTagBtn = (Button) findViewById(R.id.AddTagSubmitBtn);
            addModifyTagBtn.setText("Modifier");
        }
    }

}
