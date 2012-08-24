ActionsContentView
===================

ActionsContentView is an standalone library implements actions/content swiping view.

The library doesn't use any specific code introduced in new Android SDK versions. This allows develop an application with an action/content swiping view for every version of Android from 2.2 and up.

Some advantages of this library:

* ability to slide view by touch
* it is easy to adjust size of actions bar in XML
* support of all Android SDK version starting from 2.0 and up

There is one limitation:

* all horizontal scrolling views will not work at bounds of this view


![Example application looks on phone][1]![Example application looks on phone][2]

![Example application looks on tablet][3]

Here is exmple of usage ActionsContentView as element of XML layout:

    <RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:layout_width="match_parent"
        android:layout_height="match_parent" >
    
    ...
    
        <shared.ui.actionscontentview.ActionsContentView
            android:id="@+id/content"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            app:actions_layout="@layout/actions"
            app:content_layout="@layout/content" />
    
    ...
    
    </RelativeLayout>

Features included
============
* Slide view by touch
* Shadow dropped by content view to actions one
* Offset for content view to show part actions view. Useful to hint user that there are actions under content.

Feature to be implemented in future
============
* Add shading for actions view while scrolling content
* Left side or both side actions support
* Swipe from the bezel to the screen so horizontal scrolling will be possible

Example
============
Try out the example application on the Android Market: [ActionsContentView Example][4].

Developed By
============

* Steven Rudenko - <steven.rudenko@gmail.com>



License
=======

    Copyright 2012 Steven Rudenko

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.




 [1]: http://lh5.ggpht.com/zLNsUDWGb37WMrsCOkg5H_WOBSnovsUgGPWtBCEo8pq8u-cR6oTmAs6VzypIw0VWxQ
 [2]: http://lh3.ggpht.com/fvYvbO8cLrzyozWlKrGM8koYI6xrm-WLX3-D5nUjCyNKVFuM5C1LR4Bps-BnVStNul0
 [3]: http://lh4.ggpht.com/gjSc5WXfxL2hZqq6Rno0Byx3nHEf7-n4G8ceDV3BC0e4wm2RpFxC7I8VMPgSA9fvKyw
 [4]: https://play.google.com/store/apps/details?id=sample.actionscontentview
