package org.smartregister.chw.core.provider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.smartregister.chw.core.utils.CoreReferralUtils;
import org.smartregister.chw.core.utils.QueryConstant;
import org.smartregister.opd.configuration.OpdRegisterQueryProviderContract;

public class CoreAllClientsRegisterQueryProvider extends OpdRegisterQueryProviderContract {

    /**
     * DO NOT TOUCH 😁
     *
     * @return Query used for fetching all the ids of family members in the app
     */
    @NonNull
    @Override
    public String getObjectIdsQuery(@Nullable String filters, @Nullable String mainCondition) {
        return CoreReferralUtils.getFamilyMemberFtsSearchQuery(filters);
    }

    /**
     * DO NOT TOUCH 😁
     *
     * @return Query used  for counting all the clients
     */
    @NonNull
    @Override
    public String[] countExecuteQueries(@Nullable String filters, @Nullable String mainCondition) {
        return new String[]{
                "/**COUNT REGISTERED CHILD CLIENTS*/\n" +
                        "SELECT COUNT(*) AS c\n" +
                        "         FROM ec_child\n" +
                        "                  inner join ec_family_member on ec_family_member.base_entity_id = ec_child.base_entity_id\n" +
                        "                  inner join ec_family on ec_family.base_entity_id = ec_family_member.relational_id\n" +
                        "         WHERE ec_family_member.is_closed = '0'\n" +
                        "           AND ec_family_member.date_removed is null\n" +
                        "           AND cast(strftime('%Y-%m-%d %H:%M:%S', 'now') - strftime('%Y-%m-%d %H:%M:%S', ec_child.dob) as int) > 0",

                "/**COUNT REGISTERED ANC CLIENTS*/\n" +
                        "SELECT COUNT(*)\n" +
                        "         FROM ec_anc_register\n" +
                        "                  inner join ec_family_member on ec_family_member.base_entity_id = ec_anc_register.base_entity_id\n" +
                        "                  inner join ec_family on ec_family.base_entity_id = ec_family_member.relational_id\n" +
                        "         where ec_family_member.date_removed is null\n" +
                        "           and ec_anc_register.is_closed is 0",

                "/**COUNT REGISTERED PNC CLIENTS*/\n" +
                        "SELECT COUNT(*)\n" +
                        "         FROM ec_pregnancy_outcome\n" +
                        "                  inner join ec_family_member on ec_family_member.base_entity_id = ec_pregnancy_outcome.base_entity_id\n" +
                        "                  inner join ec_family on ec_family.base_entity_id = ec_family_member.relational_id\n" +
                        "         where ec_family_member.date_removed is null\n" +
                        "           and ec_pregnancy_outcome.is_closed is 0\n" +
                        "           AND ec_pregnancy_outcome.base_entity_id NOT IN\n" +
                        "               (SELECT base_entity_id FROM ec_anc_register WHERE ec_anc_register.is_closed IS 0)",

                "/*COUNT OTHER FAMILY MEMBERS*/\n" +
                        "SELECT  COUNT(*)\n" +
                        "FROM ec_family_member\n" +
                        "         inner join ec_family on ec_family.base_entity_id = ec_family_member.relational_id\n" +
                        "where ec_family_member.date_removed is null\n" +
                        "  AND (ec_family.entity_type = 'ec_family' OR ec_family.entity_type is null)\n" +
                        "  AND ec_family_member.base_entity_id NOT IN (\n" +
                        "    SELECT ec_anc_register.base_entity_id AS base_entity_id\n" +
                        "    FROM ec_anc_register\n" +
                        "    UNION ALL\n" +
                        "    SELECT ec_pregnancy_outcome.base_entity_id AS base_entity_id\n" +
                        "    FROM ec_pregnancy_outcome\n" +
                        "    UNION ALL\n" +
                        "    SELECT ec_child.base_entity_id AS base_entity_id\n" +
                        "    FROM ec_child\n" +
                        "    UNION ALL\n" +
                        "    SELECT ec_malaria_confirmation.base_entity_id AS base_entity_id\n" +
                        "    FROM ec_malaria_confirmation\n" +
                        "    UNION ALL\n" +
                        "    SELECT ec_family_planning.base_entity_id AS base_entity_id\n" +
                        "    FROM ec_family_planning\n" +
                        ")",

                "/*COUNT INDEPENDENT MEMBERS*/\n" +
                        "SELECT  COUNT(*)\n" +
                        "FROM ec_family_member\n" +
                        "         inner join ec_family on ec_family.base_entity_id = ec_family_member.relational_id\n" +
                        "where ec_family_member.date_removed is null\n" +
                        "  AND ec_family.entity_type = 'ec_independent_client'\n" +
                        "  AND ec_family_member.base_entity_id NOT IN (\n" +
                        "    SELECT ec_anc_register.base_entity_id AS base_entity_id\n" +
                        "    FROM ec_anc_register\n" +
                        "    UNION ALL\n" +
                        "    SELECT ec_pregnancy_outcome.base_entity_id AS base_entity_id\n" +
                        "    FROM ec_pregnancy_outcome\n" +
                        "    UNION ALL\n" +
                        "    SELECT ec_child.base_entity_id AS base_entity_id\n" +
                        "    FROM ec_child\n" +
                        "    UNION ALL\n" +
                        "    SELECT ec_malaria_confirmation.base_entity_id AS base_entity_id\n" +
                        "    FROM ec_malaria_confirmation\n" +
                        "    UNION ALL\n" +
                        "    SELECT ec_family_planning.base_entity_id AS base_entity_id\n" +
                        "    FROM ec_family_planning\n" +
                        ")",

                "/**COUNT REGISTERED MALARIA CLIENTS*/\n" +
                        "SELECT COUNT(*)\n" +
                        "FROM ec_family_member\n" +
                        "         inner join ec_family on ec_family.base_entity_id = ec_family_member.relational_id\n" +
                        "         inner join ec_malaria_confirmation on ec_family_member.base_entity_id = ec_malaria_confirmation.base_entity_id\n" +
                        "where ec_family_member.date_removed is null\n" +
                        "  AND ec_family_member.base_entity_id NOT IN (\n" +
                        "    SELECT ec_anc_register.base_entity_id AS base_entity_id\n" +
                        "    FROM ec_anc_register\n" +
                        "    UNION ALL\n" +
                        "    SELECT ec_pregnancy_outcome.base_entity_id AS base_entity_id\n" +
                        "    FROM ec_pregnancy_outcome\n" +
                        "    UNION ALL\n" +
                        "    SELECT ec_child.base_entity_id AS base_entity_id\n" +
                        "    FROM ec_child\n" +
                        "    UNION ALL\n" +
                        "    SELECT ec_family_planning.base_entity_id AS base_entity_id\n" +
                        "    FROM ec_family_planning\n" +
                        ")",

                "/**COUNT FAMILY_PLANNING CLIENTS*/\n" +
                        "SELECT COUNT(*)\n" +
                        "FROM ec_family_member\n" +
                        "         inner join ec_family on ec_family.base_entity_id = ec_family_member.relational_id\n" +
                        "         inner join ec_family_planning on ec_family_member.base_entity_id = ec_family_planning.base_entity_id\n" +
                        "where ec_family_member.date_removed is null\n" +
                        "  AND ec_family_member.base_entity_id NOT IN (\n" +
                        "    SELECT ec_anc_register.base_entity_id AS base_entity_id\n" +
                        "    FROM ec_anc_register\n" +
                        "    UNION ALL\n" +
                        "    SELECT ec_pregnancy_outcome.base_entity_id AS base_entity_id\n" +
                        "    FROM ec_pregnancy_outcome\n" +
                        "    UNION ALL\n" +
                        "    SELECT ec_child.base_entity_id AS base_entity_id\n" +
                        "    FROM ec_child\n" +
                        "    UNION ALL\n" +
                        "    SELECT ec_malaria_confirmation.base_entity_id AS base_entity_id\n" +
                        "    FROM ec_malaria_confirmation\n" +
                        ")"
        };
    }

    /**
     * DO NOT TOUCH 😁
     *
     * @return Query used for fetching all the clients in the app
     */
    @NonNull
    @Override
    public String mainSelectWhereIDsIn() {
        return QueryConstant.ALL_CLIENTS_SELECT_QUERY;
    }
}
