package com.specmate.testspecification.internal.services;

import java.util.List;

import javax.ws.rs.core.Response;

import org.eclipse.emf.ecore.EObject;
import org.osgi.service.component.annotations.Component;

import com.specmate.bdd.BDD2CEGTranslator;
import com.specmate.common.SpecmateException;
import com.specmate.common.SpecmateValidationException;
import com.specmate.emfrest.api.IRestService;
import com.specmate.emfrest.api.RestServiceBase;
import com.specmate.model.base.BaseFactory;
import com.specmate.model.base.IModelNode;
import com.specmate.model.bdd.BDDModel;
import com.specmate.model.processes.Process;
import com.specmate.model.requirements.CEGModel;
import com.specmate.model.requirements.RequirementsFactory;
import com.specmate.model.support.util.SpecmateEcoreUtil;
import com.specmate.model.testspecification.TestSpecification;
import com.specmate.model.testspecification.TestspecificationFactory;
import com.specmate.rest.RestResult;

/**
 * Service for generating test cases for a test specification that is linked to
 * a CEG model.
 *
 * @author junkerm
 */
@Component(immediate = true, service = IRestService.class)
public class TestGeneratorService extends RestServiceBase {

	/** {@inheritDoc} */
	@Override
	public String getServiceName() {
		return "generateTests";

	}

	/** {@inheritDoc} */
	@Override
	public boolean canPost(Object target, Object object) {
		return target instanceof TestSpecification;
	}

	/** {@inheritDoc} */
	@Override
	public RestResult<?> post(Object target, Object object, String token)
			throws SpecmateValidationException, SpecmateException {
		TestSpecification specification = (TestSpecification) target;
		EObject container = specification.eContainer();
		if (container instanceof CEGModel) {
			new CEGTestCaseGenerator(specification).generate();
		} else if (container instanceof Process) {
			new ProcessTestCaseGenerator(specification).generate();
		} else if (container instanceof BDDModel) {

			try{
				// translating
				BDD2CEGTranslator translator = new BDD2CEGTranslator();
				CEGModel ceg_model = translator.translate((BDDModel) container);
				List<IModelNode> ceg_nodes = (List<IModelNode>) SpecmateEcoreUtil.pickInstancesOf(ceg_model.getContents(),
						IModelNode.class);

				// setting up the CEGTestCaseGenerator with a given TestSpecification
				CEGTestCaseGenerator ceggi = new CEGTestCaseGenerator(specification);
				ceggi.setModel(ceg_model);
				ceggi.setNodes(ceg_nodes);

				// generating the test cases
				ceggi.generate();
				System.out.println("Generation was successful!");
				
			}catch(IllegalArgumentException iae){
				//we land here when the formula for the BDD is simplified to $false or $true
				System.out.println("The BDD was simplified to a terminal.");
				//TODO: open a popup window (like the one for saving before generating
			}
			
			

		} else {
			throw new SpecmateValidationException(
					"You can only generate test cases from ceg models or processes. The supplied element is of class "
							+ container.getClass().getSimpleName());
		}
		return new RestResult<>(Response.Status.NO_CONTENT);
	}

}
