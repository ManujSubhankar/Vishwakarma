package edu.sssihl.mca.vishwakarma.datamodel;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import edu.sssihl.mca.vishwakarma.datamodel.metric.IASoftwareMetric;
import edu.sssihl.mca.vishwakarma.datamodel.smell.IADesignSmell;

public class AMethod implements IAMethod {
	private ArrayList<IAField> accessedFields;
	private ArrayList<IASoftwareMetric> metrics= new ArrayList<>();
	private IAType parent;
	private int modifier;
	private String returnType;
	private IAField[] parameters;
	private String name;
	private ArrayList<String> fanOutTypes;
	private List<IAField> variables= new ArrayList<>();
	private ArrayList<IADesignSmell> smells= new ArrayList<>();
	private MethodDeclaration md;
	private boolean isOverriding= false,isGetter= false,isSetter= false;
	private ArrayList<String> overridingTypes= new ArrayList<>();

	public AMethod(MethodDeclaration md, IAType parent) {
		MethodVisitor methodVisitor= new MethodVisitor(parent.getFields());
		this.md= md;
		this.parent= parent;
		name= md.getName().toString();
		parameters= computeParameter(md);
		modifier= md.getModifiers();
		if(md.getReturnType2() != null ) {
			if(md.getReturnType2().resolveBinding() != null)
				returnType= md.getReturnType2().resolveBinding().getQualifiedName();
		}
		md.accept(methodVisitor);
		for(VariableDeclarationFragment i : methodVisitor.getVariables()) {
			variables.add(new AField(this, i, Modifier.NATIVE, false));
		}
		fanOutTypes= methodVisitor.getFanOut();
		checkGetter();
		checkSetter();
		this.accessedFields= methodVisitor.getAccessedFields();
		this.accessedFields.trimToSize();
		computeOverriding();

		this.metrics= getProject().getMetricConfiguration().getMethodLevelMetrics();
		for(IASoftwareMetric i : this.metrics) {
			i.setOwnerElement(this);
			i.computeMetric();
		}

		for(IADesignSmell i : getProject().getMetricConfiguration().getMethodLevelSmells1()) {
			i.setOwner(this);
			if(i.detectSmell())
				this.smells.add(i);
		}

		this.md= null;
	}

	private void computeOverriding() {
		ArrayList<String> typeList= new ArrayList<>();
		ArrayList<IMethodBinding[]> methodList= new ArrayList<>();
		computeAncestors(typeList, methodList);
		computeSuperInterfaces(typeList, methodList);
		for(int i= 0; i < methodList.size(); i++) {
			for(IMethodBinding j : methodList.get(i)) {
				if(compareMethod(j)) {
					this.isOverriding= true;
					this.overridingTypes.add(typeList.get(i));
					break;
				}
			}
		}
		overridingTypes.trimToSize();
	}
	
	private void computeAncestors( List<String> typeList, List<IMethodBinding[]> methodList) {
		ITypeBinding binding= null;
		if(parent.getTypeDeclaration() != null && parent.getTypeDeclaration().resolveBinding() != null) {
			binding= parent.getTypeDeclaration().resolveBinding().getSuperclass();
			while(binding != null) {
				typeList.add(binding.getQualifiedName());
				methodList.add(binding.getDeclaredMethods());
				binding= binding.getSuperclass();
			}
		}
	}

	private void computeSuperInterfaces( List<String> typeList, List<IMethodBinding[]> methodList) {
		ITypeBinding binding;
		if(parent.getTypeDeclaration() != null && parent.getTypeDeclaration().resolveBinding() != null) {
			binding= parent.getTypeDeclaration().resolveBinding();
			ITypeBinding[] inter= binding.getInterfaces();
			for(ITypeBinding i : inter) {
				typeList.add(i.getQualifiedName());
				methodList.add(i.getDeclaredMethods());
			}
		}
	}

	/**
	 * Checks whether the prototype of this AMethod matches with the prototype of given method
	 * @param meth IMethodBinding with which this AMethod is going to be compared
	 * @return true if the prototype of both the function matches
	 */
	public boolean compareMethod(IMethodBinding meth) {
		if(this.name.equals(meth.getName()) && parameters.length == meth.getParameterTypes().length) {
			boolean match= true;
			ITypeBinding[] para= meth.getTypeParameters();
			for(int k= 0; k < para.length; k++) {
				if(!para[k].getQualifiedName().equals(parameters[k].getTypeName())) {
					match= false;
					break;
				}
			}
			return match;
		}
		return false;
	}


	public void checkSetter() {
		if(md.getBody() == null) return;
		List<Statement> statements= md.getBody().statements();
		if(md.parameters().size() == 1 && statements.size() == 1 && statements.get(0) instanceof ExpressionStatement) {
			ExpressionStatement st= (ExpressionStatement) statements.get(0);
			if(st.getExpression() instanceof Assignment) {
				Assignment as= (Assignment) st.getExpression();
				if(isAccessingField(as.getLeftHandSide()) && as.getRightHandSide() instanceof SimpleName)
					isSetter= true;
			}
		}
	}

	public void checkGetter() {
		if(md.getBody() == null) return;
		List<Statement> statements= md.getBody().statements();
		if(statements.size() == 1 && statements.get(0) instanceof ReturnStatement) {
			ReturnStatement st= (ReturnStatement) statements.get(0);
			if(isAccessingField(st.getExpression()))
				isGetter= true;
		}
	}

	private boolean isAccessingField(Expression ex) {
		if(ex instanceof FieldAccess || ex instanceof SuperFieldAccess)
			return true;
		if(ex instanceof SimpleName) {
			SimpleName name= (SimpleName) ex;
			for(IAField i : parent.getFields()) {
				if(i.getBinding() != null && name.resolveBinding() != null) {
					if( i.getBinding().equals(name.resolveBinding()) ) 
						return true;
				}
			}
		}
		return false;
	}

	@Override
	public List<String> getFanOutTypes() {
		return fanOutTypes;
	}

	@Override
	public List<IADesignSmell> getSmells() {
		return smells;
	}

	@Override
	public void addDesignSmell(IADesignSmell smell) {
		smells.add(smell);
	}

	@Override
	public boolean isOverriding() {
		return isOverriding;
	}

	@Override
	public boolean isGetter() {
		return isGetter;
	}

	@Override
	public boolean isSetter() {
		return isSetter;
	}

	public List<String> getOverridingTypes() {
		return overridingTypes;
	}

	public MethodDeclaration getMethodDeclaration(){
		return md;
	}


	public List<IAField> getAccessedFields() {
		return accessedFields;
	}

	public List<IAField> getVariables() {
		return variables;
	}

	public List<IAField> getAccessedNonStaticFields() {
		List<IAField> ans= new ArrayList<>();
		for(IAField i : accessedFields) {
			if( !Modifier.isStatic(i.getModifier()) )
				ans.add(i);
		}
		return ans;
	}

	/**
	 *  
	 * @return the type signatures of the exceptions this method throws.
	 */
	public String[] getExceptions() {
		String[] str= {};
		return str;
	}

	/**
	 * 
	 * @return he number of parameters of this method.
	 */
	public int getParametersCount() {
		return parameters.length;
	}

	/**
	 * 
	 * @return the Qualified Names for the parameters of this method.
	 */
	public IAField[] getParameters() {
		return parameters;
	}

	/**
	 * 
	 * @return the names of parameters in this method.
	 */
	private AField[] computeParameter(MethodDeclaration md) {
		Object[] parameters= md.parameters().toArray();
		AField[] ans= new AField[parameters.length];
		for(int i= 0; i < parameters.length ; i++) {
			ans[i]= new AField(this,(SingleVariableDeclaration) parameters[i],Modifier.NATIVE,false);
		}
		return ans;
	}

	/**
	 * Returns the modifiers explicitly specified on this method. 
	 * The returned integer is a constant of {@link Modifier}
	 * @return  the bit-wise "or" of Modifier constants
	 */
	public int getModifier() {
		return modifier;
	}


	/**
	 * 
	 * @return the Qualified Name of the return value of this method.
	 */
	public String getReturnType() {
		return returnType;
	}

	@Override
	public AJavaElementType getElementType() {
		return AJavaElementType.Method;
	}

	@Override
	public IAProject getProject() {
		return parent.getProject();
	}

	@Override
	public String getElementName() {
		return name;
	}

	@Override
	public IAJavaElement getParentElement() {
		return parent;
	}

	@Override
	public List<IAJavaElement> getChildren() {
		return null;
	}

	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public IAJavaElement getElement(String path) {
		return null;
	}

	@Override
	public List<IASoftwareMetric> getMetrics() {
		return metrics;
	}

	@Override
	public void addMetrics(IASoftwareMetric metric) {
		this.metrics.add(metric);
	}


}
