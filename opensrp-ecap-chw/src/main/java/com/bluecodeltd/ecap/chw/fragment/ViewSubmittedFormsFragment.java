package com.bluecodeltd.ecap.chw.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.IndexDetailsActivity;
import com.bluecodeltd.ecap.chw.adapter.SubmittedFormsLIstAdapter;
import com.mapbox.geojson.LineString;

import org.json.JSONObject;
import org.smartregister.util.FormUtils;

public class ViewSubmittedFormsFragment extends Fragment { 
     
ListView forms;
String[] formNames;
FormUtils formUtils = null;
int[] formIcons ={R.drawable.tabmenu_addfamily_active,R.drawable.sidemenu_children_active,R.drawable.sidemenu_children_active,R.drawable.sidemenu_children_active};
SubmittedFormsLIstAdapter submittedFormsLIstAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visits, container, false);
        
        forms = view.findViewById(R.id.submittedFormsListview);
        formNames =getResources().getStringArray(R.array.vca_submitted_forms_options);
        submittedFormsLIstAdapter = new SubmittedFormsLIstAdapter(getContext(),formNames,formIcons);
        forms.setAdapter(submittedFormsLIstAdapter);
        forms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String value=(String)adapterView.getItemAtPosition(i);
              switch (value)
              {

                  case "Household screening":
                      ;
                      try {
                          formUtils = new FormUtils(getActivity());
                          JSONObject indexRegisterForm;

                          indexRegisterForm = formUtils.getFormJson("family_register");
                          ((IndexDetailsActivity)getActivity()).startFormActivity(indexRegisterForm);
                      } catch (Exception e) {
                          e.printStackTrace();
                      }
                        break;
                  case "Vulnerability Assessment":
                      try {
                          formUtils = new FormUtils(getActivity());
                          JSONObject indexRegisterForm;

                          indexRegisterForm = formUtils.getFormJson("vca_assessment");
                          ((IndexDetailsActivity)getActivity()).startFormActivity(indexRegisterForm);
                      } catch (Exception e) {
                          e.printStackTrace();
                      }
                      break;
                  case "Case plan":
                      try {
                          formUtils = new FormUtils(getActivity());
                          JSONObject indexRegisterForm;

                          indexRegisterForm = formUtils.getFormJson("vca_case_plan");
                          ((IndexDetailsActivity)getActivity()).startFormActivity(indexRegisterForm);
                      } catch (Exception e) {
                          e.printStackTrace();
                      }
                      break;
                  case "Referral" :
                      try {
                          formUtils = new FormUtils(getActivity());
                          JSONObject indexRegisterForm;

                          indexRegisterForm = formUtils.getFormJson("referral");
                          ((IndexDetailsActivity)getActivity()).startFormActivity(indexRegisterForm);
                      } catch (Exception e) {
                          e.printStackTrace();
                      }
                      break;
              }



            }
        });

        return view;

    }
}
