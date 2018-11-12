package com.specmate.testspecification.internal.services;

import java.util.List;

import com.specmate.bdd.BDD2CEGTranslator;
import com.specmate.common.SpecmateException;
import com.specmate.model.base.IModelNode;
import com.specmate.model.bdd.BDDModel;
import com.specmate.model.bdd.BDDNode;
import com.specmate.model.support.util.SpecmateEcoreUtil;
import com.specmate.model.testspecification.TestSpecification;

public class BDDTestCaseGenerator extends TestCaseGeneratorBase<BDDModel, BDDNode> {

	private BDD2CEGTranslator translator;
	private CEGTestCaseGenerator generator;
	
	public BDDTestCaseGenerator(TestSpecification specification) {
		super(specification, BDDModel.class, BDDNode.class);
		this.translator = new BDD2CEGTranslator();
		this.generator = new CEGTestCaseGenerator(specification);
		this.generator.setModel(translator.translate(model));
		this.generator.setNodes(nodes);
	}

	@Override
	protected void generateParameters() {
		generator.generateParameters();
	}

	@Override
	protected void generateTestCases() throws SpecmateException {
		generator.generateTestCases();
	}

}
