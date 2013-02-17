package com.itap.voiceemoticon.db;

/**
 * 文件操作监听对象
 * 
 * <br>==========================
 * <br> 公司：优视科技-游戏中心
 * <br> 开发：chenzh@ucweb.com
 * <br> 创建时间：2012-6-11上午11:23:48
 * <br>==========================
 */
public interface FileOperationListener<T> {
	public void callback(T obj);
}
