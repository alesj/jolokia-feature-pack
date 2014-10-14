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

import org.jboss.as.controller.AbstractBoottimeAddStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.registry.Resource;
import org.jboss.as.server.AbstractDeploymentChainStep;
import org.jboss.as.server.DeploymentProcessorTarget;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceTarget;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class JolokiaSubsystemAdd extends AbstractBoottimeAddStepHandler {
    static final JolokiaSubsystemAdd INSTANCE = new JolokiaSubsystemAdd();

    private JolokiaSubsystemAdd() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void populateModel(ModelNode operation, ModelNode model) throws OperationFailedException {
        JolokiaDefinition.JOLOKIA_PORT.validateAndSet(operation, model);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performBoottime(final OperationContext context, ModelNode model, Resource resource) throws OperationFailedException {
        final ModelNode portModel = JolokiaDefinition.JOLOKIA_PORT.resolveModelAttribute(context, model);
        final int port = portModel.isDefined() ? portModel.asInt() : (System.getenv("JOLOKIA_PORT") != null ? Integer.parseInt(System.getenv("JOLOKIA_PORT")) : 9090);

        context.addStep(new AbstractDeploymentChainStep() {
            public void execute(DeploymentProcessorTarget processorTarget) {
                final ServiceTarget serviceTarget = context.getServiceTarget();
            }
        }, OperationContext.Stage.RUNTIME);

    }
}