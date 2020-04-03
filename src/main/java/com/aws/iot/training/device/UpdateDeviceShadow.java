package com.aws.iot.training.device;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.services.iot.client.AWSIotDevice;
import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotTimeoutException;
import com.aws.iot.training.device.ShadowThing.Document;
import com.aws.iot.training.sampleUtil.InitIoTClient;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class UpdateDeviceShadow {

	@Autowired
	private InitIoTClient initIoTClient;
	
	@Value("${aws.iot.thingName}")
	private String thingName;
	
	private static AWSIotMqttClient awsIotClient;

    public void setClient(AWSIotMqttClient client) {
        awsIotClient = client;
    }


    private ShadowThing getDeviceState(AWSIotDevice device) throws JsonParseException, JsonMappingException, IOException {
    	ShadowThing thing = null;
    	ObjectMapper objectMapper = new ObjectMapper();
    	try {
    		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            String shadowState = device.get();
            thing = objectMapper.readValue(shadowState, ShadowThing.class);
        } catch (AWSIotException e) {
            System.out.println(System.currentTimeMillis() + ": get failed to get device state ");
        }
    	
    	return thing;
    }
    
    public void updateDeviceDesiredState(BulbType bulbType, boolean status) throws IOException, AWSIotException, AWSIotTimeoutException, InterruptedException {
        
        String payload = null;
        try {
        	 awsIotClient = initIoTClient.initClient();
             AWSIotDevice device = new AWSIotDevice(thingName);
             awsIotClient.attach(device);
             awsIotClient.connect();
            ShadowThing thing = getDeviceState(device);
            switch (bulbType) {
    		case RED:
    			thing.state.desired.redBulb = String.valueOf(status);
    			break;
    		case GREEN:
    			thing.state.desired.greenBulb = String.valueOf(status);
    			break;
    		case BLUE:
    			thing.state.desired.blueBulb = String.valueOf(status);
    			break;
    		default:
    			break;
    		}
              payload = getObjectToPayload(thing);
              device.update(payload);
        } catch (AWSIotException e) {
            System.out.println(System.currentTimeMillis() + ": update failed for " + payload);
        }
        finally {
        	awsIotClient.disconnect();
        }
        
    }
    
    
    public void updateDeviceReportedState(BulbType bulbType, boolean status) throws IOException, AWSIotException, AWSIotTimeoutException, InterruptedException {
       
    	 String payload = null;
         try {
         	  awsIotClient = initIoTClient.initClient();
              AWSIotDevice device = new AWSIotDevice(thingName);
              awsIotClient.attach(device);
              awsIotClient.connect();
             ShadowThing thing = getDeviceState(device);
             switch (bulbType) {
             case RED:
     			thing.state.reported.redBulb = String.valueOf(status);
     			break;
     		case GREEN:
     			thing.state.reported.greenBulb = String.valueOf(status);
     			break;
     		case BLUE:
     			thing.state.reported.blueBulb = String.valueOf(status);
     			break;
     		default:
     			break;
     		}
     		
               payload = getObjectToPayload(thing);
               device.update(payload);
         } catch (AWSIotException e) {
             System.out.println(System.currentTimeMillis() + ": update failed for " + payload);
         }
         finally {
         	awsIotClient.disconnect();
         }
    	
    }
    
    private String getObjectToPayload(ShadowThing thing) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String jsonState = objectMapper.writeValueAsString(thing);
        System.out.println("UpdateDeviceShadow.getObjectToPayload() .. \n"+jsonState);
		return jsonState;
	}
    
    public Document getDeviceLastStatus() throws IOException, AWSIotException, AWSIotTimeoutException,
    InterruptedException {
    	System.out.println("UpdateDeviceShadow.updateDeviceState()");
    	awsIotClient = initIoTClient.initClient();
        AWSIotDevice device = new AWSIotDevice(thingName);

        awsIotClient.attach(device);
        awsIotClient.connect();

        ShadowThing existingThing = getDeviceState(device);
        awsIotClient.disconnect();
        return existingThing.state.desired;
    }
    
}
