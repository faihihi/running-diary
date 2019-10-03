package au.edu.sydney.comp5216.running_diary.ui.pacecalculator;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PaceCalculatorViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PaceCalculatorViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Pace Calculator fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}