package com.example.electricitybillcalc;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.List;

public class BillAdapter extends ArrayAdapter<Bill> {

    private Context context;
    private List<Bill> billList;

    public BillAdapter(Context context, List<Bill> billList) {
        super(context, 0, billList);
        this.context = context;
        this.billList = billList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    android.R.layout.simple_list_item_2, parent, false
            );
        }

        Bill bill = billList.get(position);

        TextView text1 = convertView.findViewById(android.R.id.text1);
        TextView text2 = convertView.findViewById(android.R.id.text2);

        DecimalFormat df = new DecimalFormat("#,##0.00");

        text1.setText(bill.getMonth() + " " + bill.getYear());
        text2.setText("RM " + df.format(bill.getFinalCost()));

        return convertView;
    }
}