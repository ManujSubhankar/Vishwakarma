package edu.sssihl.mca.vishwakarma.datamodel.smell;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import edu.sssihl.mca.vishwakarma.datamodel.IAJavaElement;
import edu.sssihl.mca.vishwakarma.datamodel.IAProject;
import edu.sssihl.mca.vishwakarma.datamodel.IAType;
import edu.sssihl.mca.vishwakarma.datamodel.smell.IADesignSmell;
import edu.sssihl.mca.vishwakarma.datamodel.smell.SmellLevel;
import edu.sssihl.mca.vishwakarma.datamodel.smell.SmellType;

public class CyclicallyDependentModularization implements IADesignSmell {

	private IAJavaElement owner;
	private List<String> cycle;

	@Override
	public String getName() {
		return "Cyclically Dependent Modularization";
	}

	@Override
	public boolean isToBeComputedAfterProjectBuild() {
		return true;
	}

	@Override
	public SmellLevel getLevel() {
		return SmellLevel.Project;
	}

	@Override
	public SmellType getType() {
		return SmellType.Modularization;
	}

	@Override
	public void setOwner(IAJavaElement owner) {
		this.owner= owner;
	}

	@Override
	public IAJavaElement getOwner() {
		return owner;
	}

	@Override
	public boolean detectSmell() {
		HashMap<String,Node> map= new HashMap<>();
		for(IAType i : ((IAProject)owner).getAllTypes())
			map.put(i.getQualifiedName(), new Node(i));

		Collection<Node> nodes= map.values();
		for(Node i : nodes) {
			for(String j : i.value.getFanOutTypes()) {
				Node node= map.get(j);
				if(node != null)
					i.adj.add(node);
			}
		}

		for(Node i : nodes) {
			if(i.index == -1)
				Node.strongConnect(i);
		}

		ArrayList<ArrayList<String>> typeCycle= new ArrayList<>();
		for(ArrayList<Node> cycle : Node.stronglyConnectedComponent) {
			if(cycle.size() < 2) continue;
			ArrayList<String> toString= new ArrayList<>();
			for(int i= cycle.size()-1; i >= 0; i--)
				toString.add(cycle.get(i).value.getQualifiedName());
			
			
			for(Node n : cycle) {
				CyclicallyDependentModularization c= new CyclicallyDependentModularization();
				c.setOwner(n.value);
				c.setCycleElements(toString);
				n.value.addDesignSmell(c);
			}
		}
		Node.reSet();

		return false;
	}
	
	public void setCycleElements(List<String> cycle) {
		this.cycle= cycle;
	}

	@Override
	public String getMessage() {
		StringBuffer message= new StringBuffer();
		message.append(((IAType)owner).getQualifiedName());
		message.append(" is part of the cycle ");
		for(String i : cycle)
			message.append(i + " --> ");
		message.append(cycle.get(0));
		
		return message.toString();
	}
}

class Node {
	IAType value;
	static int globalIndex= 0;
	int index= -1;
	int lowLink= -1;
	boolean onStack= false;
	ArrayList<Node> adj= new ArrayList<>();
	static ArrayDeque<Node> stack= new ArrayDeque<>();
	static ArrayList<ArrayList<Node>> stronglyConnectedComponent= new ArrayList<>();

	public Node(IAType value) {
		this.value= value;
	}

	public static void strongConnect(Node n) {
		n.index= Node.globalIndex;
		n.lowLink= Node.globalIndex;
		Node.globalIndex+= 1;
		Node.stack.push(n);
		n.onStack= true;

		for(Node i : n.adj) {
			if(i.index == -1) {
				strongConnect(i);
				n.lowLink= Integer.min(n.lowLink, i.lowLink);
			}
			else if(i.onStack) {
				n.lowLink= Integer.min(n.lowLink, i.index);
			}
		}

		if(n.lowLink == n.index) {
			ArrayList<Node> connected= new ArrayList<>();
			Node w;
			do {
				w= Node.stack.pop();
				w.onStack= false;
				connected.add(w);
			}while (!w.equals(n));
			Node.stronglyConnectedComponent.add(connected);
		}
	}	
	
	public static void reSet() {
		globalIndex= 0;
		stack= new ArrayDeque<>();
		stronglyConnectedComponent= new ArrayList<>();
	}
}