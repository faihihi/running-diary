package au.edu.sydney.comp5216.running_diary.ui.runninglog;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RunningLogViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public RunningLogViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Running Log fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}