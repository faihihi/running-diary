package au.edu.sydney.comp5216.running_diary.ui.gpstracker;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GpsTrackerViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public GpsTrackerViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}