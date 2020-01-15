package project.awesomecountdown;

import android.app.Application;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.List;

public class EventRepository {

    private EventDao mEventDao;
    private LiveData<List<Event>> getEvent;
    public MutableLiveData<List<Event>> getEventByIdResult = new MutableLiveData<>();
    private MutableLiveData<Long> eventOrderID = new MutableLiveData<>();

    public EventRepository(Application application) {
        EventDatabase db = EventDatabase.getDatabase(application);
        this.mEventDao = db.mEventDao();
        this.getEvent = mEventDao.getEvent();

    }

    public MutableLiveData<Long> getEventOrderID() {
        return eventOrderID;
    }

    public LiveData<List<Event>> getEvent(){
        return this.getEvent;
    }

    public void addEvent(Event event){
        new addEventAsyncTask(mEventDao).execute(event);
    }

    public void updateEvent(List<Event> event){
        new updateEventAsyncTask(mEventDao).execute(event);
    }

    public void deleteAllEvents(){
        new deleteAllEventsAsyncTask(mEventDao).execute();
    }

    public void getEventMaxOrderID(){
        new maxOrderIdAsyncTask(mEventDao).execute();
    }

    public void deleteEventById(long id){
        new deleteEventByIdAsyncTask(mEventDao).execute(id);
    }

    public void getEventById(long id) {
        new getEventByIdAsyncTask(mEventDao).execute(id);
    }

    public void updateEditedEvent(Event event){
        new updateEditedEventAsyncTask(mEventDao).execute(event);
    }

    public class getEventByIdAsyncTask extends AsyncTask<Long,Void,List<Event>>{

        private EventDao mAsyncDao;

        public getEventByIdAsyncTask(final EventDao asyncDao) {
            mAsyncDao = asyncDao;
        }

        @Override
        protected List<Event> doInBackground(final Long... longs) {
            return mAsyncDao.getEventById(longs[0]);

        }

        @Override
        protected void onPostExecute(final List<Event> events) {
            super.onPostExecute(events);
            getEventByIdResult.setValue(events);
        }
    }

    public static class updateEditedEventAsyncTask extends AsyncTask<Event, Void, Void> {

        private EventDao mAsyncTaskDao;

        public updateEditedEventAsyncTask(final EventDao asyncTaskDao) {
            mAsyncTaskDao = asyncTaskDao;
        }

        @Override
        protected Void doInBackground(final Event... events) {
            mAsyncTaskDao.updateEditedEvent(events[0]);
            return null;
        }
    }


    public class addEventAsyncTask extends AsyncTask<Event, Void, Void> {
        private EventDao mAsyncDao;

        addEventAsyncTask(EventDao dao) {
            mAsyncDao = dao;
        }

        @Override
        protected Void doInBackground(final Event... events) {
            mAsyncDao.addEvent(events[0]);
            return null;
        }
    }

    public class updateEventAsyncTask extends AsyncTask<List<Event>, Void, Void> {
        private EventDao mAsyncDao;

        updateEventAsyncTask(EventDao dao) {
            mAsyncDao = dao;
        }


        @Override
        protected Void doInBackground(final List<Event>... lists) {
            mAsyncDao.updateEvent(lists[0]);
            return null;
        }
    }

    public class deleteAllEventsAsyncTask extends AsyncTask<Void, Void, Void> {
        private EventDao mAsyncDao;

        deleteAllEventsAsyncTask(EventDao dao) {
            mAsyncDao = dao;
        }

        @Override
        protected Void doInBackground(final Void... voids) {
            mAsyncDao.deleteAll();
            return null;
        }
    }

    public class deleteEventByIdAsyncTask extends AsyncTask<Long, Void, Void> {
        private EventDao mAsyncDao;

        deleteEventByIdAsyncTask(EventDao dao) {
            mAsyncDao = dao;
        }

        @Override
        protected Void doInBackground(final Long... longs) {
            mAsyncDao.deleteEventById(longs[0]);
            return null;
        }
    }

    public class maxOrderIdAsyncTask extends AsyncTask<Void, Void, Long> {
        private EventDao mAsyncDao;

        maxOrderIdAsyncTask(EventDao dao) {
            mAsyncDao = dao;
        }

        @Override
        protected Long doInBackground(final Void... voids) {
            long id = mAsyncDao.getMaxOrderID();
            return id;
        }

        @Override
        protected void onPostExecute(final Long aLong) {
            super.onPostExecute(aLong);
            eventOrderID.setValue(aLong);
        }
    }
}

