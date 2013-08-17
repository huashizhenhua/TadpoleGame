package com.itap.voiceemoticon.activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

@SuppressWarnings({"unchecked" })
public class NotificationCenter {
    
    public static final int MAX_COUNT = 100;
    
	private static ArrayList<WeakReference<INotify>>[] mArray;
	
	static {
		mArray = new ArrayList[MAX_COUNT];
		for (int i = 0; i < MAX_COUNT - 1; i++) {
			mArray[i] = new ArrayList<WeakReference<INotify>>();
		}
	}
	
	private static NotificationCenter mInstance = null;
	
	private NotificationCenter() {
	}
	
	public static NotificationCenter getInstance() {
		if (mInstance == null) {
			mInstance =  new NotificationCenter();
		}
		return mInstance;
	}
	
	public void register(INotify notify, int notificationID) {
		mArray[notificationID].add(new WeakReference<INotify>(notify));
	}
	
	public void unregister(INotify notify, int notificationID) {
		final int size = mArray[notificationID].size();
		INotify arrayMember;
		for (int i = 0; i < size; i++) {
			WeakReference<INotify> weakObject = mArray[notificationID].get(i);
			arrayMember = weakObject.get();
			if (arrayMember != null && arrayMember == notify) {
				mArray[notificationID].remove(weakObject);
				return;
			}
		}
	}
	
	private ArrayList<WeakReference<INotify>> mTempUseArray = new ArrayList<WeakReference<INotify>>();
	
	public void notify(Notification notification) {
		INotify notify;
		for (int i=0; i<mArray[notification.id].size(); ++i) {
			WeakReference<INotify> weakObject = mArray[notification.id].get(i);
			notify = weakObject.get();
			if (notify != null) {
				notify.notify(notification);
			} else {
				mTempUseArray.add(weakObject);
			}
		}
		
		for (int i=0; i<mTempUseArray.size(); ++i) {
			mArray[notification.id].remove(mTempUseArray.get(i));
		}
		mTempUseArray.clear();
	}
	
	public static Notification obtain(int id, Object extObj) {
		return new Notification(id, extObj);
	}
	
	public static Notification obtain(int id) {
		return new Notification(id);
	}
}
