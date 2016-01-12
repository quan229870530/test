/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: E:\\vincent0105\\UpdateTopSystem\\src\\com\\kuaxue\\download\\IOnDownloadListener.aidl
 */
package com.kuaxue.download;
public interface IOnDownloadListener extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.kuaxue.download.IOnDownloadListener
{
private static final java.lang.String DESCRIPTOR = "com.kuaxue.download.IOnDownloadListener";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.kuaxue.download.IOnDownloadListener interface,
 * generating a proxy if needed.
 */
public static com.kuaxue.download.IOnDownloadListener asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.kuaxue.download.IOnDownloadListener))) {
return ((com.kuaxue.download.IOnDownloadListener)iin);
}
return new com.kuaxue.download.IOnDownloadListener.Stub.Proxy(obj);
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
case TRANSACTION_onStart:
{
data.enforceInterface(DESCRIPTOR);
this.onStart();
reply.writeNoException();
return true;
}
case TRANSACTION_progress:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
long _arg1;
_arg1 = data.readLong();
this.progress(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_onFinish:
{
data.enforceInterface(DESCRIPTOR);
this.onFinish();
reply.writeNoException();
return true;
}
case TRANSACTION_onException:
{
data.enforceInterface(DESCRIPTOR);
this.onException();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.kuaxue.download.IOnDownloadListener
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
@Override public void onStart() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onStart, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void progress(long postion, long length) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(postion);
_data.writeLong(length);
mRemote.transact(Stub.TRANSACTION_progress, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onFinish() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onFinish, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onException() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onException, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_onStart = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_progress = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_onFinish = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_onException = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
}
public void onStart() throws android.os.RemoteException;
public void progress(long postion, long length) throws android.os.RemoteException;
public void onFinish() throws android.os.RemoteException;
public void onException() throws android.os.RemoteException;
}
