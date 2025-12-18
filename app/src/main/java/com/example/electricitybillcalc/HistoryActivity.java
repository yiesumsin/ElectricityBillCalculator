package com.example.electricitybillcalc;


import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private ListView historyListView;
    private TextView emptyTextView;
    private Button backButton;
    private DatabaseHelper dbHelper;
    private List<Bill> billList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        View rootView = findViewById(android.R.id.content);
        rootView.setPadding(0, getActionBarHeight(), 0, 0);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("⚡ Electricity Bill Calculator");
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(R.mipmap.ic_launcher);
            getSupportActionBar().setDisplayUseLogoEnabled(false);
        }


        historyListView = findViewById(R.id.historyListView);
        emptyTextView = findViewById(R.id.emptyTextView);
        backButton = findViewById(R.id.backButton);

        dbHelper = new DatabaseHelper(this);

        //load history
        loadHistory();

        //set back button listener
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //set list item click listener
        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bill bill = billList.get(position);
                Intent intent = new Intent(HistoryActivity.this, DetailActivity.class);
                intent.putExtra("BILL_ID", bill.getId());
                startActivity(intent);
            }
        });
    }

    private void loadHistory() {
        billList = dbHelper.getAllBills();

        if (billList.isEmpty()) {
            emptyTextView.setVisibility(View.VISIBLE);
            historyListView.setVisibility(View.GONE);
        } else {
            emptyTextView.setVisibility(View.GONE);
            historyListView.setVisibility(View.VISIBLE);

            BillAdapter adapter = new BillAdapter(this, billList);
            historyListView.setAdapter(adapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHistory(); //refresh list when returning from detail
    }
    private int getActionBarHeight() {
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return 0;
    }
}