/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/chenzh/Desktop/github/TadpoleGame/Tadpole/src/org/tadpole/aidl/IPluginCallback.aidl
 */
package org.tadpole.aidl;
public interface IPluginCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements org.tadpole.aidl.IPluginCallback
{
private static final java.lang.String DESCRIPTOR = "org.tadpole.aidl.IPluginCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an org.tadpole.aidl.IPluginCallback interface,
 * generating a proxy if needed.
 */
public static org.tadpole.aidl.IPluginCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof org.tadpole.aidl.IPluginCallback))) {
return ((org.tadpole.aidl.IPluginCallback)iin);
}
return new org.tadpole.aidl.IPluginCallback.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_handle:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.handle(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_startActivity:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
int _arg2;
_arg2 = data.readInt();
android.os.Bundle _arg3;
_arg3 = new android.os.Bundle();
this.startActivity(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
if ((_arg3!=null)) {
reply.writeInt(1);
_arg3.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_isHideLoadingScreen:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isHideLoadingScreen();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements org.tadpole.aidl.IPluginCallback
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public void handle(int testInt) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(testInt);
mRemote.transact(Stub.TRANSACTION_handle, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void startActivity(java.lang.String packageName, java.lang.String activityClassName, int iCallingPid, android.os.Bundle bundle) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(packageName);
_data.writeString(activityClassName);
_data.writeInt(iCallingPid);
mRemote.transact(Stub.TRANSACTION_startActivity, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
bundle.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean isHideLoadingScreen() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isHideLoadingScreen, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_handle = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_startActivity = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_isHideLoadingScreen = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
}
public void handle(int testInt) throws android.os.RemoteException;
public void startActivity(java.lang.String packageName, java.lang.String activityClassName, int iCallingPid, android.os.Bundle bundle) throws android.os.RemoteException;
public boolean isHideLoadingScreen() throws android.os.RemoteException;
}
