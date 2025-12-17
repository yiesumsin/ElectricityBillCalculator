package com.example.electricitybillcalc;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.DecimalFormat;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private TextView detailYear;
    private TextView detailMonth;
    private TextView detailUnits;
    private TextView detailRebate;
    private TextView detailTotalCharges;
    private TextView detailFinalCost;
    private Button detailBackButton;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getSupportActionBar().setTitle("📄 Bill Details");

        detailYear = findViewById(R.id.detailYear);
        detailMonth = findViewById(R.id.detailMonth);
        detailUnits = findViewById(R.id.detailUnits);
        detailRebate = findViewById(R.id.detailRebate);
        detailTotalCharges = findViewById(R.id.detailTotalCharges);
        detailFinalCost = findViewById(R.id.detailFinalCost);
        detailBackButton = findViewById(R.id.detailBackButton);

        dbHelper = new DatabaseHelper(this);

        //get bill ID from intent
        int billId = getIntent().getIntExtra("BILL_ID", -1);

        if (billId != -1) {
            loadBillDetails(billId);
        }

        //set back button listener
        detailBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadBillDetails(int billId) {
        Bill bill = dbHelper.getBillById(billId);

        if (bill != null) {
            DecimalFormat df = new DecimalFormat("#,##0.00");

            detailYear.setText(String.valueOf(bill.getYear()));
            detailMonth.setText(bill.getMonth());
            detailUnits.setText(df.format(bill.getUnits()) + " kWh");
            detailRebate.setText(String.format(Locale.getDefault(), "%.0f%%", bill.getRebate()));
            detailTotalCharges.setText("RM " + df.format(bill.getTotalCharges()));
            detailFinalCost.setText("RM " + df.format(bill.getFinalCost()));
        }
    }
}