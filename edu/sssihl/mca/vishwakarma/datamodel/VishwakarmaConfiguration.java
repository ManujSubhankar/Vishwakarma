package edu.sssihl.mca.vishwakarma.datamodel;

import java.util.*;
import edu.sssihl.mca.vishwakarma.datamodel.metric.*;
import edu.sssihl.mca.vishwakarma.datamodel.smell.CyclicallyDependentModularization;
import edu.sssihl.mca.vishwakarma.datamodel.smell.DeepHierarchy;
import edu.sssihl.mca.vishwakarma.datamodel.smell.DeficientEncapsulation;
import edu.sssihl.mca.vishwakarma.datamodel.smell.DuplicateAbstraction;
import edu.sssihl.mca.vishwakarma.datamodel.smell.HubLikeModularization;
import edu.sssihl.mca.vishwakarma.datamodel.smell.IADesignSmell;
import edu.sssihl.mca.vishwakarma.datamodel.smell.ImperativeAbstraction;
import edu.sssihl.mca.vishwakarma.datamodel.smell.InsufficientModularization;
import edu.sssihl.mca.vishwakarma.datamodel.smell.MultifacetedAbstraction;
import edu.sssihl.mca.vishwakarma.datamodel.smell.MultipathHierarchy;
import edu.sssihl.mca.vishwakarma.datamodel.smell.RebelliousHierarchy;
import edu.sssihl.mca.vishwakarma.datamodel.smell.RebelliousMethod;
import edu.sssihl.mca.vishwakarma.datamodel.smell.UnnecessaryAbstraction;
import edu.sssihl.mca.vishwakarma.datamodel.smell.UnutilizedAbstraction;
import edu.sssihl.mca.vishwakarma.datamodel.smell.WideHierarchy;

public class VishwakarmaConfiguration implements Configuration {

	@Override
	public ArrayList<IASoftwareMetric> getClassLevelMetrics1() {
		ArrayList<IASoftwareMetric> temp= new ArrayList<IASoftwareMetric>();
		temp.add(new NumberOfFields());
		temp.add(new NumberOfPublicFields());	//Index(1) is used in Deficient Encapsulation Design Smell
		temp.add(new NumberOfMethods());	//Index(2) is used in Insufficient Modularization Design Smell
		temp.add(new NumberOfPublicMethods());  //Index(3) is used in Imperative Abstraction Design Smell
		temp.add(new WeightedMethodPerClass()); //Index(4) is used in Insufficient Modularization Design Smell
		temp.add(new LackOfCohesion());		 //Index(5) is used in Multifaceted Abstraction Design Smell
		temp.add(new DepthOfInheritance());  //Index(6) is used in Deep Hierarchy Design Smell
		temp.add(new NumberOfChildren());	 //Index(7) is used in Wide Hierarchy Design Smell
		temp.add(new LinesOfCode());
		temp.add(new FanOut());  //Index(9) is used in Hub-like Modularization Design Smell
		temp.trimToSize();

		return temp;
	}

	@Override
	public ArrayList<IASoftwareMetric> getClassLevelMetrics2() {
		ArrayList<IASoftwareMetric> temp= new ArrayList<IASoftwareMetric>();
		temp.add(new FanIn());  //Index(10) is used in Hub-like Modularization Design Smell
		temp.trimToSize();

		return temp;
	}

	@Override
	public ArrayList<IASoftwareMetric> getMethodLevelMetrics() {
		ArrayList<IASoftwareMetric> temp= new ArrayList<IASoftwareMetric>();
		temp.add(new CyclomaticComplexity());
		temp.add(new ParameterCount());
		temp.add(new LinesOfCode());  //Index(2) is used in Imperative Abstraction 
		//temp.add(new IsGetter());
		//temp.add(new IsSetter());
		temp.trimToSize();
		return temp;
	}

	@Override
	public List<IADesignSmell> getClassLevelSmells1() {
		ArrayList<IADesignSmell> smell= new ArrayList<>();
		smell.add(new DeficientEncapsulation());
		smell.add(new ImperativeAbstraction());
		smell.add(new InsufficientModularization());
		smell.add(new MultifacetedAbstraction());
		smell.add(new DeepHierarchy());
		smell.add(new RebelliousHierarchy());
		smell.add(new UnnecessaryAbstraction());
		smell.trimToSize();
		return smell;
	}

	@Override
	public List<IADesignSmell> getClassLevelSmells2() {
		ArrayList<IADesignSmell> smell= new ArrayList<>();
		//smell.add(new DuplicateAbstraction());
		smell.add(new HubLikeModularization());
		smell.add(new MultipathHierarchy());
		smell.add(new UnutilizedAbstraction());
		smell.add(new WideHierarchy());
		smell.trimToSize();
		return smell;
	}
	
	@Override
	public List<IADesignSmell> getMethodLevelSmells1() {
		ArrayList<IADesignSmell> smell= new ArrayList<>();
		smell.add(new RebelliousMethod());
		smell.trimToSize();
		return smell;
	}

	@Override
	public List<IASoftwareMetric> getProjectLevelMetrics() {
		ArrayList<IASoftwareMetric> temp= new ArrayList<>();
		temp.add(new MetricsViolation());
		temp.add(new ProjectSize());
		temp.trimToSize();
		return temp;
	}

	@Override
	public List<IADesignSmell> getProjectLevelSmell() {
		ArrayList<IADesignSmell> smell= new ArrayList<>();
		smell.add(new CyclicallyDependentModularization());
		smell.add(new DuplicateAbstraction());
		return smell;
	}
	
}
