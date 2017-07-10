/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.spring.deployers.as7;

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.ADD;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.DESCRIBE;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP_ADDR;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.SUBSYSTEM;

import java.util.List;
import java.util.Locale;

import javax.xml.stream.XMLStreamException;


//import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.Extension;
import org.jboss.as.controller.ExtensionContext;
import org.jboss.as.controller.OperationContext;
//import org.jboss.as.controller.OperationDefinition;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.OperationStepHandler;
//import org.jboss.as.controller.ResourceBuilder;
//import org.jboss.as.controller.ResourceDefinition;
import org.jboss.as.controller.SubsystemRegistration;
import org.jboss.as.controller.descriptions.DescriptionProvider;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.as.controller.descriptions.common.CommonDescriptions;
import org.jboss.as.controller.parsing.ExtensionParsingContext;
import org.jboss.as.controller.parsing.ParseUtils;
import org.jboss.as.controller.persistence.SubsystemMarshallingContext;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jboss.as.controller.registry.OperationEntry;
import org.jboss.dmr.ModelNode;
import org.jboss.logging.Logger;
import org.jboss.staxmapper.XMLElementReader;
import org.jboss.staxmapper.XMLElementWriter;
import org.jboss.staxmapper.XMLExtendedStreamReader;
import org.jboss.staxmapper.XMLExtendedStreamWriter;

/**
 * @author Marius Bogoevici
 */
public class SpringExtension implements Extension {

    private static final Logger log = Logger.getLogger("org.jboss.snowdrop");

    public static final String SUBSYSTEM_NAME = "spring";

    public static final String NAMESPACE = "urn:jboss:domain:snowdrop:1.0";
    
    private static final int MANAGEMENT_API_MAJOR_VERSION = 1;
    private static final int MANAGEMENT_API_MINOR_VERSION = 2;
    @SuppressWarnings("unused")
    private static final int MANAGEMENT_API_MICRO_VERSION = 0;

    private static SpringSubsystemElementParser parser = new SpringSubsystemElementParser();

    private static final DescriptionProvider SUBSYSTEM_ADD_DESCRIPTION = new DescriptionProvider() {
        @Override
        public ModelNode getModelDescription(Locale locale) {
            return SpringDescriptionProviders.getSubsystemAddDescription(locale);
        }
    };

    /*
     * This is an implementation of the DescriptionProvider interface, which means that when you 
     * call /subsystem=tracker:read-resource-description the information you see comes from 
     * SpringDescriptionProviders.getSubsystemDescription().
     */
    static final DescriptionProvider SUBSYSTEM_DESCRIPTION = new DescriptionProvider() {
        @Override
        public ModelNode getModelDescription(Locale locale) {
            return SpringDescriptionProviders.getSubsystemDescription(locale);
        }
    };

//    private static final ResourceDefinition SUBSYSTEM_ADD_DESCRIPTION = new ResourceDefinition() {
//        @Override
//        public ModelNode getModelDescription(Locale locale) {
//            return SpringDescriptionProviders.getSubsystemAddDescription(locale);
//        }
//    };
//
//    static final ResourceDefinition SUBSYSTEM_DESCRIPTION = new ResourceDefinition() {
//        @Override
//        public ModelNode getModelDescription(Locale locale) {
//            return SpringDescriptionProviders.getSubsystemDescription(locale);
//        }
//    };
    
    private static ModelNode createAddSubSystemOperation() {
        final ModelNode subsystem = new ModelNode();
        subsystem.get(OP).set(ADD);
        subsystem.get(OP_ADDR).add(ModelDescriptionConstants.SUBSYSTEM, SUBSYSTEM_NAME);
        return subsystem;
    }

    /*
     * This initialize() method defines the model, it sets up the basics to add our subsystem to the model.
     * 
     * This is the entry point into the Extension processes. 
     * 
     * (non-Javadoc)
     * @see org.jboss.as.controller.Extension#initialize(org.jboss.as.controller.ExtensionContext)
     */
    public void initialize(ExtensionContext context) {
        log.debug("Activating Spring Extension");
        final SubsystemRegistration subsystem = context.registerSubsystem(SUBSYSTEM_NAME, MANAGEMENT_API_MAJOR_VERSION, MANAGEMENT_API_MINOR_VERSION);
        final ManagementResourceRegistration registration = subsystem.registerSubsystemModel(SUBSYSTEM_DESCRIPTION);
        registration.registerOperationHandler(ADD, SpringSubsystemAdd.INSTANCE, SUBSYSTEM_ADD_DESCRIPTION, false);
        registration.registerOperationHandler(DESCRIBE, SpringSubsystemDescribeHandler.INSTANCE, SpringSubsystemDescribeHandler.INSTANCE, false, OperationEntry.EntryType.PRIVATE);
        subsystem.registerXMLElementWriter(parser);
    }

    @Override
    public void initializeParsers(ExtensionParsingContext context) {
        log.debug("Setting up parsers");
        context.setSubsystemXmlMapping(SUBSYSTEM_NAME, NAMESPACE, parser);
    }

    static class SpringSubsystemElementParser implements XMLElementReader<List<ModelNode>>,
            XMLElementWriter<SubsystemMarshallingContext> {

        /**
         * {@inheritDoc}
         */
        @Override
        public void readElement(XMLExtendedStreamReader reader, List<ModelNode> list) throws XMLStreamException {
            ParseUtils.requireNoAttributes(reader);
            ParseUtils.requireNoContent(reader);
            final ModelNode update = new ModelNode();
            update.get(OP).set(ADD);
            update.get(OP_ADDR).add(SUBSYSTEM, SUBSYSTEM_NAME);
            list.add(createAddSubSystemOperation());
        }

        private static ModelNode createAddSubSystemOperation() {
            final ModelNode subsystem = new ModelNode();
            subsystem.get(OP).set(ADD);
            subsystem.get(OP_ADDR).add(ModelDescriptionConstants.SUBSYSTEM, SUBSYSTEM_NAME);
            return subsystem;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void writeContent(final XMLExtendedStreamWriter writer, final SubsystemMarshallingContext context) throws
                XMLStreamException {
            //TODO seems to be a problem with empty elements cleaning up the queue in FormattingXMLStreamWriter.runAttrQueue
            context.startSubsystemElement(NAMESPACE, false);
            writer.writeEndElement();

        }
    }

    private static class SpringSubsystemDescribeHandler implements OperationStepHandler, DescriptionProvider {

        static final SpringSubsystemDescribeHandler INSTANCE = new SpringSubsystemDescribeHandler();

        @Override
        public void execute(OperationContext context, ModelNode operation) throws OperationFailedException {
            context.getResult().add(createAddSubSystemOperation());
            context.completeStep();
        }

        @Override
        public ModelNode getModelDescription(Locale locale) {
            return CommonDescriptions.getSubsystemDescribeOperation(locale);
        }
    }
}
