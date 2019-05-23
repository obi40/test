package com.optimiza.ehope.web.bill.billMasterItem.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.optimiza.core.common.helper.FilterablePageRequest;
import com.optimiza.ehope.lis.helper.BillMasterItemType;
import com.optimiza.ehope.lis.model.BillMasterItem;
import com.optimiza.ehope.lis.service.BillMasterItemService;

@RestController
@RequestMapping("/services")
public class BillMasterItemController {

	@Autowired
	private BillMasterItemService billMasterItemService;

	@RequestMapping(value = "/getBillMasterItemList.srvc", method = RequestMethod.POST)
	public ResponseEntity<Page<BillMasterItem>> getBillMasterItemList(@RequestBody FilterablePageRequest filterablePageRequest) {
		Page<BillMasterItem> all = billMasterItemService.getBillMasterItemList(filterablePageRequest);
		return new ResponseEntity<Page<BillMasterItem>>(all, HttpStatus.OK);
	}

	@RequestMapping(value = "/getMasterItemPriceListByTest.srvc", method = RequestMethod.POST)
	public ResponseEntity<Page<BillMasterItem>> getMasterItemPriceListByTest(@RequestBody FilterablePageRequest filterablePageRequest) {
		return new ResponseEntity<Page<BillMasterItem>>(billMasterItemService.getMasterItemPriceListByTest(filterablePageRequest),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/addBillMasterItem.srvc", method = RequestMethod.POST)
	public ResponseEntity<BillMasterItem> addBillMasterItem(@RequestBody BillMasterItem billMasterItem) {
		BillMasterItem saved = billMasterItemService.addBillMasterItem(billMasterItem);
		return new ResponseEntity<BillMasterItem>(saved, HttpStatus.OK);
	}

	@RequestMapping(value = "/editBillMasterItem.srvc", method = RequestMethod.POST)
	public ResponseEntity<BillMasterItem> editBillMasterItem(@RequestBody BillMasterItem billMasterItem) {
		if (!billMasterItem.getType().getCode().equals(BillMasterItemType.TEST.toString())) {
			billMasterItem.getBillTestItemList().clear();
			billMasterItemService.saveBillTestItems(billMasterItem);
		}
		BillMasterItem saved = billMasterItemService.editBillMasterItem(billMasterItem);
		return new ResponseEntity<BillMasterItem>(saved, HttpStatus.OK);
	}

	@RequestMapping(value = "/activateBillMasterItem.srvc", method = RequestMethod.POST)
	public ResponseEntity<BillMasterItem> activateBillMasterItem(@RequestBody Long rid) {
		BillMasterItem saved = billMasterItemService.activateBillMasterItem(rid);
		return new ResponseEntity<BillMasterItem>(saved, HttpStatus.OK);
	}

	@RequestMapping(value = "/deactivateBillMasterItem.srvc", method = RequestMethod.POST)
	public ResponseEntity<BillMasterItem> deactivateBillMasterItem(@RequestBody Long rid) {
		BillMasterItem saved = billMasterItemService.deactivateBillMasterItem(rid);
		return new ResponseEntity<BillMasterItem>(saved, HttpStatus.OK);
	}

	@RequestMapping(value = "/saveBillTestItems.srvc", method = RequestMethod.POST)
	public ResponseEntity<String> saveBillTestItems(@RequestBody BillMasterItem billMasterItem) {
		billMasterItemService.saveBillTestItems(billMasterItem);
		return new ResponseEntity<String>("success", HttpStatus.OK);
	}

	@RequestMapping(value = "/filterBillMasterItemList.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<BillMasterItem>> filterBillMasterItemList(@RequestBody String searchValue) {
		return new ResponseEntity<List<BillMasterItem>>(billMasterItemService.filterBillMasterItemList(searchValue), HttpStatus.OK);
	}

}
