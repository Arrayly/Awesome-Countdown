package project.awesomecountdown;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.List;
public class EventViewModel extends AndroidViewModel {

    private EventRepository mEventRepository;

    private LiveData<List<Event>> getEvent;

    private MutableLiveData<Long> eventOrderID;

    public MutableLiveData<List<Event>> getEventByIdResult = new MutableLiveData<>();

    public EventViewModel(@NonNull final Application application) {
        super(application);
        mEventRepository = new EventRepository(application);
        getEvent = mEventRepository.getEvent();
        eventOrderID = mEventRepository.getEventOrderID();
        getEventByIdResult = mEventRepository.getEventByIdResult;
    }

    public LiveData<List<Event>> getEvent(){
        return this.getEvent;
    }

    public void deleteAllEvents(){
        mEventRepository.deleteAllEvents();

    }

    public void getEventById(long id){
        mEventRepository.getEventById(id);
    }

    public void deleteEventById(long id){
        mEventRepository.deleteEventById(id);
    }

    public void queryMaxOrderID(){
        mEventRepository.getEventMaxOrderID();
    }

    public MutableLiveData<Long> getEventOrderID() {
        return eventOrderID;
    }

    public void addEvent(Event event){
        mEventRepository.addEvent(event);
    }

    public void updateEvent(List<Event> event){
        mEventRepository.updateEvent(event);
    }

    public void updateEditedEvent(Event event){
        mEventRepository.updateEditedEvent(event);
    }
}

