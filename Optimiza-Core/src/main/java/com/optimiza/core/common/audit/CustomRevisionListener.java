package com.optimiza.core.common.audit;

import org.hibernate.envers.RevisionListener;

public class CustomRevisionListener implements RevisionListener {

	@Override
	public void newRevision(Object revisionEntity) {
		//System.out.println("###########  newRevision");
		//CustomRevisionEntity revision = (CustomRevisionEntity) revisionEntity;
		// SecurityContext context = SecurityContextHolder.getContext();
		// Authentication authentication = context.getAuthentication();
		// UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		//String userName = "UNKNOWN";
		// if (userDetails != null) {
		// userName = userDetails.getUsername();
		// } else {
		// userName = "UNKNOWN";
		// }
		//		revision.setUsername(SecurityUtil.getCurrentUser().getUsername());
		//		revision.setDateOperation(new Date(revision.getTimestamp()));
	}

}