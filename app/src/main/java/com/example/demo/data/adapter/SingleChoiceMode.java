/***
 Copyright (c) 2015 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain	a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 From _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.example.demo.data.adapter;

import android.os.Bundle;

public class SingleChoiceMode implements ChoiceMode {

    private static final String STATE_CHECKED = "checked_position";
    private int mCheckedPosition = -1;

    @Override
    public boolean isSingleChoice() {
        return true;
    }

    @Override
    public int getCheckedPosition() {
        return mCheckedPosition;
    }

    @Override
    public void setChecked(int position, boolean isChecked) {
        if (isChecked) {
            // set the view to checked
            mCheckedPosition = position;
        } else if (isChecked(position)) {
            // if the view is already checked, uncheck it
            mCheckedPosition = -1;
        }
    }

    @Override
    public boolean isChecked(int position) {
        return mCheckedPosition == position;
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        state.putInt(STATE_CHECKED, mCheckedPosition);
    }

    @Override
    public void onRestoreInstanceState(Bundle state) {
        mCheckedPosition = state.getInt(STATE_CHECKED, -1);
    }


}
