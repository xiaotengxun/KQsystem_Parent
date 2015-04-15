package edu.sdjzu.localtool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import edu.sdjzu.model.ChatInfo;
import edu.sdjzu.model.KQInfo;
import edu.sdjzu.model.Students;
import edu.sdjzu.model.TeachProgress;
import edu.sdjzu.model.TeachTask;

public class LocalSqlTool {
	private Context context;
	private DatabaseManager db;
	private static List<HashMap<String, String>> stuListHashMap = new ArrayList<HashMap<String, String>>();

	public LocalSqlTool(Context context) {
		super();
		this.context = context;
	}

	public String getStuName(String no) {
		String sql = "select * from Students where Sno=" + no;
		String name = "";
		db = DatabaseManager.getInstance(context);
		Cursor cursor = db.Query(sql, null);
		if (cursor.moveToNext()) {
			name = cursor.getString(cursor.getColumnIndex("Sname"));
		}
		cursor.close();
		return name;
	}

	public boolean localLogin(String username, String password) {
		db = DatabaseManager.getInstance(context);
		String sql = "select * from Students where Sno=? and Ppwd=?";
		Cursor cursor = db.Query(sql, new String[] { username, password });
		if (cursor.getCount() > 0) {
			cursor.close();
			return true;
		}
		cursor.close();
		return false;
	}

	/**
	 * 向本地插入教学任务
	 * 
	 * @param t
	 */
	public void insertTeachTaskByTaskNo(List<TeachTask> tList) {
		String sql = "delete from TeachTask;";
		db = DatabaseManager.getInstance(context);
		db.execSQL(sql);
		sql = "	insert into TeachTask(Rno,Cno,Cname,Tname,Rclass,Ctype,Rweek,Rterms) values(?,?,?,?,?,?,?,?)";
		db.beginTransaction();
		try {
			for (int i = 0; i < tList.size(); i++) {
				TeachTask t = tList.get(i);
				String[] arg = { String.valueOf(t.getTaskNo()), t.getCourseNo(), t.getCourseName(), t.getTeaName(),
						t.getTaskClass(), t.getCourseType(), t.getTaskWeek(), t.getTaskTerms() };
				db.execSQL(sql, arg);
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			db.endTransaction();
		}
	}

	/**
	 * 向本地插入学生个人信息
	 * 
	 * @param stu
	 */
	public void insertStuInfo(Students stu) {
		db = DatabaseManager.getInstance(context);
		String del = "delete from Students";
		db.execSQL(del);
		String sql = "insert into Students(Sid,Sno,Spwd,Ppwd,Sname,Ssex,Ssdept,Sclass,Sstate,Stel,Ptel,Spic) values(?,?,?,?,?,?,?,?,?,?,?,?)";
		String[] arg = { stu.getStuId(), stu.getStuNo(), stu.getStuPassword(), stu.getParPassword(), stu.getStuName(),
				stu.getStuSex(), stu.getStuSdept(), stu.getStuClass(), stu.getStuState(), stu.getStuTel(),
				stu.getParTel(), stu.getStuPic() };
		db.execSQL(sql, arg);
	}

	/**
	 * 插入进度表
	 * 
	 * @param lit
	 */
	public void insertAllJDTBbyTname(List<TeachProgress> lit) {
		if (lit.size() == 0)
			return;
		db = DatabaseManager.getInstance(context);
		String sql = "delete from TeachProgress";
		db.execSQL(sql);
		sql = "insert into TeachProgress  (Jno ,Rno,Cname,Tname,Jclass,Jweek,Jtime,Jaddress,StartTime,EndTime,IsKQ ,InMan  ,InTime) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		db.beginTransaction();
		try {
			for (int i = 0; i < lit.size(); i++) {
				TeachProgress t = lit.get(i);
				String[] arg = { String.valueOf(t.getProgressNo()), String.valueOf(t.getTaskNo()), t.getCourseName(),
						t.getTeaName(), t.getProgressClass(), t.getProgressWeek(), t.getProgressJTime(),
						t.getProgressAddress(), t.getStartTime(), t.getEndTime(), t.getIsKQ(), t.getInMan(),
						t.getInTime() };
				db.execSQL(sql, arg);
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			db.endTransaction();
		}
	}

	/**
	 * 更新考勤信息被阅读过
	 * 
	 * @param id
	 */
	public void updateKqInfo(List<Integer> listId) {
		db = DatabaseManager.getInstance(context);
		for (Integer id : listId) {
			String sql = "update KqInfo set IsRead='1' where Id='" + String.valueOf(id) + "'";
			db.execSQL(sql);
		}
	}

	/**
	 * 向本地插入最新的考勤信息
	 * 
	 * @param list
	 */
	public void insertKqInfo(List<KQInfo> list) {
		db = DatabaseManager.getInstance(context);
		String sql = "insert into KqInfo(Info,ReceiveTime,IsRead values(?,?,?))";
		db.beginTransaction();
		int counts = list.size();
		try {
			for (int i = 0; i < counts; i++) {
				KQInfo kqInfo = list.get(i);
				db.execSQL(sql,
						new String[] { kqInfo.getMsg(), kqInfo.getDateTime(), String.valueOf(kqInfo.getIsRead()) });
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
		} finally {
			db.endTransaction();
		}
	}

	/**
	 * 获得本地考勤信息
	 * 
	 * @return
	 */
	public List<KQInfo> getKqInfo() {
		db = DatabaseManager.getInstance(context);
		String sql = "select * from KqInfo ";
		Cursor cursor = db.Query(sql, null);
		List<KQInfo> list = new ArrayList<KQInfo>();
		while (cursor.moveToNext()) {
			KQInfo kqInfo = new KQInfo();
			kqInfo.setDateTime(cursor.getString(cursor.getColumnIndex("ReceiveTime")));
			kqInfo.setMsg(cursor.getString(cursor.getColumnIndex("Info")));
			kqInfo.setId(cursor.getInt(cursor.getColumnIndex("Id")));
		}
		cursor.close();
		return list;
	}

	/**
	 * 删除聊天消息
	 * 
	 * @param list
	 */
	public void deleteChatInfo(List<ChatInfo> list) {
		String sql = "delete from ChatInfo where id=";
		int size = list.size();
		if (size > 0) {
			sql += list.get(0);
		}
		for (int i = 1; i < size; i++) {
			sql += " or id=" + list.get(i);
		}
		if (size > 0) {
			db = DatabaseManager.getInstance(context);
			db.execSQL(sql);
		}
	}

	/**
	 * 根据聊天消息的id更新消息的阅读状态
	 * 
	 * @param id
	 */
	public void updateChatInfoReadState(String id) {
		String sql = "update ChatInfo set IsRead=1 where id=" + id;
		db = DatabaseManager.getInstance(context);
		db.execSQL(sql);
	}

	/**
	 * 返回详细的聊天信息
	 * 
	 * @param pSno
	 * @return
	 */
	public List<ChatInfo> getChatDetail(String pSno) {
		List<ChatInfo> listChat = new ArrayList<ChatInfo>();
		String sql = "select * from ChatInfo where SendNo=" + pSno + " or ReceiveNo=" + pSno + " order by Time asc";
		db = DatabaseManager.getInstance(context);
		Cursor cursor = db.Query(sql, null);
		while (cursor.moveToNext()) {
			ChatInfo chatInfo = new ChatInfo();
			chatInfo.setMsg(cursor.getString(cursor.getColumnIndex("Msg")));
			chatInfo.setBothsend(Integer.valueOf(cursor.getString(cursor.getColumnIndex("BothSend"))));
			chatInfo.setTime(cursor.getString(cursor.getColumnIndex("Time")));
			listChat.add(chatInfo);
		}
		cursor.close();
		int size = listChat.size();
		// for (int i = 0; i < size; ++i) {
		// ChatInfo firstChat = listChat.get(i);
		// for (int j = i; j < size; ++j) {
		// ChatInfo sendChat = listChat.get(j);
		// int compare =
		// firstChat.getTime().compareToIgnoreCase(sendChat.getTime());
		// if (compare < 0) {// 大于
		// listChat.set(i, sendChat);
		// listChat.set(j, firstChat);
		// firstChat = listChat.get(i);
		// }
		// }
		// }
		return listChat;
	}

	public void deleteChatGroup(List<String> list, String tno) {
		String sql = "delete from ChatInfo where SendNo=" + tno + " and (";
		int size = list.size();
		if (size > 0) {
			for (int i = 0; i < size - 2; ++i) {
				sql += " receiveNo=" + list.get(i) + " or";
			}
			sql += " receiveNo=" + list.get(size - 1) + ")";
			db = DatabaseManager.getInstance(context);
			db.execSQL(sql);
		}
	}

	/**
	 * 获得聊天的种类条目
	 * 
	 * @param no
	 * @return
	 */
	public List<ChatInfo> getChatGroup(String no) {
		List<ChatInfo> listChatInfo = new ArrayList<ChatInfo>();
		String sql = "select * from ChatInfo where SendNo=" + no + " group by ReceiveNo";
		db = DatabaseManager.getInstance(context);
		Cursor cursor = db.Query(sql, null);
		while (cursor.moveToNext()) {
			ChatInfo chatInfo = new ChatInfo();
			chatInfo.setId(cursor.getString(cursor.getColumnIndex("Id")));
			chatInfo.setIsRead(Integer.valueOf(cursor.getString(cursor.getColumnIndex("IsRead"))));
			chatInfo.setMsg(cursor.getString(cursor.getColumnIndex("Msg")));
			chatInfo.setSenderNo(cursor.getString(cursor.getColumnIndex("SendNo")));
			chatInfo.setSendName(cursor.getString(cursor.getColumnIndex("SendName")));
			chatInfo.setReceiveName(cursor.getString(cursor.getColumnIndex("ReceiveName")));
			chatInfo.setReceiverNo(cursor.getString(cursor.getColumnIndex("ReceiveNo")));
			chatInfo.setTime(cursor.getString(cursor.getColumnIndex("Time")));
			listChatInfo.add(chatInfo);
		}
		return listChatInfo;
	}

	/**
	 * 向本地插入聊天消息
	 * 
	 * @param list
	 */
	public void insertNewChatInfo(List<ChatInfo> list) {
		// (SendNo,ReceiveNo ,Msg ,SendName,"
		// + "ReceiveName,Time,BothSend,IsRead,SendType,ReceiveType;
		String sql = "insert into ChatInfo(SendNo,ReceiveNo,Msg,SendName,ReceiveName,Time,"
				+ "BothSend,IsRead,SendType,ReceiveType) values(?,?,?,?,?,?,?,?,?,?)";
		db = DatabaseManager.getInstance(context);
		db.beginTransaction();
		try {
			int size = list.size();
			for (int i = 0; i < size; ++i) {
				ChatInfo ch = list.get(i);
				String[] arg = new String[] { ch.getSenderNo(), ch.getReceiverNo(), ch.getMsg(), ch.getSendName(),
						ch.getReceiveName(), ch.getTime(), String.valueOf(ch.getBothsend()),
						String.valueOf(ch.getIsRead()), ch.getSendType(), ch.getReceiveType() };
				db.execSQL(sql, arg);
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.i("chen", "insert chatinfo error =" + e);
		} finally {
			db.endTransaction();
		}
	}

}
