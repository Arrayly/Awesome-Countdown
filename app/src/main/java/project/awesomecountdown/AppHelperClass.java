package project.awesomecountdown;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.provider.SyncStateContract.Constants;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import java.io.ByteArrayOutputStream;
import java.util.Calendar;

public class AppHelperClass implements MyConstants {

    public static int getDefaultColorId(Context context) {
        return ContextCompat.getColor(context, R.color.white);
    }

    public static int[] getTabIndicatorColors(Context context) {
        int[] indicatorColors = new int[3];

        indicatorColors[0] = context.getColor(R.color.tab_countdown);
        indicatorColors[1] = context.getColor(R.color.tab_event);
        indicatorColors[2] = context.getColor(R.color.tab_expired);

        return indicatorColors;
    }

    public static String getRawDateString(int year, int month, int day, int hour, int min) {
        //add +1 to month coz january is zero based
        return "" + year + "/" + month + 1 + "/" + day + " · " + hour + ":" + min + ":" + "00" + " GMT";
    }

    //Save user image into shared prefs
    public static boolean saveUserImageIntoSharedPreferences(Bitmap bitmap, Context context) {

        try {
            //convert your image to it's Base64 string representation to save it into Shared preferences
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();

            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

            SharedPreferences sharedPreferences = context.getSharedPreferences(IMAGE_BITMAP_ID, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString(IMAGE_BITMAP_ID, encodedImage);
            editor.apply();

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("error_tag", "save image failed!: " + e);
            return false;
        }

        return true;
    }

    //Get user image from shared prefs
    public static Bitmap getUserBitmapImage(Context context) {
        Bitmap bitmap = null;
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences(IMAGE_BITMAP_ID, Context.MODE_PRIVATE);
            String previouslyEncodedImage = sharedPreferences.getString(IMAGE_BITMAP_ID, "");

            if (!previouslyEncodedImage.equalsIgnoreCase("") && !previouslyEncodedImage.equals("")) {
                byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
                bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    //Delete user image from helper class
    public static void deleteUserBitmapImage(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(IMAGE_BITMAP_ID, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(IMAGE_BITMAP_ID, "");
        editor.apply();
    }

    //Check if user image exists in shared prefs
    public static boolean checkIfUserBitmapImageExists(Context context) {
        boolean imageExists = false;

        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences(IMAGE_BITMAP_ID, Context.MODE_PRIVATE);
            String previouslyEncodedImage = sharedPreferences.getString(IMAGE_BITMAP_ID, "");

            if (!previouslyEncodedImage.equalsIgnoreCase("") && !previouslyEncodedImage.equals("")) {
                imageExists = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return imageExists;
    }


    public static int getFabWidth(Context context) {
        return (int) context.getResources().getDimension(R.dimen.fab_size);
    }

    //Returns a time stamp with AM/PM tag and other adjustments
    public static String getHourlieTimeStamp(long millis){
        String timeStamp = "";
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);

        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        if (minute < 10){
            timeStamp = hour + ":" + "0"+minute;
        }else {
            timeStamp = hour + ":" +minute;
        }


        if (c.get(Calendar.AM_PM) == Calendar.PM){
            timeStamp += " " + "PM";
        }else {
            timeStamp += " " + "AM";
        }

        return timeStamp;



    }
}
