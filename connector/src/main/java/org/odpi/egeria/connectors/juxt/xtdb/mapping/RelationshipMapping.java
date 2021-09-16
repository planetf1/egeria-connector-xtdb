/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.egeria.connectors.juxt.xtdb.mapping;

import clojure.lang.IPersistentVector;
import clojure.lang.PersistentVector;
import xtdb.api.XtdbDocument;
import xtdb.api.IXtdbDatasource;
import org.odpi.egeria.connectors.juxt.xtdb.auditlog.XtdbOMRSAuditCode;
import org.odpi.egeria.connectors.juxt.xtdb.repositoryconnector.XtdbOMRSRepositoryConnector;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.instances.*;

/**
 * Maps the properties of Relationships between persistence and objects.
 */
public class RelationshipMapping extends InstanceHeaderMapping {

    public static final String INSTANCE_REF_PREFIX = "r";

    public static final String RELATIONSHIP_PROPERTIES_NS = "relationshipProperties";
    private static final String N_ENTITY_PROXIES = "entityProxies";

    public static final String ENTITY_PROXIES = getKeyword(N_ENTITY_PROXIES);

    private IXtdbDatasource db;

    /**
     * Construct a mapping from a Relationship (to map to a XTDB representation).
     * @param xtdbConnector connectivity to XTDB
     * @param relationship from which to map
     */
    public RelationshipMapping(XtdbOMRSRepositoryConnector xtdbConnector,
                               Relationship relationship) {
        super(xtdbConnector, relationship);
    }

    /**
     * Construct a mapping from a XTDB map (to map to an Egeria representation).
     * @param xtdbConnector connectivity to XTDB
     * @param xtdbDoc from which to map
     * @param db an open database connection for a point-in-time appropriate to the mapping
     */
    public RelationshipMapping(XtdbOMRSRepositoryConnector xtdbConnector,
                               XtdbDocument xtdbDoc,
                               IXtdbDatasource db) {
        super(xtdbConnector, xtdbDoc);
        this.db = db;
    }

    /**
     * Map from XTDB to Egeria.
     * @return EntityDetail
     * @see #RelationshipMapping(XtdbOMRSRepositoryConnector, XtdbDocument, IXtdbDatasource)
     */
    public Relationship toEgeria() {
        if (instanceHeader == null && xtdbDoc != null) {
            instanceHeader = new Relationship();
            fromDoc();
        }
        if (instanceHeader != null) {
            return (Relationship) instanceHeader;
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected XtdbDocument.Builder toDoc() {
        XtdbDocument.Builder builder = super.toDoc();
        Relationship relationship = (Relationship) instanceHeader;
        EntityProxy one = relationship.getEntityOneProxy();
        EntityProxy two = relationship.getEntityTwoProxy();
        builder.put(ENTITY_PROXIES, PersistentVector.create(EntityProxyMapping.getReference(one.getGUID()), EntityProxyMapping.getReference(two.getGUID())));
        InstancePropertiesMapping.addToDoc(xtdbConnector, builder, relationship.getType(), relationship.getProperties(), RELATIONSHIP_PROPERTIES_NS);
        return builder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fromDoc() {
        super.fromDoc();
        Object proxies = xtdbDoc.get(ENTITY_PROXIES);
        if (proxies instanceof IPersistentVector) {
            IPersistentVector v = (IPersistentVector) proxies;
            if (v.length() == 2) {
                String oneRef = (String) v.nth(0);
                String twoRef = (String) v.nth(1);
                EntityProxy one = getEntityProxyFromRef(oneRef);
                EntityProxy two = getEntityProxyFromRef(twoRef);
                if (one != null && two != null) {
                    ((Relationship) instanceHeader).setEntityOneProxy(one);
                    ((Relationship) instanceHeader).setEntityTwoProxy(two);
                } else {
                    xtdbConnector.logProblem(this.getClass().getName(),
                            "fromDoc",
                            XtdbOMRSAuditCode.FAILED_RETRIEVAL,
                            null,
                            "relationship",
                            instanceHeader.getGUID(),
                            "one or both of the entity proxies were not found -- 1:" + oneRef + ", 2:" + twoRef);
                    instanceHeader = null;
                    return;
                }
            }
        }
        InstanceProperties ip = InstancePropertiesMapping.getFromDoc(xtdbConnector, instanceHeader.getType(), xtdbDoc, RELATIONSHIP_PROPERTIES_NS);
        ((Relationship) instanceHeader).setProperties(ip);
    }

    /**
     * Retrieve the entity proxy details from the provided reference.
     * @param ref to the entity proxy
     * @return EntityProxy
     */
    private EntityProxy getEntityProxyFromRef(String ref) {
        XtdbDocument epDoc = xtdbConnector.getXtdbObjectByReference(db, ref);
        return EntityProxyMapping.getFromDoc(xtdbConnector, epDoc);
    }

    /**
     * Retrieve the canonical reference to the relationship with the specified GUID.
     * @param guid of the relationship to reference
     * @return String giving the XTDB reference to this relationship document
     */
    public static String getReference(String guid) {
        return getReference(INSTANCE_REF_PREFIX, guid);
    }

}