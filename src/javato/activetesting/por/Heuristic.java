package javato.activetesting.por;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

import javato.activetesting.common.Parameters;

/**
 * Class to find next path using different heuristics
 * @author lihao
 *
 */
public class Heuristic {
	/**
	 * Compute the Hamming Distance between two path
	 * @param path1
	 * @param path2
	 * @return Hamming Distance of two path
	 */
	public static int HammingDist(String path1, String path2) {
		String[] trace1 = path1.split("#");
		String[] trace2 = path2.split("#");
		int dist = 0;
		if (trace1.length > trace2.length) {
			for (int i = 0; i < trace2.length; i++) {
				if (!trace2[i].equals(trace1[i])) {
					dist++;
				}
			}
			dist += (trace1.length - trace2.length);
		} else {
			for (int i = 0; i < trace1.length; i++) {
				if (!trace1[i].equals(trace2[i])) {
					dist++;
				}
			}
			dist += (trace2.length - trace1.length);
		}
		return dist;
	}
	
	/**
	 * Compute preemption number of a path
	 * @param path path
	 * @return preemption number
	 */
	public static int PRNum(String path) {
		String[] trace = path.split("#");
		int prnum = 0;
		for (int i = 0; i < trace.length - 1; i++) {
			if (!trace[i].equals(trace[i + 1])) {
				prnum++;
			}
		}
		return prnum;
	}
	
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Usage: java javato.activetesting.por.Heuristic [ART|PR]");
			return;
		}
		File exploreFile = new File(Parameters.EXPLORE_FILE);
		File tmpPathFile = new File(Parameters.TMP_PATH_FILE);
		File doneFile = new File(Parameters.DONE_FILE);
		File errorFile = new File(Parameters.ERROR_FILE);
		
		//Create log files
		if (!exploreFile.exists()) {
			try {
				exploreFile.createNewFile();
			} catch (IOException e) {
				System.err.println("Can't create file " + Parameters.EXPLORE_FILE);
				e.printStackTrace();
			}
		}
		if (!tmpPathFile.exists()) {
			try {
				tmpPathFile.createNewFile();
			} catch (IOException e) {
				System.err.println("Can't create file " + Parameters.TMP_PATH_FILE);
				e.printStackTrace();
			}
		}
		if (!doneFile.exists()) {
			try {
				doneFile.createNewFile();
			} catch (IOException e) {
				System.err.println("Can't create file " + Parameters.DONE_FILE);
				e.printStackTrace();
			}
		}
		if (!errorFile.exists()) {
			try {
				errorFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//Heuristic is Adaptive Random Testing
		if (args[0].equals("HD")) {
			String path = null;
			BufferedReader pathReader = null;
			BufferedReader doneReader = null;
			BufferedReader tmpReader = null;
			PrintWriter pathWriter = null;
			PrintWriter tmpWriter = null;
			try {
				pathReader = new BufferedReader(new FileReader(Parameters.EXPLORE_FILE));
				doneReader = new BufferedReader(new FileReader(Parameters.DONE_FILE));
				tmpReader = new BufferedReader(new FileReader(Parameters.TMP_PATH_FILE));
				
				ArrayList<String> pathList = new ArrayList<String>();
				ArrayList<String> doneList = new ArrayList<String>();
				ArrayList<String> tmpList = new ArrayList<String>();
				
				String line;
				while ((line = pathReader.readLine()) != null) {
					pathList.add(line);
				}
				
				while ((line = doneReader.readLine()) != null) {
					doneList.add(line);
				}
				
				//No path to explore yet
				if (pathList.size() == 0) {
					//All path explored, stop
					if (doneList.size() > 0) {
						tmpWriter = new PrintWriter(new FileOutputStream(Parameters.TMP_PATH_FILE, false));
						tmpWriter.println("DONE");
						tmpWriter.close();
					}
					return;
				}
				
				while ((line = tmpReader.readLine()) != null) {
					tmpList.add(line);
				}
				
				//No path explored yet, randomly choose a path 
				if (tmpList.size() == 0) {
					Random rand = new Random();
					path = pathList.get(rand.nextInt(pathList.size()));
				} else {
					ArrayList<String> rmList = new ArrayList<String>();
					//Trim explored path
					for(int i = 0; i < doneList.size(); i++) {
						for (int j = 0; j < pathList.size(); j++) {
							if (doneList.get(i).indexOf(pathList.get(j)) != -1 ) {
								rmList.add(pathList.get(j));
							}
						}
					}
					
					for (int i = 0; i < rmList.size(); i++) {
						pathList.remove(rmList.get(i));
					}
					
					if (pathList.size() == 0) {
						tmpWriter = new PrintWriter(new FileOutputStream(Parameters.TMP_PATH_FILE, false));
						tmpWriter.println("DONE");
						tmpWriter.close();
						return;
					}
					
					//Find path to explore
					int max = -1;
					String exploredPath = tmpList.get(0);
					for (int i = 0; i < pathList.size(); i++) {
						int dist = HammingDist(exploredPath, pathList.get(i));
						if ( dist > max) {
							max = dist;
							path = pathList.get(i);
						}
					}
				}
				pathList.remove(path);   //Remove it from explore list
				doneList.add(path);      //Add it to done list
				
				//Rewrite explore list and done list
				pathWriter = new PrintWriter(new FileOutputStream(Parameters.EXPLORE_FILE, false));
				for (int i = 0; i < pathList.size(); i++) {
					pathWriter.println(pathList.get(i));
				}
				pathWriter.close();
				
				tmpWriter = new PrintWriter(new FileOutputStream(Parameters.TMP_PATH_FILE, false));
				tmpWriter.println(path);
				tmpWriter.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//Heuristic is Favor More Preemption
		if (args[0].equals("PR")) {
			String path = null;
			BufferedReader pathReader = null;
			BufferedReader doneReader = null;
			BufferedReader tmpReader = null;
			PrintWriter pathWriter = null;
			PrintWriter tmpWriter = null;
			try {
				pathReader = new BufferedReader(new FileReader(Parameters.EXPLORE_FILE));
				doneReader = new BufferedReader(new FileReader(Parameters.DONE_FILE));
				tmpReader = new BufferedReader(new FileReader(Parameters.TMP_PATH_FILE));
				
				ArrayList<String> pathList = new ArrayList<String>();
				ArrayList<String> doneList = new ArrayList<String>();
				ArrayList<String> tmpList = new ArrayList<String>();
				
				String line;
				while ((line = pathReader.readLine()) != null) {
					pathList.add(line);
				}
				
				while ((line = doneReader.readLine()) != null) {
					doneList.add(line);
				}
				
				//No path to explore yet
				if (pathList.size() == 0) {
					//All path explored, stop
					if (doneList.size() > 0) {
						tmpWriter = new PrintWriter(new FileOutputStream(Parameters.TMP_PATH_FILE, false));
						tmpWriter.println("DONE");
						tmpWriter.close();
					}
					return;
				}
				
				while ((line = tmpReader.readLine()) != null) {
					tmpList.add(line);
				}
				
				//No path explored yet, randomly choose a path 
				if (tmpList.size() == 0) {
					Random rand = new Random();
					path = pathList.get(rand.nextInt(pathList.size()));
				} else {
					ArrayList<String> rmList = new ArrayList<String>();
					//Trim explored path
					for(int i = 0; i < doneList.size(); i++) {
						for (int j = 0; j < pathList.size(); j++) {
							if (doneList.get(i).indexOf(pathList.get(j)) != -1 ) {
								rmList.add(pathList.get(j));
							}
						}
					}
					
					for (int i = 0; i < rmList.size(); i++) {
						pathList.remove(rmList.get(i));
					}
					
					if (pathList.size() == 0) {
						tmpWriter = new PrintWriter(new FileOutputStream(Parameters.TMP_PATH_FILE, false));
						tmpWriter.println("DONE");
						tmpWriter.close();
						return;
					}
					
					//Find path to explore
					int max = -1;
					for (int i = 0; i < pathList.size(); i++) {
						int prnum = PRNum(pathList.get(i));
						if ( prnum > max) {
							max = prnum;
							path = pathList.get(i);
						}
					}
				}
				pathList.remove(path);   //Remove it from explore list
				doneList.add(path);      //Add it to done list
				
				//Rewrite explore list and done list
				pathWriter = new PrintWriter(new FileOutputStream(Parameters.EXPLORE_FILE, false));
				for (int i = 0; i < pathList.size(); i++) {
					pathWriter.println(pathList.get(i));
				}
				pathWriter.close();

				tmpWriter = new PrintWriter(new FileOutputStream(Parameters.TMP_PATH_FILE, false));
				tmpWriter.println(path);
				tmpWriter.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//Heuristic is Favor Less Preemption
		if (args[0].equals("PL")) {
			String path = null;
			BufferedReader pathReader = null;
			BufferedReader doneReader = null;
			BufferedReader tmpReader = null;
			PrintWriter pathWriter = null;
			PrintWriter tmpWriter = null;
			try {
				pathReader = new BufferedReader(new FileReader(Parameters.EXPLORE_FILE));
				doneReader = new BufferedReader(new FileReader(Parameters.DONE_FILE));
				tmpReader = new BufferedReader(new FileReader(Parameters.TMP_PATH_FILE));
				
				ArrayList<String> pathList = new ArrayList<String>();
				ArrayList<String> doneList = new ArrayList<String>();
				ArrayList<String> tmpList = new ArrayList<String>();
				
				String line;
				while ((line = pathReader.readLine()) != null) {
					pathList.add(line);
				}
				
				while ((line = doneReader.readLine()) != null) {
					doneList.add(line);
				}
				
				//No path to explore yet
				if (pathList.size() == 0) {
					//All path explored, stop
					if (doneList.size() > 0) {
						tmpWriter = new PrintWriter(new FileOutputStream(Parameters.TMP_PATH_FILE, false));
						tmpWriter.println("DONE");
						tmpWriter.close();
					}
					return;
				}
				
				while ((line = tmpReader.readLine()) != null) {
					tmpList.add(line);
				}
				
				//No path explored yet, randomly choose a path 
				if (tmpList.size() == 0) {
					Random rand = new Random();
					path = pathList.get(rand.nextInt(pathList.size()));
				} else {
					ArrayList<String> rmList = new ArrayList<String>();
					//Trim explored path
					for(int i = 0; i < doneList.size(); i++) {
						for (int j = 0; j < pathList.size(); j++) {
							if (doneList.get(i).indexOf(pathList.get(j)) != -1 ) {
								rmList.add(pathList.get(j));
							}
						}
					}
					
					for (int i = 0; i < rmList.size(); i++) {
						pathList.remove(rmList.get(i));
					}
					
					if (pathList.size() == 0) {
						tmpWriter = new PrintWriter(new FileOutputStream(Parameters.TMP_PATH_FILE, false));
						tmpWriter.println("DONE");
						tmpWriter.close();
						return;
					}
					
					//Find path to explore
					int min = Integer.MAX_VALUE;
					for (int i = 0; i < pathList.size(); i++) {
						int prnum = PRNum(pathList.get(i));
						if ( prnum < min) {
							min = prnum;
							path = pathList.get(i);
						}
					}
				}
				pathList.remove(path);   //Remove it from explore list
				doneList.add(path);      //Add it to done list
				
				//Rewrite explore list and done list
				pathWriter = new PrintWriter(new FileOutputStream(Parameters.EXPLORE_FILE, false));
				for (int i = 0; i < pathList.size(); i++) {
					pathWriter.println(pathList.get(i));
				}
				pathWriter.close();

				tmpWriter = new PrintWriter(new FileOutputStream(Parameters.TMP_PATH_FILE, false));
				tmpWriter.println(path);
				tmpWriter.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//Heuristic is Random Testing
		if (args[0].equals("RT")) {
			String path = null;
			BufferedReader pathReader = null;
			BufferedReader doneReader = null;
			BufferedReader tmpReader = null;
			PrintWriter pathWriter = null;
			PrintWriter tmpWriter = null;
			try {
				pathReader = new BufferedReader(new FileReader(Parameters.EXPLORE_FILE));
				doneReader = new BufferedReader(new FileReader(Parameters.DONE_FILE));
				tmpReader = new BufferedReader(new FileReader(Parameters.TMP_PATH_FILE));
				
				ArrayList<String> pathList = new ArrayList<String>();
				ArrayList<String> doneList = new ArrayList<String>();
				ArrayList<String> tmpList = new ArrayList<String>();
				
				String line;
				while ((line = pathReader.readLine()) != null) {
					pathList.add(line);
				}
				
				while ((line = doneReader.readLine()) != null) {
					doneList.add(line);
				}
				
				//No path to explore yet
				if (pathList.size() == 0) {
					//All path explored, stop
					if (doneList.size() > 0) {
						tmpWriter = new PrintWriter(new FileOutputStream(Parameters.TMP_PATH_FILE, false));
						tmpWriter.println("DONE");
						tmpWriter.close();
					}
					return;
				}
				
				while ((line = tmpReader.readLine()) != null) {
					tmpList.add(line);
				}
				
				//No path explored yet, randomly choose a path 
				if (tmpList.size() == 0) {
					Random rand = new Random();
					path = pathList.get(rand.nextInt(pathList.size()));
				} else {
					ArrayList<String> rmList = new ArrayList<String>();
					//Trim explored path
					for(int i = 0; i < doneList.size(); i++) {
						for (int j = 0; j < pathList.size(); j++) {
							if (doneList.get(i).indexOf(pathList.get(j)) != -1 ) {
								rmList.add(pathList.get(j));
							}
						}
					}
					
					for (int i = 0; i < rmList.size(); i++) {
						pathList.remove(rmList.get(i));
					}
					
					if (pathList.size() == 0) {
						tmpWriter = new PrintWriter(new FileOutputStream(Parameters.TMP_PATH_FILE, false));
						tmpWriter.println("DONE");
						tmpWriter.close();
						return;
					}
					
					//Randomly Find path to explore
					Random rand = new Random();
					path = pathList.get(rand.nextInt(pathList.size()));
				}
				pathList.remove(path);   //Remove it from explore list
				doneList.add(path);      //Add it to done list
				
				//Rewrite explore list and done list
				pathWriter = new PrintWriter(new FileOutputStream(Parameters.EXPLORE_FILE, false));
				for (int i = 0; i < pathList.size(); i++) {
					pathWriter.println(pathList.get(i));
				}
				pathWriter.close();

				tmpWriter = new PrintWriter(new FileOutputStream(Parameters.TMP_PATH_FILE, false));
				tmpWriter.println(path);
				tmpWriter.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
