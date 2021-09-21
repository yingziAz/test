package com.mj.bean.system;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseBatchJob<M extends BaseBatchJob<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public void setJobName(java.lang.String jobName) {
		set("job_name", jobName);
	}

	public java.lang.String getJobName() {
		return get("job_name");
	}

	public void setJobDesc(java.lang.String jobDesc) {
		set("job_desc", jobDesc);
	}

	public java.lang.String getJobDesc() {
		return get("job_desc");
	}

}
