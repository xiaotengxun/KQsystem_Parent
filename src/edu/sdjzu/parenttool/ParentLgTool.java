package edu.sdjzu.parenttool;

import java.io.IOException;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class ParentLgTool {
	private Context context;
	private WebTool web;

	public ParentLgTool(Context ctx) {
		context = ctx;
		try {
			web = new WebTool(context);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 首次登陆家长所需信息全部下载
	 * 
	 * @param sno
	 */
	public void firstLogin(String sno) {
		web.getTeachTaskBySno(sno);
		web.getStuBySno(sno);
		web.getTeachProssBySno(sno);
	}

	public void secondLogin(String sno) {
		web.getTeachTaskBySno(sno);
		web.getTeachProssBySno(sno);
	}
}
