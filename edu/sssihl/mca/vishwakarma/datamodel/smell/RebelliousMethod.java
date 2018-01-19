package edu.sssihl.mca.vishwakarma.datamodel.smell;

import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.ThrowStatement;

import edu.sssihl.mca.vishwakarma.datamodel.IAJavaElement;
import edu.sssihl.mca.vishwakarma.datamodel.IAMethod;

public class RebelliousMethod implements IADesignSmell {

	IAMethod owner;
	@Override
	public String getName() {
		return "Rebellious Method";
	}

	@Override
	public boolean isToBeComputedAfterProjectBuild() {
		return false;
	}

	@Override
	public SmellLevel getLevel() {
		return SmellLevel.Method;
	}

	@Override
	public SmellType getType() {
		return SmellType.Hierarchy;
	}

	@Override
	public void setOwner(IAJavaElement owner) {
		this.owner= (IAMethod) owner;
	}

	@Override
	public IAJavaElement getOwner() {
		return this.owner;
	}

	@Override
	public boolean detectSmell() {
		if(!owner.isOverriding()) return false;
		MethodDeclaration md= owner.getMethodDeclaration();
		if(md.getBody() == null) return false;
		List<Statement> statements= md.getBody().statements();
		if(statements.size() == 0)
			return true;
		if(statements.size() == 1) {
			if(statements.get(0) instanceof ReturnStatement) {
				if(((ReturnStatement)statements.get(0)).getExpression() instanceof NullLiteral)
					return true;
				else if(statements.get(0) instanceof ThrowStatement) 
					return true;
			}
		}
		return false;
	}

	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return "This method is not actually implemented.";
	}

}
