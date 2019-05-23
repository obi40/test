package com.optimiza.core.common.helper;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.ImplicitForeignKeyNameSource;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;

public class CustomNamingStrategy extends ImplicitNamingStrategyJpaCompliantImpl {

	private static final long serialVersionUID = 1L;

	@Override
	public Identifier determineForeignKeyName(ImplicitForeignKeyNameSource source) {
		return toIdentifier(source.getTableName().getCanonicalName() + "_" + source.getColumnNames().get(0).getCanonicalName() + "_fk",
				source.getBuildingContext());
	}

}
