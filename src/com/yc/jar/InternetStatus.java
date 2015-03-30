package com.yc.jar;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
/**
 * @author Administrator
 *
 */
public class InternetStatus {

	private final static int CMNET = 3;
	private final static int CMWAP = 2;
	private final static int WIFI = 1;
	private Context context;

	public InternetStatus(Context context) {
		this.context = context;
	}

	/**
	 * 
	 * @return
	 */
	private int getAPNType() {
		int netType = -1;
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();

		if (nType == ConnectivityManager.TYPE_MOBILE) {
			if (networkInfo.getExtraInfo().toLowerCase().equals("cmnet")) {
				netType = CMNET;
			}
			else {
				netType = CMWAP;
			}

		}

		else if (nType == ConnectivityManager.TYPE_WIFI) {

			netType = WIFI;

		}

		return netType;

	}

	/**
	 * 
	 * @return
	 */
	public boolean isNetworkConnected() {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {// mNetworkInfo.isAvailable();
				return mNetworkInfo.isConnected();
			}
		}
		return false;
	}

	/**
	 * 
	 * @return
	 */
	private boolean isWifiConnected() {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo != null) {
				return mWiFiNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * 
	 * @return
	 */
	private boolean isMobileConnected() {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mMobileNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mMobileNetworkInfo != null) {
				return mMobileNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * @return
	 */
	private int getConnectedType() {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
				return mNetworkInfo.getType();
			}
		}
		return -1;
	}
	/**
	 * @return
	 */
	private boolean isNetworkAvailable() {   
		          ConnectivityManager connectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);   
		          if (connectivity == null) {   
		              Log.i("NetWorkState", "Unavailabel");   
		              return false;   
		          } else {   
		              NetworkInfo[] info = connectivity.getAllNetworkInfo();   
		              if (info != null) {   
		                  for (int i = 0; i < info.length; i++) {   
		                     if (info[i].getState() == NetworkInfo.State.CONNECTED) {   
		                         Log.i("NetWorkState", "Availabel");   
		                         return true;   
		                     }   
		                 }   
		             }   
		         }   
		         return false;   
		     }  
}