/*
 * Copyright 2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.aws.iot.training.device;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTimeoutException;
import com.aws.iot.training.sampleUtil.InitIoTClient;
import com.aws.iot.training.websocket.WebSocketIntegration;

@Component
public class DeviceShadowSubscribe implements DisposableBean {

	@Autowired
	private InitIoTClient initIoTClient;

	@Autowired
	private WebSocketIntegration integration;

	@Value("${aws.iot.thingName}")
	private String thingName;

	private AWSIotMqttClient awsIotClient;

	public void setClient(AWSIotMqttClient client) {
		awsIotClient = client;
	}

	@PostConstruct
	public void getDesiredStateOfDevice() throws InterruptedException, AWSIotException {

		awsIotClient = initIoTClient.initClient();

		awsIotClient.setWillMessage(new AWSIotMessage("client/disconnect", AWSIotQos.QOS0, awsIotClient.getClientId()));

		JavaBulbDevice bulbDevice = new JavaBulbDevice(thingName, integration);

		awsIotClient.attach(bulbDevice);
		try {
			awsIotClient.connect(10000);
		} catch (AWSIotTimeoutException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void destroy() throws Exception {
		if (awsIotClient != null) {
			awsIotClient.disconnect();
		}
	}
}
