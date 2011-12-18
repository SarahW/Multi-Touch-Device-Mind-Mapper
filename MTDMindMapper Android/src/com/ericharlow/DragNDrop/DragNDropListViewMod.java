/*
 * Copyright (C) 2010 Eric Harlow
 * 
 * Modification Copyright (C) 2011 Sarah Will
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.ericharlow.DragNDrop;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import de.sarahw.ma.phone.TabBarActivity;
import de.sarahw.ma.phone.TransferActivity;

/**
 * <p>
 * Modification of the DragNDropListView class which only allows drag and drop
 * of list items but no item reordering on drop.
 * </p>
 * <p>
 * Modified 2011-09<br>
 * </p>
 * 
 * @author Eric Harlow
 * @author (Modified by) Sarah Will
 * 
 */
@SuppressWarnings({ "unqualified-field-access", "nls" })
public class DragNDropListViewMod extends ListView {

    private static final String TAG = "DragNDropListViewMod";

    boolean                     mDragMode;

    int                         mStartPosition;
    int                         mEndPosition;
    int                         mDragPointOffset;            // Used to adjust
                                                              // drag view
                                                              // location

    ImageView                   mDragView;
    GestureDetector             mGestureDetector;
    View                        mDragItem;

    DropListener                mDropListener;
    RemoveListener              mRemoveListener;
    DragListener                mDragListener;

    private String              mDragItemText;

    public DragNDropListViewMod(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDropListener(DropListener l) {
        mDropListener = l;
    }

    public void setRemoveListener(RemoveListener l) {
        mRemoveListener = l;
    }

    public void setDragListener(DragListener l) {
        mDragListener = l;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        final int x = (int) ev.getX();
        final int y = (int) ev.getY();

        // Only start the drag if the first three fourths of the list have been
        // touched, the last fourth is reserved for scrolling
        if (action == MotionEvent.ACTION_DOWN && x < this.getWidth() * 0.75f) {
            mDragMode = true;
        }

        if (!mDragMode) {
            return super.onTouchEvent(ev);
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mStartPosition = pointToPosition(x, y);
                Log.d(TAG, "DOWN: Point to Position: " //$NON-NLS-2$
                        + mStartPosition);
                if (mStartPosition != INVALID_POSITION) {

                    int mItemPosition = mStartPosition
                            - getFirstVisiblePosition();
                    mDragPointOffset = y - getChildAt(mItemPosition).getTop();
                    mDragPointOffset -= ((int) ev.getRawY()) - y;
                    startDrag(mStartPosition - getFirstVisiblePosition(), y);
                }
                break;
            case MotionEvent.ACTION_MOVE:

                drag(0, y);// replace 0 with x if desired
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
            default:
                if (mDragMode) {
                    mDragMode = false;
                    mEndPosition = pointToPosition(x, y);
                    Log.d(TAG, "UP: Point to Position: " + mEndPosition);

                    stopDrag(mStartPosition - getFirstVisiblePosition(), x, y);

                    // MODIFICATION: Don't move the entry
                    // stopDrag(mStartPosition);

                    if (mDropListener != null
                            && mStartPosition != INVALID_POSITION
                            && mEndPosition != INVALID_POSITION)
                        mDropListener.onDrop(mStartPosition, mEndPosition);
                }
                break;
        }

        return true;
    }

    // move the drag view
    private void drag(int x, int y) {
        if (mDragView != null) {
            WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) mDragView
                    .getLayoutParams();
            layoutParams.x = x;
            layoutParams.y = y - mDragPointOffset;
            WindowManager mWindowManager = (WindowManager) getContext()
                    .getSystemService(Context.WINDOW_SERVICE);
            mWindowManager.updateViewLayout(mDragView, layoutParams);

            if (mDragListener != null)
                mDragListener.onDrag(x, y, null);// change null to "this" when
                                                 // ready to use
        }
    }

    // enable the drag view for dragging
    private void startDrag(int itemIndex, int y) {

        Log.d(TAG, "startDrag(itemIndex=" + itemIndex + ", y=" + y + ")");
        stopDrag(itemIndex);

        mDragItem = getChildAt(itemIndex);
        if (mDragItem == null) {
            return;
        }
        if (mDragItem instanceof TextView) {
            mDragItemText = ((TextView) mDragItem).getText().toString();
            Log.d(TAG, "Picked textView text:" + mDragItemText);
        }
        mDragItem.setDrawingCacheEnabled(true);
        if (mDragListener != null) {
            mDragListener.onStartDrag(mDragItem);
        }

        // Create a copy of the drawing cache so that it does not get recycled
        // by the framework when the list tries to clean up memory
        Bitmap bitmap = Bitmap.createBitmap(mDragItem.getDrawingCache());

        mDragItem.setDrawingCacheEnabled(false);

        WindowManager.LayoutParams mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.gravity = Gravity.TOP;
        mWindowParams.x = 0;
        mWindowParams.y = y - mDragPointOffset;

        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        mWindowParams.format = PixelFormat.TRANSLUCENT;
        mWindowParams.windowAnimations = 0;

        Context context = getContext();
        ImageView v = new ImageView(context);
        v.setImageBitmap(bitmap);
        v.invalidate();

        WindowManager mWindowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.addView(v, mWindowParams);
        mDragView = v;
    }

    // destroy drag view
    private void stopDrag(int itemIndex) {
        Log.d(TAG, "stopDrag(itemIndex=" + itemIndex + ")");

        if (mDragView != null) {
            if (mDragListener != null)
                mDragListener.onStopDrag(getChildAt(itemIndex));
            mDragView.setVisibility(GONE);
            WindowManager wm = (WindowManager) getContext().getSystemService(
                    Context.WINDOW_SERVICE);
            wm.removeView(mDragView);
            mDragView.setImageDrawable(null);
            mDragView = null;
        }
    }

    /**
     * Modified stopDrag method.
     * 
     * @param itemIndex
     *            the (visible) index of the item
     * @param x
     *            the x position on stopDrag
     * @param y
     *            the y position on stopDrag
     */
    private void stopDrag(int itemIndex, int x, int y) {

        Log.d(TAG, "Entering stopDrag(itemIndex=" + itemIndex + ", x=" + x
                + ",y=" + y + ")");

        if (mDragView != null) {
            if (mDragListener != null)
                mDragListener.onStopDrag(getChildAt(itemIndex));
            mDragView.setVisibility(GONE);
            WindowManager wm = (WindowManager) getContext().getSystemService(
                    Context.WINDOW_SERVICE);
            wm.removeView(mDragView);
            mDragView.setImageDrawable(null);
            mDragView = null;

            // Check if drop position is in certain area
            if (checkForDropOnArea(x, y)) {

                Log.d(TAG, "Position lies within drop image area");

                // Send item text to remote device
                if (sendItemTextToRemoteDevice(mDragItemText)) {

                    // for now do nothing
                    Log.d(TAG, "Sending text to remote device successful");

                    Log.d(TAG, "Leaving stopDrag()");
                }

            } else {
                Log.d(TAG,
                        "Leaving stopDrag(): Position lies outside of drop image area");
            }
        } else {
            Log.e(TAG, "Leaving stopDrag(): mDragView invalid (null)");
        }

    }

    /**
     * Calls the sendStringToRemoteDevice method from the TransferActivity.
     * 
     * @param dragItemText
     *            the text of the dragged list item
     * 
     * @return sendStringToRemoteDevice() result
     */
    private boolean sendItemTextToRemoteDevice(String dragItemText) {

        Log.d(TAG, "Entering sendItemTextToRemoteDevice(dragItemText="
                + dragItemText + ")");

        Activity currentActivity = TabBarActivity.myTabLayout
                .getCurrentSelectedActivity();
        if (currentActivity != null) {
            if (currentActivity instanceof TransferActivity) {

                Log.d(TAG,
                        "Leaving sendItemTextToRemoteDevice(): result of sendStringToRemoteDevice()");

                return ((TransferActivity) currentActivity)
                        .sendStringToRemoteDevice(dragItemText);

            }
        }
        Log.e(TAG,
                "Leaving sendItemTextToRemoteDevice(): false, current activity invalid (null)");
        return false;

    }

    /**
     * Calls the checkListViewItemPosForImageView() method in TransferActivity.
     * 
     * @return checkListViewItemPosForImageView() result
     */
    private boolean checkForDropOnArea(int dropX, int dropY) {

        Log.d(TAG, "Entering checkForDropOnArea(dropX=" + dropX + ", dropY="
                + dropY + ")");

        Activity currentActivity = TabBarActivity.myTabLayout
                .getCurrentSelectedActivity();

        if (currentActivity != null) {
            if (currentActivity instanceof TransferActivity) {

                Log.d(TAG,
                        "Leaving checkForDropOnArea(): result of checkListViewItemPosForImageView()");

                return ((TransferActivity) currentActivity)
                        .checkListViewItemPosForImageView(dropX, dropY);
            }
        }

        Log.e(TAG,
                "Leaving checkForDropOnArea(): false, current activity invalid (null)");
        return false;
    }

}
