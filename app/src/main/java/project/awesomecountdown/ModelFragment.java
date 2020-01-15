package project.awesomecountdown;

import androidx.fragment.app.Fragment;

public class ModelFragment extends Fragment {
    protected void startFragmentInitProcess(){
        onCreateInstances();
        onViewInit();
        onBindViewModel();
        onFragmentInitFinished();
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

    protected void onFragmentInitFinished() {
        //does nothing by default
    }

}
