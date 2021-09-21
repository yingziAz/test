package com.mj.core;

import java.io.Serializable;

/**
 * 因jfinal框架renderJson无法正常转换枚举对象,故使用一般class模拟枚举对象
 * 
 * @author frank.zong
 * 		
 */
@SuppressWarnings("serial")
public class CoreEnum implements Serializable{
	
	public CoreEnum() {
	}
	
	public CoreEnum(Integer ordinal, String name, Object value) {
		this.ordinal = ordinal;
		this.name = name;
		this.value = value;
	}
	
	private Integer ordinal;
	
	private String name;
	
	private Object value;
	
	public Integer getOrdinal() {
		return ordinal;
	}
	
	public void setOrdinal(Integer ordinal) {
		this.ordinal = ordinal;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Object getValue() {
		return value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CoreEnum [ordinal=" + ordinal + ", name=" + name + ", value=" + value + "]";
	}
}
