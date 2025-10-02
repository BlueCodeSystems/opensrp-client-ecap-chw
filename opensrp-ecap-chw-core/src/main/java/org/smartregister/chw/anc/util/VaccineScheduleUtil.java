package org.smartregister.chw.anc.util;

import android.content.Context;

import org.smartregister.immunization.domain.jsonmapping.VaccineGroup;
import org.smartregister.immunization.util.VaccinatorUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VaccineScheduleUtil {

    private VaccineScheduleUtil() {

    }

    /**
     * Returns the supported type of vaccines. for either woman or child.
     * The function loads child vaccines by default when the vaccine type is not provided
     *
     * @param context
     * @param vaccineType
     * @return
     */
    public static Map<String, VaccineGroup> getVaccineGroups(Context context, String vaccineType) {
        Map<String, VaccineGroup> groupedVaccines = new LinkedHashMap<>();

        List<VaccineGroup> vaccineGroups = "woman".equals(vaccineType) ?
                VaccinatorUtils.getSupportedWomanVaccines(context, "") :
                VaccinatorUtils.getSupportedVaccines(context);

        for (VaccineGroup vg : vaccineGroups) {
            groupedVaccines.put(vg.name, vg);
        }

        return groupedVaccines;
    }
}
