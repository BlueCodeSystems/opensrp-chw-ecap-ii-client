package com.bluecodeltd.ecap.chw.presenter;

import org.smartregister.chw.core.contract.FamilyOtherMemberProfileExtendedContract;
import org.smartregister.chw.core.interactor.CoreFamilyProfileInteractor;
import org.smartregister.chw.core.presenter.CoreFamilyOtherMemberActivityPresenter;
import com.bluecodeltd.ecap.chw.interactor.FamilyInteractor;
import com.bluecodeltd.ecap.chw.interactor.FamilyProfileInteractor;
import com.bluecodeltd.ecap.chw.model.FamilyProfileModel;
import org.smartregister.family.contract.FamilyOtherMemberContract;
import org.smartregister.family.contract.FamilyProfileContract;

public class FamilyOtherMemberActivityPresenter extends CoreFamilyOtherMemberActivityPresenter {

    public FamilyOtherMemberActivityPresenter(FamilyOtherMemberProfileExtendedContract.View view, FamilyOtherMemberContract.Model model, String viewConfigurationIdentifier, String familyBaseEntityId, String baseEntityId, String familyHead, String primaryCaregiver, String villageTown, String familyName) {
        super(view, model, viewConfigurationIdentifier, familyBaseEntityId, baseEntityId, familyHead, primaryCaregiver, villageTown, familyName);
    }

    @Override
    protected CoreFamilyProfileInteractor getFamilyProfileInteractor() {
        if (profileInteractor == null) {
            this.profileInteractor = new FamilyProfileInteractor();
        }
        return (CoreFamilyProfileInteractor) profileInteractor;
    }

    @Override
    protected FamilyProfileContract.Model getFamilyProfileModel(String familyName) {
        if (profileModel == null) {
            this.profileModel = new FamilyProfileModel(familyName);
        }
        return profileModel;
    }

    @Override
    protected void setProfileInteractor() {
        if (familyInteractor == null) {
            familyInteractor = new FamilyInteractor();
        }
    }
}
