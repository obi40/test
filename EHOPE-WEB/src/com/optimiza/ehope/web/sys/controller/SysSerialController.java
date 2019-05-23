package com.optimiza.ehope.web.sys.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.optimiza.ehope.lis.model.SysSerial;
import com.optimiza.ehope.lis.service.SysSerialService;

@RestController
@RequestMapping("/services")
public class SysSerialController {

	@Autowired
	private SysSerialService serialService;

	@RequestMapping(value = "/getSerialsData.srvc", method = RequestMethod.POST)
	public ResponseEntity<Map<Long, List<SysSerial>>> getSerialsData() {
		return new ResponseEntity<Map<Long, List<SysSerial>>>(serialService.getSerialsData(), HttpStatus.OK);
	}

	@RequestMapping(value = "/createSerials.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<SysSerial>> createSerials(@RequestBody List<SysSerial> serial) {
		return new ResponseEntity<List<SysSerial>>(serialService.createSerial(serial), HttpStatus.OK);
	}

	@RequestMapping(value = "/updateSerials.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<SysSerial>> updateSerials(@RequestBody List<SysSerial> serial) {
		return new ResponseEntity<List<SysSerial>>(serialService.updateSerial(serial), HttpStatus.OK);
	}

}
