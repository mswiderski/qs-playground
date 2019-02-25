/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.ruleflow.core.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jbpm.process.core.ParameterDefinition;
import org.jbpm.process.core.Work;
import org.jbpm.process.core.datatype.DataType;
import org.jbpm.process.core.impl.ParameterDefinitionImpl;
import org.jbpm.process.core.impl.WorkImpl;
import org.jbpm.process.core.timer.Timer;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.node.MilestoneNode;
import org.jbpm.workflow.core.node.WorkItemNode;

/**
 *
 */
public class WorkItemNodeFactory extends NodeFactory {

    public WorkItemNodeFactory(RuleFlowNodeContainerFactory nodeContainerFactory, NodeContainer nodeContainer, long id, StringBuilder recorded) {
        super(nodeContainerFactory, nodeContainer, id, recorded);
    }

    protected Node createNode() {
        return new WorkItemNode();
    }

    protected WorkItemNode getWorkItemNode() {
        return (WorkItemNode) getNode();
    }

    public WorkItemNodeFactory name(String name) {
        getNode().setName(name);
        recorded.append(".name(\"" + name + "\")");
        return this;
    }
    
    public WorkItemNodeFactory waitForCompletion(boolean waitForCompletion) {
    	getWorkItemNode().setWaitForCompletion(waitForCompletion);
    	recorded.append(".waitForCompletion(" + waitForCompletion + ")");
    	return this;
    }
    
    public WorkItemNodeFactory inMapping(String parameterName, String variableName) {
    	getWorkItemNode().addInMapping(parameterName, variableName);
    	recorded.append(".inMapping(\"" + parameterName + "\")");
    	return this;
    }

    public WorkItemNodeFactory outMapping(String parameterName, String variableName) {
    	getWorkItemNode().addOutMapping(parameterName, variableName);
    	recorded.append(".outMapping(\"" + parameterName + "\", \"" + variableName +"\")");
    	return this;
    }
    
    public WorkItemNodeFactory workName(String name) {
    	Work work = getWorkItemNode().getWork();
    	if (work == null) {
    		work = new WorkImpl();
    		getWorkItemNode().setWork(work);
    	}
    	work.setName(name);
    	recorded.append(".workName(\"" + name + "\")");
    	return this;
    }

    public WorkItemNodeFactory workParameter(String name, Object value) {
    	Work work = getWorkItemNode().getWork();
    	if (work == null) {
    		work = new WorkImpl();
    		getWorkItemNode().setWork(work);
    	}
    	work.setParameter(name, value);
    	return this;
    }
    
    public WorkItemNodeFactory workParameterDefinition(String name, DataType dataType) {
    	Work work = getWorkItemNode().getWork();
    	if (work == null) {
    		work = new WorkImpl();
    		getWorkItemNode().setWork(work);
    	}
    	Set<ParameterDefinition> parameterDefinitions = work.getParameterDefinitions();
    	parameterDefinitions.add(new ParameterDefinitionImpl(name, dataType));
    	work.setParameterDefinitions(parameterDefinitions);
    	
    	recorded.append(".workParameterDefinition(\"" + name + "\", new ObjectDataType(\"" + dataType.getStringType() + "\"))\n");
    	return this;
    }

    public WorkItemNodeFactory onEntryAction(String dialect, String action) {
        if (getWorkItemNode().getActions(dialect) != null) {
        	getWorkItemNode().getActions(dialect).add(new DroolsConsequenceAction(dialect, action));
        } else {
            List<DroolsAction> actions = new ArrayList<DroolsAction>();
            actions.add(new DroolsConsequenceAction(dialect, action));
            getWorkItemNode().setActions(MilestoneNode.EVENT_NODE_ENTER, actions);
        }
        return this;
    }

    public WorkItemNodeFactory onExitAction(String dialect, String action) {
        if (getWorkItemNode().getActions(dialect) != null) {
        	getWorkItemNode().getActions(dialect).add(new DroolsConsequenceAction(dialect, action));
        } else {
            List<DroolsAction> actions = new ArrayList<DroolsAction>();
            actions.add(new DroolsConsequenceAction(dialect, action));
            getWorkItemNode().setActions(MilestoneNode.EVENT_NODE_EXIT, actions);
        }
        return this;
    }

    public WorkItemNodeFactory timer(String delay, String period, String dialect, String action) {
    	Timer timer = new Timer();
    	timer.setDelay(delay);
    	timer.setPeriod(period);
    	getWorkItemNode().addTimer(timer, new DroolsConsequenceAction(dialect, action));
    	return this;
    }
    
}
