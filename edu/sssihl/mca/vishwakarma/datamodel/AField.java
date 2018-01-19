package edu.sssihl.mca.vishwakarma.datamodel;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class AField implements IAField {
	
	private IBinding binding;
	private int modifier;
	private String name;
	private String type;
	private IAJavaElement parent;
	private ArrayList<String> parameter;
	private ArrayList<String> fanOuts= new ArrayList<>();
	
	public AField(IAJavaElement parent,VariableDeclarationFragment fd, int modifier, boolean storeBinding) {
		this.parent= parent;
		this.modifier= modifier;
		FieldVisitor visitor= new FieldVisitor();
		fd.accept(visitor);
		this.fanOuts= visitor.getFanOut();
		name= fd.getName().toString();
		if(storeBinding)
			binding= fd.getName().resolveBinding();
		IBinding bind= fd.resolveBinding();
		if(bind != null) {
			bind= ((IVariableBinding)bind).getType();
			if(bind != null)
				type= ((ITypeBinding)bind).getQualifiedName();
				
		}
		
	}
	
	public AField(IAJavaElement parent, SingleVariableDeclaration fd, int modifier, boolean storeBinding) {
		this.parent= parent;
		this.modifier= modifier;
		FieldVisitor visitor= new FieldVisitor();
		fd.accept(visitor);
		this.fanOuts= visitor.getFanOut();
		name= fd.getName().toString();
		if(storeBinding)
			binding= fd.getName().resolveBinding();
		IBinding bind= fd.resolveBinding();
		if(bind != null) {
			bind= ((IVariableBinding)bind).getType();
			if(bind != null)
				type= ((ITypeBinding)bind).getQualifiedName().trim();
		}
	}
	
	public IBinding getBinding() {
		return binding;
	}
	
	public String getTypeName() {
		return type;
	}
	
	public void clearBinding() {
		binding= null;
	}
	
	/**
	 * Returns the modifiers explicitly specified on this field. 
	 * The returned integer is constant of {@link Modifier}
	 * @return  the bit-wise "or" of Modifier constants
	 */
	public int getModifier() {
		return modifier;
	}
	
	
	/**
	 * Returns the constant value associated with this field or null if this field has none.
	 * @return constant value
	 */
	public Object getValue() {
		return null;
	}

	@Override
	public List<String> getFanOutTypes() {
		return fanOuts;
	}
	
	@Override
	public IAProject getProject() {
		return parent.getProject();
	}
	
	@Override
	public AJavaElementType getElementType() {
		// TODO Auto-generated method stub
		return AJavaElementType.Field;
	}

	@Override
	public String getElementName() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public IAJavaElement getParentElement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IAJavaElement> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IAJavaElement getElement(String path) {
		// TODO Auto-generated method stub
		return null;
	}

}
