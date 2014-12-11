/*===============================================================================
Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of QUALCOMM Incorporated, registered in the United States 
and other countries. Trademarks of QUALCOMM Incorporated are used with permission.
===============================================================================*/

package com.example.HiddenCities;

import java.lang.ref.WeakReference;

import processing.core.PApplet;
import processing.test.Vuforia.VuforiaScene;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;


public final class LoadingDialogHandler extends Handler
{
    private final WeakReference<Activity> mActivity;
    // Constants for Hiding/Showing Loading dialog
    public static final int HIDE_LOADING_DIALOG = 0;
    public static final int SHOW_LOADING_DIALOG = 1;
    
    public View mLoadingDialogContainer;
    
    
    public LoadingDialogHandler(PApplet aPApplet)
    {
        mActivity = new WeakReference<Activity>(aPApplet.getActivity());
    }
    
    
    public void handleMessage(Message msg)
    {
        Activity imageTargets = mActivity.get();
        if (imageTargets == null)
        {
            return;
        }
        
        if (msg.what == SHOW_LOADING_DIALOG)
        {
            mLoadingDialogContainer.setVisibility(View.VISIBLE);
            
        } else if (msg.what == HIDE_LOADING_DIALOG)
        {
            mLoadingDialogContainer.setVisibility(View.GONE);
        }
    }
    
}
