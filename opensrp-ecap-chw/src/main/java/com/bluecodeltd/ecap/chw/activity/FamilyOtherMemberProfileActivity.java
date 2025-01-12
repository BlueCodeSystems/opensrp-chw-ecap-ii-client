package com.bluecodeltd.ecap.chw.activity;

import android.app.Activity;
import android.content.Context;
import android.view.Menu;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import org.json.JSONObject;
import com.bluecodeltd.ecap.chw.R;
import org.smartregister.chw.core.activity.CoreFamilyOtherMemberProfileActivity;
import org.smartregister.chw.core.activity.CoreFamilyProfileActivity;
import org.smartregister.chw.core.form_data.NativeFormsDataBinder;
import org.smartregister.chw.core.listener.OnClickFloatingMenu;
import org.smartregister.chw.core.utils.CoreConstants;
import com.bluecodeltd.ecap.chw.custom_view.FamilyMemberFloatingMenu;
import com.bluecodeltd.ecap.chw.dataloader.FamilyMemberDataLoader;
import org.smartregister.chw.fp.util.FamilyPlanningConstants;
import com.bluecodeltd.ecap.chw.fragment.FamilyOtherMemberProfileFragment;
import com.bluecodeltd.ecap.chw.presenter.FamilyOtherMemberActivityPresenter;
import com.bluecodeltd.ecap.chw.util.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.adapter.ViewPagerAdapter;
import org.smartregister.family.fragment.BaseFamilyOtherMemberProfileFragment;
import org.smartregister.family.model.BaseFamilyOtherMemberProfileActivityModel;
import org.smartregister.family.util.DBConstants;
import org.smartregister.view.contract.BaseProfileContract;

import com.bluecodeltd.ecap.chw.util.Constants;
import timber.log.Timber;

import static org.smartregister.chw.core.utils.Utils.updateToolbarTitle;

public class FamilyOtherMemberProfileActivity extends CoreFamilyOtherMemberProfileActivity {
    private FamilyMemberFloatingMenu familyFloatingMenu;
    private Flavor flavor = new FamilyOtherMemberProfileActivityFlv();

    @Override
    protected void onCreation() {
        super.onCreation();
        setIndependentClient(false);
        updateToolbarTitle(this, R.id.toolbar_title, familyName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        String gender = Utils.getValue(commonPersonObject.getColumnmaps(), DBConstants.KEY.GENDER, false);
        // Check if woman is already registered
        if (flavor.hasANC() && !presenter().isWomanAlreadyRegisteredOnAnc(commonPersonObject) && flavor.isOfReproductiveAge(commonPersonObject, "Female") &&  gender.equalsIgnoreCase("Female") ) {
            flavor.updateFpMenuItems(baseEntityId, menu);
            menu.findItem(org.smartregister.chw.core.R.id.action_anc_registration).setVisible(true);
        } else {
            menu.findItem(org.smartregister.chw.core.R.id.action_anc_registration).setVisible(false);
        }

        menu.findItem(org.smartregister.chw.core.R.id.action_sick_child_follow_up).setVisible(false);
        menu.findItem(org.smartregister.chw.core.R.id.action_malaria_diagnosis).setVisible(false);

        flavor.updateMalariaMenuItems(baseEntityId, menu);

        if (gender.equalsIgnoreCase("Male") && flavor.isOfReproductiveAge(commonPersonObject, "Male")) {
            flavor.updateMaleFpMenuItems(baseEntityId, menu);
        }

        return true;
    }

    @Override
    public FamilyOtherMemberActivityPresenter presenter() {
        return (FamilyOtherMemberActivityPresenter) presenter;
    }

    @Override
    protected void startAncRegister() {
        AncRegisterActivity.startAncRegistrationActivity(FamilyOtherMemberProfileActivity.this, baseEntityId, PhoneNumber,
                Constants.JSON_FORM.getAncRegistration(), null, familyBaseEntityId, familyName);
    }

    @Override
    protected void startMalariaRegister() {
        MalariaRegisterActivity.startMalariaRegistrationActivity(FamilyOtherMemberProfileActivity.this, baseEntityId, familyBaseEntityId);
    }

    @Override
    protected void startFpRegister() {
        String dob = org.smartregister.family.util.Utils.getValue(commonPersonObject.getColumnmaps(), DBConstants.KEY.DOB, false);
        String gender = org.smartregister.family.util.Utils.getValue(commonPersonObject.getColumnmaps(), DBConstants.KEY.GENDER, false);

        FpRegisterActivity.startFpRegistrationActivity(FamilyOtherMemberProfileActivity.this, baseEntityId, dob, CoreConstants.JSON_FORM.getFpRegistrationForm(gender), FamilyPlanningConstants.ActivityPayload.REGISTRATION_PAYLOAD_TYPE);
    }

    @Override
    protected void startFpChangeMethod() {
        String dob = org.smartregister.family.util.Utils.getValue(commonPersonObject.getColumnmaps(), DBConstants.KEY.DOB, false);
        String gender = org.smartregister.family.util.Utils.getValue(commonPersonObject.getColumnmaps(), DBConstants.KEY.GENDER, false);

        FpRegisterActivity.startFpRegistrationActivity(this, baseEntityId, dob, CoreConstants.JSON_FORM.getFpChangeMethodForm(gender), FamilyPlanningConstants.ActivityPayload.CHANGE_METHOD_PAYLOAD_TYPE);
    }

    @Override
    protected void removeIndividualProfile() {
        IndividualProfileRemoveActivity.startIndividualProfileActivity(FamilyOtherMemberProfileActivity.this,
                commonPersonObject, familyBaseEntityId, familyHead, primaryCaregiver, FamilyRegisterActivity.class.getCanonicalName());
    }

    @Override
    protected void startEditMemberJsonForm(Integer title_resource, CommonPersonObjectClient client) {
        String titleString = title_resource != null ? getResources().getString(title_resource) : null;
        boolean isPrimaryCareGiver = commonPersonObject.getCaseId().equalsIgnoreCase(primaryCaregiver);
        String eventName = Utils.metadata().familyMemberRegister.updateEventType;
        String everSchool = client.getColumnmaps().get(CoreConstants.JsonAssets.FAMILY_MEMBER.EVER_SCHOOL);
        String schoolLevel = client.getColumnmaps().get(CoreConstants.JsonAssets.FAMILY_MEMBER.SCHOOL_LEVEL);

        String uniqueID = commonPersonObject.getColumnmaps().get(DBConstants.KEY.UNIQUE_ID);

        NativeFormsDataBinder binder = new NativeFormsDataBinder(getContext(), client.getCaseId());
        binder.setDataLoader(new FamilyMemberDataLoader(familyName, isPrimaryCareGiver, everSchool, schoolLevel, titleString, eventName, uniqueID));
        JSONObject jsonObject = binder.getPrePopulatedForm(CoreConstants.JSON_FORM.getFamilyMemberRegister());

        try {
            if (jsonObject != null)
                startFormActivity(jsonObject);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    protected BaseProfileContract.Presenter getFamilyOtherMemberActivityPresenter(
            String familyBaseEntityId, String baseEntityId, String familyHead, String primaryCaregiver, String villageTown, String familyName) {
        return new FamilyOtherMemberActivityPresenter(this, new BaseFamilyOtherMemberProfileActivityModel(),
                null, familyBaseEntityId, baseEntityId, familyHead, primaryCaregiver, villageTown, familyName);
    }

    @Override
    protected FamilyMemberFloatingMenu getFamilyMemberFloatingMenu() {
        if (familyFloatingMenu == null) {
            familyFloatingMenu = new FamilyMemberFloatingMenu(this);
        }
        return familyFloatingMenu;
    }

    @Override
    protected Context getFamilyOtherMemberProfileActivity() {
        return FamilyOtherMemberProfileActivity.this;
    }

    @Override
    protected Class<? extends CoreFamilyProfileActivity> getFamilyProfileActivity() {
        return FamilyProfileActivity.class;
    }

    @Override
    protected void initializePresenter() {
        super.initializePresenter();
        onClickFloatingMenu = flavor.getOnClickFloatingMenu(this, familyBaseEntityId,baseEntityId);
    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        BaseFamilyOtherMemberProfileFragment profileOtherMemberFragment = FamilyOtherMemberProfileFragment.newInstance(this.getIntent().getExtras());
        adapter.addFragment(profileOtherMemberFragment, "");

        viewPager.setAdapter(adapter);

        return viewPager;
    }

    @Override
    protected BaseFamilyOtherMemberProfileFragment getFamilyOtherMemberProfileFragment() {
        return FamilyOtherMemberProfileFragment.newInstance(getIntent().getExtras());
    }

    /**
     * build implementation differences file
     */
    public interface Flavor {
        OnClickFloatingMenu getOnClickFloatingMenu(final Activity activity, final String familyBaseEntityId , final String baseEntityId);

        boolean isOfReproductiveAge(CommonPersonObjectClient commonPersonObject, String gender);

        void updateFpMenuItems(@Nullable String baseEntityId, @Nullable Menu menu);

        void updateMalariaMenuItems(@Nullable String baseEntityId, @Nullable Menu menu);

        void updateMaleFpMenuItems(@Nullable String baseEntityId, @Nullable Menu menu);

        boolean hasANC();
    }

    @Override
    protected void startMalariaFollowUpVisit() {
        MalariaFollowUpVisitActivity.startMalariaFollowUpActivity(this, baseEntityId);
    }

    @Override
    protected void startHfMalariaFollowupForm() {
        //Implements from super
    }

    @Override
    protected void setIndependentClient(boolean isIndependentClient) {
        super.isIndependent = isIndependentClient;
    }
}
