package edu.sssihl.mca.vishwakarma.datamodel.metric;

import java.util.List;

import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import edu.sssihl.mca.vishwakarma.datamodel.IAJavaElement;
import edu.sssihl.mca.vishwakarma.datamodel.IAMethod;

public class CyclomaticComplexity implements IASoftwareMetric {
	
	private int value;
	private int cyclo= 0;
	private IAMethod owner;
	
	@Override
	public String getName() {
		return "Cyclomatic Complexity";
	}

	@Override
	public String getShortName() {
		return "CC";
	}

	@Override
	public int getValue() {
		return value;
	}

	@Override
	public boolean isToBeComputedAfterProjectBuilt() {
		return false;
	}

	@Override
	public MetricLevel getLevel() {
		return MetricLevel.Method;
	}

	@Override
	public void computeMetric() {
		value= getPathCount(owner.getMethodDeclaration().getBody(),1)+cyclo;
		cyclo= 0;
	}
	
	private int getPathCount(Statement body, int current) {
		if(body == null) return 1;
		
		int ans= 1;
		if(body instanceof Block) {
			for( Object i : ((Block)body).statements() ) {
				if(i instanceof ReturnStatement) {
					cyclo+= ans * current;
					return 0;
					//return 1;
				}
				ans*= getPathInBlockStatement((Statement)i, ans*current);
			}
		}
		else if(body instanceof ReturnStatement) {
			cyclo+= ans * current;
			return 0;
			//return 1;
		}
		else
			ans*= getPathInBlockStatement(body, ans*current);
		
		return ans;
	}
	
	private int getPathInBlockStatement(Statement stmt, int current) {
		if(stmt instanceof DoStatement) 
			return getPathCount(((DoStatement)stmt).getBody(), current)+1;
		
		else if(stmt instanceof EnhancedForStatement) 
			return getPathCount(((EnhancedForStatement)stmt).getBody(), current)+1;
		
		else if(stmt instanceof ForStatement)
			return getPathCount(((ForStatement)stmt).getBody(), current)+1;
		
		else if(stmt instanceof IfStatement) {
			int then= getPathCount(((IfStatement)stmt).getThenStatement(), current);
			if(((IfStatement)stmt).getElseStatement() != null)
				then+= getPathCount(((IfStatement)stmt).getElseStatement(), current);
			else then++;
			return  then; 
								
		}
		
		else if(stmt instanceof SwitchStatement)
			return getPathInSwithStatement((SwitchStatement)stmt, current);
		
		else if(stmt instanceof TryStatement) {
			return getPathCount(((TryStatement)stmt).getBody(), current) *
					getPathCount(((TryStatement)stmt).getFinally(), current);
		}
			
		else if(stmt instanceof WhileStatement)
			return getPathCount(((WhileStatement)stmt).getBody(), current)+1;
		
		else if(stmt instanceof SynchronizedStatement)
			return getPathCount(((SynchronizedStatement)stmt).getBody(), current);
		
		else 
			return 1;
	
	}
	
	private int getPathInSwithStatement(SwitchStatement node, int current) {
		int ans= 0;
		int temp= 1;
		int nextCase= 1;
		List<Object> stmts= node.statements();
		Object stmt;
		int size= stmts.size();
		
		for(int i= 1; i < size; i++) {
			stmt= stmts.get(i);
			temp*= getPathInBlockStatement((Statement)stmt, current*ans);
			if(stmt instanceof BreakStatement || i == size-1) {
				ans+= temp;
				temp= 1;
				if(nextCase != 0) {
					i= nextCase;
					nextCase= 0;
				}
				continue;
			}
			else if(stmt instanceof SwitchCase && nextCase != 0)
				nextCase= i;
			
		}
		
		if(! (stmts.get(size-1) instanceof BreakStatement) )
			ans+= temp;
		
		return ans;
	}

	@Override
	public void setOwnerElement(IAJavaElement owner) {
		this.owner= (IAMethod)owner;
	}

	@Override
	public IAJavaElement getOwnerElement() {
		return owner;
	}
	
	@Override
	public int getThreshold() {
		return owner.getProject().getThreshold().getMethodThreshold(getShortName());
	}

}
