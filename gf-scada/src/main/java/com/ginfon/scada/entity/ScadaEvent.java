package com.ginfon.scada.entity;

public class ScadaEvent {
	// 设备编号
	private Integer lineNo;
	// 触发解除
	private Integer triggerType;
	// 事件类型
	private Integer stateId;
	// 触发时间
	private String triggerTime;
	// 父线体号
	private Integer parentLineNo;

	public Integer getLineNo() {
		return lineNo;
	}

	public void setLineNo(Integer deviceNo) {
		this.lineNo = deviceNo;
	}

	public Integer getTriggerType() {
		return triggerType;
	}

	public void setTriggerType(Integer triggerType) {
		this.triggerType = triggerType;
	}

	public Integer getStateId() {
		return stateId;
	}

	public void setStateId(Integer eventType) {
		this.stateId = eventType;
	}

	public String getTriggerTime() {
		return triggerTime;
	}

	public void setTriggerTime(String triggerTime) {
		this.triggerTime = triggerTime;
	}

	public Integer getParentLineNo() {
		return parentLineNo;
	}

	public void setParentLineNo(Integer parentLineNo) {
		this.parentLineNo = parentLineNo;
	}
}
