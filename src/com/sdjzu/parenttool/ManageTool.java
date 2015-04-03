package com.sdjzu.parenttool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;

import com.sdjzu.localtool.LocalSqlTool;
import com.yc.jar.InternetStatus;

import edu.sdjzu.attr.Attr;
import edu.sdjzu.model.KQInfo;
import edu.sdjzu.model.KQStuPerson;

public class ManageTool {
	private Context context;

	/**
	 * 根据Spinner的选择项,用户名密码和来启动指定的Activity，并保存登陆信息
	 * 
	 * @param context
	 *            当前Acitivity的上下文
	 * @param username
	 *            用户名
	 * @param password
	 *            用户密码
	 */
	public boolean LoginStartActivity(String username, String password, SharedPreferences sp) {
		boolean isSuccess = false;
		InternetStatus is = new InternetStatus(context);
		boolean isF = sp.getBoolean(Attr.isFirstLogin, true);
		if (is.isNetworkConnected()) {
			if (WebLoginSuccess(username, password)) {
				ManagerLoginTool teaWebTool = new ManagerLoginTool(context);
				if (isF) {// 第一次获得所有数据操作
					try {
						teaWebTool.firstLogin(username);
						isSuccess = true;
						sp.edit().putBoolean(Attr.isFirstLogin, false).commit();
					} catch (Exception e) {
						sp.edit().putBoolean(Attr.isFirstLogin, true);
					} catch (Error ex) {
						sp.edit().putBoolean(Attr.isFirstLogin, true);
					}
				} else {// 更新数据操作
					try {
						teaWebTool.secondLogin(username);
						isSuccess = true;
					} catch (Error ex) {
					}
				}
			}
		} else {
			return LocalLoginSuccess(username, password);
		}
		return isSuccess;
	}

	public ManageTool(Context context) {
		super();
		this.context = context;
	}

	/**
	 * 根据用户类型进行登录，成功则返回true
	 * 
	 * @param userType
	 * @param username
	 * @param password
	 * @return
	 */
	private boolean WebLoginSuccess(String username, String password) {
		WebTool web = null;
		try {
			web = new WebTool(context);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (web != null) {
			return web.LoginSuccess(Attr.userType, username, password);
		} else
			return false;
	}

	/**
	 * 
	 * 根据学生编号获得学生的考勤详细信息
	 * 
	 * @param sno
	 * @return
	 */
	public List<KQStuPerson> getKQStuPerson(String sno) {
		WebTool webTool = null;
		try {
			webTool = new WebTool(context);
			return webTool.getKQStuPerson(sno);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ArrayList<KQStuPerson>();

	}

	/**
	 * 获得学生缺勤请假迟到次数已经到课率
	 * 
	 * @param uno
	 * @return
	 */
	public String getKqCount(String sno) {
		WebTool webTool = null;
		try {
			webTool = new WebTool(context);
			return webTool.getKqCount(sno);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	/**
	 * 更新考勤信息被阅读过
	 * 
	 * @param id
	 */
	public void updateKqInfo(List<Integer> listId) {
		new LocalSqlTool(context).updateKqInfo(listId);
	}
	
	
	/**
	 * 向本地插入最新的考勤信息
	 * 
	 * @param list
	 */
	public void insertKqInfo(List<KQInfo> list) {
		new LocalSqlTool(context).insertKqInfo(list);
	}
	/**
	 * 获得本地考勤信息
	 * 
	 * @return
	 */
	public List<KQInfo> getKqInfo() {
		return new LocalSqlTool(context).getKqInfo();
	}
	/**
	 * 根据导员编号获得最新的考勤通知信息
	 * 
	 * @param uno
	 * @return
	 */
	public List<String> getLatestKqInfoByParentNo(String no) {
		try {
			WebTool web = new WebTool(context);
			return web.getLatestKqInfoByParentNo(no);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<String>();
	}
	/**
	 * 无网络连接状态下从本地登陆
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	private boolean LocalLoginSuccess(String username, String password) {
		return false;// new LocalSqlTool(context).localLogin(username,
						// password);
	}

}
