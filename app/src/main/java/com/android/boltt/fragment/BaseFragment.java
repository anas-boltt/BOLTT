package com.android.boltt.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

/**
 * This is the base implementation of Fragments in the app.
 * Make sure you extend every fragment you make by this BaseFragment
 */
public abstract class BaseFragment extends Fragment {

    protected Context mContext;
    public BaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }


    /**
     * Method to replace fragment with tag
     *
     * @param fragmentTagToBeAdded
     * @param className
     * @param bundle
     * @param containerId
     */
    public void replaceFragment(String fragmentTagToBeAdded,
                                Class className,
                                Bundle bundle,
                                int containerId,
                                boolean isAnimation,
                                int startAnimation,
                                int endAnimation) {

        if (fragmentTagToBeAdded != null) {
            FragmentTransaction fragmentTransaction = getFragmentManager()
                    .beginTransaction();
            if (isAnimation) {
                fragmentTransaction.setCustomAnimations(startAnimation, endAnimation);
            }
            fragmentTransaction.replace(
                    containerId,
                    Fragment.instantiate(getActivity(),className.getName(), bundle),
                    fragmentTagToBeAdded);
            fragmentTransaction.addToBackStack(fragmentTagToBeAdded);
            fragmentTransaction.commitAllowingStateLoss();
        }
    }
}
