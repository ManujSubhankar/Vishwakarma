package edu.sssihl.mca.vishwakarma.datamodel;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.FrameworkUtil;
import edu.sssihl.mca.vishwakarma.Activator;
import edu.sssihl.mca.vishwakarma.views.ViewLabelProvider;

public class VishwakarmaThreshold implements Threshold {
	private File file;
	private HashMap<String, Integer> classThres= new HashMap<>();
	private HashMap<String, Integer> methodThres= new HashMap<>();
	
	public void getReady() {
		File dir= new File(Activator.getDefault().getStateLocation().toString() + "/cofig.txt");
		file= dir;
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			init();
		}
		fillFields();
		
		methodThres.put("PC",5);
		methodThres.put("LOC",100);
		methodThres.put("CC", 8);
		methodThres.put("IG",100);
		methodThres.put("IS",100);
	}
	
	private void fillFields() {
		for(String[] str : getData()) {
			classThres.put(str[0], Integer.parseInt(str[1]));
		}
	}
	
	public void init(){
		try {
			InputStream in= FileLocator.find(FrameworkUtil.getBundle(ViewLabelProvider.class), new Path("icons/config.txt"), null).openStream();
			BufferedOutputStream out= new BufferedOutputStream(new FileOutputStream(file));
			byte[] buf= new byte[1024];
			while(true) {
				int c= in.read(buf);
				if(c == -1) break;
				out.write(buf, 0, c);
			}
			out.flush();
			out.close();
		} catch (IOException e) { e.printStackTrace();}
	}
	
	public String[][] getData() {
		String[][] ans= null;
		try {
			ArrayList<String> data= new ArrayList<>();
			BufferedReader in= new BufferedReader(new FileReader(file));
			String temp;
			temp= in.readLine();
			while(temp != null) {
				data.add(temp.replaceAll("\\s",""));
				temp= in.readLine();
			}

			ans= new String[data.size()][];
			for(int i= 0; i < data.size(); i++) {
				ans[i]= data.get(i).split(",");
			}

		}catch (Exception e) {e.printStackTrace();}
		return ans;
	}
	
	public void setData(String[][] data) {
		try {
			System.out.println(data.length);
			FileOutputStream out= new FileOutputStream(file);
			PrintWriter write= new PrintWriter(out);
			FileChannel channel= out.getChannel();
			channel.truncate(0);
			for(int i= 0; i < data.length; i++) {
				write.println(data[i][0]+", "+data[i][1]+", "+data[i][2]);
			}
			write.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		fillFields();
	}
	
	/* (non-Javadoc)
	 * @see edu.sssihl.mca.vishwakarma.datamodel.Threshold#getClassThreshold(java.lang.String)
	 */
	@Override
	public int getClassThreshold(String key) {
		int ans= 0;
		try {
			ans= classThres.get(key);
		}catch (Exception e) { 
			e.printStackTrace();
		}
		return ans;
	}
	
	/* (non-Javadoc)
	 * @see edu.sssihl.mca.vishwakarma.datamodel.Threshold#getMethodThreshold(java.lang.String)
	 */
	@Override
	public int getMethodThreshold(String key) {
		return methodThres.get(key);
	}
}
