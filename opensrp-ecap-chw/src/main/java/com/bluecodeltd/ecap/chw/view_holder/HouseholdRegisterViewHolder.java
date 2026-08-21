package com.bluecodeltd.ecap.chw.view_holder;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.dao.GraduationDao;
import com.bluecodeltd.ecap.chw.dao.HouseholdDao;
import com.bluecodeltd.ecap.chw.model.GraduationBenchmarkModel;
import com.bluecodeltd.ecap.chw.model.GraduationModel;
import com.bluecodeltd.ecap.chw.model.Household;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.bluecodeltd.ecap.chw.util.Threading;

public class HouseholdRegisterViewHolder extends RecyclerView.ViewHolder{

    // Caps the member-icon row so it can never grow wide enough to push the chevron off-screen.
    private static final int MAX_VISIBLE_MEMBER_ICONS = 3;

    private TextView familyNameTextView;

    private TextView villageTextView;

    private TextView vcaCountBadge;

    private ImageView homeIcon;
    private View statusStrip;
    private Boolean isGraduated;

    LinearLayout hLayout;
    // Use centralized Threading

    public HouseholdRegisterViewHolder(@NonNull View itemView) {
        super(itemView);
        familyNameTextView = itemView.findViewById(R.id.familyNameTextView);
        villageTextView = itemView.findViewById(R.id.villageTextView);
        vcaCountBadge = itemView.findViewById(R.id.vca_count_badge);
        hLayout = itemView.findViewById(R.id.child_wrapper);
        homeIcon = itemView.findViewById(R.id.home_icon);
        statusStrip = itemView.findViewById(R.id.status_strip);
    }

    public void setupViews(String family, String householdId, String baseId, String isClosed, List<String> genderList, String screened, List<String> birthdateList, String vcaCount, Context context){
        familyNameTextView.setText(family);
        villageTextView.setText(householdId);
        villageTextView.setTag(householdId);
        setupVcaCountBadge(vcaCount, context);

        // Set a baseline icon quickly; async refine below
        if ("true".equals(screened)) {
            homeIcon.setImageResource(R.drawable.tabmenu_home_active);
        } else {
            homeIcon.setImageResource(R.drawable.tabmenu_home);
        }
        homeIcon.clearColorFilter();
        homeIcon.setTag(householdId);
        statusStrip.setBackgroundColor(ContextCompat.getColor(context, R.color.register_household_icon));

        Threading.ioBestEffort(() -> {
            try {
                GraduationModel graduationModel = GraduationDao.getGraduationStatus(householdId);
                Household householdByBase = HouseholdDao.getHouseholdByBaseId(baseId);
                String householdStatus = (householdByBase != null) ? householdByBase.getStatus() : null;
                Household house = HouseholdDao.getHousehold(householdId);

                Threading.main(() -> {
                    if (!householdId.equals(homeIcon.getTag())) return; // view recycled
                    try {
                        if (house != null && householdId.equals(villageTextView.getTag())) {
                            String landmark = house.getLandmark();
                            if (landmark != null && !landmark.trim().isEmpty()) {
                                villageTextView.setText(householdId + " • " + landmark.trim());
                            } else {
                                villageTextView.setText(householdId);
                            }
                        }

                        if (graduationModel != null && "1".equals(graduationModel.getGraduation_status())) {
                            homeIcon.setImageResource(R.mipmap.graduation);
                            statusStrip.setBackgroundColor(ContextCompat.getColor(context, R.color.status_green));
                            return;
                        }

                        if (householdStatus != null && "1".equals(householdStatus)) {
                            homeIcon.setImageResource(R.drawable.tabmenu_home);
                            homeIcon.setColorFilter(ContextCompat.getColor(context, com.nerdstone.neatformcore.R.color.colorRed));
                            statusStrip.setBackgroundColor(ContextCompat.getColor(context, R.color.pie_chart_red));
                        } else {
                            if ("true".equals(screened)) {
                                homeIcon.setImageResource(R.drawable.tabmenu_home_active);
                            } else {
                                homeIcon.setImageResource(R.drawable.tabmenu_home);
                            }
                            homeIcon.clearColorFilter();
                            statusStrip.setBackgroundColor(ContextCompat.getColor(context, R.color.register_household_icon));
                        }

                        if (house != null) {
                            String householdCaseStatus = house.getHousehold_case_status();
                            String deRegistrationReason = house.getDe_registration_reason();
                            if (householdCaseStatus != null &&
                                    ("0".equals(householdCaseStatus) ||
                                            ("2".equals(householdCaseStatus) &&
                                                    deRegistrationReason != null &&
                                                    ("Exited without graduation".equals(deRegistrationReason) ||
                                                            "Moved (Relocated)".equals(deRegistrationReason) ||
                                                            "other".equals(deRegistrationReason))))) {
                                homeIcon.setImageResource(R.drawable.inactive_house);
                                homeIcon.clearColorFilter();
                                statusStrip.setBackgroundColor(ContextCompat.getColor(context, R.color.stat_pill_unknown));
                            }
                        }
                    } catch (Exception ignored) {}
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        //This prevents Duplication of Icons
        hLayout.removeAllViews();

        if( isClosed!=null && isClosed.equals("0")){
            int avatarSizePx = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36, context.getResources().getDisplayMetrics()));
            int iconSizePx = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, context.getResources().getDisplayMetrics()));
            int avatarMarginPx = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, context.getResources().getDisplayMetrics()));

            // Cap visible avatars so a large household can never push the chevron off-screen.
            // genderList and birthdateList come from two independent DAO queries and can
            // disagree on row count for the same household, so bound by both.
            int visibleCount = Math.min(Math.min(genderList.size(), birthdateList.size()), MAX_VISIBLE_MEMBER_ICONS);

            for(int i=0; i < visibleCount; i++) {

                String myage = getAgeWithoutText(birthdateList.get(i));
                int age;
                try {
                    age = Integer.parseInt(myage);
                } catch (NumberFormatException e) {
                    age = -1;
                }

                // Same avatar-circle treatment as home_icon: a tinted circular backdrop behind the member icon.
                FrameLayout avatar = new FrameLayout(context);
                LinearLayout.LayoutParams avatarParams = new LinearLayout.LayoutParams(avatarSizePx, avatarSizePx);
                avatarParams.gravity = Gravity.CENTER;
                avatarParams.setMarginStart(avatarMarginPx);
                avatar.setLayoutParams(avatarParams);
                avatar.setBackgroundResource(R.drawable.circle_light_grey);

                ImageView image = new ImageView(context);
                FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(iconSizePx, iconSizePx);
                imageParams.gravity = Gravity.CENTER;
                image.setLayoutParams(imageParams);
                image.setScaleType(ImageView.ScaleType.FIT_CENTER);

                if (genderList.get(i).equals("male") && age < 20){

                    image.setImageResource(R.drawable.row_boy);
                    ViewCompat.setBackgroundTintList(avatar, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.stat_pill_male_bg)));

                } else if(genderList.get(i).equals("female") && age < 20) {

                    image.setImageResource(R.drawable.row_girl);
                    ViewCompat.setBackgroundTintList(avatar, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.stat_pill_female_bg)));

                } else {
                    image.setImageResource(R.drawable.ic_person_black_24dp);
                    image.setColorFilter(ContextCompat.getColor(context, R.color.client_list_header_dark_grey));
                    ViewCompat.setBackgroundTintList(avatar, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.light_grey)));
                }

                avatar.addView(image);
                hLayout.addView(avatar);

            }

            int remaining = genderList.size() - visibleCount;
            if (remaining > 0) {
                FrameLayout overflow = new FrameLayout(context);
                LinearLayout.LayoutParams overflowParams = new LinearLayout.LayoutParams(avatarSizePx, avatarSizePx);
                overflowParams.gravity = Gravity.CENTER;
                overflowParams.setMarginStart(avatarMarginPx);
                overflow.setLayoutParams(overflowParams);
                overflow.setBackgroundResource(R.drawable.circle_light_grey);
                ViewCompat.setBackgroundTintList(overflow, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.stat_pill_unknown_bg)));

                TextView overflowText = new TextView(context);
                FrameLayout.LayoutParams textParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                textParams.gravity = Gravity.CENTER;
                overflowText.setLayoutParams(textParams);
                overflowText.setText("+" + remaining);
                overflowText.setTextColor(ContextCompat.getColor(context, R.color.stat_pill_unknown));
                overflowText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
                overflowText.setTypeface(overflowText.getTypeface(), Typeface.BOLD);

                overflow.addView(overflowText);
                hLayout.addView(overflow);
            }
        }


    }

    private void setupVcaCountBadge(String vcaCount, Context context) {
        if (vcaCountBadge == null) {
            return;
        }
        int count = 0;
        try { count = Integer.parseInt(vcaCount); } catch (Exception ignored) {}

        vcaCountBadge.setText(count == 1 ? "1 CA" : count + " CAs");
        vcaCountBadge.setTextColor(ContextCompat.getColor(context, count > 0 ? R.color.kpi_children : R.color.stat_pill_unknown));
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
        if (birthdate == null || birthdate.trim().isEmpty()) {
            return "Not Set";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-u");
        LocalDate localDateBirthdate;
        try {
            localDateBirthdate = LocalDate.parse(birthdate, formatter);
        } catch (Exception e) {
            Log.w("HouseholdRegisterVH", "Unparseable birthdate: " + birthdate, e);
            return "Not Set";
        }
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
