package com.jio.devicetracker.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.jio.devicetracker.R;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

public class DeviceNameActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_name);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.DEVICE_NAME_TITLE);
        Button backBtn = findViewById(R.id.back);
        backBtn.setVisibility(View.VISIBLE);
        title.setTypeface(Util.mTypeface(this,5));
        TextView iconSelectionText = findViewById(R.id.icon_selection);
        iconSelectionText.setTypeface(Util.mTypeface(this,3));
        RelativeLayout toolbarLayout = findViewById(R.id.toolbarlayout);
        toolbarLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.cardviewlayout_device_background_color));
        initiateUI();


    }

    public void initiateUI(){
        ImageView motherIcon = findViewById(R.id.mother_icon);
        ImageView fatherIcon = findViewById(R.id.father_icon);
        ImageView husbandIcon = findViewById(R.id.husband_icon);
        ImageView wifeIcon = findViewById(R.id.wife_icon);
        ImageView kidIcon = findViewById(R.id.kid_icon);
        ImageView otherIcon = findViewById(R.id.other_icon);
        ImageView dogIcon = findViewById(R.id.dog_icon);
        ImageView catIcon = findViewById(R.id.cat_icon);
        ImageView otherPetIcon = findViewById(R.id.other_pet_icon);

        motherIcon.setOnClickListener(this);
        fatherIcon.setOnClickListener(this);
        wifeIcon.setOnClickListener(this);
        husbandIcon.setOnClickListener(this);
        kidIcon.setOnClickListener(this);
        otherIcon.setOnClickListener(this);
        dogIcon.setOnClickListener(this);
        catIcon.setOnClickListener(this);
        otherPetIcon.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.mother_icon:
                getNameIcon("Mother");
                break;
            case R.id.father_icon:
                getNameIcon("Father");
                break;
            case R.id.husband_icon:
                getNameIcon("Husband");
                break;
            case R.id.wife_icon:
                getNameIcon("Wife");
                break;
            case R.id.kid_icon:
                getNameIcon("Kid");
                break;
            case R.id.other_icon:
                getNameIcon("Other");
                break;
            case R.id.dog_icon:
                getNameIcon("Dog");
                break;
            case R.id.cat_icon:
                getNameIcon("Cat");
                break;
            case R.id.other_pet_icon:
                getNameIcon("OtherPet");
                break;

            default:
                break;

        }

    }

    /**
     *Provide the icon title to select for device.
     */
    private void getNameIcon(String title) {
       /* Intent intent = new Intent(this,QRReaderInstruction.class);
        intent.putExtra("Title",title);
        startActivity(intent);*/

    }
}
