/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.egeria.connectors.juxt.crux.repositoryconnector;

import org.odpi.openmetadata.frameworks.connectors.properties.beans.ConnectorType;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.repositoryconnector.OMRSRepositoryConnectorProviderBase;

/**
 * In the Open Connector Framework (OCF), a ConnectorProvider is a factory for a specific type of connector.
 * The CruxOMRSRepositoryConnectorProvider is the connector provider for the CruxOMRSRepositoryConnector.
 * It extends OMRSRepositoryConnectorProviderBase which in turn extends the OCF ConnectorProviderBase.
 * ConnectorProviderBase supports the creation of connector instances.
 * <p>
 * The CruxOMRSRepositoryConnectorProvider must initialize ConnectorProviderBase with the Java class
 * name of the OMRS Connector implementation (by calling super.setConnectorClassName(className)).
 * Then the connector provider will work.
 * <br><br>
 * The permitted configuration options include:
 * <ul>
 *     <li></li>
 * </ul>
 */
public class CruxOMRSRepositoryConnectorProvider extends OMRSRepositoryConnectorProviderBase {

    static final String CONNECTOR_TYPE_GUID = "33d8b72f-b3e4-45ac-a6b3-d1f547dc5320";
    static final String CONNECTOR_TYPE_NAME = "OMRS Crux Repository Connector";
    static final String CONNECTOR_TYPE_DESC = "OMRS Crux Repository Connector that uses Crux as a back-end historical repository for metadata.";

    /**
     * Constructor used to initialize the ConnectorProviderBase with the Java class name of the specific
     * OMRS Connector implementation.
     */
    public CruxOMRSRepositoryConnectorProvider() {

        Class<?> connectorClass = CruxOMRSRepositoryConnector.class;
        super.setConnectorClassName(connectorClass.getName());

        ConnectorType connectorType = new ConnectorType();
        connectorType.setType(ConnectorType.getConnectorTypeType());
        connectorType.setGUID(CONNECTOR_TYPE_GUID);
        connectorType.setQualifiedName(CONNECTOR_TYPE_NAME);
        connectorType.setDisplayName(CONNECTOR_TYPE_NAME);
        connectorType.setDescription(CONNECTOR_TYPE_DESC);
        connectorType.setConnectorProviderClassName(this.getClass().getName());

        // List<String> recognizedConfigurationProperties = new ArrayList<>();
        // connectorType.setRecognizedConfigurationProperties(recognizedConfigurationProperties);

        super.connectorTypeBean = connectorType;

    }

}
