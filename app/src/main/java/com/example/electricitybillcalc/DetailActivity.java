package com.example.electricitybillcalc;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private TextView detailYear;
    private TextView detailMonth;
    private TextView detailUnits;
    private TextView detailRebate;
    private TextView detailTotalCharges;
    private TextView detailFinalCost;
    private Button detailBackButton;
    private Button editButton;
    private Button deleteButton;

    private DatabaseHelper dbHelper;
    private int currentBillId;
    private Bill currentBill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        View rootView = findViewById(android.R.id.content);
        rootView.setPadding(0, getActionBarHeight(), 0, 0);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("⚡ Electricity Bill Calculator");
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(R.mipmap.ic_launcher);
            getSupportActionBar().setDisplayUseLogoEnabled(false);
        }

        // Initialize views
        detailYear = findViewById(R.id.detailYear);
        detailMonth = findViewById(R.id.detailMonth);
        detailUnits = findViewById(R.id.detailUnits);
        detailRebate = findViewById(R.id.detailRebate);
        detailTotalCharges = findViewById(R.id.detailTotalCharges);
        detailFinalCost = findViewById(R.id.detailFinalCost);
        detailBackButton = findViewById(R.id.detailBackButton);
        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);

        dbHelper = new DatabaseHelper(this);

        // Get bill ID from intent
        currentBillId = getIntent().getIntExtra("BILL_ID", -1);

        if (currentBillId != -1) {
            loadBillDetails(currentBillId);
        }

        // Set back button listener
        detailBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Set edit button listener
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog();
            }
        });

        // Set delete button listener
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });
    }

    private void loadBillDetails(int billId) {
        currentBill = dbHelper.getBillById(billId);

        if (currentBill != null) {
            DecimalFormat df = new DecimalFormat("#,##0.00");

            detailYear.setText(String.valueOf(currentBill.getYear()));
            detailMonth.setText(currentBill.getMonth());
            detailUnits.setText(df.format(currentBill.getUnits()) + " kWh");
            detailRebate.setText(String.format(Locale.getDefault(), "%.0f%%", currentBill.getRebate()));
            detailTotalCharges.setText("RM " + df.format(currentBill.getTotalCharges()));
            detailFinalCost.setText("RM " + df.format(currentBill.getFinalCost()));
        }
    }

    private void showEditDialog() {
        if (currentBill == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Bill");

        // Inflate the custom dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edit_bill_dialog, null);
        builder.setView(dialogView);

        // Initialize dialog views
        Spinner monthSpinner = dialogView.findViewById(R.id.editMonthSpinner);
        Spinner yearSpinner = dialogView.findViewById(R.id.editYearSpinner);
        EditText unitsEditText = dialogView.findViewById(R.id.editUnitsEditText);
        RadioGroup rebateRadioGroup = dialogView.findViewById(R.id.editRebateRadioGroup);
        TextView currentValuesTextView = dialogView.findViewById(R.id.currentValuesTextView);
        TextView errorMessage = dialogView.findViewById(R.id.editErrorMessage);

        // Setup month spinner
        String[] months = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, months
        );
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);

        // Setup year spinner (similar to MainActivity)
        List<Integer> years = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int year = 2020; year <= currentYear + 1; year++) {
            years.add(year);
        }

        ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, years
        );
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        // Set current values
        DecimalFormat df = new DecimalFormat("#,##0.00");
        currentValuesTextView.setText(String.format(
                "Current Values:\nMonth: %s\nYear: %d\nUnits: %s kWh\nRebate: %.0f%%\nTotal: RM %s\nFinal: RM %s",
                currentBill.getMonth(),
                currentBill.getYear(),
                df.format(currentBill.getUnits()),
                currentBill.getRebate(),
                df.format(currentBill.getTotalCharges()),
                df.format(currentBill.getFinalCost())
        ));

        // Pre-fill current values
        // Set month
        for (int i = 0; i < months.length; i++) {
            if (months[i].equals(currentBill.getMonth())) {
                monthSpinner.setSelection(i);
                break;
            }
        }

        // Set year
        int yearPosition = years.indexOf(currentBill.getYear());
        if (yearPosition != -1) {
            yearSpinner.setSelection(yearPosition);
        }

        // Set units
        unitsEditText.setText(String.valueOf(currentBill.getUnits()));

        // Set rebate
        int rebatePercent = (int) currentBill.getRebate();
        int radioId = getRebateRadioButtonId(rebatePercent);
        if (radioId != -1) {
            rebateRadioGroup.check(radioId);
        }

        builder.setPositiveButton("SAVE CHANGES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Validate and save changes
                saveChanges(monthSpinner, yearSpinner, unitsEditText, rebateRadioGroup, errorMessage);
            }
        });

        builder.setNegativeButton("CANCEL", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void saveChanges(Spinner monthSpinner, Spinner yearSpinner, EditText unitsEditText,
                             RadioGroup rebateRadioGroup, TextView errorMessage) {

        // Get selected month and year
        String month = monthSpinner.getSelectedItem().toString();
        int year = (int) yearSpinner.getSelectedItem();

        // Validate units
        String unitsText = unitsEditText.getText().toString().trim();
        if (unitsText.isEmpty()) {
            errorMessage.setText("Please enter electricity units");
            errorMessage.setVisibility(View.VISIBLE);
            return;
        }

        double units;
        try {
            units = Double.parseDouble(unitsText);
            if (units <= 0) {
                errorMessage.setText("Units must be greater than 0");
                errorMessage.setVisibility(View.VISIBLE);
                return;
            }
            if (units > 1000) {
                errorMessage.setText("Units cannot exceed 1000 kWh");
                errorMessage.setVisibility(View.VISIBLE);
                return;
            }
        } catch (NumberFormatException e) {
            errorMessage.setText("Please enter a valid number for units");
            errorMessage.setVisibility(View.VISIBLE);
            return;
        }

        // Get rebate percentage
        int selectedRadioId = rebateRadioGroup.getCheckedRadioButtonId();
        if (selectedRadioId == -1) {
            errorMessage.setText("Please select a rebate percentage");
            errorMessage.setVisibility(View.VISIBLE);
            return;
        }

        RadioButton selectedRadioButton = rebateRadioGroup.findViewById(selectedRadioId);
        String rebateText = selectedRadioButton.getText().toString().replace("%", "");
        double rebate = Double.parseDouble(rebateText);

        // Update the bill in database
        boolean success = dbHelper.updateBill(currentBillId, year, month, units, rebate);

        if (success) {
            Toast.makeText(this, "Bill updated successfully!", Toast.LENGTH_SHORT).show();
            // Reload the bill details to show updated values
            loadBillDetails(currentBillId);
        } else {
            Toast.makeText(this, "Failed to update bill", Toast.LENGTH_SHORT).show();
        }
    }

    private int getRebateRadioButtonId(int rebatePercent) {
        switch (rebatePercent) {
            case 0: return R.id.editRadio0;
            case 1: return R.id.editRadio1;
            case 2: return R.id.editRadio2;
            case 3: return R.id.editRadio3;
            case 4: return R.id.editRadio4;
            case 5: return R.id.editRadio5;
            default: return -1;
        }
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Bill")
                .setMessage("Are you sure you want to delete this bill? This action cannot be undone.")
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean deleted = dbHelper.deleteBill(currentBillId);
                        if (deleted) {
                            Toast.makeText(DetailActivity.this, "Bill deleted successfully", Toast.LENGTH_SHORT).show();
                            finish(); // Go back to history
                        } else {
                            Toast.makeText(DetailActivity.this, "Failed to delete bill", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("CANCEL", null)
                .setIcon(android.R.drawable.ic_delete)
                .show();
    }
    private int getActionBarHeight() {
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return 0;
    }
}