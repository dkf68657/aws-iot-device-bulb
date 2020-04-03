package com.aws.iot.training.device;

import com.amazonaws.services.iot.client.AWSIotDevice;
import com.amazonaws.services.iot.client.AWSIotDeviceProperty;
import com.aws.iot.training.sampleUtil.Message;
import com.aws.iot.training.websocket.WebSocketIntegration;

public class JavaBulbDevice extends AWSIotDevice{

	private WebSocketIntegration integration;
	
	public JavaBulbDevice(String thingName,WebSocketIntegration integration) {
		super(thingName);
		this.integration = integration;
	}

	@AWSIotDeviceProperty(name="red-bulb")
	private boolean redbulb;
	
	@AWSIotDeviceProperty(name="green-bulb")
	private boolean greenbulb;
	
	@AWSIotDeviceProperty(name="blue-bulb")
	private boolean bluebulb;

	public boolean isRedbulb() {
		return redbulb;
	}

	public void setRedbulb(boolean redbulb) throws Exception {
		integration.sendMessage(new Message("red",String.valueOf(redbulb)), "/topic/bulbstatus");
		this.redbulb = redbulb;
	}

	public boolean isGreenbulb() {
		return greenbulb;
	}

	public void setGreenbulb(boolean greenbulb) throws Exception {
		integration.sendMessage(new Message("green",String.valueOf(greenbulb)), "/topic/bulbstatus");
		this.greenbulb = greenbulb;
	}

	public boolean isBluebulb() {
		return bluebulb;
	}

	public void setBluebulb(boolean bluebulb) throws Exception {
		integration.sendMessage(new Message("blue",String.valueOf(bluebulb)), "/topic/bulbstatus");
		this.bluebulb = bluebulb;
	}
	
}
