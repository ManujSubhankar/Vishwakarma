package edu.sssihl.mca.vishwakarma.datamodel;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import com.sun.swing.internal.plaf.synth.resources.synth;

import edu.sssihl.mca.vishwakarma.datamodel.metric.IASoftwareMetric;
import edu.sssihl.mca.vishwakarma.datamodel.smell.IADesignSmell;

public class AType implements IAType {
	private ArrayList<IAType> types= new ArrayList<>();
	private ArrayList<IAType> subClasses= new ArrayList<>();
	private ArrayList<IAMethod> methods= new ArrayList<>();
	private ArrayList<IAField> fields= new ArrayList<>();
	private ArrayList<String> fanOutType;
	private ArrayList<IAType> fanInType= new ArrayList<>();
	private boolean Class;
	private ArrayList<IASoftwareMetric> metrics;
	private ArrayList<IADesignSmell> smells= new ArrayList<>();
	private boolean Interface;
	private boolean Enum;
	private String name;
	private String superClass= null;
	private ArrayList<String> superInterfaces= new ArrayList<>();
	private IAJavaElement parentElement;
	private IAPackage Package;
	private TypeDeclaration typeDeclaration;
	private EnumDeclaration enumDeclaration;
	private TypeVisitor typeVisitor;

	public AType(TypeDeclaration td, IAJavaElement parent, IAPackage Package) {
		if(td.isInterface()) {
			Interface= true;
			Class= false;
		}
		else {
			Interface= false;
			Class= true;
		}
		typeDeclaration= td;
		this.Package= Package;
		this.parentElement= parent;
		name= td.getName().toString();
		typeVisitor= new TypeVisitor();
		td.accept(typeVisitor);
		fillFields();
		typeDeclaration= null;
		typeVisitor= null;
	}

	public AType(EnumDeclaration ed, IAJavaElement parent, IAPackage Package) {
		this.Package= Package;
		this.parentElement= parent;
		Enum= true;
		Class= false;
		Interface= false;
		enumDeclaration= ed;
		name= ed.getName().toString();
		typeVisitor= new TypeVisitor();
		ed.accept(typeVisitor);
		fillFields();
		enumDeclaration= null;
		typeVisitor= null;
	}

	private void fillFields() {
		if(!this.Enum) {
			computeSuperClass();
			computeSuperInterfaces();
		}
		
		computeFields();
		computeMethods();
		computeTypes();

		for(IAField i : fields)
			i.clearBinding();
		
		fanOutType= typeVisitor.getFanOut();
		computeFanOutType();
		computeMetrics();
		computeSmells();
	}
	
	private void computeFields () {
		for(FieldDeclaration i : typeVisitor.getFieldDeclarations()) {
			@SuppressWarnings("unchecked")
			List<VariableDeclarationFragment> f= i.fragments();
			for(VariableDeclarationFragment j : f) {
				fields.add(new AField(this,j, i.getModifiers(),true));
			}
		}
		fields.trimToSize();
	}
	
	private void computeTypes () {
		for(TypeDeclaration i : typeVisitor.getTypes()) {
			types.add(new AType(i,this, Package));
		}
		for(EnumDeclaration i : typeVisitor.getEnums()) {
			types.add(new AType(i,this, Package));
		}
		types.trimToSize();
	}
	
	private void computeMethods () {
		for(MethodDeclaration i : typeVisitor.getMethods()) {
			methods.add(new AMethod(i,this));
		}
		methods.trimToSize();
	}
	
	
	private void computeMetrics () {
		this.metrics= getProject().getMetricConfiguration().getClassLevelMetrics1();
		for(IASoftwareMetric i : this.metrics) {
			i.setOwnerElement(this);
			i.computeMetric();
		}
	}
	
	private void computeSmells () {
		for(IADesignSmell i : getProject().getMetricConfiguration().getClassLevelSmells1()) {
			i.setOwner(this);
			if(i.detectSmell())
				this.smells.add(i);
		}
	}
	
	private void computeFanOutType() {
		for(IAMethod i : methods) {
			for(String j : i.getFanOutTypes()) {
				if( !fanOutType.contains(j) )
					fanOutType.add(j);	
			}
		}
		for(IAField field : this.fields) {
			for(String j : field.getFanOutTypes()) {
				if( !fanOutType.contains(j) )
					fanOutType.add(j);
			}
		}
		fanInType.trimToSize();
	}
	
	private void computeSuperClass() {
		ITypeBinding binding= this.typeDeclaration.resolveBinding();
		if(binding != null) {
			binding= binding.getSuperclass();
			if(superClass == null && binding != null ) {
				if(!binding.getName().toString().equals("Object"))
					superClass= binding.getQualifiedName();
			}
		}
	}

	private void computeSuperInterfaces() {
		ITypeBinding binding= this.typeDeclaration.resolveBinding();
		ITypeBinding[] inter= binding.getInterfaces();
		for(ITypeBinding i : inter) {
			superInterfaces.add(i.getQualifiedName());
		}
		superInterfaces.trimToSize();
	}
	
	
	@Override
	public void doAfterBuildTasks() {
		List<IASoftwareMetric> met= getProject().getMetricConfiguration().getClassLevelMetrics2();
		for(IASoftwareMetric i : met) {
			i.setOwnerElement(this);
			i.computeMetric();
		}
		this.metrics.addAll(met);
		this.metrics.trimToSize();
		
		for(IADesignSmell i : getProject().getMetricConfiguration().getClassLevelSmells2()) {
			i.setOwner(this);
			if(i.detectSmell())
				this.smells.add(i);
		}
		this.smells.trimToSize();
	}
	
	@Override
	public void addDesignSmell (IADesignSmell smell) {
		this.smells.add(smell);
	}
	
	@Override
	public void addMetrics(IASoftwareMetric metric) {
		this.metrics.add(metric);
	}
	
	
	@Override
	public TypeDeclaration getTypeDeclaration() {
		return typeDeclaration;
	}
	
	@Override
	public EnumDeclaration getEnumDeclaration() {
		return enumDeclaration;
	}
	
	@Override
	public List<String> getFanOutTypes() {
		return fanOutType;
	}
	
	@Override
	public List<IAType> getFanInTypes() {
		return fanInType;
	}
	
	@Override
	synchronized public void addFanInType(IAType type) {
		fanInType.add(type);
	}

	/**
	 * 
	 * @return the fields declared by this type
	 */
	@Override
	public List<IAField> getFields() {
		return fields;
	}
	
	/**
	 * 
	 * @return the methods and constructors declared by this type
	 */
	@Override
	public List<IAMethod> getMethods() {
		return methods;
	}

	
	/**
	 * 
	 * @return the immediate member types declared by this type.
	 */
	@Override
	public List<IAType> getTypes() {
		return types;
	}

	@Override
	public List<IAType> getAllTypes() {
		List<IAType> temp= new ArrayList<>();
		for(IAType i : types) {
			temp.add(i);
			temp.addAll(i.getTypes());
		}
		return temp;
	}


	/**
	 * 
	 * @return true if the type represents a class, false otherwise.
	 */
	@Override
	public boolean isClass() {
		return Class;
	}


	/**
	 * 
	 * @return true if the type represents a interface, false otherwise.
	 */
	@Override
	public boolean isInterface() {
		return Interface;
	}


	/**
	 * 
	 * @return true if the type represents a enum, false otherwise.
	 */
	@Override
	public boolean isEnum() {
		return  Enum;
	}
	
	@Override
	public List<IASoftwareMetric> getMetrics() {
		return metrics;
	}
	
	@Override
	public List<IADesignSmell> getSmells() {
		return smells;
	}

	@Override
	public IAJavaElement getElement(String path) {
		if(path == null) return null;
		if(path.length() == 0) return this;
		IAType temp= null;
		String str;
		for(int i= types.size() - 1; i >= 0; i--) {
			if( path.startsWith(types.get(i).getElementName()) )
				temp= types.get(i);
		}
		if(temp == null)
			return null;
		if(temp.getElementName().equals(path))
			return temp;
		if(path.length() >= temp.getElementName().length()+1) {
			str= path.substring(temp.getElementName().length()+1);
			return temp.getElement(str);
		}
		return null;
	}

	@Override
	public String getSuperClass() {
		return superClass;
	}

	@Override
	public List<String> getSuperInterfaces () {
		return superInterfaces;
	}

	@Override
	public IAType getType(String name) {
		IAType ans= null;
		for(IAType i : types) {
			if(i.getElementName().equals(name)) {
				ans= i;
				break;
			}
		}
		return ans;
	}

	@Override
	public List<IAType> getSubClasses() {
		return subClasses;
	}
	
	@Override
	public IAPackage getPackage() {
		IAJavaElement par= parentElement;
		while(!(par instanceof IAPackage))
			par= par.getParentElement();
		return (IAPackage)par;
	}
	
	@Override
	public IAProject getProject() {
		return Package.getProject();
	}
	
	@Override
	public AJavaElementType getElementType() {
		return AJavaElementType.Type;
	}

	@Override
	public String getElementName() {
		return name;
	}
	
	@Override
	public String getQualifiedName() {
		if(parentElement instanceof IAType)
			return ((IAType) parentElement).getQualifiedName() + "." + name;
		return Package.getElementName() + "." + name;
	}

	@Override
	public IAJavaElement getParentElement() {
		return parentElement;
	}

	@Override
	public List<IAJavaElement> getChildren() {
		ArrayList<IAJavaElement> childrens= new ArrayList<>();
		childrens.addAll(fields);
		childrens.addAll(methods);
		childrens.addAll(types);
		return childrens;
	}

	@Override
	public boolean hasChildren() {
		if(getChildren().size() == 0)
			return false;
		return true;
	}

	@Override
	synchronized public void addSubClass(IAType child) {
		subClasses.add(child);
	} 
	
}
