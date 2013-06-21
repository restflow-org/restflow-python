package org.restflow.python;

import org.restflow.WorkflowContext;
import org.restflow.WorkflowContextBuilder;
import org.restflow.python.PythonActor;
import org.restflow.python.PythonActorBuilder;
import org.restflow.test.RestFlowTestCase;
import org.restflow.util.StdoutRecorder;


public class TestPythonActor extends RestFlowTestCase {

	private WorkflowContext _context;
	
	public void setUp() throws Exception {
		super.setUp();
		_context = new WorkflowContextBuilder()
			.build();
	}
	
	public void testGetAugmentedStepScript_NoInputsOutputsOrState() throws Exception {

		final PythonActor actor = new PythonActorBuilder()
			.context(_context)
			.name("Hello")
			.step("print 'Hello world!'")
			.build();

		actor.elaborate();
		actor.configure();
		actor.initialize();
				
		assertEquals(
			"# AUGMENTED STEP SCRIPT FOR ACTOR Hello" 													+ EOL +
			"" 																							+ EOL +
			"# import packages requried all python actors"	 											+ EOL +
			"import os" 																				+ EOL +
			"" 																							+ EOL +
			"# BEGINNING OF ORIGINAL SCRIPT" 															+ EOL +
			"" 																							+ EOL +
			"print 'Hello world!'" 																		+ EOL +
			"" 																							+ EOL +
			"# END OF ORIGINAL SCRIPT" 																	+ EOL +
			"" 																							+ EOL +
			"# signal end of output from original script" 												+ EOL +
			"print '__END_OF_SCRIPT_OUTPUT__'" 															+ EOL +
			"" 																							+ EOL +
			"# Serialization of actor outputs"															+ EOL +
			"" 																							+ EOL 
			, actor.getAugmentedStepScript());
		
		// run the workflow while capturing stdout and stderr 
		StdoutRecorder recorder = new StdoutRecorder(new StdoutRecorder.WrappedCode() {
			public void execute() throws Exception {actor.step();}});
			
		// confirm expected stdout showing three values printed
		assertEquals(
			"Hello world!" 					+ EOL ,
			recorder.getStdoutRecording());
	}
		
	public void testGetAugmentedStepScript_WithInputs_NoOutputsOrState() throws Exception {

		final PythonActor actor = new PythonActorBuilder()
			.context(_context)
			.name("Hello")
			.input("greeting")
			.step("print greeting, 'world!'")
			.build();

		actor.elaborate();
		actor.configure();
		actor.initialize();
		
		actor.setInputValue("greeting", "Goodbye");
		
		assertEquals(
			"# AUGMENTED STEP SCRIPT FOR ACTOR Hello" 													+ EOL +
			"" 																							+ EOL +
			"# import packages requried all python actors"	 											+ EOL +
			"import os" 																				+ EOL +
			"" 																							+ EOL +
			"# define functions for enabling and disabling actor inputs" 								+ EOL +
			"def enableInput(input)      :   global enabledInputs;    enabledInputs   += ' ' + input" 	+ EOL +
			"def disableInput(input)     :   global disabledInputs;   disabledInputs  += ' ' + input" 	+ EOL +
			"" 																							+ EOL +
			"# initialize input control variables"		 												+ EOL +
			"enabledInputs   = ''" 																		+ EOL +
			"disabledInputs  = ''" 																		+ EOL +
			"" 																							+ EOL +
			"# initialize actor input variables"														+ EOL +
			"greeting='Goodbye'"																		+ EOL +
			""																							+ EOL +
			"# BEGINNING OF ORIGINAL SCRIPT"															+ EOL +
			""																							+ EOL +
			"print greeting, 'world!'"																	+ EOL +
			"" 																							+ EOL +
			"# END OF ORIGINAL SCRIPT" 																	+ EOL +
			"" 																							+ EOL +
			"# signal end of output from original script" 												+ EOL +
			"print '__END_OF_SCRIPT_OUTPUT__'" 															+ EOL +
			"" 																							+ EOL +
			"# Serialization of actor outputs" 															+ EOL +
			"print 'enabledInputs: \"%s\"' % enabledInputs"												+ EOL +
			"print 'disabledInputs: \"%s\"' % disabledInputs" 											+ EOL +
			""																							+ EOL +
			"" 																							+ EOL 
			, actor.getAugmentedStepScript());
		
		// run the workflow while capturing stdout and stderr 
		StdoutRecorder recorder = new StdoutRecorder(new StdoutRecorder.WrappedCode() {
			public void execute() throws Exception {actor.step();}});
			
		// confirm expected stdout showing three values printed
		assertEquals(
			"Goodbye world!" 					+ EOL ,
			recorder.getStdoutRecording());
	}

	public void testGetAugmentedStepScript_WithOutputs_NoInputsOrState() throws Exception {

		final PythonActor actor = new PythonActorBuilder()
			.context(_context)
			.name("Hello")
			.step("greeting='Nice to meet you.'")
			.output("greeting")
			.build();

		actor.elaborate();
		actor.configure();
		actor.initialize();
		
		assertEquals(
			"# AUGMENTED STEP SCRIPT FOR ACTOR Hello" 													+ EOL +
			"" 																							+ EOL +
			"# import packages requried all python actors"	 											+ EOL +
			"import os" 																				+ EOL +
			"" 																							+ EOL +
			"# define functions for enabling and disabling actor outputs" 								+ EOL +
			"def enableOutput(output)    :   global enabledOutputs;   enabledOutputs  += ' ' + output" 	+ EOL +
			"def disableOutput(output)   :   global disabledOutputs;  disabledOutputs += ' ' + output" 	+ EOL +
			"" 																							+ EOL +
			"# initialize output control variables" 													+ EOL +
			"enabledOutputs  = ''" 																		+ EOL +
			"disabledOutputs = ''" 																		+ EOL +
			"" 																							+ EOL +
			"# BEGINNING OF ORIGINAL SCRIPT" 															+ EOL +
			"" 																							+ EOL +
			"greeting='Nice to meet you.'" 																+ EOL +
			"" 																							+ EOL +
			"# END OF ORIGINAL SCRIPT" 																	+ EOL +
			"" 																							+ EOL +
			"# signal end of output from original script" 												+ EOL +
			"print '__END_OF_SCRIPT_OUTPUT__'" 															+ EOL +
			"" 																							+ EOL +
			"# Serialization of actor outputs" 															+ EOL +
			"print 'greeting: ', (\"\\\"%s\\\"\" % greeting, \'~\')[greeting==None]"					+ EOL +
			"" 																							+ EOL +
			"print 'enabledOutputs: \"%s\"' % enabledOutputs" 											+ EOL +
			"print 'disabledOutputs: \"%s\"' % disabledOutputs" 										+ EOL +
			""																							+ EOL +
			"" 																							+ EOL 
			, actor.getAugmentedStepScript());
		
		// run the workflow while capturing stdout and stderr 
		StdoutRecorder recorder = new StdoutRecorder(new StdoutRecorder.WrappedCode() {
			public void execute() throws Exception {actor.step();}});
			
		// confirm expected stdout showing three values printed
		assertEquals("", recorder.getStdoutRecording());
		
		assertEquals("Nice to meet you.", actor.getOutputValue("greeting"));
	}

	public void testGetAugmentedStepScript_WithState_NoInputsOrOutput() throws Exception {

		final PythonActor actor = new PythonActorBuilder()
			.context(_context)
			.name("Hello")
			.state("greeting")
			.step("greeting='Nice to meet you.'")
			.build();

		actor.elaborate();
		actor.configure();
		actor.initialize();
		
		assertEquals(
			"# AUGMENTED STEP SCRIPT FOR ACTOR Hello" 													+ EOL +
			"" 																							+ EOL +
			"# import packages requried all python actors"	 											+ EOL +
			"import os" 																				+ EOL +
			"" 																							+ EOL +
			"# initialize actor state variables" 														+ EOL +
			"greeting=None" 																			+ EOL +
			""							 																+ EOL +
			"# BEGINNING OF ORIGINAL SCRIPT" 															+ EOL +
			""							 																+ EOL +
			"greeting='Nice to meet you.'" 																+ EOL +
			""							 																+ EOL +
			"# END OF ORIGINAL SCRIPT" 																	+ EOL +
			""							 																+ EOL +
			"# signal end of output from original script" 												+ EOL +
			"print '__END_OF_SCRIPT_OUTPUT__'" 															+ EOL +
			""							 																+ EOL +
			"# Serialization of actor outputs" 															+ EOL +
			"print 'greeting: ', (\"\\\"%s\\\"\" % greeting, '~')[greeting==None]"						+ EOL +
			"" 																							+ EOL +
			"" 																							+ EOL 
			, actor.getAugmentedStepScript());
		
		// run the workflow while capturing stdout and stderr 
		StdoutRecorder recorder = new StdoutRecorder(new StdoutRecorder.WrappedCode() {
			public void execute() throws Exception {actor.step();}});
			
		// confirm expected stdout showing three values printed
		assertEquals("", recorder.getStdoutRecording());
		
		assertEquals("Nice to meet you.", actor.getStateValue("greeting"));
	}
	
	public void testGetAugmentedStepScript_WithInputsAndOutput_NoState() throws Exception {

		final PythonActor actor = new PythonActorBuilder()
			.context(_context)
			.name("Multiplier")
			.input("x")
			.input("y")
			.step("z = x * y")
			.output("z")
			.type("x", "Integer")
			.type("y", "Integer")
			.type("z", "Integer")
			.build();

		actor.elaborate();
		actor.configure();
		actor.initialize();
		
		actor.setInputValue("x", 3);
		actor.setInputValue("y", 12);
		
		assertEquals(
			"# AUGMENTED STEP SCRIPT FOR ACTOR Multiplier"												+ EOL +
			""																							+ EOL +
			"# import packages requried all python actors"	 											+ EOL +
			"import os" 																				+ EOL +
			"" 																							+ EOL +
			"# define functions for enabling and disabling actor inputs"								+ EOL +
			"def enableInput(input)      :   global enabledInputs;    enabledInputs   += ' ' + input"	+ EOL +
			"def disableInput(input)     :   global disabledInputs;   disabledInputs  += ' ' + input"	+ EOL +
			""																							+ EOL +
			"# initialize input control variables"														+ EOL +
			"enabledInputs   = ''"																		+ EOL +
			"disabledInputs  = ''"																		+ EOL +
			""																							+ EOL +
			"# define functions for enabling and disabling actor outputs"								+ EOL +
			"def enableOutput(output)    :   global enabledOutputs;   enabledOutputs  += ' ' + output"	+ EOL +
			"def disableOutput(output)   :   global disabledOutputs;  disabledOutputs += ' ' + output"	+ EOL +
			""																							+ EOL +
			"# initialize output control variables"														+ EOL +
			"enabledOutputs  = ''"																		+ EOL +
			"disabledOutputs = ''"																		+ EOL +
			""																							+ EOL +
			"# initialize actor input variables"														+ EOL +
			"y=12"																						+ EOL +
			"x=3"																						+ EOL +
			""																							+ EOL +
			"# BEGINNING OF ORIGINAL SCRIPT"															+ EOL +
			""																							+ EOL +
			"z = x * y"																					+ EOL +
			""																							+ EOL +
			"# END OF ORIGINAL SCRIPT"																	+ EOL +
			""																							+ EOL +
			"# signal end of output from original script"												+ EOL +
			"print '__END_OF_SCRIPT_OUTPUT__'"															+ EOL +
			""																							+ EOL +
			"# Serialization of actor outputs"															+ EOL +
			"print 'z: ', (z, 'null')[z==None]"															+ EOL +
			""																							+ EOL +
			"print 'enabledInputs: \"%s\"' % enabledInputs"												+ EOL +
			"print 'disabledInputs: \"%s\"' % disabledInputs"											+ EOL +
			""																							+ EOL +
			"print 'enabledOutputs: \"%s\"' % enabledOutputs"											+ EOL +
			"print 'disabledOutputs: \"%s\"' % disabledOutputs" 										+ EOL +
			"" 																							+ EOL +
			"" 																							+ EOL
			, actor.getAugmentedStepScript());
		
		// run the workflow while capturing stdout and stderr 
		StdoutRecorder recorder = new StdoutRecorder(new StdoutRecorder.WrappedCode() {
			public void execute() throws Exception {actor.step();}});
			
		// confirm expected stdout showing three values printed
		assertEquals("", recorder.getStdoutRecording());
		
		assertEquals(36, actor.getOutputValue("z"));
	}
}
