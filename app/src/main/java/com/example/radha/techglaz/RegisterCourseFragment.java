package com.example.radha.techglaz;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.Calendar;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

public class RegisterCourseFragment extends Fragment implements PaymentResultListener {

    EditText startDate, endDate;
    private int mYear, mMonth, mDay;
    RadioGroup radioGroup;
    RadioButton radioOnline;
    RadioButton radioOffline;
    Button makePayment;
    private Checkout razorPayCheckout;

    public RegisterCourseFragment() {
        // Required empty public constructor
    }

    public static RegisterCourseFragment newInstance(String param1, String param2) {
        RegisterCourseFragment fragment = new RegisterCourseFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register_course, container, false);

        startDate = view.findViewById(R.id.rc_startdate);
        endDate = view.findViewById(R.id.rc_enddate);
        radioGroup = view.findViewById(R.id.radioModeOfTraining);
        radioOnline = view.findViewById(R.id.radioButton1);
        radioOffline = view.findViewById(R.id.radioButton2);
        makePayment = view.findViewById(R.id.payment_btn);

        Checkout.preload(getContext());

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(startDate);
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(endDate);
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radioButton1){
                    radioOffline.setChecked(false);
                }
                else if(checkedId == R.id.radioButton2){
                    radioOnline.setChecked(false);
                }
            }
        });

        makePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPayment("1000000");
            }
        });

        return view;
    }

    private void showDatePickerDialog(EditText editText) {
        // Get current date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Set date to EditText
                        editText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }

    private void startPayment(String amount) {
        razorPayCheckout = new Checkout();
        razorPayCheckout.setKeyID("YOUR_API_KEY");
        razorPayCheckout.setImage(R.drawable.techglaz_logo);

        double finalamount = Float.parseFloat(amount)*100;

        try {
            JSONObject options = new JSONObject();
            options.put("name", "Techglaz Labs");
            options.put("description", "Payment for your service");
            options.put("image",R.drawable.techglaz_logo);
            options.put("theme.color","#85ADD8E6");
            options.put("currency", "INR");
            options.put("amount", finalamount + ""); // amount in paise (10000 = ₹100)
            options.put("prefill.email","raj@gmail.com");
            options.put("prefill.contact","7050341039");

            razorPayCheckout.open(getActivity(), options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        Toast.makeText(getActivity(), "Payment successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
        // Handle success logic, e.g., update UI or proceed with next steps
    }

    @Override
    public void onPaymentError(int code, String response) {
        Toast.makeText(getActivity(), "Payment failed: " + response, Toast.LENGTH_SHORT).show();
        // Handle failure logic, e.g., show error message to the user
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        razorPayCheckout.onActivityResult(requestCode, resultCode, data);
    }

}