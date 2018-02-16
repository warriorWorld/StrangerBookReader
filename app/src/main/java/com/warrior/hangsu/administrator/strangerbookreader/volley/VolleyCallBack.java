package com.warrior.hangsu.administrator.strangerbookreader.volley;

import com.android.volley.VolleyError;

public interface VolleyCallBack<ResultObj> {
	/**
	 * 请求数据成功之后的回调
	 * 
	 * @param <T>
	 * @param request
	 *            返回对象
	 */
	public abstract void loadSucceed(ResultObj result);

	/**
	 * 请求数据失败之后的回调
	 * 
	 * @param error
	 *            错误信息
	 */
	public abstract void loadFailed(VolleyError error);

}
