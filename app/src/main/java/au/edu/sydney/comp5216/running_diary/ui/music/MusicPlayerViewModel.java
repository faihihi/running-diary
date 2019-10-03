package au.edu.sydney.comp5216.running_diary.ui.music;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MusicPlayerViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public MusicPlayerViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Music Player fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
