package project.awesomecountdown;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class DataTransactionViewModel extends AndroidViewModel {

    public MutableLiveData<Boolean> fabClicked = new MutableLiveData<>();

    public MutableLiveData<Boolean> showUndoSnackBar = new MutableLiveData<>();

    public MutableLiveData<Boolean> undoEventDelete = new MutableLiveData<>();

    public MutableLiveData<Boolean> deleteAllEventsSelected = new MutableLiveData<>();

    public MutableLiveData<String> searchQueryTabOne = new MutableLiveData<>();

    public MutableLiveData<String> searchQueryTabTwo = new MutableLiveData<>();

    public MutableLiveData<String> searchQueryTabThree = new MutableLiveData<>();

    public MutableLiveData<Integer> viewPagerPosition = new MutableLiveData<>();

    public DataTransactionViewModel(@NonNull final Application application) {
        super(application);
    }

    public void setFabClicked(boolean value) {
        fabClicked.setValue(value);
    }

    public void setShowUndoSnackBar(boolean value) {
        showUndoSnackBar.setValue(value);
    }

    public void setUndoEventDelete(boolean value) {
        undoEventDelete.setValue(value);
    }

    public void setDeleteAllEventsSelected(boolean value) {
        deleteAllEventsSelected.setValue(value);
    }
}
