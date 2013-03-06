package org.tadpole.aidl;

interface IPluginCallback {
   void handle(int testInt);
   void startActivity(String packageName, String activityClassName, int iCallingPid,out Bundle bundle);
   boolean isHideLoadingScreen();
}
