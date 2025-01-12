package com.bluecodeltd.ecap.chw.activity;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.bluecodeltd.ecap.chw.R;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

public class AncMedicalHistoryActivityFlv extends DefaultChwAncMedicalHistoryActivityFlv {

    @Override
    protected void processAncCard(String has_card, Context context) {
        // super.processAncCard(has_card, context);
        linearLayoutAncCard.setVisibility(View.GONE);
    }

    @Override
    protected void processHealthFacilityVisit(List<Map<String, String>> hf_visits, Context context) {
        //super.processHealthFacilityVisit(hf_visits, context);

        if (hf_visits != null && hf_visits.size() > 0) {
            linearLayoutHealthFacilityVisit.setVisibility(View.VISIBLE);

            int x = 0;
            for (Map<String, String> vals : hf_visits) {
                View view = inflater.inflate(org.smartregister.chw.core.R.layout.medial_history_anc_visit, null);

                TextView tvTitle = view.findViewById(org.smartregister.chw.core.R.id.title);
                TextView tvTests = view.findViewById(org.smartregister.chw.core.R.id.tests);

                view.findViewById(org.smartregister.chw.core.R.id.weight).setVisibility(View.GONE);
                view.findViewById(org.smartregister.chw.core.R.id.bp).setVisibility(View.GONE);
                view.findViewById(org.smartregister.chw.core.R.id.hb).setVisibility(View.GONE);
                view.findViewById(org.smartregister.chw.core.R.id.ifa_received).setVisibility(View.GONE);


                tvTitle.setText(MessageFormat.format(context.getString(org.smartregister.chw.core.R.string.anc_visit_date), (hf_visits.size() - x), vals.get("anc_hf_visit_date")));
                tvTests.setText(MessageFormat.format(context.getString(org.smartregister.chw.core.R.string.tests_done_details), vals.get("tests_done")));

                linearLayoutHealthFacilityVisitDetails.addView(view, 0);

                x++;
            }
        }
    }
}
