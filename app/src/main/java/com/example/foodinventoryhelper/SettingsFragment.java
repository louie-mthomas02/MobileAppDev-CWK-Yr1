package com.example.foodinventoryhelper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.foodinventoryhelper.browseRecipes.ViewRecipeActivity;
import com.google.android.material.slider.RangeSlider;

public class SettingsFragment extends Fragment {

    CheckBox kcal, protein, sodium, carb, isVeg, noGluten, noLactose;
    RangeSlider kcalSlider, proteinSlider, sodiumSlider, carbSlider;
    Button resetBtn;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        //When opening the fragment, locate all the user-editable elements
        isVeg = (CheckBox) v.findViewById(R.id.vegOnlyCheckBox);
        noGluten = (CheckBox) v.findViewById(R.id.glutenFreeCheckBox);
        noLactose = (CheckBox) v.findViewById(R.id.lactoseIntolerantCheckBox);

        kcal = (CheckBox) v.findViewById(R.id.enable_kcal_range);
        kcalSlider = v.findViewById(R.id.kcalRangeSlider);

        protein = (CheckBox) v.findViewById(R.id.enable_protein_range);
        proteinSlider = v.findViewById(R.id.protein_range_slider);

        sodium = (CheckBox) v.findViewById(R.id.enable_sodium_range);
        sodiumSlider = v.findViewById(R.id.sodiumRangeSlider);

        carb = (CheckBox) v.findViewById(R.id.enable_carb_range);
        carbSlider = v.findViewById(R.id.carbRangeSlider);

        resetBtn = v.findViewById(R.id.resetBtn);
        Button userGuideBtn = v.findViewById(R.id.userGuideBtn);
        //////////////////////////////////////////////////////////////////


        //Make each of the checkboxes enable/disable their respective limit sliders
        kcal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (kcal.isChecked()){
                    kcalSlider.setEnabled(true);
                } else {
                    kcalSlider.setEnabled(false);
                }
            }
        });

        protein.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (protein.isChecked()){
                    proteinSlider.setEnabled(true);
                } else {
                    proteinSlider.setEnabled(false);
                }
            }
        });

        sodium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sodium.isChecked()){
                    sodiumSlider.setEnabled(true);
                } else {
                    sodiumSlider.setEnabled(false);
                }
            }
        });

        carb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (carb.isChecked()){
                    carbSlider.setEnabled(true);
                } else {
                    carbSlider.setEnabled(false);
                }
            }
        });
        ///////////////////////////////////////////////////////////////////////////

        //Make the reset button force the checkboxes/sliders to default values
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isVeg.setChecked(false);
                noGluten.setChecked(false);
                noLactose.setChecked(false);

                kcal.setChecked(false);
                protein.setChecked(false);
                sodium.setChecked(false);
                carb.setChecked(false);

                kcalSlider.setEnabled(false);
                proteinSlider.setEnabled(false);
                sodiumSlider.setEnabled(false);
                carbSlider.setEnabled(false);

                kcalSlider.setValues((float) 2000, (float) 2500);
                proteinSlider.setValues((float) 17, (float) 30);
                sodiumSlider.setValues((float) 250, (float) 500);
                carbSlider.setValues((float) 60,(float) 75);

                //Alert to the user that their settings have been reset
                Context ctx = getContext();
                CharSequence txt = "Settings Reset to Default Values";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(ctx, txt, duration);
                toast.show();
                ////////////////////////////////////////////////////////
            }
        });
        //////////////////////////////////////////////////////////////////////

        userGuideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getActivity(), UserGuide.class);
                startActivity(i);
            }
        });

        return v;
    }

    //When opening the fragment, set the checkboxes/sliders to previously set values
    @Override
    public void onResume(){
        super.onResume();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);

        isVeg.setChecked(sharedPreferences.getBoolean("isVegetarian", false));
        noGluten.setChecked(sharedPreferences.getBoolean("glutenFree", false));
        noLactose.setChecked(sharedPreferences.getBoolean("lactoseIntolerant", false));


        kcal.setChecked(sharedPreferences.getBoolean("kcalLimit", false));
        kcalSlider.setEnabled(sharedPreferences.getBoolean("kcalLimit", false));
        kcalSlider.setValues(sharedPreferences.getFloat("kcalMin", 2000), sharedPreferences.getFloat("kcalMax", 2500));

        protein.setChecked(sharedPreferences.getBoolean("proteinLimit", false));
        proteinSlider.setEnabled(sharedPreferences.getBoolean("proteinLimit", false));
        proteinSlider.setValues(sharedPreferences.getFloat("proteinMin", 17), sharedPreferences.getFloat("proteinMax", 30));

        sodium.setChecked(sharedPreferences.getBoolean("sodiumLimit", false));
        sodiumSlider.setEnabled(sharedPreferences.getBoolean("sodiumLimit", false));
        sodiumSlider.setValues(sharedPreferences.getFloat("sodiumMin", 250), sharedPreferences.getFloat("sodiumMax", 500));

        carb.setChecked(sharedPreferences.getBoolean("carbLimit", false));
        carbSlider.setEnabled(sharedPreferences.getBoolean("carbLimit", false));
        carbSlider.setValues(sharedPreferences.getFloat("carbMin", 60), sharedPreferences.getFloat("carbMax", 75));
    }


    //When closing the fragment, store the values that the checkboxes/sliders currently are
    @Override
    public void onPause(){
        super.onPause();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor myEditor = sharedPreferences.edit();

        myEditor.putBoolean("isVegetarian", isVeg.isChecked());
        myEditor.putBoolean("glutenFree", noGluten.isChecked());
        myEditor.putBoolean("lactoseIntolerant", noLactose.isChecked());

        myEditor.putBoolean("kcalLimit", kcal.isChecked());
        myEditor.putFloat("kcalMin", kcalSlider.getValues().get(0));
        myEditor.putFloat("kcalMax", kcalSlider.getValues().get(1));

        myEditor.putBoolean("proteinLimit", protein.isChecked());
        myEditor.putFloat("proteinMin", proteinSlider.getValues().get(0));
        myEditor.putFloat("proteinMax", proteinSlider.getValues().get(1));

        myEditor.putBoolean("sodiumLimit", sodium.isChecked());
        myEditor.putFloat("sodiumMin", sodiumSlider.getValues().get(0));
        myEditor.putFloat("sodiumMax", sodiumSlider.getValues().get(1));

        myEditor.putBoolean("carbLimit", carb.isChecked());
        myEditor.putFloat("carbMin", carbSlider.getValues().get(0));
        myEditor.putFloat("carbMax", carbSlider.getValues().get(1));

        myEditor.commit();
    }

}