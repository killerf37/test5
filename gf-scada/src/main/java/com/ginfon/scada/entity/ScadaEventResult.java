package com.ginfon.scada.entity;

import com.ginfon.core.annotation.Excel;
/**
 * 	将数据库查询出来的{@link ScadaEvent}转换成人能看得懂的东西，保存在该类中。
 * @author Mark
 *
 */
public class ScadaEventResult {
	
	/**
	 * 	线体号，对应JB/CB这些名称而不是数字号码
	 */
	@Excel(name = "线号")
	private String deviceName;
	
	/**
	 * 	皮带号，对应某个线体下的指定皮带号码。
	 */
	@Excel(name = "编号")
	private int beltNo;
	
	/**
	 * 	状态描述文本。
	 */
	@Excel(name = "状态")
	private String status;
	
	/**
	 * 	触发状态，触发还是解除。
	 */
	@Excel(name = "触发解除")
	private String triggerStatus;
	
	/**
	 * 	触发时间。
	 */
	@Excel(name = "触发时间")
	private String triggerTime;
	
	
	public ScadaEventResult() {}


	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public int getBeltNo() {
		return beltNo;
	}

	public void setBeltNo(int beltNo) {
		this.beltNo = beltNo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTriggerStatus() {
		return triggerStatus;
	}

	public void setTriggerStatus(String triggerStatus) {
		this.triggerStatus = triggerStatus;
	}

	public String getTriggerTime() {
		return triggerTime;
	}

	public void setTriggerTime(String triggerTime) {
		this.triggerTime = triggerTime;
	}
}
