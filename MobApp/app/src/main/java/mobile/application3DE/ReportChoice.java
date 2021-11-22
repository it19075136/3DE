package mobile.application3DE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import mobile.application3DE.orientation.AttentionResultChartView;
import mobile.application3DE.utilities.BaseActivity;

public class ReportChoice extends BaseActivity {

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_choice);
    }

    public void directToChart(View view) {
        intent = new Intent(this, AttentionResultChartView.class);
        startActivity(intent);
    }

    public void directToTable(View view) {
        intent = new Intent(this, FinalResultsStats.class);
        startActivity(intent);
    }

}