package com.aws.iot.training.device;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotTimeoutException;
import com.aws.iot.training.device.ShadowThing.Document;

@Service
public class DeviceService {
	
	@Autowired
	private UpdateDeviceShadow updateDeviceShadow;
	
	@Autowired
	private DeviceShadowSubscribe deviceShadowSubscribe;
	
	public void updateDeviceDesiredStatus(BulbType bulbType, boolean status) throws IOException, AWSIotException, AWSIotTimeoutException, InterruptedException {
		updateDeviceShadow.updateDeviceDesiredState(bulbType, status);
	}
	
	/**
	 * This method need to invoke on while Application UP.
	 * There is a another post construct is written for this simulator hence invoking manually
	 * @throws InterruptedException
	 * @throws AWSIotException
	 */
	public void getDeviceStatus() throws InterruptedException, AWSIotException {
		deviceShadowSubscribe.getDesiredStateOfDevice();
	}
	
	
	public Document getDeviceLastStatus() throws InterruptedException, AWSIotException, IOException, AWSIotTimeoutException {
		return updateDeviceShadow.getDeviceLastStatus();
	}
	
	
}
