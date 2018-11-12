package com.specmate.dummydata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.eclipse.emf.cdo.common.id.CDOWithID;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.logicng.formulas.Formula;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;

import com.specmate.bdd.BDD2CEGTranslator;
import com.specmate.common.SpecmateException;
import com.specmate.model.base.BaseFactory;
import com.specmate.model.base.Folder;
import com.specmate.model.base.IContentElement;
import com.specmate.model.base.IModelConnection;
import com.specmate.model.base.IModelNode;
import com.specmate.model.bdd.BDDConnection;
import com.specmate.model.bdd.BDDModel;
import com.specmate.model.bdd.BDDNoTerminalNode;
import com.specmate.model.bdd.BDDNode;
import com.specmate.model.bdd.BDDTerminalNode;
import com.specmate.model.bdd.BddFactory;
import com.specmate.model.processes.Process;
import com.specmate.model.processes.ProcessConnection;
import com.specmate.model.processes.ProcessDecision;
import com.specmate.model.processes.ProcessEnd;
import com.specmate.model.processes.ProcessNode;
import com.specmate.model.processes.ProcessStart;
import com.specmate.model.processes.ProcessStep;
import com.specmate.model.processes.ProcessesFactory;
import com.specmate.model.requirements.CEGConnection;
import com.specmate.model.requirements.CEGModel;
import com.specmate.model.requirements.CEGNode;
import com.specmate.model.requirements.NodeType;
import com.specmate.model.requirements.Requirement;
import com.specmate.model.requirements.RequirementsFactory;
import com.specmate.model.support.util.SpecmateEcoreUtil;
import com.specmate.persistency.IChange;
import com.specmate.persistency.IPersistencyService;
import com.specmate.persistency.ITransaction;
import com.specmate.search.api.IModelSearchService;

@Component(immediate = true)
public class DummyDataService {
	CDOWithID id;
	private IPersistencyService persistencyService;
	private IModelSearchService searchService;
	private BDD2CEGTranslator translator;

	@Reference
	public void setPersistency(IPersistencyService persistencyService) {
		this.persistencyService = persistencyService;
	}

	@Reference
	public void setSearchService(IModelSearchService searchService) {
		// ensure search service is activated before writing dummy data
		this.searchService = searchService;
	}

	private LogService logService;

	@Reference
	public void setLogService(LogService logService) {
		this.logService = logService;
	}

	public void createTranslator() {
		this.translator = new BDD2CEGTranslator();
	}

	@Activate
	public void activate() throws SpecmateException {
		new Thread(() -> {
			try {
				// Wait a bit, to avoid the problem that the search service is
				// not yet attached
				// to the system wide event bus and therefore the search index
				// does not contain
				// the dummy data.
				Thread.sleep(5000);
				createTranslator();
				fillDummyData();
			} catch (Exception e) {
				logService.log(LogService.LOG_ERROR, "Error while writing dummy data.", e);
			}
		}).start();
	}

	private void fillDummyData() throws SpecmateException {
		ITransaction transaction = this.persistencyService.openTransaction();
		Resource resource = transaction.getResource();
		EObject testProject1 = SpecmateEcoreUtil.getEObjectWithName(DummyProject.TEST_DATA_PROJECT,
				resource.getContents());

		if (testProject1 == null) {
			Folder testFolder = BaseFactory.eINSTANCE.createFolder();
			testFolder.setId(DummyProject.TEST_DATA_PROJECT);
			testFolder.setName(DummyProject.TEST_DATA_PROJECT);

			loadMiniTrainingTestData(testFolder);
			loadGenericTestData(testFolder);
			loadUserStudyTestData(testFolder);

			try {
				transaction.doAndCommit(new IChange<Object>() {
					@Override
					public Object doChange() throws SpecmateException {
						transaction.getResource().getContents().add(testFolder);
						return null;
					}
				});
			} catch (Exception e) {
				logService.log(LogService.LOG_ERROR, e.getMessage());
			}
		}

		// Create another project for manual testing purposes, e.g. to verify
		// authentication behavior
		EObject testProject2 = SpecmateEcoreUtil.getEObjectWithName("another-project", resource.getContents());

		if (testProject2 == null) {
			Folder testFolder = BaseFactory.eINSTANCE.createFolder();
			testFolder.setId("another-project");
			testFolder.setName("another-project");

			loadMiniTrainingTestData(testFolder);
			loadGenericTestData(testFolder);
			loadUserStudyTestData(testFolder);

			try {
				transaction.doAndCommit(new IChange<Object>() {

					@Override
					public Object doChange() throws SpecmateException {
						transaction.getResource().getContents().add(testFolder);
						return null;
					}
				});
			} catch (

			Exception e) {
				logService.log(LogService.LOG_ERROR, e.getMessage());
			}
		}

		transaction.close();
	}

	private void loadGenericTestData(Folder testFolder) {
		Folder libfolder1 = BaseFactory.eINSTANCE.createFolder();
		libfolder1.setId("libfolder1");
		libfolder1.setName("Lib Folder 1");
		Folder libfolder2 = BaseFactory.eINSTANCE.createFolder();
		libfolder2.setId("libfolder2");
		libfolder2.setName("Lib Folder 2");
		Folder libfolder3 = BaseFactory.eINSTANCE.createFolder();
		libfolder3.setId("libfolder3");
		libfolder3.setName("Lib Folder 3");

		Folder folder1 = BaseFactory.eINSTANCE.createFolder();
		folder1.setId("Folder-1");
		folder1.setName("Release 2016");

		Folder folder2 = BaseFactory.eINSTANCE.createFolder();
		folder2.setId("Folder-2");
		folder2.setName("Release 2017");

		Folder folder3 = BaseFactory.eINSTANCE.createFolder();
		folder3.setId("Folder-3");
		folder3.setName("Release JR");

		Requirement requirement1 = RequirementsFactory.eINSTANCE.createRequirement();
		requirement1.setId("Requirement-1");
		requirement1.setExtId("123");
		requirement1.setName("Zuschlag und Summenprüfung");
		requirement1.setDescription(
				"Das System ermöglicht die Suche nach Säumnis bzw. Prämienzuschlag wenn eine Einzelrechnung vorhanden ist, "
						+ "eine Reduktion gebucht wurde, und die Betragsart entweder SZ oder BZ ist. Eine Summenprüfung wird "
						+ "durchgeführt, falls eine Einzelabrechnung vorhanden ist.");
		requirement1.setImplementingBOTeam("Business Analysts");
		requirement1.setImplementingITTeam("The IT Nerds");
		requirement1.setImplementingUnit("Allianz IT and Infrastructure");
		requirement1.setNumberOfTests(4);
		requirement1.setPlannedRelease("Release 10 - Mount Everest");
		requirement1.setStatus("In Progress");
		requirement1.setTac("All tests must pass and the code is reviewed");
		requirement1.setIsRegressionRequirement(true);
		requirement1.setPlatform("ABS");

		Requirement requirement2 = RequirementsFactory.eINSTANCE.createRequirement();
		requirement2.setId("Requirement-2");
		requirement2.setName("My Second Requirement");

		CEGModel model1 = RequirementsFactory.eINSTANCE.createCEGModel();
		model1.setName("Model 1");
		model1.setDescription("This is the first CEG model");
		model1.setId("Model-1");

		CEGNode node1 = RequirementsFactory.eINSTANCE.createCEGNode();
		node1.setId("node-1");
		node1.setName("The first node");
		node1.setDescription("Condition 1 is met");
		node1.setX(1);
		node1.setY(100);
		node1.setVariable("Var1");
		node1.setCondition("is true");

		CEGNode node2 = RequirementsFactory.eINSTANCE.createCEGNode();
		node2.setId("node-2");
		node2.setName("The second node");
		node2.setDescription("Condition 2 is met");
		node2.setX(200);
		node2.setY(100);
		node2.setVariable("Var2");
		node2.setCondition("is greater than 100");

		CEGNode node3 = RequirementsFactory.eINSTANCE.createCEGNode();
		node3.setId("node-3");
		node3.setName("The third node");
		node3.setDescription("Condition 3 is met");
		node3.setX(400);
		node3.setY(100);
		node3.setVariable("Customer");
		node3.setCondition("is present");

		CEGNode node4 = RequirementsFactory.eINSTANCE.createCEGNode();
		node4.setId("node-4");
		node4.setName("The fourth node");
		node4.setDescription("Condition 4 is met");
		node4.setX(300);
		node4.setY(250);
		node4.setVariable("Contract");
		node4.setCondition("is signed");

		CEGConnection connection1 = RequirementsFactory.eINSTANCE.createCEGConnection();
		connection1.setId("conn-1");
		connection1.setName("The first connection");
		connection1.setSource(node1);
		connection1.setTarget(node4);

		CEGConnection connection2 = RequirementsFactory.eINSTANCE.createCEGConnection();
		connection2.setId("conn-2");
		connection2.setName("The second connection");
		connection2.setSource(node2);
		connection2.setTarget(node4);

		CEGConnection connection3 = RequirementsFactory.eINSTANCE.createCEGConnection();
		connection3.setId("conn-3");
		connection3.setName("The third connection");
		connection3.setSource(node3);
		connection3.setTarget(node4);

		model1.getContents().add(node1);
		model1.getContents().add(node2);
		model1.getContents().add(node3);
		model1.getContents().add(node4);
		model1.getContents().add(connection1);
		model1.getContents().add(connection2);
		model1.getContents().add(connection3);

		CEGModel model2 = RequirementsFactory.eINSTANCE.createCEGModel();
		model2.setName("Model 2");
		model2.setDescription("This is the second CEG model");
		model2.setId("Model-2");

		Requirement requirement3 = RequirementsFactory.eINSTANCE.createRequirement();
		requirement3.setId("Requirement-3");
		requirement3.setName("Test Requirement JR");
		requirement3.setDescription(
				"Das System ermöglicht die Suche nach Säumnis bzw. Prämienzuschlag wenn eine Einzelrechnung vorhanden ist, "
						+ "eine Reduktion gebucht wurde, und die Betragsart entweder SZ oder BZ ist. Eine Summenprüfung wird "
						+ "durchgeführt, falls eine Einzelabrechnung vorhanden ist.");
		requirement3.setImplementingBOTeam("Business Analysts");
		requirement3.setImplementingITTeam("The IT Nerds");
		requirement3.setImplementingUnit("Allianz IT and Infrastructure");
		requirement3.setNumberOfTests(4);
		requirement3.setPlannedRelease("Release 10 - Mount Everest");
		requirement3.setStatus("In Progress");
		requirement3.setTac("All tests must pass and the code is reviewed");

		CEGModel lmModel = RequirementsFactory.eINSTANCE.createCEGModel();
		lmModel.setName("Liggesmeyer");
		lmModel.setDescription(
				"Die Operation \"Zähle Zeichen\" liest Zeichen von der Tastatur, solange große Konsonanten oder große Vokale eingegeben werden sowie die Gesamtzahl kleiner ist als der Maximalwert des Datentyps integer.\nIst ein gelesenes Zeichen ein großer Konsonant oder Vokal, so wird die Gesamtzahl um eins erhöht. Falls das eingelesene Zeichen ein großer Vokal ist, so wird auch die Vokalanzahl um eins erhöht.");
		lmModel.setId("LM-1");

		CEGNode lmNode1 = RequirementsFactory.eINSTANCE.createCEGNode();
		lmNode1.setVariable("Großer Konsonant");
		lmNode1.setCondition("eingegeben");
		lmNode1.setId("lmNode1");
		lmNode1.setX(20);
		lmNode1.setY(0);
		lmNode1.setType(NodeType.AND);

		CEGNode lmNode2 = RequirementsFactory.eINSTANCE.createCEGNode();
		lmNode2.setVariable("Großer Vokal");
		lmNode2.setCondition("eingegeben");
		lmNode2.setId("lmNode2");
		lmNode2.setX(20);
		lmNode2.setY(120);
		lmNode2.setType(NodeType.AND);

		CEGNode lmNode3 = RequirementsFactory.eINSTANCE.createCEGNode();
		lmNode3.setVariable("Gesamtzahl");
		lmNode3.setCondition("< max.Integerwert");
		lmNode3.setId("lmNode3");
		lmNode3.setX(20);
		lmNode3.setY(240);
		lmNode3.setType(NodeType.AND);

		CEGNode lmNode4 = RequirementsFactory.eINSTANCE.createCEGNode();
		lmNode4.setVariable("Z1");
		lmNode4.setCondition("is present");
		lmNode4.setId("lmNode4");
		lmNode4.setX(260);
		lmNode4.setY(0);
		lmNode4.setType(NodeType.OR);

		CEGNode lmNode5 = RequirementsFactory.eINSTANCE.createCEGNode();
		lmNode5.setVariable("Operation");
		lmNode5.setCondition("wird beendet");
		lmNode5.setId("lmNode5");
		lmNode5.setX(260);
		lmNode5.setY(300);
		lmNode5.setType(NodeType.OR);

		CEGNode lmNode6 = RequirementsFactory.eINSTANCE.createCEGNode();
		lmNode6.setVariable("Gesamtanzahl");
		lmNode6.setCondition("wird erhöht");
		lmNode6.setId("lmNode6");
		lmNode6.setX(500);
		lmNode6.setY(0);
		lmNode6.setType(NodeType.AND);

		CEGNode lmNode7 = RequirementsFactory.eINSTANCE.createCEGNode();
		lmNode7.setVariable("Vokalanzahl");
		lmNode7.setCondition("wird erhöht");
		lmNode7.setId("lmNode7");
		lmNode7.setX(500);
		lmNode7.setY(120);
		lmNode7.setType(NodeType.AND);

		CEGNode lmNode8 = RequirementsFactory.eINSTANCE.createCEGNode();
		lmNode8.setVariable("Zeichen");
		lmNode8.setCondition("wird gelesen");
		lmNode8.setId("lmNode8");
		lmNode8.setX(500);
		lmNode8.setY(240);
		lmNode8.setType(NodeType.AND);

		CEGConnection lmConn1 = RequirementsFactory.eINSTANCE.createCEGConnection();
		lmConn1.setId("lmConn1");
		lmConn1.setName("-");
		lmConn1.setSource(lmNode1);
		lmConn1.setTarget(lmNode4);

		CEGConnection lmConn2 = RequirementsFactory.eINSTANCE.createCEGConnection();
		lmConn2.setId("lmConn2");
		lmConn2.setName("-");
		lmConn2.setSource(lmNode2);
		lmConn2.setTarget(lmNode4);

		CEGConnection lmConn3 = RequirementsFactory.eINSTANCE.createCEGConnection();
		lmConn3.setId("lmConn3");
		lmConn3.setName("-");
		lmConn3.setSource(lmNode2);
		lmConn3.setTarget(lmNode7);

		CEGConnection lmConn4 = RequirementsFactory.eINSTANCE.createCEGConnection();
		lmConn4.setId("lmConn4");
		lmConn4.setName("-");
		lmConn4.setSource(lmNode3);
		lmConn4.setTarget(lmNode6);

		CEGConnection lmConn5 = RequirementsFactory.eINSTANCE.createCEGConnection();
		lmConn5.setId("lmConn5");
		lmConn5.setName("-");
		lmConn5.setSource(lmNode3);
		lmConn5.setTarget(lmNode7);

		CEGConnection lmConn6 = RequirementsFactory.eINSTANCE.createCEGConnection();
		lmConn6.setId("lmConn6");
		lmConn6.setName("-");
		lmConn6.setSource(lmNode3);
		lmConn6.setTarget(lmNode8);

		CEGConnection lmConn7 = RequirementsFactory.eINSTANCE.createCEGConnection();
		lmConn7.setId("lmConn7");
		lmConn7.setName("-");
		lmConn7.setSource(lmNode3);
		lmConn7.setTarget(lmNode5);
		lmConn7.setNegate(true);

		CEGConnection lmConn8 = RequirementsFactory.eINSTANCE.createCEGConnection();
		lmConn8.setId("lmConn8");
		lmConn8.setName("-");
		lmConn8.setSource(lmNode4);
		lmConn8.setTarget(lmNode6);

		CEGConnection lmConn9 = RequirementsFactory.eINSTANCE.createCEGConnection();
		lmConn9.setId("lmConn9");
		lmConn9.setName("-");
		lmConn9.setSource(lmNode4);
		lmConn9.setTarget(lmNode8);

		CEGConnection lmConn10 = RequirementsFactory.eINSTANCE.createCEGConnection();
		lmConn10.setId("lmConn10");
		lmConn10.setName("-");
		lmConn10.setSource(lmNode4);
		lmConn10.setTarget(lmNode5);
		lmConn10.setNegate(true);

		lmModel.getContents().add(lmNode1);
		lmModel.getContents().add(lmNode2);
		lmModel.getContents().add(lmNode3);
		lmModel.getContents().add(lmNode4);
		lmModel.getContents().add(lmNode5);
		lmModel.getContents().add(lmNode6);
		lmModel.getContents().add(lmNode7);
		lmModel.getContents().add(lmNode8);
		lmModel.getContents().add(lmConn1);
		lmModel.getContents().add(lmConn2);
		lmModel.getContents().add(lmConn3);
		lmModel.getContents().add(lmConn4);
		lmModel.getContents().add(lmConn5);
		lmModel.getContents().add(lmConn6);
		lmModel.getContents().add(lmConn7);
		lmModel.getContents().add(lmConn8);
		lmModel.getContents().add(lmConn9);
		lmModel.getContents().add(lmConn10);

		requirement3.getContents().add(lmModel);

		Requirement requirement4 = RequirementsFactory.eINSTANCE.createRequirement();
		requirement4.setId("Requirement-4");
		requirement4.setName("Data Collection Process");

		Process process1 = ProcessesFactory.eINSTANCE.createProcess();
		process1.setName("Create Customer");
		process1.setId("process-1");
		process1.setDescription("This is the process for creating new customers.");

		ProcessNode process1Start = ProcessesFactory.eINSTANCE.createProcessStart();
		process1Start.setName("Start");
		process1Start.setId("process-1-start");
		process1Start.setX(200);
		process1Start.setY(40);
		ProcessNode processNode1 = ProcessesFactory.eINSTANCE.createProcessStep();
		processNode1.setName("Collect Data");
		processNode1.setId("process-node-1");
		processNode1.getTracesTo().add(requirement2);
		processNode1.setX(200);
		processNode1.setY(100);
		ProcessConnection processConnection0 = ProcessesFactory.eINSTANCE.createProcessConnection();
		processConnection0.setName("Process Connection 0");
		processConnection0.setId("process-connection-0");
		processConnection0.setSource(process1Start);
		processConnection0.setTarget(processNode1);
		ProcessNode processNode2 = ProcessesFactory.eINSTANCE.createProcessStep();
		processNode2.setName("...");
		processNode2.setId("process-node-2");
		processNode2.setX(200);
		processNode2.setY(200);
		ProcessNode processNode3 = ProcessesFactory.eINSTANCE.createProcessStep();
		processNode3.setName("Profit");
		processNode3.setId("process-node-3");
		processNode3.setX(200);
		processNode3.setY(300);
		ProcessNode processDecisionNode1 = ProcessesFactory.eINSTANCE.createProcessDecision();
		processDecisionNode1.setName("Decision 1");
		processDecisionNode1.setId("decision-1");
		processDecisionNode1.setDescription("The first decision");
		processDecisionNode1.setX(400);
		processDecisionNode1.setY(200);
		ProcessConnection processConnection1 = ProcessesFactory.eINSTANCE.createProcessConnection();
		processConnection1.setName("Process Connection 1");
		processConnection1.setId("process-connection-1");
		processConnection1.setSource(processNode1);
		processConnection1.setTarget(processDecisionNode1);
		ProcessConnection processConnection2 = ProcessesFactory.eINSTANCE.createProcessConnection();
		processConnection2.setName("Process Connection 2");
		processConnection2.setId("process-connection-2");
		processConnection2.setCondition("Condition met");
		processConnection2.setSource(processDecisionNode1);
		processConnection2.setTarget(processNode3);
		ProcessNode process1End = ProcessesFactory.eINSTANCE.createProcessEnd();
		process1End.setName("End");
		process1End.setId("process-1-end");
		process1End.setX(200);
		process1End.setY(260);
		ProcessConnection processConnection3 = ProcessesFactory.eINSTANCE.createProcessConnection();
		processConnection3.setName("Process Connection 3");
		processConnection3.setId("process-connection-3");
		processConnection3.setSource(processNode3);
		processConnection3.setTarget(process1End);
		ProcessConnection processConnection4 = ProcessesFactory.eINSTANCE.createProcessConnection();
		processConnection4.setName("Process Connection 4");
		processConnection4.setId("process-connection-4");
		processConnection4.setCondition("Condition not met");
		processConnection4.setSource(processDecisionNode1);
		processConnection4.setTarget(processNode2);
		ProcessConnection processConnection5 = ProcessesFactory.eINSTANCE.createProcessConnection();
		processConnection5.setName("Process Connection 5");
		processConnection5.setId("process-connection-5");
		processConnection5.setSource(processNode2);
		processConnection5.setTarget(process1End);

		process1.getContents().add(process1Start);
		process1.getContents().add(process1End);
		process1.getContents().add(processNode1);
		process1.getContents().add(processNode2);
		process1.getContents().add(processNode3);
		process1.getContents().add(processDecisionNode1);
		process1.getContents().add(processConnection0);
		process1.getContents().add(processConnection1);
		process1.getContents().add(processConnection2);
		process1.getContents().add(processConnection3);
		process1.getContents().add(processConnection4);
		process1.getContents().add(processConnection5);

		requirement4.getContents().add(process1);

		requirement1.getContents().add(model1);
		requirement1.getContents().add(model2);

		folder1.getContents().add(requirement1);
		folder1.getContents().add(requirement2);
		folder1.getContents().add(requirement4);
		folder3.getContents().add(requirement3);
		folder1.getContents().add(requirement4);
		testFolder.getContents().add(folder1);
		testFolder.getContents().add(folder2);
		testFolder.getContents().add(folder3);
		testFolder.getContents().add(libfolder1);
		testFolder.getContents().add(libfolder2);
		testFolder.getContents().add(libfolder3);

		/*
		 * Viewing a BDD in the browser.
		 */
		
		BDDModel bdd_m = hardCodedBDD();
		requirement1.getContents().add(bdd_m);
		
		/**
		 * Testing the translation of CEGs into BDDs.
		 */

		CEGModel translated = translator.translate(cycleBDD());
		requirement1.getContents().add(translated);
	}

	private BDDModel cycleBDD() {
		BDDModel model = BddFactory.eINSTANCE.createBDDModel();

		// node 1
		BDDNoTerminalNode node1 = BddFactory.eINSTANCE.createBDDNoTerminalNode();
		node1.setId("node-1");
		node1.setVariable("A");
		node1.setCondition("is true");

		// node 2
		BDDNoTerminalNode node2 = BddFactory.eINSTANCE.createBDDNoTerminalNode();
		node2.setId("node-2");
		node2.setVariable("B");
		node2.setCondition("is true");

		// node 3
		BDDNoTerminalNode node3 = BddFactory.eINSTANCE.createBDDNoTerminalNode();
		node3.setId("node-3");
		node3.setVariable("C");
		node3.setCondition("is true");

		// node 4
		BDDNoTerminalNode node4 = BddFactory.eINSTANCE.createBDDNoTerminalNode();
		node4.setId("node-4");
		node4.setVariable("D");
		node4.setCondition("is true");

		// terminal 1
		BDDTerminalNode terminal1 = BddFactory.eINSTANCE.createBDDTerminalNode();
		terminal1.setId("term-1");
		terminal1.setValue(false);

		// terminal 2
		BDDTerminalNode terminal2 = BddFactory.eINSTANCE.createBDDTerminalNode();
		terminal2.setId("term-2");
		terminal2.setValue(true);

		// terminal 3
		BDDTerminalNode terminal3 = BddFactory.eINSTANCE.createBDDTerminalNode();
		terminal3.setId("term-3");
		terminal3.setValue(false);

		// connection 1
		BDDConnection connection1 = BddFactory.eINSTANCE.createBDDConnection();
		connection1.setId("conn-1");
		connection1.setSource(node1);
		connection1.setTarget(node2);

		// connection 2
		BDDConnection connection2 = BddFactory.eINSTANCE.createBDDConnection();
		connection2.setId("conn-2");
		connection2.setNegate(true);
		connection2.setSource(node1);
		connection2.setTarget(terminal3);

		// connection 3
		BDDConnection connection3 = BddFactory.eINSTANCE.createBDDConnection();
		connection3.setId("conn-3");	
		connection3.setSource(node2);
		connection3.setTarget(node3);

		// connection 4
		BDDConnection connection4 = BddFactory.eINSTANCE.createBDDConnection();
		connection4.setId("conn-4");
		connection4.setNegate(true);
		connection4.setSource(node2);
		connection4.setTarget(terminal1);

		// connection 5
		BDDConnection connection5 = BddFactory.eINSTANCE.createBDDConnection();
		connection5.setId("conn-5");
		connection5.setSource(node3);
		connection5.setTarget(terminal3);

		// connection 6
		BDDConnection connection6 = BddFactory.eINSTANCE.createBDDConnection();
		connection6.setId("conn-6");
		connection6.setNegate(true);
		connection6.setSource(node3);
		connection6.setTarget(node4);

		// connection 7
		BDDConnection connection7 = BddFactory.eINSTANCE.createBDDConnection();
		connection7.setId("conn-7");
		connection7.setSource(node4);
		connection7.setTarget(terminal2);

		// connection 8
		BDDConnection connection8 = BddFactory.eINSTANCE.createBDDConnection();
		connection8.setId("conn-8");
		connection8.setNegate(true);
		connection8.setSource(node4);
		connection8.setTarget(node2);

		model.getContents().add(node1);
		model.getContents().add(node2);
		model.getContents().add(node3);
		model.getContents().add(node4);
		model.getContents().add(terminal1);
		model.getContents().add(terminal2);
		model.getContents().add(terminal3);
		model.getContents().add(connection1);
		model.getContents().add(connection2);
		model.getContents().add(connection3);
		model.getContents().add(connection4);
		model.getContents().add(connection5);
		model.getContents().add(connection6);
		model.getContents().add(connection7);
		model.getContents().add(connection8);
		
		return model;
	}

	/*
	 * Outputs a big BDDModel.
	 */
	private BDDModel bigBDD() {
		// model settings
		BDDModel model = BddFactory.eINSTANCE.createBDDModel();
		model.setId("Model-bdd-big");

		// random generator
		Random r = new Random();

		// array to store 20 BDD nonterminals
		BDDNoTerminalNode[] nonterminals = new BDDNoTerminalNode[20];

		// create 20 nonterminals, put them into array
		for (int i = 0; i <= 19; i++) {
			BDDNoTerminalNode node = BddFactory.eINSTANCE.createBDDNoTerminalNode();
			node.setId("node-" + i);
			// 10 possible variables: "0" to "9"
			node.setVariable(Integer.toString(r.nextInt(10)));
			node.setCondition("is true");
			nonterminals[i] = node;
			model.getContents().add(node);
			// System.out.println(node.getId() + ": " + node.getVariable() + ",
			// " + node.getCondition());
		}

		// defining one of each terminal
		BDDTerminalNode terminal0 = BddFactory.eINSTANCE.createBDDTerminalNode();
		terminal0.setId("term-0");
		terminal0.setValue(false);
		model.getContents().add(terminal0);
		BDDTerminalNode terminal1 = BddFactory.eINSTANCE.createBDDTerminalNode();
		terminal1.setId("term-1");
		terminal1.setValue(true);
		model.getContents().add(terminal1);

		// the next target nonterminal
		int targetcount = 1;

		// creating two out conns for each node
		for (int i = 0; i <= 19; i++) {
			// connection1 (not negated)
			BDDConnection conn1 = BddFactory.eINSTANCE.createBDDConnection();
			conn1.setId("conn-" + i + "-high");
			conn1.setSource(nonterminals[i]);
			if (targetcount <= 19) {
				// still target nodes left
				conn1.setTarget(nonterminals[targetcount]);
				targetcount++;
			} else {
				// none left => 0 or 1 terminal
				if (r.nextInt(3) == 1) {
					conn1.setTarget(terminal1);
				} else {
					conn1.setTarget(terminal0);
				}
			}
			model.getContents().add(conn1);

			// connection2 (negated)
			BDDConnection conn2 = BddFactory.eINSTANCE.createBDDConnection();
			conn2.setId("conn-" + i + "-low");
			conn2.setSource(nonterminals[i]);
			conn2.setNegate(true);
			if (targetcount <= 19) {
				// still target nodes left
				conn2.setTarget(nonterminals[targetcount]);
				targetcount++;
			} else {
				// none left => 0 or 1terminal
				if (r.nextInt(3) == 1) {
					conn2.setTarget(terminal1);
				} else {
					conn2.setTarget(terminal0);
				}
			}
			model.getContents().add(conn2);
		}

		// print all connections of BDD for testing
		for (IModelConnection conn : (List<IModelConnection>) SpecmateEcoreUtil.pickInstancesOf(model.getContents(),
				IModelConnection.class)) {
			BDDConnection bdd_conn = (BDDConnection) conn;
			if (bdd_conn.isNegate()) {
				System.out.println(bdd_conn.getSource().getId() + " -/> " + bdd_conn.getTarget().getId());
			} else {
				System.out.println(bdd_conn.getSource().getId() + " --> " + bdd_conn.getTarget().getId());
			}
		}
		return model;
	}

	/*
	 * A BDD consisting of a nonterminal with two terminal children.
	 */
	private BDDModel hardSimple() {
		BDDModel simple = BddFactory.eINSTANCE.createBDDModel();
		simple.setId("Model-simple");

		// terminal 1
		BDDTerminalNode terminal1 = BddFactory.eINSTANCE.createBDDTerminalNode();
		terminal1.setId("term-1");
		terminal1.setValue(false);

		// node 1
		BDDNoTerminalNode node1 = BddFactory.eINSTANCE.createBDDNoTerminalNode();
		node1.setId("node-1");
		node1.setVariable("A");
		node1.setCondition("is true");

		// terminal 0
		BDDTerminalNode terminal0 = BddFactory.eINSTANCE.createBDDTerminalNode();
		terminal0.setId("term-0");
		terminal0.setValue(true);

		// connection 1
		BDDConnection connection1 = BddFactory.eINSTANCE.createBDDConnection();
		connection1.setId("conn-1");
		connection1.setSource(node1);
		connection1.setTarget(terminal1);

		// connection 2
		BDDConnection connection2 = BddFactory.eINSTANCE.createBDDConnection();
		connection2.setId("conn-2");
		connection2.setNegate(true);
		connection2.setSource(node1);
		connection2.setTarget(terminal0);

		simple.getContents().add(node1);
		simple.getContents().add(terminal1);
		simple.getContents().add(terminal0);
		simple.getContents().add(connection1);
		simple.getContents().add(connection2);

		return simple;
	}

	/*
	 * Returns a BDD consisting of a 1-terminal only.
	 */
	private BDDModel hardTerminal() {
		BDDModel oans = BddFactory.eINSTANCE.createBDDModel();
		oans.setId("1-Terminal");

		// terminal 1
		BDDTerminalNode terminal1 = BddFactory.eINSTANCE.createBDDTerminalNode();
		terminal1.setId("term-1");
		terminal1.setValue(true);

		oans.getContents().add(terminal1);
		return oans;
	}

	/*
	 * Returns a BDD that models the example requirement from the Specmate
	 * paper.
	 */
	private BDDModel specmateBDD() {
		BDDModel model = BddFactory.eINSTANCE.createBDDModel();

		// node 1
		BDDNoTerminalNode node1 = BddFactory.eINSTANCE.createBDDNoTerminalNode();
		node1.setId("node-1");
		node1.setVariable("age");
		node1.setCondition(">=18");

		// node 2
		BDDNoTerminalNode node2 = BddFactory.eINSTANCE.createBDDNoTerminalNode();
		node2.setId("node-2");
		node2.setVariable("age");
		node2.setCondition(">=17");

		// node 3
		BDDNoTerminalNode node3 = BddFactory.eINSTANCE.createBDDNoTerminalNode();
		node3.setId("node-3");
		node3.setVariable("Consent of par.");
		node3.setCondition("is available");

		// node 4
		BDDNoTerminalNode node4 = BddFactory.eINSTANCE.createBDDNoTerminalNode();
		node4.setId("node-4");
		node4.setVariable("Registration");
		node4.setCondition("is available");

		// node 5
		BDDNoTerminalNode node5 = BddFactory.eINSTANCE.createBDDNoTerminalNode();
		node5.setId("node-5");
		node5.setVariable("Registration");
		node5.setCondition("is available");

		// terminal 1
		BDDTerminalNode terminal1 = BddFactory.eINSTANCE.createBDDTerminalNode();
		terminal1.setId("term-1");
		terminal1.setValue(true);

		// connection 1
		BDDConnection connection1 = BddFactory.eINSTANCE.createBDDConnection();
		connection1.setId("conn-1");
		connection1.setNegate(true);
		connection1.setSource(node1);
		connection1.setTarget(node2);

		// connection 2
		BDDConnection connection2 = BddFactory.eINSTANCE.createBDDConnection();
		connection2.setId("conn-2");
		connection2.setSource(node1);
		connection2.setTarget(node5);

		// connection 3
		BDDConnection connection3 = BddFactory.eINSTANCE.createBDDConnection();
		connection3.setId("conn-3");
		connection3.setSource(node2);
		connection3.setTarget(node3);

		// connection 4
		BDDConnection connection4 = BddFactory.eINSTANCE.createBDDConnection();
		connection4.setId("conn-4");
		connection4.setSource(node3);
		connection4.setTarget(node4);

		// connection 5
		BDDConnection connection5 = BddFactory.eINSTANCE.createBDDConnection();
		connection5.setId("conn-5");
		connection5.setSource(node4);
		connection5.setTarget(terminal1);

		// connection 6
		BDDConnection connection6 = BddFactory.eINSTANCE.createBDDConnection();
		connection6.setId("conn-6");
		connection6.setSource(node5);
		connection6.setTarget(terminal1);

		model.getContents().add(node1);
		model.getContents().add(node2);
		model.getContents().add(node3);
		model.getContents().add(node4);
		model.getContents().add(node5);
		model.getContents().add(terminal1);
		model.getContents().add(connection1);
		model.getContents().add(connection2);
		model.getContents().add(connection3);
		model.getContents().add(connection4);
		model.getContents().add(connection5);
		model.getContents().add(connection6);

		return model;
	}

	private BDDModel wrongBDD() {
		BDDModel model = BddFactory.eINSTANCE.createBDDModel();

		// node 1
		BDDNoTerminalNode node1 = BddFactory.eINSTANCE.createBDDNoTerminalNode();
		node1.setId("node-1");
		node1.setVariable("A");
		node1.setCondition("is true");

		// node 2
		BDDNoTerminalNode node2 = BddFactory.eINSTANCE.createBDDNoTerminalNode();
		node2.setId("node-2");
		node2.setVariable("B");
		node2.setCondition("is true");

		// node 3
		BDDNoTerminalNode node3 = BddFactory.eINSTANCE.createBDDNoTerminalNode();
		node3.setId("node-3");
		node3.setVariable("C");
		node3.setCondition("is true");

		// node 4
		BDDNoTerminalNode node4 = BddFactory.eINSTANCE.createBDDNoTerminalNode();
		node4.setId("node-4");
		node4.setVariable("B");
		node4.setCondition("is true");

		// terminal 1
		BDDTerminalNode terminal1 = BddFactory.eINSTANCE.createBDDTerminalNode();
		terminal1.setId("term-1");
		terminal1.setValue(false);

		// terminal 2
		BDDTerminalNode terminal2 = BddFactory.eINSTANCE.createBDDTerminalNode();
		terminal2.setId("term-2");
		terminal2.setValue(false);

		// terminal 3
		BDDTerminalNode terminal3 = BddFactory.eINSTANCE.createBDDTerminalNode();
		terminal3.setId("term-3");
		terminal3.setValue(false);

		// terminal 4
		BDDTerminalNode terminal4 = BddFactory.eINSTANCE.createBDDTerminalNode();
		terminal4.setId("term-4");
		terminal4.setValue(true);

		// connection 1
		BDDConnection connection1 = BddFactory.eINSTANCE.createBDDConnection();
		connection1.setId("conn-1");
		connection1.setNegate(true);
		connection1.setSource(node1);
		connection1.setTarget(node2);

		// connection 2
		BDDConnection connection2 = BddFactory.eINSTANCE.createBDDConnection();
		connection2.setId("conn-2");
		connection2.setSource(node1);
		connection2.setTarget(terminal1);

		// connection 3
		BDDConnection connection3 = BddFactory.eINSTANCE.createBDDConnection();
		connection3.setId("conn-3");
		connection3.setSource(node2);
		connection3.setTarget(node3);

		// connection 4
		BDDConnection connection4 = BddFactory.eINSTANCE.createBDDConnection();
		connection4.setId("conn-4");
		connection4.setNegate(true);
		connection4.setSource(node2);
		connection4.setTarget(terminal1);

		// connection 5
		BDDConnection connection5 = BddFactory.eINSTANCE.createBDDConnection();
		connection5.setId("conn-5");
		connection5.setSource(node3);
		connection5.setTarget(terminal2);

		// connection 6
		BDDConnection connection6 = BddFactory.eINSTANCE.createBDDConnection();
		connection6.setId("conn-6");
		connection6.setNegate(true);
		connection6.setSource(node3);
		connection6.setTarget(node4);

		// connection 7
		BDDConnection connection7 = BddFactory.eINSTANCE.createBDDConnection();
		connection7.setId("conn-7");
		connection7.setSource(node4);
		connection7.setTarget(terminal3);

		// connection 8
		BDDConnection connection8 = BddFactory.eINSTANCE.createBDDConnection();
		connection8.setId("conn-8");
		connection8.setNegate(true);
		connection8.setSource(node4);
		connection8.setTarget(terminal4);

		model.getContents().add(node1);
		model.getContents().add(node2);
		model.getContents().add(node3);
		model.getContents().add(node4);
		model.getContents().add(terminal1);
		model.getContents().add(terminal2);
		model.getContents().add(terminal3);
		model.getContents().add(terminal4);
		model.getContents().add(connection1);
		model.getContents().add(connection2);
		model.getContents().add(connection3);
		model.getContents().add(connection4);
		model.getContents().add(connection5);
		model.getContents().add(connection6);
		model.getContents().add(connection7);
		model.getContents().add(connection8);

		return model;
	}

	private BDDModel okBDD() {
		BDDModel model = BddFactory.eINSTANCE.createBDDModel();

		// node 1
		BDDNoTerminalNode node1 = BddFactory.eINSTANCE.createBDDNoTerminalNode();
		node1.setId("node-1");
		node1.setVariable("A");
		node1.setCondition("is true");

		// node 2
		BDDNoTerminalNode node2 = BddFactory.eINSTANCE.createBDDNoTerminalNode();
		node2.setId("node-2");
		node2.setVariable("B");
		node2.setCondition("is true");

		// node 3
		BDDNoTerminalNode node3 = BddFactory.eINSTANCE.createBDDNoTerminalNode();
		node3.setId("node-3");
		node3.setVariable("C");
		node3.setCondition("is true");

		// node 4
		BDDNoTerminalNode node4 = BddFactory.eINSTANCE.createBDDNoTerminalNode();
		node4.setId("node-4");
		node4.setVariable("B");
		node4.setCondition("is true");

		// terminal 1
		BDDTerminalNode terminal1 = BddFactory.eINSTANCE.createBDDTerminalNode();
		terminal1.setId("term-1");
		terminal1.setValue(true);

		// terminal 2
		BDDTerminalNode terminal2 = BddFactory.eINSTANCE.createBDDTerminalNode();
		terminal2.setId("term-2");
		terminal2.setValue(false);

		// terminal 3
		BDDTerminalNode terminal3 = BddFactory.eINSTANCE.createBDDTerminalNode();
		terminal3.setId("term-3");
		terminal3.setValue(false);

		// terminal 4
		BDDTerminalNode terminal4 = BddFactory.eINSTANCE.createBDDTerminalNode();
		terminal4.setId("term-4");
		terminal4.setValue(true);

		// connection 1
		BDDConnection connection1 = BddFactory.eINSTANCE.createBDDConnection();
		connection1.setId("conn-1");
		connection1.setNegate(true);
		connection1.setSource(node1);
		connection1.setTarget(node2);

		// connection 2
		BDDConnection connection2 = BddFactory.eINSTANCE.createBDDConnection();
		connection2.setId("conn-2");
		connection2.setSource(node1);
		connection2.setTarget(node4);

		// connection 3
		BDDConnection connection3 = BddFactory.eINSTANCE.createBDDConnection();
		connection3.setId("conn-3");
		connection3.setSource(node2);
		connection3.setTarget(node3);

		// connection 4
		BDDConnection connection4 = BddFactory.eINSTANCE.createBDDConnection();
		connection4.setId("conn-4");
		connection4.setNegate(true);
		connection4.setSource(node2);
		connection4.setTarget(terminal1);

		// connection 5
		BDDConnection connection5 = BddFactory.eINSTANCE.createBDDConnection();
		connection5.setId("conn-5");
		connection5.setSource(node3);
		connection5.setTarget(terminal2);

		// connection 6
		BDDConnection connection6 = BddFactory.eINSTANCE.createBDDConnection();
		connection6.setId("conn-6");
		connection6.setNegate(true);
		connection6.setSource(node3);
		connection6.setTarget(node4);

		// connection 7
		BDDConnection connection7 = BddFactory.eINSTANCE.createBDDConnection();
		connection7.setId("conn-7");
		connection7.setSource(node4);
		connection7.setTarget(terminal3);

		// connection 8
		BDDConnection connection8 = BddFactory.eINSTANCE.createBDDConnection();
		connection8.setId("conn-8");
		connection8.setNegate(true);
		connection8.setSource(node4);
		connection8.setTarget(terminal4);

		model.getContents().add(node1);
		model.getContents().add(node2);
		model.getContents().add(node3);
		model.getContents().add(node4);
		model.getContents().add(terminal1);
		model.getContents().add(terminal2);
		model.getContents().add(terminal3);
		model.getContents().add(terminal4);
		model.getContents().add(connection1);
		model.getContents().add(connection2);
		model.getContents().add(connection3);
		model.getContents().add(connection4);
		model.getContents().add(connection5);
		model.getContents().add(connection6);
		model.getContents().add(connection7);
		model.getContents().add(connection8);

		return model;
	}

	private BDDModel onepathBDD() {
		BDDModel model = BddFactory.eINSTANCE.createBDDModel();

		// node 1
		BDDNoTerminalNode node1 = BddFactory.eINSTANCE.createBDDNoTerminalNode();
		node1.setId("node-1");
		node1.setVariable("A");
		node1.setCondition("is true");

		// node 2
		BDDNoTerminalNode node2 = BddFactory.eINSTANCE.createBDDNoTerminalNode();
		node2.setId("node-2");
		node2.setVariable("B");
		node2.setCondition("is true");

		// node 3
		BDDNoTerminalNode node3 = BddFactory.eINSTANCE.createBDDNoTerminalNode();
		node3.setId("node-3");
		node3.setVariable("C");
		node3.setCondition("is true");

		// node 4
		BDDNoTerminalNode node4 = BddFactory.eINSTANCE.createBDDNoTerminalNode();
		node4.setId("node-4");
		node4.setVariable("B");
		node4.setCondition("is true");

		// node 5
		BDDNoTerminalNode node5 = BddFactory.eINSTANCE.createBDDNoTerminalNode();
		node5.setId("node-5");
		node5.setVariable("D");
		node5.setCondition("is true");

		// node 6
		BDDNoTerminalNode node6 = BddFactory.eINSTANCE.createBDDNoTerminalNode();
		node6.setId("node-6");
		node6.setVariable("A");
		node6.setCondition("is true");

		// terminal 1
		BDDTerminalNode terminal1 = BddFactory.eINSTANCE.createBDDTerminalNode();
		terminal1.setId("term-1");
		terminal1.setValue(true);

		// connection 1
		BDDConnection connection1 = BddFactory.eINSTANCE.createBDDConnection();
		connection1.setId("conn-1");
		connection1.setNegate(true);
		connection1.setSource(node1);
		connection1.setTarget(node2);

		// connection 2
		BDDConnection connection2 = BddFactory.eINSTANCE.createBDDConnection();
		connection2.setId("conn-2");
		connection2.setSource(node2);
		connection2.setTarget(node3);

		// connection 3
		BDDConnection connection3 = BddFactory.eINSTANCE.createBDDConnection();
		connection3.setId("conn-3");
		connection3.setNegate(true);
		connection3.setSource(node3);
		connection3.setTarget(node4);

		// connection 4
		BDDConnection connection4 = BddFactory.eINSTANCE.createBDDConnection();
		connection4.setId("conn-4");
		connection4.setSource(node4);
		connection4.setTarget(node5);

		// connection 5
		BDDConnection connection5 = BddFactory.eINSTANCE.createBDDConnection();
		connection5.setId("conn-5");
		connection5.setSource(node5);
		connection5.setTarget(node6);

		// connection 6
		BDDConnection connection6 = BddFactory.eINSTANCE.createBDDConnection();
		connection6.setId("conn-6");
		connection6.setNegate(true);
		connection6.setSource(node6);
		connection6.setTarget(terminal1);

		model.getContents().add(node1);
		model.getContents().add(node2);
		model.getContents().add(node3);
		model.getContents().add(node4);
		model.getContents().add(node5);
		model.getContents().add(node6);
		model.getContents().add(terminal1);
		model.getContents().add(connection1);
		model.getContents().add(connection2);
		model.getContents().add(connection3);
		model.getContents().add(connection4);
		model.getContents().add(connection5);
		model.getContents().add(connection6);

		return model;
	}

	private BDDModel unorderedBDD() {
		BDDModel model = BddFactory.eINSTANCE.createBDDModel();

		// node 1
		BDDNoTerminalNode node1 = BddFactory.eINSTANCE.createBDDNoTerminalNode();
		node1.setId("node-1");
		node1.setVariable("A");
		node1.setCondition("is true");

		// node 2
		BDDNoTerminalNode node2 = BddFactory.eINSTANCE.createBDDNoTerminalNode();
		node2.setId("node-2");
		node2.setVariable("B");
		node2.setCondition("is true");

		// node 3
		BDDNoTerminalNode node3 = BddFactory.eINSTANCE.createBDDNoTerminalNode();
		node3.setId("node-3");
		node3.setVariable("C");
		node3.setCondition("is true");

		// node 4
		BDDNoTerminalNode node4 = BddFactory.eINSTANCE.createBDDNoTerminalNode();
		node4.setId("node-4");
		node4.setVariable("D");
		node4.setCondition("is true");

		// node 5
		BDDNoTerminalNode node5 = BddFactory.eINSTANCE.createBDDNoTerminalNode();
		node5.setId("node-5");
		node5.setVariable("C");
		node5.setCondition("is true");

		// node 6
		BDDNoTerminalNode node6 = BddFactory.eINSTANCE.createBDDNoTerminalNode();
		node6.setId("node-6");
		node6.setVariable("B");
		node6.setCondition("is true");

		// node 7
		BDDNoTerminalNode node7 = BddFactory.eINSTANCE.createBDDNoTerminalNode();
		node7.setId("node-7");
		node7.setVariable("E");
		node7.setCondition("is true");

		// terminal 1
		BDDTerminalNode terminal1 = BddFactory.eINSTANCE.createBDDTerminalNode();
		terminal1.setId("term-1");
		terminal1.setValue(false);

		// terminal 2
		BDDTerminalNode terminal2 = BddFactory.eINSTANCE.createBDDTerminalNode();
		terminal2.setId("term-2");
		terminal2.setValue(true);

		// terminal 3
		BDDTerminalNode terminal3 = BddFactory.eINSTANCE.createBDDTerminalNode();
		terminal3.setId("term-3");
		terminal3.setValue(false);

		// terminal 4
		BDDTerminalNode terminal4 = BddFactory.eINSTANCE.createBDDTerminalNode();
		terminal4.setId("term-4");
		terminal4.setValue(true);

		// terminal 5
		BDDTerminalNode terminal5 = BddFactory.eINSTANCE.createBDDTerminalNode();
		terminal5.setId("term-5");
		terminal5.setValue(false);

		// connection 1
		BDDConnection connection1 = BddFactory.eINSTANCE.createBDDConnection();
		connection1.setId("conn-1");
		connection1.setNegate(true);
		connection1.setSource(node1);
		connection1.setTarget(node2);

		// connection 2
		BDDConnection connection2 = BddFactory.eINSTANCE.createBDDConnection();
		connection2.setId("conn-2");
		connection2.setSource(node1);
		connection2.setTarget(node3);

		// connection 3
		BDDConnection connection3 = BddFactory.eINSTANCE.createBDDConnection();
		connection3.setId("conn-3");
		connection3.setNegate(true);
		connection3.setSource(node2);
		connection3.setTarget(node4);

		// connection 4
		BDDConnection connection4 = BddFactory.eINSTANCE.createBDDConnection();
		connection4.setId("conn-4");
		connection4.setSource(node2);
		connection4.setTarget(node5);

		// connection 5
		BDDConnection connection5 = BddFactory.eINSTANCE.createBDDConnection();
		connection5.setId("conn-5");
		connection5.setNegate(true);
		connection5.setSource(node3);
		connection5.setTarget(node4);

		// connection 6
		BDDConnection connection6 = BddFactory.eINSTANCE.createBDDConnection();
		connection6.setId("conn-6");
		connection6.setSource(node3);
		connection6.setTarget(node6);

		// connection 7
		BDDConnection connection7 = BddFactory.eINSTANCE.createBDDConnection();
		connection7.setId("conn-7");
		connection7.setNegate(true);
		connection7.setSource(node4);
		connection7.setTarget(terminal1);

		// connection 8
		BDDConnection connection8 = BddFactory.eINSTANCE.createBDDConnection();
		connection8.setId("conn-8");
		connection8.setSource(node4);
		connection8.setTarget(node7);

		// connection 9
		BDDConnection connection9 = BddFactory.eINSTANCE.createBDDConnection();
		connection9.setId("conn-9");
		connection9.setNegate(true);
		connection9.setSource(node5);
		connection9.setTarget(node7);

		// connection 10
		BDDConnection connection10 = BddFactory.eINSTANCE.createBDDConnection();
		connection10.setId("conn-10");
		connection10.setSource(node5);
		connection10.setTarget(terminal4);

		// connection 11
		BDDConnection connection11 = BddFactory.eINSTANCE.createBDDConnection();
		connection11.setId("conn-11");
		connection11.setNegate(true);
		connection11.setSource(node6);
		connection11.setTarget(node7);

		// connection 12
		BDDConnection connection12 = BddFactory.eINSTANCE.createBDDConnection();
		connection12.setId("conn-12");
		connection12.setSource(node6);
		connection12.setTarget(terminal5);

		// connection 13
		BDDConnection connection13 = BddFactory.eINSTANCE.createBDDConnection();
		connection13.setId("conn-13");
		connection13.setNegate(true);
		connection13.setSource(node7);
		connection13.setTarget(terminal2);

		// connection 14
		BDDConnection connection14 = BddFactory.eINSTANCE.createBDDConnection();
		connection14.setId("conn-14");
		connection14.setSource(node7);
		connection14.setTarget(terminal3);

		model.getContents().add(node1);
		model.getContents().add(node2);
		model.getContents().add(node3);
		model.getContents().add(node4);
		model.getContents().add(node5);
		model.getContents().add(node6);
		model.getContents().add(node7);
		model.getContents().add(terminal1);
		model.getContents().add(terminal2);
		model.getContents().add(terminal3);
		model.getContents().add(terminal4);
		model.getContents().add(terminal5);
		model.getContents().add(connection1);
		model.getContents().add(connection2);
		model.getContents().add(connection3);
		model.getContents().add(connection4);
		model.getContents().add(connection5);
		model.getContents().add(connection6);
		model.getContents().add(connection7);
		model.getContents().add(connection8);
		model.getContents().add(connection9);
		model.getContents().add(connection10);
		model.getContents().add(connection11);
		model.getContents().add(connection12);
		model.getContents().add(connection13);
		model.getContents().add(connection14);

		return model;
	}

	/*
	 * Returns a Specmate BDD model that can be used for tests.
	 */
	private BDDModel hardCodedBDD() {
		// model properties
		BDDModel model = BddFactory.eINSTANCE.createBDDModel();
		model.setId("hard-coded-bdd");
		model.setName("Hard Coded BDD");
		model.setDescription("This is the hard coded BDD.");

		// node 1
		BDDNoTerminalNode node1 = BddFactory.eINSTANCE.createBDDNoTerminalNode();
		node1.setId("node-1");
		node1.setName("node-1");
		node1.setVariable("A");
		node1.setCondition("is true");

		// node 2
		BDDNoTerminalNode node2 = BddFactory.eINSTANCE.createBDDNoTerminalNode();
		node2.setId("node-2");
		node2.setName("node-2");
		node2.setVariable("B");
		node2.setCondition("is true");

		// node 3
		BDDNoTerminalNode node3 = BddFactory.eINSTANCE.createBDDNoTerminalNode();
		node3.setId("node-3");
		node3.setName("node-3");
		node3.setVariable("C");
		node3.setCondition("is true");

		// terminal 1
		BDDTerminalNode terminal1 = BddFactory.eINSTANCE.createBDDTerminalNode();
		terminal1.setId("term-1");
		terminal1.setName("term-1");
		terminal1.setValue(true);

		// terminal 2 (implicit 0-terminals)
		/*
		 * BDDTerminalNode terminal2 =
		 * BddFactory.eINSTANCE.createBDDTerminalNode();
		 * terminal2.setId("term-2"); terminal2.setValue(false);
		 */

		// terminal 3 (implicit 0-terminals)
		/*
		 * BDDTerminalNode terminal3 =
		 * BddFactory.eINSTANCE.createBDDTerminalNode();
		 * terminal3.setId("term-3"); terminal3.setValue(false);
		 */

		// terminal 4
		BDDTerminalNode terminal4 = BddFactory.eINSTANCE.createBDDTerminalNode();
		terminal4.setId("term-4");
		terminal4.setName("term-4");
		terminal4.setValue(true);

		// connection 1
		BDDConnection connection1 = BddFactory.eINSTANCE.createBDDConnection();
		connection1.setId("conn-1");
		connection1.setName("conn-1");
		connection1.setNegate(true);
		connection1.setSource(node1);
		connection1.setTarget(node2);

		// connection 2
		BDDConnection connection2 = BddFactory.eINSTANCE.createBDDConnection();
		connection2.setId("conn-2");
		connection2.setName("conn-2");
		connection2.setSource(node1);
		connection2.setTarget(terminal4);

		// connection 3
		BDDConnection connection3 = BddFactory.eINSTANCE.createBDDConnection();
		connection3.setId("conn-3");
		connection3.setName("conn-3");
		connection3.setNegate(true);
		connection3.setSource(node2);
		connection3.setTarget(node3);

		// connection 4 (implicit 0-terminals)
		/*
		 * BDDConnection connection4 =
		 * BddFactory.eINSTANCE.createBDDConnection();
		 * connection4.setId("conn-4"); connection4.setSource(node2);
		 * connection4.setTarget(terminal3);
		 */

		// connection 5 (implicit 0-terminals)
		/*
		 * BDDConnection connection5 =
		 * BddFactory.eINSTANCE.createBDDConnection();
		 * connection5.setId("conn-5"); connection5.setNegate(true);
		 * connection5.setSource(node3); connection5.setTarget(terminal2);
		 */

		// connection 6
		BDDConnection connection6 = BddFactory.eINSTANCE.createBDDConnection();
		connection6.setId("conn-6");
		connection6.setName("conn-6");
		connection6.setSource(node3);
		connection6.setTarget(terminal1);

		model.getContents().add(node1);
		model.getContents().add(node2);
		model.getContents().add(node3);
		model.getContents().add(terminal1);
		// model.getContents().add(terminal2);
		// model.getContents().add(terminal3);
		model.getContents().add(terminal4);
		model.getContents().add(connection1);
		model.getContents().add(connection2);
		model.getContents().add(connection3);
		// model.getContents().add(connection4);
		// model.getContents().add(connection5);
		model.getContents().add(connection6);

		return model;
	}

	private void loadMiniTrainingTestData(Folder testFolder) {

		Folder evalFolder = BaseFactory.eINSTANCE.createFolder();
		testFolder.getContents().add(evalFolder);
		evalFolder.setId("evalFolder");
		evalFolder.setName("Evaluation");

		String excelRequirement = "Markiert der Nutzer mit der Linken Maustaste eine Zelle, so werden alle bestehenden Markierungen gelöscht und die aktuelle Zelle wird markiert."
				+ "Hält der Nutzer zusätzlich die Shift-Taste gedrückt, werden alle Zellen zwischen der letzten markierten Zelle und der aktuellen Zelle auch markiert."
				+ "Hält der Nutzer zusätzlich die Strg-Taste gedrückt, werden die bestehenden Markierungen nicht gelöscht, sondern die aktuelle Zelle zusätzlich markiert.";

		Requirement excelRequirment = RequirementsFactory.eINSTANCE.createRequirement();
		excelRequirment.setId("EvalRequirement-1");
		excelRequirment.setName("Excel Zellenmarkierung");
		excelRequirment.setDescription(excelRequirement);
		evalFolder.getContents().add(excelRequirment);

		String driverLicenceRequirementText = "Prüfung, ob Fahren eines Fahrzeugs erlaubt ist: Das Fahren eines Fahrzeugs ist erlaubt, wenn der Fahrer älter als 18 Jahre ist und "
				+ "einen Führerschein mit sich führt. Das Fahren des Fahrzeugs ist außerdem erlaubt, wenn der Fahrer zwar noch nicht 18 Jahre alt ist, aber einen Führerschein bereits besitzt "
				+ "und eine vollährige Begleitperson mit im Fahrzeug fährt.";

		Requirement driverLicenceRequirement = RequirementsFactory.eINSTANCE.createRequirement();
		driverLicenceRequirement.setId("driverLicenceReq-1");
		driverLicenceRequirement.setName("Erlaubnis Autofahren");
		driverLicenceRequirement.setDescription(driverLicenceRequirementText);
		evalFolder.getContents().add(driverLicenceRequirement);

		CEGModel evalModel1 = createExcelCEGExample(excelRequirement);

		excelRequirment.getContents().add(evalModel1);

	}

	private void loadUserStudyTestData(Folder testFolder) {
		Folder studyFolder = BaseFactory.eINSTANCE.createFolder();
		testFolder.getContents().add(studyFolder);
		studyFolder.setId("studyFolder");
		studyFolder.setName("User Study");

		studyFolder.getContents().add(createSingleStudyFolder("Study-1", "study-1"));
		studyFolder.getContents().add(createSingleStudyFolder("Study-2", "study-2"));
		studyFolder.getContents().add(createSingleStudyFolder("Study-3", "study-3"));
		studyFolder.getContents().add(createSingleStudyFolder("Study-4", "study-4"));
	}

	private Folder createSingleStudyFolder(String name, String id) {
		Folder studyFolder = BaseFactory.eINSTANCE.createFolder();
		studyFolder.setId(id);
		studyFolder.setName(name);

		String atmRequirementText = "Ist die Bankkarte gültig, und hat der Nutzer die korrekte PIN eingegeben, und ist Geld im Automaten verfügbar, so wird das Geld ausgezahlt.\n"
				+ "Falls nicht genügend Geld im Automaten verfügbar ist, fragt der Automat nach einem neuen Geldbetrag.\n"
				+ "\n"
				+ "Ist die Bankkarte zwar gültig, aber der Nutzer gibt die falsche PIN ein, so fragt der Automat nach einer neuen PIN. Gibt der Nutzer drei "
				+ "Mal eine falsche PIN ein, behält der Automat die Karte ein.\n" + "\n"
				+ "Ist die Bankkarte nicht gültig, wird die Bankkarte zurückgewiesen. ";

		Requirement atmRequirement = RequirementsFactory.eINSTANCE.createRequirement();
		atmRequirement.setId("atmrequireemnt");
		atmRequirement.setName("Bankautomat - Geld abheben");
		atmRequirement.setDescription(atmRequirementText);
		studyFolder.getContents().add(atmRequirement);

		Requirement processRequirement = RequirementsFactory.eINSTANCE.createRequirement();
		processRequirement.setId("processrequirement");
		processRequirement.setName("Bankautomat - Gesamtprozess");
		processRequirement.setDescription("Dieser Prozess beschreibt die Interaktion eines Kunden mit dem Geldautomat");

		studyFolder.getContents().add(processRequirement);

		Process process = ProcessesFactory.eINSTANCE.createProcess();
		process.setId("process1");
		process.setName("Prozessmodell");
		processRequirement.getContents().add(process);

		ProcessStart start = ProcessesFactory.eINSTANCE.createProcessStart();
		start.setId("start");
		start.setX(50);
		start.setY(100);
		process.getContents().add(start);

		ProcessStep step1 = ProcessesFactory.eINSTANCE.createProcessStep();
		step1.setId("step1");
		step1.setX(200);
		step1.setY(100);
		step1.setName("Automat aktivieren");
		process.getContents().add(step1);

		ProcessStep step2 = ProcessesFactory.eINSTANCE.createProcessStep();
		step2.setId("step2");
		step2.setX(400);
		step2.setY(100);
		step2.setName("Karte und PIN prüfen");
		process.getContents().add(step2);

		ProcessStep step3 = ProcessesFactory.eINSTANCE.createProcessStep();
		step3.setId("step3");
		step3.setX(400);
		step3.setY(200);
		step3.setName("Funktionsauswahl");
		process.getContents().add(step3);

		ProcessDecision decision = ProcessesFactory.eINSTANCE.createProcessDecision();
		decision.setId("decision1");
		decision.setX(400);
		decision.setY(300);
		process.getContents().add(decision);

		ProcessStep step4 = ProcessesFactory.eINSTANCE.createProcessStep();
		step4.setId("step4");
		step4.setX(300);
		step4.setY(400);
		step4.setName("Geld abheben wählen");
		process.getContents().add(step4);

		ProcessStep step5 = ProcessesFactory.eINSTANCE.createProcessStep();
		step5.setId("step5");
		step5.setX(300);
		step5.setY(500);
		step5.setName("Geldbetrag auswählen");
		process.getContents().add(step5);

		ProcessStep step6 = ProcessesFactory.eINSTANCE.createProcessStep();
		step6.setId("step6");
		step6.setX(300);
		step6.setY(600);
		step6.setName("Geld auszahlen");
		process.getContents().add(step6);

		ProcessStep step7 = ProcessesFactory.eINSTANCE.createProcessStep();
		step7.setId("step7");
		step7.setX(500);
		step7.setY(400);
		step7.setName("Kontostand wählen");
		process.getContents().add(step7);

		ProcessStep step8 = ProcessesFactory.eINSTANCE.createProcessStep();
		step8.setId("step8");
		step8.setX(500);
		step8.setY(500);
		step8.setName("Kontostand anzeigen");
		process.getContents().add(step8);

		ProcessStep step9 = ProcessesFactory.eINSTANCE.createProcessStep();
		step9.setId("step9");
		step9.setX(800);
		step9.setY(600);
		step9.setName("Karte ausgeben");
		process.getContents().add(step9);

		ProcessEnd end = ProcessesFactory.eINSTANCE.createProcessEnd();
		end.setId("end");
		end.setX(800);
		end.setY(300);
		process.getContents().add(end);

		ProcessConnection conn1 = ProcessesFactory.eINSTANCE.createProcessConnection();
		conn1.setName("conn1");
		conn1.setSource(start);
		conn1.setTarget(step1);
		conn1.setId("conn1");
		process.getContents().add(conn1);

		ProcessConnection conn2 = ProcessesFactory.eINSTANCE.createProcessConnection();
		conn2.setName("conn2");
		conn2.setSource(step1);
		conn2.setTarget(step2);
		conn2.setId("conn2");
		process.getContents().add(conn2);

		ProcessConnection conn3 = ProcessesFactory.eINSTANCE.createProcessConnection();
		conn3.setName("conn3");
		conn3.setSource(step2);
		conn3.setTarget(step3);
		conn3.setId("conn3");
		process.getContents().add(conn3);

		ProcessConnection conn4 = ProcessesFactory.eINSTANCE.createProcessConnection();
		conn4.setName("conn4");
		conn4.setSource(step3);
		conn4.setTarget(decision);
		conn4.setId("conn4");
		process.getContents().add(conn4);

		ProcessConnection conn5 = ProcessesFactory.eINSTANCE.createProcessConnection();
		conn5.setName("conn5");
		conn5.setSource(decision);
		conn5.setTarget(step4);
		conn5.setId("conn5");
		conn5.setCondition("abheben");
		process.getContents().add(conn5);

		ProcessConnection conn6 = ProcessesFactory.eINSTANCE.createProcessConnection();
		conn6.setName("conn6");
		conn6.setSource(decision);
		conn6.setTarget(step7);
		conn6.setId("conn6");
		conn6.setCondition("kontostand");
		process.getContents().add(conn6);

		ProcessConnection conn7 = ProcessesFactory.eINSTANCE.createProcessConnection();
		conn7.setName("conn7");
		conn7.setSource(decision);
		conn7.setTarget(end);
		conn7.setId("conn7");
		conn7.setCondition("ende");
		process.getContents().add(conn7);

		ProcessConnection conn8 = ProcessesFactory.eINSTANCE.createProcessConnection();
		conn8.setName("conn8");
		conn8.setSource(step4);
		conn8.setTarget(step5);
		conn8.setId("conn8");
		process.getContents().add(conn8);

		ProcessConnection conn9 = ProcessesFactory.eINSTANCE.createProcessConnection();
		conn9.setName("conn9");
		conn9.setSource(step5);
		conn9.setTarget(step6);
		conn9.setId("conn9");
		process.getContents().add(conn9);

		ProcessConnection conn10 = ProcessesFactory.eINSTANCE.createProcessConnection();
		conn10.setName("conn10");
		conn10.setSource(step6);
		conn10.setTarget(step9);
		conn10.setId("conn10");
		process.getContents().add(conn10);

		ProcessConnection conn11 = ProcessesFactory.eINSTANCE.createProcessConnection();
		conn11.setName("conn11");
		conn11.setSource(step9);
		conn11.setTarget(end);
		conn11.setId("conn11");
		process.getContents().add(conn11);

		ProcessConnection conn12 = ProcessesFactory.eINSTANCE.createProcessConnection();
		conn12.setName("conn12");
		conn12.setSource(step7);
		conn12.setTarget(step8);
		conn12.setId("conn12");
		process.getContents().add(conn12);

		ProcessConnection conn13 = ProcessesFactory.eINSTANCE.createProcessConnection();
		conn13.setName("conn13");
		conn13.setSource(step8);
		conn13.setTarget(step9);
		conn13.setId("conn13");
		process.getContents().add(conn13);

		return studyFolder;

	}

	private CEGModel createExcelCEGExample(String excelRequirement) {
		CEGModel evalModel1 = RequirementsFactory.eINSTANCE.createCEGModel();
		evalModel1.setName("Excel Zeilenmarkierung");
		evalModel1.setDescription(excelRequirement);
		evalModel1.setId("EvalModel-1");

		CEGNode evalNode1 = RequirementsFactory.eINSTANCE.createCEGNode();
		evalNode1.setId("evalnode-1");
		evalNode1.setName("evalnode-1");
		evalNode1.setDescription("");
		evalNode1.setX(100);
		evalNode1.setY(100);
		evalNode1.setVariable("Linke Maustaste");
		evalNode1.setCondition("gedrückt");
		evalModel1.getContents().add(evalNode1);

		CEGNode evalNode2 = RequirementsFactory.eINSTANCE.createCEGNode();
		evalNode2.setId("evalnode-2");
		evalNode2.setName("evalnode-2");
		evalNode2.setDescription("");
		evalNode2.setX(100);
		evalNode2.setY(200);
		evalNode2.setVariable("Strg Taste");
		evalNode2.setCondition("gedrückt");
		evalModel1.getContents().add(evalNode2);

		CEGNode evalNode3 = RequirementsFactory.eINSTANCE.createCEGNode();
		evalNode3.setId("evalnode-3");
		evalNode3.setName("evalnode-3");
		evalNode3.setDescription("");
		evalNode3.setX(100);
		evalNode3.setY(300);
		evalNode3.setVariable("Shift-Taste");
		evalNode3.setCondition("gedrückt");
		evalModel1.getContents().add(evalNode3);

		CEGNode evalNode4 = RequirementsFactory.eINSTANCE.createCEGNode();
		evalNode4.setId("evalnode-4");
		evalNode4.setName("evalnode-4");
		evalNode4.setDescription("");
		evalNode4.setX(500);
		evalNode4.setY(100);
		evalNode4.setVariable("Aktuelle Zelle");
		evalNode4.setCondition("markiert");
		evalModel1.getContents().add(evalNode4);

		CEGNode evalNode5 = RequirementsFactory.eINSTANCE.createCEGNode();
		evalNode5.setId("evalnode-5");
		evalNode5.setName("evalnode-5");
		evalNode5.setDescription("");
		evalNode5.setX(500);
		evalNode5.setY(200);
		evalNode5.setVariable("Vorhandene Markierung");
		evalNode5.setCondition("gelöscht");
		evalModel1.getContents().add(evalNode5);

		CEGNode evalNode6 = RequirementsFactory.eINSTANCE.createCEGNode();
		evalNode6.setId("evalnode-6");
		evalNode6.setName("evalnode-6");
		evalNode6.setDescription("");
		evalNode6.setX(500);
		evalNode6.setY(300);
		evalNode6.setVariable("Alle Zwischenzellen");
		evalNode6.setCondition("markiert");
		evalModel1.getContents().add(evalNode6);

		CEGConnection evalConn1 = RequirementsFactory.eINSTANCE.createCEGConnection();
		evalConn1.setId("evalConn1");
		evalConn1.setName("-");
		evalConn1.setSource(evalNode1);
		evalConn1.setTarget(evalNode4);
		evalConn1.setNegate(false);
		evalModel1.getContents().add(evalConn1);

		CEGConnection evalConn2 = RequirementsFactory.eINSTANCE.createCEGConnection();
		evalConn2.setId("evalConn2");
		evalConn2.setName("-");
		evalConn2.setSource(evalNode1);
		evalConn2.setTarget(evalNode5);
		evalConn2.setNegate(false);
		evalModel1.getContents().add(evalConn2);

		CEGConnection evalConn3 = RequirementsFactory.eINSTANCE.createCEGConnection();
		evalConn3.setId("evalConn3");
		evalConn3.setName("-");
		evalConn3.setSource(evalNode1);
		evalConn3.setTarget(evalNode6);
		evalConn3.setNegate(false);
		evalModel1.getContents().add(evalConn3);

		CEGConnection evalConn4 = RequirementsFactory.eINSTANCE.createCEGConnection();
		evalConn4.setId("evalConn4");
		evalConn4.setName("-");
		evalConn4.setSource(evalNode2);
		evalConn4.setTarget(evalNode5);
		evalConn4.setNegate(true);
		evalModel1.getContents().add(evalConn4);

		CEGConnection evalConn5 = RequirementsFactory.eINSTANCE.createCEGConnection();
		evalConn5.setId("evalConn5");
		evalConn5.setName("-");
		evalConn5.setSource(evalNode3);
		evalConn5.setTarget(evalNode5);
		evalConn5.setNegate(true);
		evalModel1.getContents().add(evalConn5);

		CEGConnection evalConn6 = RequirementsFactory.eINSTANCE.createCEGConnection();
		evalConn6.setId("evalConn6");
		evalConn6.setName("-");
		evalConn6.setSource(evalNode3);
		evalConn6.setTarget(evalNode6);
		evalConn6.setNegate(false);
		evalModel1.getContents().add(evalConn6);
		return evalModel1;
	}

}
