package au.edu.sydney.comp5216.running_diary.ui.pacecalculator;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import au.edu.sydney.comp5216.running_diary.R;

/**
 * PaceCalculatorFragment is started when Calculator navigation is clicked
 */
public class PaceCalculatorFragment extends Fragment implements View.OnClickListener {

    // Set variables
    EditText hour_input, min_input, sec_input, distance;
    Double hr, min, sec, dist;
    TextView hour_result, min_result, sec_result, speed_result_txt;
    LinearLayout pace_result, speed_result;
    Button calculatePace;

    /**
     * Create view when fragment starts
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_pacecalculator, container, false);

        // Set up all view variables
        hour_input = (EditText) root.findViewById(R.id.hour_input);
        min_input = (EditText) root.findViewById(R.id.min_input);
        sec_input = (EditText) root.findViewById(R.id.sec_input);
        distance = (EditText) root.findViewById(R.id.distance_input);

        hour_result = (TextView) root.findViewById(R.id.hour_pace);
        min_result = (TextView) root.findViewById(R.id.min_pace);
        sec_result = (TextView) root.findViewById(R.id.sec_pace);
        speed_result_txt = (TextView) root.findViewById(R.id.speed_result_txt);

        pace_result = (LinearLayout) root.findViewById(R.id.pace_result);
        speed_result = (LinearLayout) root.findViewById(R.id.speed_result);

        // Set on click listener for calculate button
        calculatePace = (Button) root.findViewById(R.id.calculate_pace);
        calculatePace.setOnClickListener(this);

        return root;
    }

    /**
     * When calculate button is clicked, calculate the pace and speed and set views
     * @param view
     */
    @Override
    public void onClick(View view) {
        // Display toast if some input fields are empty
        if(isEmpty(hour_input) || isEmpty(min_input) || isEmpty(sec_input) || isEmpty(distance)){
            Toast.makeText(getActivity(), "Please fill in all the fields", Toast.LENGTH_LONG).show();
            return;
        }

        // Hide keyboard
        hideKeyboard();

        hr = Double.parseDouble(hour_input.getText().toString());
        min = Double.parseDouble(min_input.getText().toString());
        sec = Double.parseDouble(sec_input.getText().toString());
        dist = Double.parseDouble(distance.getText().toString());

        Double hr_ans = 0.0, min_ans = 0.0, sec_ans = 0.0;
        String speed_txt = "";

        // Check if time input of hh, mm, ss is not more than 59
        if(hr <= 59 && min <= 59 && sec <= 59 && dist > 0){
            // Calculate speed
            Double speed_temp = dist/(((((hr * 60) + min) * 60) + sec)/3600);
            speed_txt = String.valueOf(Math.round(speed_temp * 1000.0) / 1000.0);

            // Calculate pace
            Double sec_result = (Double) ((((hr * 60) + min) * 60) + sec) / dist;

            // Change pace format to hh:mm:ss
            if(sec_result / 60 >= 60){
                hr_ans = sec_result / 3600;
                min_ans = (hr_ans - getInteger(hr_ans)) * 60;
                sec_ans = (min_ans - getInteger(min_ans)) * 60;
            } else{
                hr_ans = 0.0;
                min_ans = sec_result / 60;
                sec_ans = (min_ans - getInteger(min_ans)) * 60;
            }

        } else if(dist == 0){ // If distance is 0, set answer to 0 for pace and speed
            hr_ans = 0.0;
            min_ans = 0.0;
            sec_ans = 0.0;

            speed_txt = "0";
        } else { // Display toast if time input is more than 59
            Toast.makeText(getActivity(), "Time (hh/mm/ss) cannot be more than 60 sec", Toast.LENGTH_LONG).show();
            return;
        }

        // Round up/down the values
        String hr_str = String.valueOf((int) Math.floor(hr_ans));
        String min_str = String.valueOf((int) Math.floor(min_ans));
        String sec_str = String.valueOf((int) Math.round(sec_ans));

        // Set format to hh:mm:ss
        if(hr_str.length() == 1){hr_str = "0" + hr_str;}
        if(min_str.length() == 1){min_str = "0" + min_str;}
        if(sec_str.length() == 1){sec_str = "0" + sec_str;}

        // Set answer texts and turn visibility on
        hour_result.setText(hr_str);
        min_result.setText(min_str);
        sec_result.setText(sec_str);
        speed_result_txt.setText(speed_txt);

        pace_result.setVisibility(View.VISIBLE);
        speed_result.setVisibility(View.VISIBLE);
    }

    /**
     * Get first digit of number from decimal number (ex. "b" from "ab.cd")
     * @param num
     * @return first digit of input number in Double
     */
    protected Double getInteger(Double num){
        String doubleAsString = String.valueOf(num);
        int indexOfDecimal = doubleAsString.indexOf(".");
        doubleAsString = doubleAsString.substring(0, indexOfDecimal);
        return Double.parseDouble(doubleAsString);
    }

    /**
     * Check if edit text field is empty
     * @param etText
     * @return boolean
     */
    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    /**
     * Hide keyboard
     */
    public void hideKeyboard() {
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }
}