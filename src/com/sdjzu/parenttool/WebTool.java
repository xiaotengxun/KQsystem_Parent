package com.sdjzu.parenttool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Log;

import com.sdjzu.localtool.LocalSqlTool;

import edu.sdjzu.ksoap.tools.MyAndroidHttpTransport;
import edu.sdjzu.model.KQStuPerson;
import edu.sdjzu.model.Students;
import edu.sdjzu.model.TeachProgress;
import edu.sdjzu.model.TeachTask;

public class WebTool {
	private LocalSqlTool localSqlTool;
	@SuppressWarnings("unused")
	private Context context;
	private String NAMESPACE = "http://chenshuwan.org/";
	private String METHOD_NAME = "";
	private String SOAP_ACTION = "";
	private String URL = "http://jsjzy.sdjzu.edu.cn/sdjzu/service1.asmx";
	private MyAndroidHttpTransport ht;

	public WebTool(Context context) throws IOException {
		super();
		this.context = context;
		localSqlTool = new LocalSqlTool(context);
		ht = new MyAndroidHttpTransport(URL);
	}

	/**
	 * 根据用户类型进行登录，成功则返回true
	 * 
	 * @param userType
	 * @param username
	 * @param password
	 * @return
	 */
	public boolean LoginSuccess(String userType, String username, String password) {
		boolean istrue = false;
		METHOD_NAME = "LoginSuccess";
		SOAP_ACTION = NAMESPACE + METHOD_NAME;
		SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
		rpc.addProperty("userType", userType);
		rpc.addProperty("username", username);
		rpc.addProperty("password", password);
		// AndroidHttpTransport ht = new AndroidHttpTransport(URL);
		ht.debug = true;
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.bodyOut = rpc;
		envelope.dotNet = true;
		(new MarshalBase64()).register(envelope);
		envelope.setOutputSoapObject(rpc);
		try {
			ht.call(SOAP_ACTION, envelope);
		} catch (XmlPullParserException e1) {
			return false;
		} catch (IOException e) {
			return false;
		}
		try {
			if (envelope.getResponse() != null) {
				istrue = Boolean.valueOf(envelope.getResponse().toString());
				System.out.println("用户" + username + "是否登录成功？" + istrue);
			}
		} catch (SoapFault e) {
			e.printStackTrace();
		}
		return istrue;
	}

	/**
	 * 返回与学生学号有关的教学任务表
	 * 
	 * @param sno
	 * @return
	 */
	public List<TeachTask> getTeachTaskBySno(String sno) {
		// getRWTBbySno
		List<TeachTask> tlist = new ArrayList<TeachTask>();
		METHOD_NAME = "getRWTBbySno";
		SOAP_ACTION = NAMESPACE + METHOD_NAME;
		SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
		rpc.addProperty("sno", sno);
		// AndroidHttpTransport ht = new AndroidHttpTransport(URL);
		ht.debug = true;
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.bodyOut = rpc;
		envelope.dotNet = true;
		(new MarshalBase64()).register(envelope);
		envelope.setOutputSoapObject(rpc);
		try {
			ht.call(SOAP_ACTION, envelope);
		} catch (XmlPullParserException e1) {
			e1.printStackTrace();
			return tlist;
		} catch (IOException e) {
			e.printStackTrace();
			return tlist;
		}
		try {
			if (envelope.getResponse() != null) {
				Object result = (Object) envelope.getResponse();
				SoapObject data = (SoapObject) result;
				int counts = data.getPropertyCount();
				for (int i = 0; i < counts; i++) {
					SoapObject s = (SoapObject) data.getProperty(i);
					TeachTask t = new TeachTask();
					t.setCourseName(s.getProperty("Cname").toString());
					t.setCourseNo(s.getProperty("Cno").toString());
					t.setCourseType(s.getProperty("Ctype").toString());
					t.setTaskClass(s.getProperty("Rclass").toString());
					t.setTaskNo(Integer.valueOf(s.getProperty("Rno").toString()));
					t.setTaskTerms(s.getProperty("Rterms").toString());
					t.setTaskWeek(s.getProperty("Rweek").toString());
					t.setTeaName(s.getProperty("Tname").toString());
					Log.i("chen",
							"课程:" + t.getCourseName() + "      上课班级:" + t.getTaskClass() + "    老师:" + t.getTeaName());
					tlist.add(t);
				}
			}
		} catch (SoapFault e) {
			e.printStackTrace();
			return tlist;
		}
		localSqlTool.insertTeachTaskByTaskNo(tlist);

		return tlist;
	}

	/**
	 * 根据学生学号获取学生信息
	 * 
	 * @param sno
	 * @return
	 */
	public Students getStuBySno(String sno) {
		Students stu = null;
		METHOD_NAME = "getStuBySno";
		SOAP_ACTION = NAMESPACE + METHOD_NAME;
		SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
		rpc.addProperty("sno", sno);
		// AndroidHttpTransport ht = new AndroidHttpTransport(URL);
		ht.debug = true;
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.bodyOut = rpc;
		envelope.dotNet = true;
		(new MarshalBase64()).register(envelope);
		envelope.setOutputSoapObject(rpc);
		try {
			ht.call(SOAP_ACTION, envelope);
		} catch (XmlPullParserException e1) {
			e1.printStackTrace();
			return stu;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return stu;
		}
		try {
			if (envelope.getResponse() != null) {
				Object result = (Object) envelope.getResponse();
				SoapObject data = (SoapObject) result;
				stu = new Students();
				stu.setStuNo(data.getProperty("Sno").toString());
				stu.setStuId(data.getProperty("Sid").toString());
				stu.setStuPassword(data.getProperty("Spwd").toString());
				stu.setParPassword(data.getProperty("Ppwd").toString());
				stu.setStuName(data.getProperty("Sname").toString());
				stu.setStuSex(data.getProperty("Ssex").toString());
				stu.setStuSdept(data.getProperty("Ssdept").toString());
				stu.setStuClass(data.getProperty("Sclass").toString());
				stu.setStuState(data.getProperty("Sstate").toString());
				stu.setStuTel(data.getProperty("Stel").toString());
				stu.setParTel(data.getProperty("Ptel").toString());
				stu.setStuPic(data.getProperty("Spic").toString());
				System.out.println("  学号:" + stu.getStuNo() + "   id:" + stu.getStuId() + "   密码:"
						+ stu.getParPassword() + "    学生姓名:" + stu.getStuName() + "    学生性别:" + stu.getStuSex()
						+ "    学院: " + stu.getStuSdept() + "      班级:" + stu.getStuClass() + "        在校状态:"
						+ stu.getStuState() + "     手机号: " + stu.getStuTel() + "    ");
			}
		} catch (SoapFault e) {
			e.printStackTrace();
		}
		if (stu != null)
			localSqlTool.insertStuInfo(stu);
		return stu;
	}

	/**
	 * 获得与sno有关的进度表
	 * 
	 * @param sno
	 * @return
	 */
	public List<TeachProgress> getTeachProssBySno(String sno) {
		// getJDTBbySno
		List<TeachProgress> tlist = new ArrayList<TeachProgress>();

		METHOD_NAME = "getJDTBbySno";
		SOAP_ACTION = NAMESPACE + METHOD_NAME;
		SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
		rpc.addProperty("sno", sno);
		// AndroidHttpTransport ht = new AndroidHttpTransport(URL);
		ht.debug = true;
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.bodyOut = rpc;
		envelope.dotNet = true;
		(new MarshalBase64()).register(envelope);
		envelope.setOutputSoapObject(rpc);
		try {
			ht.call(SOAP_ACTION, envelope);
		} catch (XmlPullParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return tlist;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return tlist;
		}
		try {
			if (envelope.getResponse() != null) {
				Object result = (Object) envelope.getResponse();
				SoapObject data = (SoapObject) result;
				int counts = data.getPropertyCount();
				for (int i = 0; i < counts; i++) {
					SoapObject s = (SoapObject) data.getProperty(i);
					TeachProgress t = new TeachProgress();
					t.setCourseName(s.getProperty("Cname").toString());
					t.setEndTime(s.getProperty("EndTime").toString());
					t.setInMan(s.getProperty("InMan").toString());
					t.setInTime(s.getProperty("InTime").toString());
					t.setIsKQ(s.getProperty("IsKQ").toString());
					t.setProgressAddress(s.getProperty("Jaddress").toString());
					t.setProgressClass(s.getProperty("Jclass").toString());
					t.setProgressNo(Integer.valueOf(s.getProperty("Jno").toString()));
					t.setProgressJTime(s.getProperty("Jtime").toString());
					t.setProgressWeek(s.getProperty("Jweek").toString());
					t.setStartTime(s.getProperty("StartTime").toString());
					t.setTaskNo(Integer.valueOf(s.getProperty("Rno").toString()));
					t.setTeaName(s.getProperty("Tname").toString());
					Log.i("chen",
							"     进度号:" + t.getProgressNo() + "     课程名:" + t.getCourseName() + "    上课节次 :"
									+ t.getProgressJTime() + "    上课周次:" + t.getProgressWeek());
					tlist.add(t);
				}
			}
		} catch (SoapFault e) {
			e.printStackTrace();
			return tlist;
		}
		localSqlTool.insertAllJDTBbyTname(tlist);
		return tlist;
	}

	/**
	 * 
	 * 根据学生编号获得学生的考勤详细信息
	 * 
	 * @param sno
	 * @return
	 */
	public List<KQStuPerson> getKQStuPerson(String sno) {
		// getStuKQTBbyUno
		List<KQStuPerson> listPKQ = new ArrayList<KQStuPerson>();
		METHOD_NAME = "getKQStuPersonBySno";
		SOAP_ACTION = NAMESPACE + METHOD_NAME;
		SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
		rpc.addProperty("sno", sno);
		// AndroidHttpTransport ht = new AndroidHttpTransport(URL);
		ht.debug = true;
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.bodyOut = rpc;
		envelope.dotNet = true;
		(new MarshalBase64()).register(envelope);
		envelope.setOutputSoapObject(rpc);
		try {
			ht.call(SOAP_ACTION, envelope);
		} catch (XmlPullParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return listPKQ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return listPKQ;
		}
		try {
			if (envelope.getResponse() != null) {
				Object result = (Object) envelope.getResponse();
				SoapObject data = (SoapObject) result;
				int counts = data.getPropertyCount();
				for (int i = 0; i < counts; i++) {
					SoapObject s = (SoapObject) data.getProperty(i);
					KQStuPerson stu = new KQStuPerson();
					stu.setDate(s.getProperty("Date").toString());
					stu.setjClass(s.getProperty("JClass").toString());
					stu.setType(s.getProperty("Type").toString());
					stu.setWeek(s.getProperty("Week").toString());
					stu.setWeekDay(s.getProperty("WeekDay").toString());
					listPKQ.add(stu);
				}
			}
		} catch (SoapFault e) {
			e.printStackTrace();
		}
		return listPKQ;
	}

	/**
	 * 获得学生缺勤请假迟到次数已经到课率
	 * 
	 * @param uno
	 * @return
	 */
	public String getKqCount(String sno) {
		// getStuKQTBbyUno
		String result="";
		METHOD_NAME = "getStuPersonKqBySno";
		SOAP_ACTION = NAMESPACE + METHOD_NAME;
		SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
		rpc.addProperty("sno", sno);
		// AndroidHttpTransport ht = new AndroidHttpTransport(URL);
		ht.debug = true;
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.bodyOut = rpc;
		envelope.dotNet = true;
		(new MarshalBase64()).register(envelope);
		envelope.setOutputSoapObject(rpc);
		try {
			ht.call(SOAP_ACTION, envelope);
		} catch (XmlPullParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return result;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return result;
		}
		try {
			if (envelope.getResponse() != null) {
				Object results = (Object) envelope.getResponse();
				result=results.toString();
			}
		} catch (SoapFault e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 根据导员编号获得最新的考勤通知信息
	 * 
	 * @param uno
	 * @return
	 */
	public List<String> getLatestKqInfoByUno(String uno) {
		// getStuKQTBbyUno
		List<String> listPKQ = new ArrayList<String>();
		METHOD_NAME = "getLatestKqInfoByNo";
		SOAP_ACTION = NAMESPACE + METHOD_NAME;
		SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
		rpc.addProperty("keyNo", uno);
		// AndroidHttpTransport ht = new AndroidHttpTransport(URL);
		ht.debug = true;
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.bodyOut = rpc;
		envelope.dotNet = true;
		(new MarshalBase64()).register(envelope);
		envelope.setOutputSoapObject(rpc);
		try {
			ht.call(SOAP_ACTION, envelope);
		} catch (XmlPullParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return listPKQ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return listPKQ;
		}
		try {
			if (envelope.getResponse() != null) {
				Object result = (Object) envelope.getResponse();
				SoapObject data = (SoapObject) result;
				int counts = data.getPropertyCount();
				Log.i("chen", "ncounts=" + counts);
				for (int i = 0; i < counts; i++) {
					listPKQ.add(data.getProperty(i).toString());
				}
			}
		} catch (SoapFault e) {
			e.printStackTrace();
		}
		return listPKQ;
	}
}
