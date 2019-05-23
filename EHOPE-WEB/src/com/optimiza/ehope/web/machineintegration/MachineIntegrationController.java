package com.optimiza.ehope.web.machineintegration;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.optimiza.ehope.lis.service.MachineIntergrationService;
import com.optimiza.ehope.lis.wrapper.MachineResult;

@RestController
@RequestMapping("/services")
public class MachineIntegrationController {

	@Autowired
	private MachineIntergrationService machineIntegrationService;

	//	@Autowired
	//	private SimpMessagingTemplate template;

	@RequestMapping(value = "/submitActualTestResults.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<MachineResult>> submitActualTestResults(@RequestBody List<MachineResult> machineResults) {
		List<MachineResult> processedMachineResults = machineIntegrationService.submitActualTestResults(machineResults);
		//		template.convertAndSend("/topic/messages", new OutputMessage("newResultsReceived", machineResults.get(0).getBarcode()));
		return new ResponseEntity<List<MachineResult>>(processedMachineResults, HttpStatus.OK);
	}

}
