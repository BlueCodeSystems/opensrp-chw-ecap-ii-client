package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.CasePlanModel;
import com.bluecodeltd.ecap.chw.model.Child;
import com.bluecodeltd.ecap.chw.model.FamilyServiceModel;
import com.bluecodeltd.ecap.chw.model.GraduationBenchmarkModel;
import com.bluecodeltd.ecap.chw.model.Household;

import org.smartregister.dao.AbstractDao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HouseholdDao extends AbstractDao {


    public static void deleteRecord (String hhId, String id, List<Child> children) {

        for (int i = 0; i < children.size(); i++) {

            Child child = children.get(i);

            String sql = "UPDATE ec_client_index SET is_closed = '1' WHERE base_entity_id = '" + child.getBase_entity_id() + "'";
            updateDB(sql);


        }

        String sql = "UPDATE ec_household SET is_closed = '1' WHERE base_entity_id = '" + id + "'";
        updateDB(sql);

        String sql2 = "UPDATE ec_household SET is_closed = '1' WHERE household_id = '" + hhId + "'";
        updateDB(sql2);

        String sql3 = "UPDATE ec_mother_index SET is_closed = '1' WHERE household_id = '" + hhId + "'";
        updateDB(sql3);

    }

    public static void deleteRecordfromSearch (String hhId, String id, List<Child> children) {

        for (int i = 0; i < children.size(); i++) {

            Child child = children.get(i);

            String sql = "UPDATE ec_client_index_search SET is_closed = '1' WHERE object_id = '" + child.getBase_entity_id() + "'";
            updateDB(sql);
        }

        String sql = "UPDATE ec_household_search SET is_closed = '1' WHERE object_id = '" + id + "'";
        updateDB(sql);

        String sql2 = "UPDATE ec_client_index_search SET is_closed = '1' WHERE object_id = '" + id + "'";
        updateDB(sql2);

        String sql3 = "UPDATE ec_mother_index_search SET is_closed = '1' WHERE household_id = '" + hhId + "'";
        updateDB(sql3);

    }

    public static String checkIfScreened (String household_id) {

        String sql = "SELECT screened FROM ec_household WHERE screened = 'true' AND household_id = '" + household_id + "' GROUP BY household_id";

        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "screened");

        List<String> values = AbstractDao.readData(sql, dataMap);

        if(values.size() > 0 ){
            return "true";
        } else {
            return "false";
        }

    }
    public static String checkIfCaregiverIsPositive (String household_id) {

        String sql = "SELECT caregiver_hiv_status FROM ec_household WHERE household_id = '" + household_id + "'";

        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "caregiver_hiv_status");

        List<String> values = AbstractDao.readData(sql, dataMap);

        if(values != null && values.size() > 0 ){
            return values.get(0);
        } else {
            return "0";
        }

    }
    public static String countNumberoFHouseholds () {

        String sql = "SELECT count(DISTINCT household_id ) AS houses FROM ec_household WHERE screened = 'true' AND status IS NULL OR status != '1'";

        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "houses");

        List<String> values = AbstractDao.readData(sql, dataMap);

        if(values != null && values.size() > 0 ){
            return values.get(0);
        } else {
            return "0";
        }

    }

    public static String countNumberOfHouseholdsByCaseworkerPhone ( String caseworkerPhoneNumber)
    {

        String sql = "SELECT count(DISTINCT household_id ) AS phone FROM ec_household WHERE screened = 'true' AND phone = '" + caseworkerPhoneNumber + "' AND status IS NULL OR status != '1'";

        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "phone");

        List<String> values = AbstractDao.readData(sql, dataMap);

        if(values != null && values.size() > 0 ){
            return values.get(0);
        } else {
            return "0";
        }

    }


    public static List<Household> getDuplicatedHousehold (String householdID) {

        String sql = "SELECT ec_household.*, ec_household.village AS adolescent_village, ec_household.base_entity_id AS bid FROM ec_household WHERE  ec_household.household_id  = '" + householdID + "'";

        List<Household> values = AbstractDao.readData(sql, getHouseholdMap());

        if (values == null || values.size() == 0) {
            return new ArrayList<>();
        } else {
            return values;
        }



    }
    public static Household getHousehold (String householdID) {

       // String sql = "SELECT ec_household.*, ec_household.village AS adolescent_village, ec_household.base_entity_id AS bid FROM ec_household  WHERE ec_household.household_id = '" + householdID + "' ";
        String sql = "SELECT *,A.* FROM (SELECT ec_household.*, ec_household.village AS adolescent_village, ec_household.base_entity_id AS bid FROM ec_household WHERE household_id = '" + householdID + "') AS A LEFT JOIN (SELECT * FROM ec_client_index WHERE household_id = '" + householdID + "' AND (deleted IS NULL OR deleted != '1') AND (ec_client_index.index_check_box = '1' OR index_check_box = 'yes')) AS B ON A.household_id = B.household_id";

                List<Household> values = AbstractDao.readData(sql, getHouseholdMap());
        if (values == null || values.size() == 0)
        {
            return new Household();
        }

        return values.get(0);

    }
    public static Household getHouseholdByBaseId (String baseID) {

        String sql = "SELECT ec_household.*, ec_household.village AS adolescent_village, ec_household.base_entity_id AS bid FROM ec_household WHERE ec_household.base_entity_id = '" + baseID + "'" ;

        List<Household> values = AbstractDao.readData(sql, getHouseholdMap());


        if (values == null || values.size() == 0)
        {
            return new Household();
        }

        return values.get(0);

    }


    public static List<FamilyServiceModel> getServicesByHousehold(String householdId) {

        String sql = "SELECT * FROM ec_household_service_report WHERE household_id = '" + householdId + "' AND (delete_status IS NULL OR delete_status <> '1')";

        List<FamilyServiceModel> values = AbstractDao.readData(sql, getServiceModelMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;

    }

    public static DataMap<FamilyServiceModel> getServiceModelMap() {
        return c -> {

            FamilyServiceModel record = new FamilyServiceModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setHousehold_id(getCursorValue(c, "household_id"));
            record.setDate(getCursorValue(c, "date"));
            record.setServices_caregiver(getCursorValue(c, "services_caregiver"));
            record.setServices_household(getCursorValue(c, "services_household"));
            record.setOther_services_caregiver(getCursorValue(c, "other_service_caregiver"));
            record.setOther_services_household(getCursorValue(c, "other_service_household"));
            record.setServices(getCursorValue(c, "services"));
            record.setSchooled_services(getCursorValue(c,"schooled_services"));
            record.setOther_schooled_services(getCursorValue(c,"other_schooled_services"));
            record.setSafe_services(getCursorValue(c,"safe_services"));
            record.setOther_safe_services(getCursorValue(c,"other_safe_services"));
            record.setStable_services(getCursorValue(c,"stable_services"));
            record.setOther_stable_services(getCursorValue(c,"other_stable_services"));
            record.setHh_level_services(getCursorValue(c,"hh_level_services"));
            record.setOther_hh_level_services(getCursorValue(c,"other_hh_level_services"));
            record.setHealth_services(getCursorValue(c,"health_services"));
            record.setOther_health_services(getCursorValue(c,"other_health_services"));


            return record;
        };
    }

    public static List<FamilyServiceModel> getSingleReport(String id) {

        String sql = "SELECT * FROM ec_household_service_report WHERE base_entity_id = '" + id + "'";

        List<FamilyServiceModel> values = AbstractDao.readData(sql, getServiceModelMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;

    }

    public static List<CasePlanModel> getCasePlansById(String householdId) {

        String sql = "SELECT * FROM ec_caregiver_case_plan WHERE household_id = '" + householdId + "' AND case_plan_date IS NOT NULL AND (delete_status IS NULL OR delete_status <> '1') ORDER BY case_plan_date DESC ";

        List<CasePlanModel> values = AbstractDao.readData(sql, getCasePlanMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;

    }
    public static DataMap<CasePlanModel> getCasePlanMap() {
        return c -> {

            CasePlanModel record = new CasePlanModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setUnique_id(getCursorValue(c, "unique_id"));
            record.setCase_plan_date(getCursorValue(c, "case_plan_date"));
            record.setCase_plan_status(getCursorValue(c, "case_plan_status"));
            record.setType(getCursorValue(c, "type"));
            record.setVulnerability(getCursorValue(c, "vulnerability"));
            record.setGoal(getCursorValue(c, "goal"));
            record.setServices(getCursorValue(c, "services"));
            record.setService_referred(getCursorValue(c, "service_referred"));
            record.setInstitution(getCursorValue(c, "institution"));
            record.setDue_date(getCursorValue(c, "due_date"));
            record.setQuarter(getCursorValue(c, "quarter"));
            record.setStatus(getCursorValue(c, "status"));
            record.setComment(getCursorValue(c, "comment"));
            record.setHousehold_id(getCursorValue(c,"household_id"));

            return record;
        };
    }

    public static DataMap<Household> getHouseholdMap() {
        return c -> {

            Household record = new Household();
            record.setUnique_id(getCursorValue(c, "unique_id"));
            //household_case_status
            record.setHousehold_case_status(getCursorValue(c, "household_case_status"));
            record.setFirst_name(getCursorValue(c, "first_name"));
            record.setLast_name(getCursorValue(c, "last_name"));
            record.setGender(getCursorValue(c, "gender"));
            record.setAdolescent_birthdate(getCursorValue(c, "adolescent_birthdate"));
            record.setSubpop1(getCursorValue(c, "subpop1"));
            record.setSubpop2(getCursorValue(c, "subpop2"));
            record.setSubpop3(getCursorValue(c, "subpop3"));
            record.setSubpop4(getCursorValue(c, "subpop4"));
            record.setSubpop5(getCursorValue(c, "subpop5"));
            record.setSubpop(getCursorValue(c, "subpop"));
            record.setCaregiver_name(getCursorValue(c, "caregiver_name"));
            record.setCaregiver_sex(getCursorValue(c, "caregiver_sex"));
            record.setCaregiver_birth_date(getCursorValue(c, "caregiver_birth_date"));
            record.setPhysical_address(getCursorValue(c, "physical_address"));
            record.setCaregiver_phone(getCursorValue(c, "caregiver_phone"));
            record.setCaregiver_hiv_status(getCursorValue(c, "caregiver_hiv_status"));
            record.setActive_on_treatment(getCursorValue(c, "active_on_treatment"));
            record.setCaregiver_art_number(getCursorValue(c, "caregiver_art_number"));
            record.setRelation(getCursorValue(c, "relation"));
            record.setViral_load_results(getCursorValue(c, "viral_load_results"));
            record.setDate_of_last_viral_load(getCursorValue(c, "date_of_last_viral_load"));
            record.setVl_suppressed(getCursorValue(c, "vl_suppressed"));
            record.setCaseworker_name(getCursorValue(c, "caseworker_name"));
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setBid(getCursorValue(c, "bid"));
            record.setHousehold_id(getCursorValue(c, "household_id"));
            record.setVillage(getCursorValue(c, "village"));
            record.setUser_gps(getCursorValue(c, "user_gps"));
            record.setDistrict(getCursorValue(c, "district"));
            record.setPartner(getCursorValue(c, "partner"));
            record.setLandmark(getCursorValue(c, "landmark"));
            record.setMother_screening_date(getCursorValue(c, "mother_screening_date"));
            record.setScreening_date(getCursorValue(c, "screening_date"));
            record.setScreening_location_home(getCursorValue(c, "screening_location_home"));
            record.setViolence_six_months(getCursorValue(c, "violence_six_months"));
            record.setChildren_violence_six_months(getCursorValue(c, "children_violence_six_months"));
            record.setBiological_children(getCursorValue(c, "biological_children"));
            record.setEnrolled_pmtct(getCursorValue(c, "enrolled_pmtct"));
            record.setScreened(getCursorValue(c, "screened"));
            record.setEnrollment_date(getCursorValue(c, "enrollment_date"));
            record.setEntry_type(getCursorValue(c, "entry_type"));
            record.setOther_entry_type(getCursorValue(c, "other_entry_type"));
            record.setMonthly_expenses(getCursorValue(c, "monthly_expenses"));
            record.setMales_less_5(getCursorValue(c, "males_less_5"));
            record.setFemales_less_5(getCursorValue(c, "females_less_5"));
            record.setMales_10_17(getCursorValue(c, "males_10_17"));
            record.setFemales_10_17(getCursorValue(c, "females_10_17"));
            record.setIncome(getCursorValue(c, "monthly_expenses"));
            record.setFam_source_income(getCursorValue(c, "fam_source_income"));
            record.setPregnant_women(getCursorValue(c, "pregnant_women"));
            record.setBeds(getCursorValue(c, "beds"));
            record.setMalaria_itns(getCursorValue(c, "malaria_itns"));
            record.setHousehold_member_had_malaria(getCursorValue(c, "household_member_had_malaria"));
            record.setEmergency_name(getCursorValue(c, "emergency_name"));
            record.setE_relationship(getCursorValue(c, "e_relationship"));
            record.setContact_address(getCursorValue(c, "contact_address"));
            record.setContact_number(getCursorValue(c, "contact_number"));
            record.setSecure(getCursorValue(c, "secure"));
            record.setApproved_family(getCursorValue(c, "approved_family"));
            record.setAdolescent_village(getCursorValue(c, "adolescent_village"));
            record.setApproved_by(getCursorValue(c, "approved_by"));
            record.setIs_caregiver_virally_suppressed(getCursorValue(c, "is_caregiver_virally_suppressed"));
            record.setViral_load_results(getCursorValue(c, "viral_load_results"));
            record.setDate_of_last_viral_load(getCursorValue(c, "date_of_last_viral_load"));
            record.setCarried_by(getCursorValue(c, "carried_by"));
            record.setCaregiver_education(getCursorValue(c, "caregiver_education"));
            record.setMarital_status(getCursorValue(c, "marital_status"));
            record.setMarriage_partner_name(getCursorValue(c, "marriage_partner_name"));
            record.setHighest_grade(getCursorValue(c, "highest_grade"));
            record.setSpouse_name(getCursorValue(c, "spouse_name"));
            record.setHomeaddress(getCursorValue(c, "homeaddress"));
            record.setAt_risk_reasons(getCursorValue(c, "at_risk_reasons"));
            record.setReason_for_hiv_risk(getCursorValue(c, "reason_for_hiv_risk"));
            record.setConsent_check_box(getCursorValue(c, "consent_check_box"));
            record.setCaregiver_phone(getCursorValue(c, "caregiver_phone"));
            record.setWard(getCursorValue(c, "ward"));
            record.setProvince(getCursorValue(c, "province"));
            record.setFacility(getCursorValue(c, "facility"));
            record.setPhone(getCursorValue(c, "phone"));
            record.setDate_edited(getCursorValue(c, "date_edited"));
            record.setStatus(getCursorValue(c, "status"));
            record.setCase_status(getCursorValue(c,"case_status"));
            record.setDe_registration_date(getCursorValue(c,"de_registration_date"));
            record.setTransfer_reason(getCursorValue(c,"transfer_reason"));
            record.setOther_de_registration_reason(getCursorValue(c,"other_de_registration_reason"));
            record.setDe_registration_reason(getCursorValue(c,"de_registration_reason"));

            return record;
        };
    }

    public static List<CasePlanModel> getDomainsById(String householdID, String caseDate) {

        String sql = "SELECT * FROM ec_caregiver_case_plan_domain WHERE household_id = '" + householdID + "' AND case_plan_date = '" + caseDate + "' AND case_plan_date IS NOT NULL AND (delete_status IS NULL OR delete_status <> '1') ORDER BY case_plan_date DESC ";

        List<CasePlanModel> values = AbstractDao.readData(sql, getCasePlanMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;

    }

    public static GraduationBenchmarkModel getGraduationStatus(String householdID) {

        String sql = "SELECT *  FROM ec_graduation WHERE household_id = '" + householdID + "'";

        DataMap<GraduationBenchmarkModel> dataMap = c -> {
            GraduationBenchmarkModel model = new GraduationBenchmarkModel();
            model.setHousehold_id(getCursorValue(c, "household_id"));
            model.setDate_assessment(getCursorValue(c, "date_assessment"));
            model.setHiv_status_enrolled(getCursorValue(c, "hiv_status_enrolled"));
            model.setCaregiver_hiv_status_enrolled(getCursorValue(c, "caregiver_hiv_status_enrolled"));
            model.setPrevious_asmt_date(getCursorValue(c, "previous_asmt_date"));
            model.setVirally_suppressed(getCursorValue(c, "virally_suppressed"));
            model.setPrevention(getCursorValue(c, "prevention"));
            model.setUndernourished(getCursorValue(c, "undernourished"));
            model.setSchool_fees(getCursorValue(c, "school_fees"));
            model.setMedical_costs(getCursorValue(c, "medical_costs"));
            model.setRecord_abuse(getCursorValue(c, "record_abuse"));
            model.setCaregiver_beaten(getCursorValue(c, "caregiver_beaten"));
            model.setChild_beaten(getCursorValue(c, "child_beaten"));
            model.setAware_sexual(getCursorValue(c, "aware_sexual"));
            model.setAgainst_will(getCursorValue(c, "against_will"));
            model.setStable_guardian(getCursorValue(c, "stable_guardian"));
            model.setChildren_in_school(getCursorValue(c, "children_in_school"));
            model.setIn_school(getCursorValue(c, "in_school"));
            model.setYear_school(getCursorValue(c, "year_school"));
            model.setRepeat_school(getCursorValue(c, "repeat_school"));
            model.setAdditional_information(getCursorValue(c, "additional_information"));
            model.setGraduation_status(getCursorValue(c, "graduation_status"));

            return model;
        };

        List<GraduationBenchmarkModel> models = AbstractDao.readData(sql, dataMap);

        if (models == null || models.isEmpty()) {
            return null;
        }

        return models.get(0);
    }
    public static GraduationBenchmarkModel getAllHouseholdsGraduationStatus() {

        String sql = "SELECT *  FROM ec_graduation";

        DataMap<GraduationBenchmarkModel> dataMap = c -> {
            GraduationBenchmarkModel model = new GraduationBenchmarkModel();
            model.setHousehold_id(getCursorValue(c, "household_id"));
            model.setDate_assessment(getCursorValue(c, "date_assessment"));
            model.setHiv_status_enrolled(getCursorValue(c, "hiv_status_enrolled"));
            model.setCaregiver_hiv_status_enrolled(getCursorValue(c, "caregiver_hiv_status_enrolled"));
            model.setPrevious_asmt_date(getCursorValue(c, "previous_asmt_date"));
            model.setVirally_suppressed(getCursorValue(c, "virally_suppressed"));
            model.setPrevention(getCursorValue(c, "prevention"));
            model.setUndernourished(getCursorValue(c, "undernourished"));
            model.setSchool_fees(getCursorValue(c, "school_fees"));
            model.setMedical_costs(getCursorValue(c, "medical_costs"));
            model.setRecord_abuse(getCursorValue(c, "record_abuse"));
            model.setCaregiver_beaten(getCursorValue(c, "caregiver_beaten"));
            model.setChild_beaten(getCursorValue(c, "child_beaten"));
            model.setAware_sexual(getCursorValue(c, "aware_sexual"));
            model.setAgainst_will(getCursorValue(c, "against_will"));
            model.setStable_guardian(getCursorValue(c, "stable_guardian"));
            model.setChildren_in_school(getCursorValue(c, "children_in_school"));
            model.setIn_school(getCursorValue(c, "in_school"));
            model.setYear_school(getCursorValue(c, "year_school"));
            model.setRepeat_school(getCursorValue(c, "repeat_school"));
            model.setAdditional_information(getCursorValue(c, "additional_information"));

            return model;
        };

        List<GraduationBenchmarkModel> models = AbstractDao.readData(sql, dataMap);

        if (models == null || models.isEmpty()) {
            return null;
        }

        return models.get(0);
    }

    public static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date currentDate = new Date();
        String formattedDate = dateFormat.format(currentDate);
        return formattedDate;
    }

    public static void updateGraduatedVCAs(String hhId) {
        String currentDate = getCurrentDate();
        String sql = "UPDATE ec_client_index SET case_status = '0',de_registration_date =  '" +currentDate+"',  reason = 'Graduated (Household has met the graduation benchmarks in ALL domains)'\n" +
                " WHERE household_id = '" +hhId+"'";
        updateDB(sql);
    }











}
