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
				
	public Document getDeviceLastStatus() throws InterruptedException, AWSIotException, IOException, AWSIotTimeoutException {
		return updateDeviceShadow.getDeviceLastStatus();
	}
	
}
