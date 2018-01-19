package edu.sssihl.mca.vishwakarma.datamodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;

import edu.sssihl.mca.vishwakarma.datamodel.metric.IASoftwareMetric;
import edu.sssihl.mca.vishwakarma.datamodel.smell.IADesignSmell;

public class AProject implements IAProject {
	private Configuration config;
	private Threshold threshold;
	private List<IASoftwareMetric> metrics;
	private String name;
	private ArrayList<IAPackage> packages= new ArrayList<>();
	private ArrayList<IAType> allTypes= new ArrayList<>();
	private ArrayList<IAMethod> allMethods= new ArrayList<>();

	public AProject(IJavaProject pro, Configuration config, Threshold threshold) {
		this.config= config;
		this.threshold= threshold;
		name= pro.getElementName();

		long start, end;
		start= System.currentTimeMillis();
		System.out.println("Start time : " + start);

		try {
			IPackageFragmentRoot[] root= pro.getPackageFragmentRoots();
			float f= (float) root.length;
			for(IPackageFragmentRoot i : root) {
				if(!i.isExternal() && !i.isArchive()) {
					for(IJavaElement j : i.getChildren()) {
						packages.add(new APackage((IPackageFragment)j,this));
					}
				}
			}

			Object[] arr=  packages.toArray();
			Arrays.sort(arr);
			packages= new ArrayList<>();
			for(Object i : arr)
				packages.add((APackage)i);
			findAllTypes();
			findAllMethods();
			setChildren2();

			end= System.currentTimeMillis();
			System.out.println("End time : " + end);
			System.out.println("Total time taken : " + (float)(end-start)/(float)1000 + "sec");

		} catch (Exception e) {
			e.printStackTrace();
		}

		metrics= config.getProjectLevelMetrics();
		for(IASoftwareMetric i : metrics) {
			i.setOwnerElement(this);
			i.computeMetric();
		}

		for(IADesignSmell i : config.getProjectLevelSmell()) {
			i.setOwner(this);
			i.detectSmell();
		}

	}

	private void findAllTypes() {
		for(IAPackage i : packages) {
			allTypes.addAll(i.getAllTypes());
		}
	}

/*	private void setChildren() throws InterruptedException {
		for(IAType i : allTypes) {
			IAType superClass;
			//Add itself to the super class's children list
			if(i.getSuperClass() != null) {
				superClass=(IAType)getElement(i.getSuperClass());
				if(superClass != null)
					superClass.addSubClass(i);
			}
			//Add itself to the list fan-in of the types it has used 
			for(String j : i.getFanOutTypes()) {
				superClass=(IAType)getElement(j);
				if(superClass != null)
					superClass.addFanInType(i);
			}
			//Add itself to the super interfaces' children list
			for(String j : i.getSuperInterfaces()) {
				superClass=(AType)getElement(j);
				if(superClass != null)
					superClass.addSubClass(i);
			}

		}

		for(IAType i : allTypes) 
			i.doAfterBuildTasks();
	}*/

	private void setChildren2() throws InterruptedException {
		ExecutorService exec= Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		for(IAType i : allTypes) {
			Runnable run= new Runnable() {
				@Override
				public void run() {
					IAType superClass;
					//Add itself to the super class's children list
					if(i.getSuperClass() != null) {
						superClass=(IAType)getElement(i.getSuperClass());
						if(superClass != null)
							superClass.addSubClass(i);
					}
					//Add itself to the list fan-in of the types it has used 
					for(String j : i.getFanOutTypes()) {
						superClass=(IAType)getElement(j);
						if(superClass != null)
							superClass.addFanInType(i);
					}
					//Add itself to the super interfaces' children list
					for(String j : i.getSuperInterfaces()) {
						superClass=(AType)getElement(j);
						if(superClass != null)
							superClass.addSubClass(i);
					}
				}
			};
			exec.submit(run);
		}
		exec.shutdown();
		if(!exec.awaitTermination(100000, TimeUnit.DAYS))
			System.out.println("Time Out.....");
		doAfterBuildTasks();
		
	}
	
	public void doAfterBuildTasks() throws InterruptedException {
		ExecutorService exec= Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		for(IAType i : allTypes) {
			Runnable run= new Runnable() {
				@Override
				public void run() {
					i.doAfterBuildTasks();
				}
			};
			exec.submit(run);
		}
		exec.shutdown();
		if(!exec.awaitTermination(100000, TimeUnit.DAYS))
			System.out.println("Time Out.....");
	}

	public List<IAType> getAllTypes() {
		return allTypes;
	}

	public void findAllMethods() {
		for(IAType i : allTypes)
			allMethods.addAll(i.getMethods());
	}

	public List<IAMethod> getAllMethods() {
		return allMethods;
	}

	public List<IAPackage> getAllPackages(){
		return packages;
	}


	public IAJavaElement getElement(String path) {
		if(path == null) return null;
		IAPackage temp= null;
		String str;
		for(int i= packages.size() - 1; i >= 0; i--) {
			if( path.startsWith(packages.get(i).getElementName()) ) {
				temp= packages.get(i);
				break;
			}
		}
		if(temp == null)
			return null;
		str= path.substring(temp.getElementName().length()+1);
		return temp.getElement(str);
	}

	@Override
	public List<IASoftwareMetric> getMetrics() {
		return metrics;
	}

	@Override
	public Threshold getThreshold() {
		return threshold;
	}

	@Override
	public Configuration getMetricConfiguration() {
		return config;
	}

	@Override
	public AJavaElementType getElementType() {
		return AJavaElementType.Project;
	}

	@Override
	public String getElementName() {
		return name;
	}

	@Override
	public IAJavaElement getParentElement() {
		return null;
	}

	@Override
	public List<IAJavaElement> getChildren() {
		List<IAJavaElement> ans= new ArrayList<>();
		ans.addAll(packages);
		return ans;
	}

	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public IAProject getProject() {
		return this;
	} 
}

