package com.example.electricitybillcalc;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Spinner yearSpinner;
    private Spinner monthSpinner;
    private EditText unitsEditText;
    private RadioGroup rebateRadioGroup;
    private TextView totalChargesTextView;
    private TextView rebateTextView;
    private TextView finalCostTextView;
    private Button calculateButton;
    private Button saveButton;
    private Button viewHistoryButton;
    private Button aboutButton;
    private Button clearButton;

    private double totalCharges = 0.0;
    private double finalCost = 0.0;
    private double rebatePercentage = 0.0;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View rootView = findViewById(android.R.id.content);
        rootView.setPadding(0, getActionBarHeight(), 0, 0);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("⚡ Electricity Bill Calculator");
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(R.mipmap.ic_launcher);
            getSupportActionBar().setDisplayUseLogoEnabled(false);
        }



        //initialize database helper
        dbHelper = new DatabaseHelper(this);

        //initialize views
        initViews();

        //setup month spinner
        setupMonthSpinner();
        //setup year spinner
        setupYearSpinner();

        //setup button click listeners
        setupButtonListeners();
    }

    private void initViews() {
        yearSpinner = findViewById(R.id.yearSpinner);
        monthSpinner = findViewById(R.id.monthSpinner);
        unitsEditText = findViewById(R.id.unitsEditText);
        rebateRadioGroup = findViewById(R.id.rebateRadioGroup);
        totalChargesTextView = findViewById(R.id.totalChargesTextView);
        rebateTextView = findViewById(R.id.rebateTextView);
        finalCostTextView = findViewById(R.id.finalCostTextView);
        calculateButton = findViewById(R.id.calculateButton);
        saveButton = findViewById(R.id.saveButton);
        viewHistoryButton = findViewById(R.id.viewHistoryButton);
        aboutButton = findViewById(R.id.aboutButton);
        clearButton = findViewById(R.id.clearButton);

        //set default radio selection
        RadioButton radio0 = findViewById(R.id.radio0);
        if (radio0 != null) {
            radio0.setChecked(true);
        }
    }


    private void setupYearSpinner() {
        //create years from 2020 to current year + 1
        List<Integer> years = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int year = 2020; year <= currentYear + 1; year++) {
            years.add(year);
        }

        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, years
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(adapter);

        //set default to current year
        int defaultYearIndex = years.indexOf(currentYear);
        if (defaultYearIndex != -1) {
            yearSpinner.setSelection(defaultYearIndex);
        }
    }
    private void setupMonthSpinner() {
        String[] months = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, months
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(adapter);
    }

    private void setupButtonListeners() {
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateBill();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToDatabase();
            }
        });

        viewHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });

        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showClearConfirmationDialog();
            }
        });

        //set rebate selection listener
        rebateRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio0) rebatePercentage = 0.0;
                else if (checkedId == R.id.radio1) rebatePercentage = 1.0;
                else if (checkedId == R.id.radio2) rebatePercentage = 2.0;
                else if (checkedId == R.id.radio3) rebatePercentage = 3.0;
                else if (checkedId == R.id.radio4) rebatePercentage = 4.0;
                else if (checkedId == R.id.radio5) rebatePercentage = 5.0;

                rebateTextView.setText(String.format(Locale.getDefault(), "%.0f%%", rebatePercentage));
            }
        });
    }

    private void calculateBill() {
        //validate input
        String unitsText = unitsEditText.getText().toString().trim();
        if (unitsText.isEmpty()) {
            showErrorDialog("Please enter electricity units");
            return;
        }

        double units;
        try {
            units = Double.parseDouble(unitsText);
            if (units <= 0) {
                showErrorDialog("Please enter a valid positive number for units");
                return;
            }
        } catch (NumberFormatException e) {
            showErrorDialog("Please enter a valid number for units");
            return;
        }

        //calculate charges based on block system
        totalCharges = calculateBlockCharges(units);

        //get rebate percentage from selected radio button
        int selectedRadioId = rebateRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedRadioId);
        if (selectedRadioButton != null) {
            String rebateText = selectedRadioButton.getText().toString().replace("%", "");
            rebatePercentage = Double.parseDouble(rebateText);
        }

        //calculate final cost
        double rebateAmount = totalCharges * (rebatePercentage / 100);
        finalCost = totalCharges - rebateAmount;

        //update UI
        DecimalFormat df = new DecimalFormat("#,##0.00");
        totalChargesTextView.setText("RM " + df.format(totalCharges));
        rebateTextView.setText(String.format(Locale.getDefault(), "%.0f%%", rebatePercentage));
        finalCostTextView.setText("RM " + df.format(finalCost));

        //show success message
        Toast.makeText(this, "Calculation completed!", Toast.LENGTH_SHORT).show();
    }

    private double calculateBlockCharges(double units) {
        double remainingUnits = units;
        double total = 0.0;

        // Block 1: First 200 kWh (21.8 sen/kWh = 0.218 RM/kWh)
        if (remainingUnits > 0) {
            double block1Units = Math.min(remainingUnits, 200.0);
            total += block1Units * 0.218;
            remainingUnits -= block1Units;
        }

        // Block 2: Next 100 kWh (33.4 sen/kWh = 0.334 RM/kWh)
        if (remainingUnits > 0) {
            double block2Units = Math.min(remainingUnits, 100.0);
            total += block2Units * 0.334;
            remainingUnits -= block2Units;
        }

        // Block 3: Next 300 kWh (51.6 sen/kWh = 0.516 RM/kWh)
        if (remainingUnits > 0) {
            double block3Units = Math.min(remainingUnits, 300.0);
            total += block3Units * 0.516;
            remainingUnits -= block3Units;
        }

        // Block 4: Above 600 kWh (54.6 sen/kWh = 0.546 RM/kWh)
        if (remainingUnits > 0) {
            total += remainingUnits * 0.546;
        }

        return total;
    }

    private void saveToDatabase() {
        String month = monthSpinner.getSelectedItem().toString();
        int year = (int) yearSpinner.getSelectedItem();
        String unitsText = unitsEditText.getText().toString().trim();

        if (unitsText.isEmpty() || finalCost == 0.0) {
            showErrorDialog("Please calculate the bill before saving");
            return;
        }

        double units = Double.parseDouble(unitsText);

        //create Bill object
        Bill bill = new Bill(year, month, units, rebatePercentage, totalCharges, finalCost);

        //insert into database
        long id = dbHelper.insertBill(bill);

        if (id != -1) {
            Toast.makeText(this, "Bill saved successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to save bill", Toast.LENGTH_SHORT).show();
        }
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void showClearConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Clear All Data")
                .setMessage("Are you sure you want to clear all saved bills? This action cannot be undone.")
                .setPositiveButton("Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int rowsDeleted = dbHelper.deleteAllBills();
                        Toast.makeText(MainActivity.this,
                                "Cleared " + rowsDeleted + " records", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_menu_delete)
                .show();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
    private int getActionBarHeight() {
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return 0;
    }
}