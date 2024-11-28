package com.bluecodeltd.ecap.chw.view_holder;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.dao.HouseholdDao;
import com.bluecodeltd.ecap.chw.model.GraduationBenchmarkModel;
import com.bluecodeltd.ecap.chw.model.Household;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HouseholdRegisterViewHolder extends RecyclerView.ViewHolder{

    private TextView familyNameTextView;

    private TextView villageTextView;

    private ImageView homeIcon;
    private Boolean isGraduated;

    LinearLayout hLayout;

    public HouseholdRegisterViewHolder(@NonNull View itemView) {
        super(itemView);
        familyNameTextView = itemView.findViewById(R.id.familyNameTextView);
        villageTextView = itemView.findViewById(R.id.villageTextView);
        hLayout = itemView.findViewById(R.id.child_wrapper);
        homeIcon = itemView.findViewById(R.id.home_icon);
    }

    public void setupViews(String family, String householdId, String isClosed, String village, List<String> genderList, String screened, List<String> birthdateList, Context context){
        familyNameTextView.setText(family);
        villageTextView.setText(village);

        isGraduated = checkGraduationStatus(householdId);

        if(isGraduated){
            homeIcon.setImageResource(R.mipmap.graduation);
        } else {
            if(HouseholdDao.getHouseholdByBaseId(isClosed).getStatus() == null || !HouseholdDao.getHouseholdByBaseId(isClosed).getStatus().equals("1") ){
                if (screened != null && screened.equals("true")){

                    homeIcon.setImageResource(R.mipmap.ic_home_active);
                } else {

                    homeIcon.setImageResource(R.mipmap.ic_home);
                }

            } else {

                homeIcon.setImageResource(R.mipmap.ic_home);
                homeIcon.setColorFilter(ContextCompat.getColor(context, R.color.colorRed));
            }
        }
        Household house = HouseholdDao.getHousehold(householdId);

        if (house != null) {
            String householdCaseStatus = house.getHousehold_case_status();
            String deRegistrationReason = house.getDe_registration_reason();

            if (householdCaseStatus != null &&
                    ("0".equals(householdCaseStatus) ||
                            ("2".equals(householdCaseStatus) && deRegistrationReason != null &&
                                    ("Exited without graduation".equals(deRegistrationReason) ||
                                            "Moved (Relocated)".equals(deRegistrationReason) ||
                                            "other".equals(deRegistrationReason))))) {
                homeIcon.setImageResource(R.drawable.inactive_house);
            }
        }

        //This prevents Duplication of Icons
        hLayout.removeAllViews();

        if( isClosed!=null && isClosed.equals("0")){
            for(int i=0; i < genderList.size(); i++) {

                String myage = getAgeWithoutText(birthdateList.get(i));
                int age = Integer.parseInt(myage);

                ImageView image = new ImageView(context);

                LinearLayout.LayoutParams params =  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                params.gravity = Gravity.CENTER;
                params.width = 40;
                params.height = 40;
                image.setLayoutParams(params);

                if (genderList.get(i).equals("male") && age < 20){

                    image.setImageResource(R.mipmap.ic_boy_child);

                } else if(genderList.get(i).equals("female") && age < 20) {

                    image.setImageResource(R.mipmap.ic_girl_child);

                } else {
                    image.setImageResource(R.drawable.ic_person_black_24dp);
                    image.setColorFilter(ContextCompat.getColor(context, R.color.dark_grey));
                }

                hLayout.addView(image);

            }
        }


    }
public boolean checkGraduationStatus(String householdId){
    GraduationBenchmarkModel model = HouseholdDao.getGraduationStatus(householdId);

    boolean check = false;
    if (model != null) {
        if (model.getGraduation_status() != null && model.getGraduation_status().equals("1")) {
            homeIcon.setImageResource(R.mipmap.graduation);
            check = true;
        }
    }
    return check;
}
//    public  boolean checkIfGraduated (String householdId){
//
//        boolean check = false;
//
//       // check = GraduationDao.checkHouseholdGratuated(householdId);
//        GraduationBenchmarkModel model = HouseholdDao.getGraduationStatus(householdId);
//
//        if (model != null) {
//            final String YES = "yes";
//            final String NO = "no";
//
//            boolean isEnrolledInHivProgram = model.getHiv_status_enrolled() != null && YES.equals(model.getHiv_status_enrolled());
//            boolean isCaregiverEnrolledInHivProgram = model.getCaregiver_hiv_status_enrolled() != null && YES.equals(model.getCaregiver_hiv_status_enrolled());
//            boolean isVirallySuppressed = model.getVirally_suppressed() != null && YES.equals(model.getVirally_suppressed());
//            boolean isPreventionApplied = model.getPrevention() != null && YES.equals(model.getPrevention());
//            boolean isUndernourished = model.getUndernourished() != null && YES.equals(model.getUndernourished());
//            boolean hasSchoolFees = model.getSchool_fees() != null && YES.equals(model.getSchool_fees());
//            boolean hasMedicalCosts = model.getMedical_costs() != null && YES.equals(model.getMedical_costs());
//            boolean isRecordAbuseAbsent = model.getRecord_abuse() != null && NO.equals(model.getRecord_abuse());
//            boolean isCaregiverBeatenAbsent = model.getCaregiver_beaten() != null && NO.equals(model.getCaregiver_beaten());
//            boolean isChildBeatenAbsent = model.getChild_beaten() != null && NO.equals(model.getChild_beaten());
//            boolean isAgainstWillAbsent = model.getAgainst_will() != null && NO.equals(model.getAgainst_will());
//            boolean isStableGuardian = model.getStable_guardian() != null && YES.equals(model.getStable_guardian());
//            boolean hasChildrenInSchool = model.getChildren_in_school() != null && YES.equals(model.getChildren_in_school());
//            boolean isInSchool = model.getIn_school() != null && YES.equals(model.getIn_school());
//            boolean hasYearInSchool = model.getYear_school() != null && YES.equals(model.getYear_school());
//            boolean hasRepeatedSchool = model.getRepeat_school() != null && YES.equals(model.getRepeat_school());
//
//            if (isEnrolledInHivProgram && isCaregiverEnrolledInHivProgram && isVirallySuppressed && isPreventionApplied
//                    && isUndernourished && hasSchoolFees && hasMedicalCosts && isRecordAbuseAbsent
//                    && isCaregiverBeatenAbsent && isChildBeatenAbsent && isAgainstWillAbsent && isStableGuardian
//                    && hasChildrenInSchool && isInSchool && hasYearInSchool && hasRepeatedSchool) {
//
//                homeIcon.setImageResource(R.mipmap.graduation);
//                check = true;
//            }
//        }
//
//        return check;
//    }

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
}
