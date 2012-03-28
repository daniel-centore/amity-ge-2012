package game;
import java.util.*;
import java.io.*;
	
public class Params {
	public HashMap<String, String> map = new HashMap<String, String>();
	public Params(String fname)
	{
		try {
			Scanner input = new Scanner(new File(fname));
			String line;
			while (input.hasNext()) {
				line = input.nextLine();
				if (line.equals("")) {
					break;
				}
				StringTokenizer toks = new StringTokenizer(line, "=");
				String key = toks.nextToken();
				String val = toks.nextToken();
				map.put(key, val);
			}
			input.close();
		}
		catch (Exception e) {
			System.err.printf("Problem reading %s%n", fname);
		}
	}
	public String string(String key)
	{ return map.get(key); }
	public int integer(String key)
	{ return Integer.parseInt(string(key)); }
	public char character(String key)
	{ return string(key).charAt(0); }
}
