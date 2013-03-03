/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/chenzh/Desktop/github/TadpoleGame/Tadpole/src/org/tadpole/aidl/PluginServiceConnect.aidl
 */
package org.tadpole.aidl;
public interface PluginServiceConnect extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements org.tadpole.aidl.PluginServiceConnect
{
private static final java.lang.String DESCRIPTOR = "org.tadpole.aidl.PluginServiceConnect";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an org.tadpole.aidl.PluginServiceConnect interface,
 * generating a proxy if needed.
 */
public static org.tadpole.aidl.PluginServiceConnect asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof org.tadpole.aidl.PluginServiceConnect))) {
return ((org.tadpole.aidl.PluginServiceConnect)iin);
}
return new org.tadpole.aidl.PluginServiceConnect.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
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
case TRANSACTION_test:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.test(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_registerCallback:
{
data.enforceInterface(DESCRIPTOR);
org.tadpole.aidl.IPluginCallback _arg0;
_arg0 = org.tadpole.aidl.IPluginCallback.Stub.asInterface(data.readStrongBinder());
this.registerCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unRegisterCallback:
{
data.enforceInterface(DESCRIPTOR);
org.tadpole.aidl.IPluginCallback _arg0;
_arg0 = org.tadpole.aidl.IPluginCallback.Stub.asInterface(data.readStrongBinder());
this.unRegisterCallback(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements org.tadpole.aidl.PluginServiceConnect
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void test(int testInt) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(testInt);
mRemote.transact(Stub.TRANSACTION_test, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void registerCallback(org.tadpole.aidl.IPluginCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void unRegisterCallback(org.tadpole.aidl.IPluginCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unRegisterCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_test = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_registerCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_unRegisterCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
}
public void test(int testInt) throws android.os.RemoteException;
public void registerCallback(org.tadpole.aidl.IPluginCallback callback) throws android.os.RemoteException;
public void unRegisterCallback(org.tadpole.aidl.IPluginCallback callback) throws android.os.RemoteException;
}
