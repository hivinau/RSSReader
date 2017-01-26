package fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.helpers;

import android.os.*;
import android.view.*;
import android.content.*;
import android.transition.*;
import android.support.v7.app.*;
import android.support.v4.app.*;

public class FragmentHelper {

    public static int addFragment(AppCompatActivity activity, String fragmentClassname, int containerId, Bundle bundle) {

        int id = -1;

        try {

            Fragment fragment = Fragment.instantiate(activity, fragmentClassname);

            if(fragment != null) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    fragment.setEnterTransition(new Slide(Gravity.END));
                    fragment.setExitTransition(new Slide(Gravity.START));
                }

                if(bundle != null) {

                    fragment.setArguments(bundle);
                }

                FragmentManager manager = activity.getSupportFragmentManager();

                FragmentTransaction transaction = manager.beginTransaction();
                //transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.replace(containerId, fragment, fragmentClassname);
                transaction.addToBackStack(fragmentClassname);

                id = transaction.commit();
            }

        } catch (Exception ignored) {

            ignored.printStackTrace();
        }

        return id;
    }

    public static void popToFragment(AppCompatActivity activity, int id) {

        FragmentManager manager = activity.getSupportFragmentManager();
        manager.popBackStack(id, 0);
    }

    public static boolean popBackStack(AppCompatActivity activity) {

        FragmentManager manager = activity.getSupportFragmentManager();

        boolean popped = false;

        if(manager.getBackStackEntryCount() > 1) {

            manager.popBackStack();
            popped = true;
        }

        return popped;
    }

    public static boolean fragmentExist(AppCompatActivity activity, int id) {

        FragmentManager manager = activity.getSupportFragmentManager();

        return manager.findFragmentById(id) != null;
    }

    public static int fragmentsCount(AppCompatActivity activity) {

        FragmentManager manager = activity.getSupportFragmentManager();

        return manager.getBackStackEntryCount();
    }

    public static void finishWithResult(AppCompatActivity activity, int resultCode, Bundle bundle) {

        if(activity != null) {

            Intent intent = new Intent();

            if(bundle != null) {

                intent.putExtras(bundle);
            }

            activity.setResult(resultCode, intent);
            activity.finish();
        }
    }
}