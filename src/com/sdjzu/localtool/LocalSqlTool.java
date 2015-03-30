package com.sdjzu.localtool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
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

	/**
	 * 向本地插入教学任务
	 * 
	 * @param t
	 */
	public void insertTeachTaskByTaskNo(List<TeachTask> tList) {
		String sql = "	insert into TeachTask(Rno,Cno,Cname,Tname,Rclass,Ctype,Rweek,Rterms) values(?,?,?,?,?,?,?,?)";
		db = DatabaseManager.getInstance(context);
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
		String sql = "insert into TeachProgress  (Jno ,Rno,Cname,Tname,Jclass,Jweek,Jtime,Jaddress,StartTime,EndTime,IsKQ ,InMan  ,InTime) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
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
		String sql = "insert into KqInfo(Info,IsRead,ReceiveTime,InMan values(?,?,?,?))";
		db.beginTransaction();
		int counts = list.size();
		try {
			for (int i = 0; i < counts; i++) {
				KQInfo kqInfo = list.get(i);
				db.execSQL(sql,
						new String[] { kqInfo.getMsg(), String.valueOf(kqInfo.getIsRead()), kqInfo.getDateTime(),
								kqInfo.getTname() });
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
			kqInfo.setIsRead(cursor.getInt(cursor.getColumnIndex("IsRead")));
			kqInfo.setMsg(cursor.getString(cursor.getColumnIndex("Info")));
			kqInfo.setTname(cursor.getString(cursor.getColumnIndex("InMan")));
			kqInfo.setId(cursor.getInt(cursor.getColumnIndex("Id")));
		}
		cursor.close();
		return list;
	}
}
