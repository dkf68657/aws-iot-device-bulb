package com.aws.iot.training.device;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotTimeoutException;
import com.aws.iot.training.device.ShadowThing.Document;
import com.aws.iot.training.sampleUtil.Message;

@Controller
@RequestMapping("/device")
public class DeviceSimulatorController {

	@Autowired
	private DeviceService deviceService;	
	@GetMapping("")
    public String index(@RequestParam(name = "name", required = false, defaultValue = "World") String name,
			HttpServletRequest request, Model model) {
        return "device";
    }
	
	@RequestMapping(value = "/getLastStatus", produces=MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    @ResponseBody
    public Document getLastStatus(HttpServletRequest request) {
		Document document = null;
		try {
			document = deviceService.getDeviceLastStatus();
		} catch (InterruptedException | AWSIotException | IOException | AWSIotTimeoutException e) {
			System.out.println("Not able to capture device last status.");
			e.printStackTrace();
		}
        return document;
    }
	
	@RequestMapping(value = "/sessionid", produces=MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    @ResponseBody
    public Message sessionid(HttpServletRequest request) {
        return new Message(request.getSession().getId());
    }
			
}
