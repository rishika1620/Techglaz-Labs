package com.example.radha.techglaz;

import static androidx.core.content.ContextCompat.registerReceiver;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import com.razorpay.AutoReadOtpHelper;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

public class RegisterCourseFragment extends Fragment implements PaymentResultListener {

    private AutoReadOtpHelper otpReceiver;

    EditText startDate, endDate;
    EditText name,email,phone_no,address,collegeName,branch,yearOfGraduation, courseName;
    private int mYear, mMonth, mDay;
    RadioGroup radioGroup;
    RadioButton radioOnline;
    RadioButton radioOffline;
    Button makePayment;
    private Checkout razorPayCheckout;
    private PopupWindow popupWindow;

    String mode  = null;
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
        courseName = view.findViewById(R.id.rc_coursename);
        name = view.findViewById(R.id.rc_username);
        email = view.findViewById(R.id.rc_email);
        phone_no= view.findViewById(R.id.rc_phone_no);
        address = view.findViewById(R.id.rc_address);
        collegeName = view.findViewById(R.id.rc_collge_name);
        branch = view.findViewById(R.id.rc_branch);
        yearOfGraduation = view.findViewById(R.id.rc_yearOfGraduation);


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

                if(startDate.getText().toString() == null){
                    startDate.setError("Please fill the Start Date");
                }
                else{

                }
                showDatePickerDialog(endDate);
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radioButton1){
                    radioOffline.setChecked(false);
                    mode = "Offline";
                }
                else if(checkedId == R.id.radioButton2){
                    radioOnline.setChecked(false);
                    mode = "Online";
                }
            }
        });

        courseName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow();
            }
        });

        makePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkDetails()){
                    startPayment("1");
                }
            }
        });
        return view;
    }

    private void showDatePickerDialog(EditText editText) {
        // Get current date
       /* final Calendar c = Calendar.getInstance();
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

        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    EditText editText = (EditText) v;
                    if (editText.getCompoundDrawables()[2] != null) {
                        // Detect touch on drawableRight
                        Rect bounds = editText.getCompoundDrawables()[2].getBounds();
                        float x = event.getX();
                        float y = event.getY();
                        int drawableRightStart = editText.getWidth() - editText.getPaddingRight() - bounds.width();
                        if (x >= drawableRightStart && x <= editText.getWidth() - editText.getPaddingRight()) {
                            // Handle the drawableRight click event
                            datePickerDialog.show();
                            return true;
                        }
                    }
                }
                return false;
            }
        });*/

        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create the DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year1, month1, dayOfMonth) -> {
                    // Set the selected date on the EditText
                    String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                    editText.setText(selectedDate);
                },
                year, month, day);

        // Show the DatePickerDialog
        datePickerDialog.show();
    }

    private RegisterCourse_Model userDetails(){
        RegisterCourse_Model rc_details = new RegisterCourse_Model(name.getText().toString(),email.getText().toString(),phone_no.getText().toString(),
                address.getText().toString(),collegeName.getText().toString(),branch.getText().toString(),yearOfGraduation.getText().toString(),
                courseName.getText().toString(),mode,startDate.getText().toString(),endDate.getText().toString());
        return rc_details;
    }

    private Date getDate(String dateString) {
        SimpleDateFormat dateFormat = null;
        Date date = null;
        try{
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                date = dateFormat.parse(dateString);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;

    }

    public boolean checkDetails(){
        if(name.getText().toString().isEmpty()){
            showError(name);
            return false;
        } else if(email.getText().toString().isEmpty()){
            showError(email);
            return false;
        } else if(phone_no.getText().toString().isEmpty()){
            showError(phone_no);
            return false;
        } else if(address.getText().toString().isEmpty()){
            showError(address);
            return false;
        }else if(collegeName.getText().toString().isEmpty()){
            showError(collegeName);
            return false;
        }else if(branch.getText().toString().isEmpty()){
            showError(branch);
            return false;
        } else if (yearOfGraduation.getText().toString().isEmpty()) {
            showError(yearOfGraduation);
            return false;
        } else if(startDate.getText().toString().isEmpty()){
            showError(startDate);
            return false;
        } else if(endDate.getText().toString().isEmpty()){
            showError(endDate);
            return false;
        } else if(courseName.getText().toString().isEmpty()){
            showError(courseName);
            return false;
        } else{
            return true;
        }
    }

    private void showError(EditText editText){
        editText.setError("Field is required");
    }


    private void startPayment(String amount) {
        razorPayCheckout = new Checkout();
        razorPayCheckout.setKeyID("rzp_test_qxBKxzfAUXIh0e");
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
        MongoDB_Database mongoDBDatabase = new MongoDB_Database(getContext());
        mongoDBDatabase.setupDatabase();

        mongoDBDatabase.registerCourseDB(userDetails(), new MongoDB_Database.DBCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(getActivity(), "Registration successful: ", Toast.LENGTH_SHORT).show();
                // Handle success logic, e.g., update UI or proceed with next steps
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getActivity(), "Registration failed ", Toast.LENGTH_SHORT).show();
            }
        });
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

    private void showPopupWindow() {
        // Inflate the popup_layout.xml
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.coursename_popup, null);

        // Creating the PopupWindow
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        String[] titles = {getString(R.string.aurdino_title), getString(R.string.iot_title), getString(R.string.robotics_title),getString(R.string.embeddedSystem_title),getString(R.string.java_title),getString(R.string.webDevelopment_title),getString(R.string.appDevelopment_title)};

        // Set the adapter for the ListView in the PopupWindow
        ListView listView = popupView.findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, titles);
        listView.setAdapter(adapter);

        // Set the on item click listener for the ListView
        listView.setOnItemClickListener((parent, view, position, id) -> {
            courseName.setText(titles[position]);
            popupWindow.dismiss();
        });

        // Show the PopupWindow above the keyboard
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(courseName, Gravity.NO_GRAVITY, 0, 0);
        popupWindow.update(100, courseName.getHeight() + courseName.getTop(), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onResume() {
        super.onResume();
        otpReceiver = new AutoReadOtpHelper(getActivity());
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        requireActivity().registerReceiver(otpReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (otpReceiver != null) {
            requireActivity().unregisterReceiver(otpReceiver);
            otpReceiver = null;
        }
    }

}