package edu.sssihl.mca.vishwakarma.datamodel.smell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.sssihl.mca.vishwakarma.datamodel.IAJavaElement;
import edu.sssihl.mca.vishwakarma.datamodel.IAProject;
import edu.sssihl.mca.vishwakarma.datamodel.IAType;

public class DuplicateAbstraction implements IADesignSmell {
	
	private IAJavaElement owner;
	private List<IAType> duplicates;

	@Override
	public String getName() {
		return "Duplicate Abstraction";
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
		return SmellType.Abstraction;
	}

	@Override
	public void setOwner(IAJavaElement owner) {
		this.owner= owner;
	}
	
	@Override
	public IAJavaElement getOwner(){
		return owner;
	}

//	@Override
//	public boolean detectSmell() {
//		List<IAType> types= (List<IAType>) ((ArrayList)owner.getAllTypes()).clone(); 
//		while(types.size() > 0) {
//			IAType type= types.get(0);
//			List<IAType> dup= new ArrayList<>();
//			for(int i= 0; i < types.size(); i++) {
//				if(types.get(i).getElementName().equals(type.getElementName())) {
//					dup.add(types.remove(i));
//				}
//			}
//			if(dup.size() > 0)
//				duplicates.add(dup);
//		}
//		if(duplicates.size() > 0)
//			return true;
//		duplicates= null;
//		return false;
//	}
	
	@Override
	public boolean detectSmell() {
		HashMap<String, List<IAType>> map= new HashMap<>();
		List<IAType> types= ((IAProject)owner).getAllTypes();
		for(IAType i : types) {
			List<IAType> temp= map.get(i.getElementName());
			if(temp == null) 
				temp= new ArrayList<IAType>();
			temp.add(i);
			map.put(i.getElementName(), temp);
		}
		List<List<IAType>> dup= new ArrayList<>();
		for(List<IAType> list : map.values()) {
			if(list.size() > 1)
				dup.add(list);
		}
		
		for(List<IAType> i : dup) {
			for(IAType j : i) {
				DuplicateAbstraction d= new DuplicateAbstraction();
				d.setOwner(j);
				d.setDuplicates(i);
				j.addDesignSmell(d);
			}
		}
		return false;
	}
	
	public void setDuplicates(List<IAType> dup) {
		this.duplicates= dup;
	}

	@Override
	public String getMessage() {
		StringBuffer message= new StringBuffer();
		message.append(((IAType)owner).getQualifiedName());
		message.append(" has the same name as ");
		for(IAType i : duplicates)
			message.append(i.getQualifiedName() + ", ");
		return message.toString();
	}

}
