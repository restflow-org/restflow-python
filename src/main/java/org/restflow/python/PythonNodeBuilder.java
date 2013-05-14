package org.restflow.python;

import org.restflow.nodes.ActorNodeBuilder;
import org.restflow.nodes.ActorWorkflowNode;

public class PythonNodeBuilder extends ActorNodeBuilder {
	
	private PythonActorBuilder _pythonActorBuilder = new PythonActorBuilder();

	public PythonNodeBuilder step(String script) {
		_pythonActorBuilder.step(script);
		return this;
	}
	
	public PythonNodeBuilder state(String name) {
		_pythonActorBuilder.state(name);
		return this;
	}
	
	public ActorWorkflowNode build() throws Exception {
		_pythonActorBuilder.context(_context);
		_pythonActorBuilder.types(_types);
		_actor = _pythonActorBuilder.build();
		return super.build();
	}
}
