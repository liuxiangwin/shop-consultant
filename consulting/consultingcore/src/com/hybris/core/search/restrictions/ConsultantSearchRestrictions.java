/**
 *
 */
package com.hybris.core.search.restrictions;


/**
 * @author Alan Liu
 *
 */
public class ConsultantSearchRestrictions
{
	/*
	 * final String alias = SearchRestriction.RESTRICTION_TYPE_ALIAS; final TypeManager typeman =
	 * getSession().getTypeManager(); final UserGroup catalogViewers =
	 * getSession().getUserManager().getUserGroupByGroupID(Constants.USER.CUSTOMER_USERGROUP); //customergroup
	 * 
	 * 1.1.a catalog viewers may only see categories within visible catalog versions
	 * 
	 * if (typeman.getSearchRestriction(typeman.getComposedType(Category.class), "Frontend_Category") == null) {
	 * typeman.createRestriction("Frontend_Category", catalogViewers, typeman.getComposedType(Category.class), "{" +
	 * alias + ":" + CatalogConstants.Attributes.Category.CATALOGVERSION + "} " + "IN (?session." +
	 * CatalogConstants.SESSION_CATALOG_VERSIONS + ")"); }
	 * 
	 * 1.1.b restricted catalog viewers may only see categories which they are allowed to (and within a active catalog
	 * version - inherited )
	 * 
	 * if (typeman.getSearchRestriction(typeman.getComposedType(Category.class), "Frontend_RestrictedCategory") == null)
	 * { typeman.createRestriction("Frontend_RestrictedCategory", catalogViewers,
	 * typeman.getComposedType(Category.class), "EXISTS ({{" + "SELECT {" + Item.PK + "} " + "FROM {" +
	 * CatalogConstants.Relations.CATEGORY2PRINCIPALRELATION + "} " + "WHERE {" + Link.SOURCE + "}={" + alias + ":" +
	 * Item.PK + "} " + "AND {" + Link.TARGET + "} IN ( ?session.user, ?session.user." + User.ALLGROUPS + " ) " + "}})");
	 * }
	 */
}
