package morgantech.com.gms.Utils;
import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {

    public void setPreferencesString(Context context, String key, String value){

        SharedPreferences.Editor editor=context.getSharedPreferences(Constraints.PROJECT_KEY, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getPreferencesString(Context context, String key){


        SharedPreferences prefs=context.getSharedPreferences(Constraints.PROJECT_KEY, Context.MODE_PRIVATE);
        String position=prefs.getString(key, "");
        return position;
    }

    public void deletePrefrence(Context context)
    {
        SharedPreferences prefs=context.getSharedPreferences(Constraints.PROJECT_KEY, Context.MODE_PRIVATE);
        prefs.edit().clear().commit();


    }


    public void setPreferencesBoolean(Context context, String key, Boolean value){

        SharedPreferences.Editor editor=context.getSharedPreferences(Constraints.PROJECT_KEY, Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public Boolean getPreferencesBoolean(Context context, String key){
        SharedPreferences prefs=context.getSharedPreferences(Constraints.PROJECT_KEY, Context.MODE_PRIVATE);
        Boolean flag=prefs.getBoolean(key, false);
        return flag;
    }

    public void setPreferencesInt(Context context,String key, int value){
        SharedPreferences.Editor editor=context.getSharedPreferences(Constraints.PROJECT_KEY, Context.MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public int getPreferencesInt(Context context,String key){


        SharedPreferences prefs=context.getSharedPreferences(Constraints.PROJECT_KEY, Context.MODE_PRIVATE);
        int position=prefs.getInt(key, 0);
        return position;
    }
}