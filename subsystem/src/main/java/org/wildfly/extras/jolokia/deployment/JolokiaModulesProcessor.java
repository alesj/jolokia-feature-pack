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

package org.wildfly.extras.jolokia.deployment;

import org.jboss.as.server.deployment.Attachments;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.server.deployment.module.ModuleDependency;
import org.jboss.as.server.deployment.module.ModuleSpecification;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleLoader;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class JolokiaModulesProcessor extends JolokiaDeploymentUnitProcessor {
    private static final ModuleIdentifier JOLOKIA_CORE = ModuleIdentifier.create("org.jolokia.core");
    private static final ModuleIdentifier JOLOKIA_JSR160 = ModuleIdentifier.create("org.jolokia.jsr160");
    private static final ModuleIdentifier JSON_SIMPLE = ModuleIdentifier.create("org.json.simple");

    public JolokiaModulesProcessor(String warName) {
        super(warName);
    }

    protected void deploy(DeploymentUnit unit) throws DeploymentUnitProcessingException {
        final ModuleLoader loader = Module.getBootModuleLoader();
        final ModuleSpecification moduleSpecification = unit.getAttachment(Attachments.MODULE_SPECIFICATION);

        moduleSpecification.addSystemDependency(createModuleDependency(loader, JOLOKIA_CORE));
        moduleSpecification.addSystemDependency(createModuleDependency(loader, JOLOKIA_JSR160));
        moduleSpecification.addSystemDependency(createModuleDependency(loader, JSON_SIMPLE));
    }

    private static ModuleDependency createModuleDependency(ModuleLoader loader, ModuleIdentifier moduleIdentifier) {
        return new ModuleDependency(loader, moduleIdentifier, false, false, true, false);
    }
}
