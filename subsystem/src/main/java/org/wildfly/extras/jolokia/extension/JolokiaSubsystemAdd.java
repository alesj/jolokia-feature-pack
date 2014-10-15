/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014, Red Hat, Inc., and individual contributors
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

package org.wildfly.extras.jolokia.extension;

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.ADD;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.ARCHIVE;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.BYTES;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.CONTENT;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.DEPLOYMENT;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.ENABLED;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.PATH;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.PERSISTENT;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.RUNTIME_NAME;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.jboss.as.controller.AbstractBoottimeAddStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.OperationStepHandler;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.ProcessType;
import org.jboss.as.controller.operations.common.Util;
import org.jboss.as.controller.registry.ImmutableManagementResourceRegistration;
import org.jboss.as.controller.registry.Resource;
import org.jboss.as.server.AbstractDeploymentChainStep;
import org.jboss.as.server.DeploymentProcessorTarget;
import org.jboss.as.server.deployment.Phase;
import org.jboss.dmr.ModelNode;
import org.wildfly.extras.jolokia.deployment.JolokiaModulesProcessor;
import org.wildfly.extras.jolokia.deployment.JolokiaServletProcessor;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class JolokiaSubsystemAdd extends AbstractBoottimeAddStepHandler {
    static final JolokiaSubsystemAdd INSTANCE = new JolokiaSubsystemAdd();

    private String warName;

    private JolokiaSubsystemAdd() {
    }

    private synchronized String getWarName() {
        if (warName == null) {
            warName = "jolokia_" + (Math.abs(new Random().nextInt())) + ".war";
        }
        return warName;
    }

    @Override
    protected void populateModel(OperationContext context, ModelNode operation, Resource resource) throws OperationFailedException {
        super.populateModel(context, operation, resource);

        if (context.getProcessType() == ProcessType.STANDALONE_SERVER) {
            if (requiresRuntime(context)) {  // only add the step if we are going to actually deploy the war
                PathAddress deploymentAddress = PathAddress.pathAddress(PathElement.pathElement(DEPLOYMENT, getWarName()));
                ModelNode op = Util.createOperation(ADD, deploymentAddress);
                op.get(ENABLED).set(true);
                op.get(PERSISTENT).set(false);
                op.get(RUNTIME_NAME).set(getWarName());
                op.get(CONTENT).add(WarUtils.getDummyWar());

                ImmutableManagementResourceRegistration rootResourceRegistration = context.getRootResourceRegistration();
                OperationStepHandler handler = rootResourceRegistration.getOperationHandler(deploymentAddress, ADD);

                context.addStep(op, handler, OperationContext.Stage.MODEL);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performBoottime(final OperationContext context, ModelNode model, Resource resource) throws OperationFailedException {
        context.addStep(new AbstractDeploymentChainStep() {
            public void execute(DeploymentProcessorTarget processorTarget) {
                processorTarget.addDeploymentProcessor(JolokiaExtension.SUBSYSTEM_NAME, Phase.PARSE, Phase.PARSE_WEB_COMPONENTS - 1, new JolokiaServletProcessor(getWarName()));
                processorTarget.addDeploymentProcessor(JolokiaExtension.SUBSYSTEM_NAME, Phase.DEPENDENCIES, Phase.DEPENDENCIES_JPA - 5, new JolokiaModulesProcessor(getWarName()));
            }
        }, OperationContext.Stage.RUNTIME);

    }
}