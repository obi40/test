package com.optimiza.ehope.lis.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.optimiza.core.base.service.GenericService;
import com.optimiza.ehope.lis.helper.EhopeRights;
import com.optimiza.ehope.lis.model.EmrVisit;
import com.optimiza.ehope.lis.model.OrderArtifact;
import com.optimiza.ehope.lis.repo.OrderArtifactRepo;

/**
 * OrderArtifactService.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Jan/31/2019
 * 
 */
@Service("OrderArtifactService")
public class OrderArtifactService extends GenericService<OrderArtifact, OrderArtifactRepo> {

	@Autowired
	private OrderArtifactRepo repo;

	@Override
	protected OrderArtifactRepo getRepository() {
		return repo;
	}

	public OrderArtifact saveArtifact(OrderArtifact artifact) {
		return repo.save(artifact);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_ARTIFACT_FINALIZED + "')")
	public List<Object> getByOrderIdFinalized(Long orderId) {
		return getByOrderId(orderId);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_ARTIFACT + "')")
	public List<Object> getByOrderIdNonFinalized(Long orderId) {
		return getByOrderId(orderId);
	}

	public List<Object> getByOrderId(Long orderId) {
		return getRepository().getByOrderId(orderId);
	}

	public void deleteArtifactById(Long rid) {
		repo.delete(rid);
	}

	public void saveArtifacts(MultipartFile[] artifacts, EmrVisit order) throws IOException {
		for (int i = 0; i < artifacts.length; i++) {
			MultipartFile multipartFile = artifacts[i];
			OrderArtifact artifact = new OrderArtifact();
			artifact.setOrder(order);
			artifact.setContent(multipartFile.getBytes());
			artifact.setFileName(multipartFile.getOriginalFilename());
			artifact.setSize(multipartFile.getSize());
			artifact.setExtension(multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf(".")));
			artifact.setContentType(multipartFile.getContentType());
			repo.save(artifact);
		}
	}

	public void deleteArtifacts(List<Long> artifactIdsToDelete) {
		for (Long rid : artifactIdsToDelete) {
			repo.delete(rid);
		}
	}

}
