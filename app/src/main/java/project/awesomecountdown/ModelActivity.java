package project.awesomecountdown;

import androidx.appcompat.app.AppCompatActivity;

public class ModelActivity extends AppCompatActivity {

    protected void startActivityInitProcess(){
        onCreateInstances();
        onViewInit();
        onBindViewModel();
        onActivityInitFinished();
    }

    protected void onCreateInstances(){
        //does nothing by default
    }

    protected void onViewInit(){
        //does nothing by default
    }

    protected void onBindViewModel(){
        //does nothing by default
    }

    protected void onActivityInitFinished() {
        //does nothing by default
    }
}