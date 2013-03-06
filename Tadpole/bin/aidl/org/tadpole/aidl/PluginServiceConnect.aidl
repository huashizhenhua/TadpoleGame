package org.tadpole.aidl;
import org.tadpole.aidl.IPluginCallback;

interface PluginServiceConnect {
   void test(int testInt);
   void registerCallback(IPluginCallback callback);
   void unRegisterCallback(IPluginCallback callback);
}

					
