package com.bluecodeltd.ecap.chw.adapter;

import static com.bluecodeltd.ecap.chw.util.IndexClientsUtils.getAllSharedPreferences;
import static com.bluecodeltd.ecap.chw.util.IndexClientsUtils.getFormTag;
import static org.smartregister.chw.fp.util.FpUtil.getClientProcessorForJava;
import static org.smartregister.opd.utils.OpdJsonFormUtils.tagSyncMetadata;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.HouseholdServiceActivity;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.dao.HouseholdDao;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.model.FamilyServiceModel;
import com.bluecodeltd.ecap.chw.model.GraduationBenchmarkModel;
import com.bluecodeltd.ecap.chw.model.Household;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.FormUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class HouseholdServiceAdapter extends RecyclerView.Adapter<HouseholdServiceAdapter.ViewHolder>{


    Context context;
    List<FamilyServiceModel> services;
    ObjectMapper oMapper;
    private static final long REFRESH_DELAY = 100;
    private Handler handler = new Handler();


    public HouseholdServiceAdapter(List<FamilyServiceModel> services, Context context){

        super();

        this.services = services;
        this.context = context;

    }

    @Override
    public HouseholdServiceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_service, parent, false);

        HouseholdServiceAdapter.ViewHolder viewHolder = new HouseholdServiceAdapter.ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HouseholdServiceAdapter.ViewHolder holder, final int position) {

        final FamilyServiceModel service = services.get(position);

        holder.setIsRecyclable(false);

        holder.txtDate.setText(service.getDate());
//        holder.txtServices.setText("household");
        holder.txtserviceType.setText(service.getServices());
        //holder.txtserviceType.setText(service.getServices());

//        if(service.getServices().equals("caregiver")){
//
//            JSONArray jsonArray = null;
//            try {
//                jsonArray = new JSONArray(service.getServices_caregiver());
//
//                String[] strArr = new String[jsonArray.length()];
//
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    strArr[i] = jsonArray.getString(i);
//                }
//
//                holder.txtServices.setText(strArr.length + " Services");
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        } else {
//
//            JSONArray jsonArray = null;
//            try {
//                jsonArray = new JSONArray(service.getServices_household());
//
//                String[] strArr = new String[jsonArray.length()];
//
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    strArr[i] = jsonArray.getString(i);
//                }
//
//                holder.txtServices.setText(strArr.length + " Services");
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }

        holder.linearLayout.setOnClickListener(v -> {
            Household household = HouseholdDao.getHousehold(service.getHousehold_id());
            GraduationBenchmarkModel model = HouseholdDao.getGraduationStatus(service.getHousehold_id());

            if (model != null) {
                final String YES = "yes";
                final String NO = "no";

                boolean isEnrolledInHivProgram = model.getHiv_status_enrolled() != null && YES.equals(model.getHiv_status_enrolled());
                boolean isCaregiverEnrolledInHivProgram = model.getCaregiver_hiv_status_enrolled() != null && YES.equals(model.getCaregiver_hiv_status_enrolled());
                boolean isVirallySuppressed = model.getVirally_suppressed() != null && YES.equals(model.getVirally_suppressed());
                boolean isPreventionApplied = model.getPrevention() != null && YES.equals(model.getPrevention());
                boolean isUndernourished = model.getUndernourished() != null && YES.equals(model.getUndernourished());
                boolean hasSchoolFees = model.getSchool_fees() != null && YES.equals(model.getSchool_fees());
                boolean hasMedicalCosts = model.getMedical_costs() != null && YES.equals(model.getMedical_costs());
                boolean isRecordAbuseAbsent = model.getRecord_abuse() != null && NO.equals(model.getRecord_abuse());
                boolean isCaregiverBeatenAbsent = model.getCaregiver_beaten() != null && NO.equals(model.getCaregiver_beaten());
                boolean isChildBeatenAbsent = model.getChild_beaten() != null && NO.equals(model.getChild_beaten());
                boolean isAgainstWillAbsent = model.getAgainst_will() != null && NO.equals(model.getAgainst_will());
                boolean isStableGuardian = model.getStable_guardian() != null && YES.equals(model.getStable_guardian());
                boolean hasChildrenInSchool = model.getChildren_in_school() != null && YES.equals(model.getChildren_in_school());
                boolean isInSchool = model.getIn_school() != null && YES.equals(model.getIn_school());
                boolean hasYearInSchool = model.getYear_school() != null && YES.equals(model.getYear_school());
                boolean hasRepeatedSchool = model.getRepeat_school() != null && YES.equals(model.getRepeat_school());

                if (isEnrolledInHivProgram && isCaregiverEnrolledInHivProgram && isVirallySuppressed && isPreventionApplied
                        && isUndernourished && hasSchoolFees && hasMedicalCosts && isRecordAbuseAbsent
                        && isCaregiverBeatenAbsent && isChildBeatenAbsent && isAgainstWillAbsent && isStableGuardian
                        && hasChildrenInSchool && isInSchool && hasYearInSchool && hasRepeatedSchool) {

                    showDialogBox(service.getHousehold_id(), "`s household graduated");
                }
            } else if (household.getCase_status().equals("0") || household.getCase_status().equals("2")) {
                showDialogBox(service.getHousehold_id(), "`s has been inactive or de-registered");
            } else {
                if (v.getId() == R.id.itemm) {

                    FormUtils formUtils = null;
                    try {
                        formUtils = new FormUtils(context);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    try {
                        openFormUsingFormUtils(context, "service_report_household", service);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

        });
        holder.delete.setOnClickListener(v -> {
            try {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("You are about to delete this household service ");
                builder.setNegativeButton("NO", (dialog, id) -> {
                    //  Action for 'NO' Button
                    dialog.cancel();

                }).setPositiveButton("YES",((dialogInterface, i) -> {
                    FormUtils formUtils = null;
                    try {
                        formUtils = new FormUtils(context);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    service.setDelete_status("1");
                    JSONObject vcaScreeningForm = formUtils.getFormJson("service_report_household");
                    try {
                        CoreJsonFormUtils.populateJsonForm(vcaScreeningForm, new ObjectMapper().convertValue(service, Map.class));
                        vcaScreeningForm.put("entity_id", service .getBase_entity_id());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {

                        ChildIndexEventClient childIndexEventClient = processRegistration(vcaScreeningForm.toString());
                        if (childIndexEventClient == null) {
                            return;
                        }
                        saveRegistration(childIndexEventClient,true);


                    } catch (Exception e) {
                        Timber.e(e);
                    }
                   refreshActivity();

                }));

                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("Alert");
                alert.show();

            } catch (Exception e) {
                Timber.e(e);
            }
        });


    }
    public void refreshActivity() {
        handler.postDelayed(refreshRunnable, REFRESH_DELAY);
    }

    private Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            Activity activity = (HouseholdServiceActivity) context;
            activity.recreate();
        }
    };
    public void showDialogBox(String householdId,String message){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_layout);
        dialog.show();

        TextView dialogMessage = dialog.findViewById(R.id.dialog_message);
        Household house = HouseholdDao.getHousehold(householdId);
        dialogMessage.setText(house.getCaregiver_name() + message);

        android.widget.Button dialogButton = dialog.findViewById(R.id.dialog_button);
        dialogButton.setOnClickListener(v -> dialog.dismiss());

    }

    public void openFormUsingFormUtils(Context context, String formName, FamilyServiceModel service) throws JSONException {

        oMapper = new ObjectMapper();


        FormUtils formUtils = null;
        try {
            formUtils = new FormUtils(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject formToBeOpened;

        formToBeOpened = formUtils.getFormJson(formName);

        formToBeOpened.getJSONObject("step1").getJSONArray("fields").getJSONObject(0).remove("read_only");
        formToBeOpened.put("entity_id", service.getBase_entity_id());

        CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(service, Map.class));

        startFormActivity(formToBeOpened);

    }

    public void startFormActivity(JSONObject jsonObject) {

        Form form = new Form();
        form.setWizard(false);
        form.setName("Service Report");
        form.setHideSaveLabel(true);
        form.setNextLabel("Next");
        form.setPreviousLabel("Previous");
        form.setSaveLabel("Submit");
        form.setActionBarBackground(R.color.dark_grey);
        Intent intent = new Intent(context, org.smartregister.family.util.Utils.metadata().familyFormActivity);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, jsonObject.toString());
        ((Activity) context).startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);


    }
    public ChildIndexEventClient processRegistration(String jsonString){

        try {
            JSONObject formJsonObject = new JSONObject(jsonString);

            String encounterType = formJsonObject.getString(JsonFormConstants.ENCOUNTER_TYPE);

            String entityId = formJsonObject.optString("entity_id");

            if(entityId.isEmpty()){
                entityId  = org.smartregister.util.JsonFormUtils.generateRandomUUIDString();
            }


            JSONObject metadata = formJsonObject.getJSONObject(Constants.METADATA);


            JSONArray fields = org.smartregister.util.JsonFormUtils.fields(formJsonObject);

            switch (encounterType) {

                case "Household Service Report":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, "ec_household_service_report");
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId );
                        return new ChildIndexEventClient(event, client);
                    }
                    break;
            }
        } catch (JSONException e) {
            Timber.e(e);
        }

        return null;
    }
    public boolean saveRegistration(ChildIndexEventClient childIndexEventClient, boolean isEditMode) {

        Runnable runnable = () -> {

            Event event = childIndexEventClient.getEvent();
            Client client = childIndexEventClient.getClient();

            if (event != null && client != null) {
                try {
                    ECSyncHelper ecSyncHelper = getECSyncHelper();

                    JSONObject newClientJsonObject = new JSONObject(org.smartregister.util.JsonFormUtils.gson.toJson(client));

                    JSONObject existingClientJsonObject = ecSyncHelper.getClient(client.getBaseEntityId());

                    if (isEditMode) {
                        JSONObject mergedClientJsonObject =
                                org.smartregister.util.JsonFormUtils.merge(existingClientJsonObject, newClientJsonObject);
                        ecSyncHelper.addClient(client.getBaseEntityId(), mergedClientJsonObject);

                    } else {
                        ecSyncHelper.addClient(client.getBaseEntityId(), newClientJsonObject);
                    }

                    JSONObject eventJsonObject = new JSONObject(org.smartregister.util.JsonFormUtils.gson.toJson(event));
                    ecSyncHelper.addEvent(event.getBaseEntityId(), eventJsonObject);

                    Long lastUpdatedAtDate = getAllSharedPreferences().fetchLastUpdatedAtDate(0);
                    Date currentSyncDate = new Date(lastUpdatedAtDate);

                    //Get saved event for processing
                    List<EventClient> savedEvents = ecSyncHelper.getEvents(Collections.singletonList(event.getFormSubmissionId()));
                    getClientProcessorForJava().processClient(savedEvents);
                    getAllSharedPreferences().saveLastUpdatedAtDate(currentSyncDate.getTime());


                } catch (Exception e) {
                    Timber.e(e);
                }
            }

        };

        try {
            AppExecutors appExecutors = new AppExecutors();
            appExecutors.diskIO().execute(runnable);
            return true;
        } catch (Exception exception) {
            Timber.e(exception);
            return false;
        }
    }
    private ECSyncHelper getECSyncHelper() {
        return ChwApplication.getInstance().getEcSyncHelper();
    }
    @Override
    public int getItemCount() {

        return services.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView txtDate,txtserviceType, txtServices ;
        ImageView delete;
        LinearLayout linearLayout;


        public ViewHolder(View itemView) {

            super(itemView);

            linearLayout = itemView.findViewById(R.id.itemm);
            txtDate  = itemView.findViewById(R.id.date);
            txtserviceType = itemView.findViewById(R.id.service);
            txtServices = itemView.findViewById(R.id.services);
            delete = itemView.findViewById(R.id.delete_record);

        }


        @Override
        public void onClick(View v) {

        }
    }

}
