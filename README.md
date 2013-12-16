ActionsContentView
===================

ActionsContentView is an standalone library implements actions/content swiping view.

The library doesn't use any specific code introduced in new Android SDK versions. This allows develop an application with an action/content swiping view for every version of Android from 2.2 and up.

Some advantages of this library:

* ability to slide view by touch
* it is easy to adjust size of actions bar in XML
* support of all Android SDK versions, starting from 2.0 and up

There is one limitation:

* all horizontal scrolling views will not work at bounds of this view, except you will use swipe from the bezel mode (read Useful hints for more information).

Screenshots
-----------

![Example application looks on phone][1]![Example application looks on phone][2]

![Example application looks on tablet][3]

Features included
-----------------
* Slide view by touch.
* Shadow dropped by content view to actions one.
* Offset for content view to show part actions view. Useful to hint user that there are actions under content.
* Add shading for actions view while scrolling content.
* Swipe from the bezel to the screen so horizontal scrolling is possible
* Effects for Action and Content views. See the example below.

Feature to be implemented in future
-----------------------------------
* Left, right or both side sliding support


Examples
=============

Here is an example of ActionsContentView as an element of an XML layout:

```xml
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent" >

    ...

    <shared.ui.actionscontentview.ActionsContentView
        android:id="@+id/content"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_above="@id/divider"
        app:actions_layout="@layout/actions"
        app:actions_spacing="0dp"
        app:content_layout="@layout/content"
        app:shadow_drawable="@drawable/shadow"
        app:shadow_width="8dip"
        app:spacing="64dip"
        app:spacing_type="right_offset" />
    
    ...
   
</RelativeLayout>
```
Example application
-------------------
Try out the example application on the Android Play Store: [ActionsContentView Example][4].

Useful hints
============

Enable horizontal scrolling
-------------
To enable horizontal scrolling at content view, you should set swipe from bezel mode.
It is easy to do this by adding next row to XML layout:

```xml
<shared.ui.actionscontentview.ActionsContentView
    ...
    app:swiping_type="edge"
    ...
    />
```

or by next line(s) of code:

```java
viewActionsContentView.setSwipingType(ActionsContentView.SWIPING_EDGE);
```


Parallax effect for actions layout
-------------
To create parallax effect we should create a translate animation and use it as the effect for the actions layout.
Here are steps to get it done:

Create <project_path>/res/anim/actions.xml and put next code into it:

```xml
<?xml version="1.0" encoding="utf-8"?>
<translate xmlns:android="http://schemas.android.com/apk/res/android"
    android:fromXDelta="0"
    android:interpolator="@android:anim/accelerate_decelerate_interpolator"
    android:toXDelta="-200" />
```
Add the next line to the XML layout of ActionsContentView:

```xml
<shared.ui.actionscontentview.ActionsContentView
    ...
    app:effect_actions="@anim/actions"
    ...
    />
```
Parallax demo can be seen [here][5].

How to use
============

Please follow next instruction to import library project into your Eclipse workspace:

1. Clone GitHub somewhere
1. Open Eclipse workspace
1. Press menu File->Import->Android->Existing Android code into workspace
1. Import ActionsContentViewLib project
1. Select your project and press menu Project->Properties
1. Select Android.
1. Press Add at Library section. Select ActionsContentViewLib.
1. Apply->OK

If you have got error like this one: "Android requires compiler compliance level 5.0 or 6.0. Found '1.7' instead. Please use Android Tools > Fix Project Properties" use next steps to resolve it:

1. Open workspace with project
2. Select project
3. Press menu: Project->Properties
4. Goto tab Java Compiler
5. Check 'Enable project specific settings'
6. Set 'Compiler compliance level' to 1.6
7. Apply->OK

Do this for all Android projects or workspace setting and problem should be resolved.


-------------------------------------------------------------------------------

Developed By
============

* Steven Rudenko - <steven.rudenko@gmail.com>

License
=======

    Copyright 2013 Steven Rudenko

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.




 [1]: https://lh5.ggpht.com/jDdm1FbB13aXq11J61__URorAlT-h12kvU0VlaaDdL1PF5wNrUOVJmdKMqlz506hIg
 [2]: https://lh4.ggpht.com/98rCqlg4r2aUJCFKZ-_-yTJpVd2OAN4SMzqpiDAvDX-IM0IDTXcvoPEKfQJWQM1IXUU-
 [3]: https://lh6.ggpht.com/yYy24DPwltmo1Xp0SPAyWzpKOIF7azoTvlveH3X4XWkHo_xm0UQ1lcT-1NJl8QUWBCDA
 [4]: https://play.google.com/store/apps/details?id=sample.actionscontentview
 [5]: http://img534.imageshack.us/img534/6403/actionscontentviewparal.gif
