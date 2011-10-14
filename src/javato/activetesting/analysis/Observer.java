package javato.activetesting.analysis;

import javato.activetesting.common.Parameters;
import javato.activetesting.common.WeakIdentityHashMap;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Observer {

    private static WeakIdentityHashMap objectMap = new WeakIdentityHashMap(3511);
    private static HashMap<Object, String> threadMap = new HashMap<Object, String>();
    private static int currentId = readInteger(Parameters.usedObjectId, 1);
    private static ArrayList<String> iidToLineMap = null;

    public static Long idInt(int f, int s) {
        long l = f;
        l = l << 32;
        l += s;
        return l;
    }

    public static String getIidToLine(Integer iid) {
        ObjectInputStream in;
        if (iidToLineMap != null) {
            return iidToLineMap.get(iid).replaceAll(".html#", "#");
        } else {
            try {
	        File f = new File(Parameters.iidToLineMapFile);
                in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(f.getAbsolutePath())));
                iidToLineMap = (ArrayList<String>) in.readObject();
                in.close();
                return iidToLineMap.get(iid).replaceAll(".html#", "#");
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    synchronized public static Integer uniqueId(Object o) {
        Object val = objectMap.get(o);
        if (val == null) {
            val = currentId++;
            objectMap.put(o, val);
        }
        return (Integer) val;
    }

    synchronized public static String uniqueThreadId(Object o, boolean isParent, String parentId, int iid) {
    	String val = threadMap.get(o);
    	if (isParent) {
    		if (val == null) {
    			val = "main";
    			threadMap.put(o, val);
    		}
    	} else {
    		if (val == null) {
    			String line = getIidToLine(iid);
    			String[] lineNum = line.split("#");
    			String childId = parentId + "%" + lineNum[1];
    			String baseId = childId;
    			int count = 1;
    			while (threadMap.containsValue(childId)) {
    				childId = baseId;
    				childId += ("[" + count + "]");
    				count++;
    			}
    			threadMap.put(o, childId);
    			val = childId;
    		} 
    	}
    	return val;
    }
    
    synchronized public static Object idToObject(int id) {
        for (Object ret : objectMap.keySet()) {
            Integer val = (Integer) objectMap.get(ret);
            if (val != null) {
                if (val == id)
                    return ret;
            }
        }
        return "Unknown Object";
    }
    
    public static Long id(Object o, int x) {
        return idInt(uniqueId(o), x);
    }

    static public int readInteger(String filename, int defaultVal) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
            int ret = Integer.parseInt(in.readLine());
            in.close();
            return ret;
        } catch (Exception e) {
        }
        return 0;
    }

    public static void writeIntegerList(String file, int val) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(file));
            for (int i = 1; i < val; i++) {
                pw.print(i + ",");
            }
            if (val > 0)
                pw.println(val);
            else
                pw.println();
            pw.close();
        } catch (IOException e) {
            System.err.println("Error while writing to " + file);
            System.exit(1);
        }

    }
}
