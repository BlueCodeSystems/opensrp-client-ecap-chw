package com.bluecodeltd.ecap.chw.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.IndexDetailsActivity;
import com.bluecodeltd.ecap.chw.dao.GradDao;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.dao.MuacDao;
import com.bluecodeltd.ecap.chw.model.Child;
import com.bluecodeltd.ecap.chw.model.GradModel;
import com.bluecodeltd.ecap.chw.model.MuacModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rey.material.widget.Button;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.util.FormUtils;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class ChildrenAdapter extends RecyclerView.Adapter<ChildrenAdapter.ViewHolder>{

    Context context;

    List<Child> children;
    String txtMuac;
    GradModel gradModel;
    MuacModel muacModel, cModel;
    ObjectMapper oMapper, gradMapper;


    public ChildrenAdapter(List<Child> children, Context context, String txtMuac){

        super();

        this.children = children;
        this.txtMuac = txtMuac;
        this.context = context;
    }

    @Override
    public ChildrenAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_child, parent, false);

        ChildrenAdapter.ViewHolder viewHolder = new ChildrenAdapter.ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ChildrenAdapter.ViewHolder holder, final int position) {

        final Child child = children.get(position);


        holder.fullName.setText(child.getFirst_name() + " " + child.getLast_name());


        String dob = child.getAdolescent_birthdate();
        String memberAge = getAgeWithoutText(dob);


        String caseStatus = IndexPersonDao.getIndexStatus(child.getBaseEntity_id());
        String subpop1 = child.getSubpop1();
        String positive = child.getIs_hiv_positive();
        String artnumber = child.getArt_number();
        String lastVl = child.getVl_last_result();

        if(child.getIndex_check_box() != null && child.getIndex_check_box().equals("1")){
            holder.is_index.setVisibility(View.VISIBLE);
        } else {
            holder.is_index.setVisibility(View.GONE);
        }

//        if (artnumber != null && ((positive != null) && child.getIs_hiv_positive().equals("yes")) || ((subpop1 != null) && child.getSubpop1().equals("true")) || Integer.parseInt(memberAge) < 5 || (Integer.parseInt(memberAge) > 9 && Integer.parseInt(memberAge) < 18)) {
//
//            holder.gradBtn.setVisibility(View.VISIBLE);
//
//        } else {
//            holder.gradBtn.setVisibility(View.GONE);
//        }
        isGraduationButtonToBeDisplayed(holder,isEligibleForEnrollment(child,memberAge));

        gradModel = GradDao.getGrad(child.getUnique_id());

        if(gradModel == null){
            holder.gradBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.grad_bg));
            holder.gradBtn.setColorFilter(ContextCompat.getColor(context, R.color.dark_grey));

        } else {
//            holder.gradBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.grad_bg2));
            holder.gradBtn.setColorFilter(ContextCompat.getColor(context, R.color.colorGreen));

        }

        holder.gradBtn.setOnClickListener(v->{

            FormUtils formUtils = null;
            try {
                formUtils = new FormUtils(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
            JSONObject formToBeOpened;

            formToBeOpened = formUtils.getFormJson("grad");
            try {
                formToBeOpened.getJSONObject("step1").put("title", child.getFirst_name() + " " + child.getLast_name() + " : " + holder.age.getText().toString() + " - " + child.getGender());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (v.getId() == R.id.grad_id) {
                try {
                    openFormUsingFormUtils(context, "grad", child, holder.age.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });




        if(caseStatus != null && caseStatus.equals("1")){

            holder.colorView.setBackgroundColor(Color.parseColor("#05b714"));

        } else if (caseStatus != null && caseStatus.equals("0")) {

            holder.colorView.setBackgroundColor(Color.parseColor("#ff0000"));

        } else if(caseStatus != null && caseStatus.equals("2")){

            holder.colorView.setBackgroundColor(Color.parseColor("#ffa500"));
        } else{
            holder.colorView.setBackgroundColor(Color.parseColor("#696969"));
        }


        if(dob != null){

            holder.age.setText("Age : " + getAge(dob));

        } else {

            holder.age.setText("Not Set");

        }


        // Enable MUAC Button
        if(caseStatus != null && txtMuac.equals("1") && (caseStatus.equals("0") || caseStatus.equals("1") || caseStatus.equals("2"))){

            holder.muacButton.setVisibility(View.VISIBLE);

            cModel = MuacDao.getMuac(child.getUnique_id());

            if(cModel != null){

                holder.muacButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_icon_info, 0, 0, 0);


            } else {

                holder.muacButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_icon_warning, 0, 0, 0);

            }

        } else {
            holder.muacButton.setVisibility(View.GONE);
        }


        holder.muacButton.setOnClickListener(v -> {

            FormUtils formUtils = null;
            try {
                formUtils = new FormUtils(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
            JSONObject formToBeOpened;

            formToBeOpened = formUtils.getFormJson("muac");
            try {
                formToBeOpened.getJSONObject("step1").put("title", child.getFirst_name() + " " + child.getLast_name() + " : " + holder.age.getText().toString() + " - " + child.getGender());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                formToBeOpened.getJSONObject("step1").getJSONArray("fields").getJSONObject(0).put("value", child.getUnique_id());
            } catch (JSONException e) {
                e.printStackTrace();
            }


            switch (v.getId()) {

                case (R.id.muac):

                    try {
                        openFormUsingFormUtils(context,"muac", child, holder.age.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
            }
        } );

        holder.lview.setOnClickListener(v -> {

            switch (v.getId()) {

                case (R.id.register_columns):

                    String subpop3 = child.getSubpop3();
                    assert subpop3 != null;

                    if(Integer.parseInt(memberAge) < 20){

                        Intent intent = new Intent(context, IndexDetailsActivity.class);
                        intent.putExtra("fromIndex", "321");
                        intent.putExtra("Child",  child.getUnique_id());
                        context.startActivity(intent);

                    } else {

                        Toasty.warning(context, "Member is not enrolled on the Program", Toast.LENGTH_LONG, true).show();

                    }

                    break;
            }
        });

    }

    private String getAge(String birthdate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-u");
        LocalDate localDateBirthdate = LocalDate.parse(birthdate, formatter);
        LocalDate today =LocalDate.now();
        Period periodBetweenDateOfBirthAndNow = Period.between(localDateBirthdate, today);
        if(periodBetweenDateOfBirthAndNow.getYears() >0)
        {
            return periodBetweenDateOfBirthAndNow.getYears() +" Years";
        }
        else if (periodBetweenDateOfBirthAndNow.getYears() == 0 && periodBetweenDateOfBirthAndNow.getMonths() > 0){
            return periodBetweenDateOfBirthAndNow.getMonths() +" Months ";
        }
        else if(periodBetweenDateOfBirthAndNow.getYears() == 0 && periodBetweenDateOfBirthAndNow.getMonths() ==0){
            return periodBetweenDateOfBirthAndNow.getDays() +" Days ";
        }
        else return "Not Set";
    }

    private String getAgeWithoutText(String birthdate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-u");
        LocalDate localDateBirthdate = LocalDate.parse(birthdate, formatter);
        LocalDate today =LocalDate.now();
        Period periodBetweenDateOfBirthAndNow = Period.between(localDateBirthdate, today);
        if(periodBetweenDateOfBirthAndNow.getYears() >0)
        {
            return String.valueOf(periodBetweenDateOfBirthAndNow.getYears());
        }
        else if (periodBetweenDateOfBirthAndNow.getYears() == 0 && periodBetweenDateOfBirthAndNow.getMonths() > 0){
            return String.valueOf(periodBetweenDateOfBirthAndNow.getMonths());
        }
        else if(periodBetweenDateOfBirthAndNow.getYears() == 0 && periodBetweenDateOfBirthAndNow.getMonths() ==0){
            return String.valueOf(periodBetweenDateOfBirthAndNow.getDays());
        }
        else return "Not Set";
    }

    public void openFormUsingFormUtils(Context context, String formName, Child child, String myage) throws JSONException {

        oMapper = new ObjectMapper();
        FormUtils formUtils = null;
        try {
            formUtils = new FormUtils(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject formToBeOpened;

        formToBeOpened = formUtils.getFormJson(formName);
        formToBeOpened.getJSONObject("step1").put("title", child.getFirst_name() + " " + child.getLast_name() + " : " + myage + " - " + child.getGender());
        formToBeOpened.getJSONObject("step1").getJSONArray("fields").getJSONObject(0).put("value", child.getUnique_id());

        switch (formName) {



            case "muac":

                muacModel = MuacDao.getMuac(child.getUnique_id());


                if(muacModel == null){

                    CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(child, Map.class));

                } else {

                    formToBeOpened.put("entity_id", this.muacModel.getBase_entity_id());
                    CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(muacModel, Map.class));
                }

                break;

            case "grad":
                //Initialize Graduation Button
                gradModel = GradDao.getGrad(child.getUnique_id());

                if(gradModel == null){

                    CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(child, Map.class));

                } else {

                    formToBeOpened.put("entity_id", this.gradModel.getBase_entity_id());
                    CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(gradModel, Map.class));
                }


                break;
        }

        startFormActivity(formToBeOpened);

    }

    public void startFormActivity(JSONObject jsonObject) {

        Intent intent = new Intent(context, org.smartregister.family.util.Utils.metadata().familyFormActivity);
        Form form = new Form();

        form.setWizard(false);
        form.setHideSaveLabel(true);
        form.setNextLabel("");

        intent = new Intent(context, org.smartregister.family.util.Utils.metadata().familyFormActivity);

        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, jsonObject.toString());
        ((Activity) context).startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);

    }

    @Override
    public int getItemCount() {

        return children.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView fullName, age, is_index;
        View colorView;
        RelativeLayout lview;
        Button muacButton;
        ImageButton gradBtn;

        public ViewHolder(View itemView) {

            super(itemView);


            fullName = itemView.findViewById(R.id.familyNameTextView);
            age = itemView.findViewById(R.id.child_age);
            lview = itemView.findViewById(R.id.register_columns);
            colorView = itemView.findViewById(R.id.mycolor);
            muacButton = itemView.findViewById(R.id.muac);
            gradBtn = itemView.findViewById(R.id.grad_id);
            is_index = itemView.findViewById(R.id.index_icon);

        }

        // Click event for all items
        @Override
        public void onClick(View v) {

        }



    }

    public Boolean isEligibleForEnrollment(Child child, String memberAge) {
        Boolean check = null;
//
//            if(checkAgeEligibility(memberAge)) {
                if (child.getIs_hiv_positive().equals("yes") || child.getSubpop1() != null ||
                        child.getSubpop3() != null || child.getSubpop4() != null ||
                        child.getSubpop5() != null || child.getSubpop6() != null) {
                    check = true;
//                return false;
                } else {
                    check = false;

                }
         //   }
//        return true;
        return check;
        }


    public void isGraduationButtonToBeDisplayed(ViewHolder holder,Boolean check){
        if(check !=null && check) {
        holder.gradBtn.setVisibility(View.VISIBLE);
    } else {
            holder.gradBtn.setVisibility(View.GONE);
        }
}

public Boolean checkAgeEligibility(String age)
{
    if(Integer.parseInt(age) <= 2)
    {
        return false;
    }

    return true;
}
}
