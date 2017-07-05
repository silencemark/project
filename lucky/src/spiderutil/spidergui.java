package spiderutil;

import java.awt.*;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

class node {
	private Object data;
	private node next;
	private node prev;

	public node(Object o) {
		data = o;
		prev = next = null;
	}

	public String toString() {
		if (next != null)
			return data.toString() + "\n" + next.toString();
		return data.toString();
	}

	public node getNext() {
		return next;
	}

	public void setNext(node n) {
		next = n;
	}

	public node getPrev() {
		return prev;
	}

	public void setPrev(node n) {
		prev = n;
	}

	public Object getData() {
		return data;
	}
}

class linkedlist {
	node head;
	node tail;

	public linkedlist() {
		tail = head = null;
	}

	public String toString() {
		if (head == null)
			return "Empty list";
		return head.toString();
	}

	public void insert(Object o) {
		if (tail == null) {
			head = tail = new node(o);
		} else {
			node nn = new node(o);
			tail.setNext(nn);
			tail = nn;
		}
	}

	public boolean contains(Object o) {
		for (node n = head; n != null; n = n.getNext()) {
			if (o.equals(n.getData()))
				return true;
		}
		return false;
	}

	public Object pop() {
		if (head == null)
			return null;
		Object ret = head.getData();
		head = head.getNext();
		if (head == null)
			tail = null;
		return ret;
	}

	public boolean isEmpty() {
		return head == null;
	}
}

class list {
	protected node tail;
	protected node ptr;
	private boolean stop;

	public list() {
		ptr = tail = null;
		stop = false;
	}

	public boolean isEmpty() {
		return tail == null;
	}

	public void reset() {
		stop = false;
		ptr = tail;
	}

	public String toString() {
		if (tail == null)
			return "Empty list";
		String ret = "";
		for (node n = tail.getNext(); n != tail; n = n.getNext())
			ret += n.getData().toString() + "\n";
		ret += tail.getData().toString();
		return ret;
	}

	public Object get() {
		if (ptr == null)
			return null;
		ptr = ptr.getNext();
		if (ptr == tail.getNext()) {
			if (stop)
				return null;
			stop = true;
			return tail.getNext().getData();
		}
		return ptr.getData();
	}

	public void insert(Object o, boolean attail) {
		node nn = new node(o);
		if (tail == null) {
			nn.setNext(nn);
			nn.setPrev(nn);
			ptr = tail = nn;
			return;
		}
		if (attail) {
			tail.getNext().setPrev(nn);
			nn.setNext(tail.getNext());
			tail.setNext(nn);
			nn.setPrev(tail);
			tail = nn;
		} else {
			nn.setNext(tail.getNext());
			nn.setPrev(tail);
			tail.setNext(nn);
			nn.getNext().setPrev(nn);
		}
	}

	public void insert(Object o) {
	}
}

class stack extends list {
	public stack() {
		super();
	}

	public void insert(Object o) {
		insert(o, false);
	}
}

class queue extends list {
	public queue() {
		super();
	}

	public void insert(Object o) {
		insert(o, true);
	}

	public String peek() {
		if (tail == null)
			return "";
		return tail.getNext().getData().toString();
	}

	public Object pop() {
		if (tail == null)
			return null;
		Object ret = tail.getNext().getData();
		if (tail.getNext() == tail) {
			tail = ptr = null;
		} else {
			if (tail.getNext() == ptr)
				ptr = ptr.getNext();
			tail.setNext(tail.getNext().getNext());
		}
		return ret;
	}
}

class hashtable {
	private Vector table;
	private int size;

	public hashtable() {
		size = 991;
		table = new Vector();
		for (int i = 0; i < size; i++) {
			table.add(new linkedlist());
		}
	}

	public void insert(Object o) {
		int index = o.hashCode();
		index = index % size;
		if (index < 0)
			index += size;
		linkedlist ol = (linkedlist) table.get(index);
		ol.insert(o);
	}

	public boolean contains(Object o) {
		int index = o.hashCode();
		index = index % size;
		if (index < 0)
			index += size;
		return ((linkedlist) (table.get(index))).contains(o);
	}

	public String toString() {
		String ret = "";
		for (int i = 0; i < size; i++) {
			if (!((linkedlist) (table.get(i))).isEmpty()) {
				ret += "\n";
				ret += table.get(i).toString();
			}
		}
		return ret;
	}
}

class spider implements Runnable {
	public queue todo;
	public stack done;
	public stack errors;
	public stack omittions;
	private hashtable allsites;
	private String last = "";
	int maxsites;
	int visitedsites;
	int TIMEOUT;
	String base;
	String[] badEndings2 = { "ps", "gz" };
	String[] badEndings3 = { "pdf", "txt", "zip", "jpg", "mpg", "gif", "mov",
			"tut", "req", "abs", "swf", "tex", "dvi", "bin", "exe", "rpm" };
	String[] badEndings4 = { "jpeg", "mpeg" };

	public spider(String starturl, int max, String b) {
		TIMEOUT = 5000;
		base = b;
		allsites = new hashtable();
		todo = new queue();
		done = new stack();
		errors = new stack();
		omittions = new stack();
		try {
			URL u = new URL(starturl);
			todo.insert(u);
		} catch (Exception e) {
			System.out.println(e);
			errors.insert("bad starting url " + starturl + "," + e.toString());
		}
		maxsites = max;
		visitedsites = 0;
	}

	/*
	 * how many millisec to wait for each page
	 */
	public void setTimer(int amount) {
		TIMEOUT = amount;
	}

	/*
	 * strips the '#' anchor off a url
	 */
	private URL stripRef(URL u) {
		try {
			return new URL(u.getProtocol(), u.getHost(), u.getPort(), u
					.getFile());
		} catch (Exception e) {
			return u;
		}
	}

	/*
	 * adds a url for future processing
	 */
	public void addSite(URL toadd) {
		if (null != toadd.getRef())
			toadd = stripRef(toadd);
		if (!allsites.contains(toadd)) {
			allsites.insert(toadd);
			if (!toadd.toString().startsWith(base)) {
				omittions.insert("foreign URL: " + toadd.toString());
				return;
			}
			if (!toadd.toString().startsWith("http")
					&& !toadd.toString().startsWith("HTTP")) {
				omittions.insert("ignoring URL: " + toadd.toString());
				return;
			}

			String s = toadd.getFile();
			String last = "";
			String[] comp = {};
			if (s.charAt(s.length() - 3) == '.') {
				last = s.substring(s.length() - 2);
				comp = badEndings2;
			} else if (s.charAt(s.length() - 4) == '.') {
				last = s.substring(s.length() - 3);
				comp = badEndings3;
			} else if (s.charAt(s.length() - 5) == '.') {
				last = s.substring(s.length() - 4);
				comp = badEndings4;
			}
			for (int i = 0; i < comp.length; i++) {
				if (last.equalsIgnoreCase(comp[i])) {// loop through all bad
														// extensions
					omittions.insert("ignoring URL:" + toadd.toString());
					return;
				}
			}

			todo.insert(toadd);
		}
	}

	/*
	 * true if there are pending urls and the maximum hasn't beenreached
	 */
	public boolean hasMore() {
		return !todo.isEmpty() && visitedsites < maxsites;
	}

	/*
	 * returns the next site, works like enumeration, will return newvalues each
	 * time
	 */
	private URL getNextSite() {
		last = todo.peek();
		visitedsites++;
		return (URL) todo.pop();
	}

	/*
	 * Just to see what we are doing now...
	 */
	public String getCurrent() {
		return last;
	}

	/*
	 * process the next site
	 */
	public void doNextSite() {
		URL current = getNextSite();
		if (current == null)
			return;
		try {
			// System.err.println("Processing #"+visitedsites+":"+current);
			parse(current);
			done.insert(current);
		} catch (Exception e) {
			errors.insert("Bad site: " + current.toString() + ","
					+ e.toString());
		}
	}

	public void run() {
		while (hasMore())
			doNextSite();
	}

	/*
	 * to print out the internal data structures
	 */
	public String toString() {
		return getCompleted() + getErrors();
	}

	private String getErrors() {
		if (errors.isEmpty())
			return "No errors\n";
		else
			return "Errors:\n" + errors.toString() + "\nEnd oferrors\n";
	}

	private String getCompleted() {
		return "Completed Sites:\n" + done.toString()
				+ "\nEnd of completedsites\n";
	}

	/*
	 * Parses a web page at (site) and adds all the urls it sees
	 */
	private void parse(URL site) throws Exception {
		String source = getText(site);
		String title = getTitle(source);
		if (title.indexOf("404") != -1 || title.indexOf("Error") != -1
				|| title.indexOf("Not Found") != -1) {
			throw new Exception(("404, Not Found: " + site));
		}
		int loc, beg;
		boolean hasLT = false;
		boolean hasSp = false;
		boolean hasF = false;
		boolean hasR = false;
		boolean hasA = false;
		boolean hasM = false;
		boolean hasE = false;
		for (loc = 0; loc < source.length(); loc++) {
			char c = source.charAt(loc);
			if (!hasLT) {
				hasLT = (c == '<');
			}

			// search for "<a "
			else if (hasLT && !hasA && !hasF) {
				if (c == 'a' || c == 'A')
					hasA = true;
				else if (c == 'f' || c == 'F')
					hasF = true;
				else
					hasLT = false;
			} else if (hasLT && hasA && !hasF && !hasSp) {
				if (c == ' ' || c == '\t' || c == '\n')
					hasSp = true;
				else
					hasLT = hasA = false;
			}

			// search for "<frame "
			else if (hasLT && hasF && !hasA && !hasR) {
				if (c == 'r' || c == 'R')
					hasR = true;
				else
					hasLT = hasF = false;
			} else if (hasLT && hasF && hasR && !hasA) {
				if (c == 'a' || c == 'A')
					hasA = true;
				else
					hasLT = hasF = hasR = false;
			} else if (hasLT && hasF && hasR && hasA && !hasM) {
				if (c == 'm' || c == 'M')
					hasM = true;
				else
					hasLT = hasF = hasR = hasA = false;
			} else if (hasLT && hasF && hasR && hasA && hasM && !hasE) {
				if (c == 'e' || c == 'E')
					hasE = true;
				else
					hasLT = hasF = hasR = hasA = hasM = false;
			} else if (hasLT && hasF && hasR && hasA && hasM && hasE && !hasSp) {
				if (c == ' ' || c == '\t' || c == '\n')
					hasSp = true;
				else
					hasLT = hasF = hasR = hasA = hasM = hasE = false;
			}

			// found "<frame "
			else if (hasLT && hasF && hasR && hasA && hasM && hasE && hasSp) {
				hasLT = hasF = hasR = hasA = hasM = hasE = hasSp = false;
				beg = loc;
				loc = source.indexOf(">", loc);
				if (loc == -1) {
					errors.insert("malformed frame at" + site.toString());
					loc = beg;
				} else {
					try {
						parseFrame(site, source.substring(beg, loc));
					} catch (Exception e) {
						errors.insert("while parsing " + site.toString()
								+ ",error parsing frame: " + e.toString());
					}
				}
			}

			// found "<a "
			else if (hasLT && hasA && hasSp && !hasF) {
				hasLT = hasA = hasSp = false;
				beg = loc;
				loc = source.indexOf(">", loc);
				if (loc == -1) {
					errors.insert("malformed linked at" + site.toString());
					loc = beg;
				} else {
					try {
						parseLink(site, source.substring(beg, loc));
					} catch (Exception e) {
						errors.insert("while parsing " + site.toString()
								+ ",error parsing link: " + e.toString());
					}
				}
			}
		}
	}

	/*
	 * parses a frame
	 */
	private void parseFrame(URL at_page, String s) throws Exception {
		int beg = s.indexOf("src");
		if (beg == -1)
			beg = s.indexOf("SRC");
		if (beg == -1)
			return;// doesn't have a src, ignore
		beg = s.indexOf("=", beg);
		if (beg == -1)
			throw new Exception("while parsing" + at_page.toString()
					+ ", bad frame, missing \'=\' after src:" + s);
		int start = beg;
		for (; beg < s.length(); beg++) {
			if (s.charAt(beg) == '\'')
				break;
			if (s.charAt(beg) == '\"')
				break;
		}
		int end = beg + 1;
		for (; end < s.length(); end++) {
			if (s.charAt(beg) == s.charAt(end))
				break;
		}
		beg++;
		if (beg >= end) {// missing quotes... just take the first token
							// after"src="
			for (beg = start + 1; beg < s.length() && (s.charAt(beg) == ' '); beg++) {
			}
			for (end = beg + 1; end < s.length() && (s.charAt(beg) != ' ')
					&& (s.charAt(beg) != '>'); end++) {
			}
		}

		if (beg >= end) {
			errors.insert("while parsing " + at_page.toString()
					+ ",bad frame: " + s);
			return;
		}

		String linkto = s.substring(beg, end);
		if (linkto.startsWith("mailto:") || linkto.startsWith("Mailto:"))
			return;
		if (linkto.startsWith("javascript:")
				|| linkto.startsWith("Javascript:"))
			return;
		if (linkto.startsWith("news:") || linkto.startsWith("Javascript:"))
			return;
		try {
			addSite(new URL(at_page, linkto));
			return;
		} catch (Exception e1) {
		}
		try {
			addSite(new URL(linkto));
			return;
		} catch (Exception e2) {
		}
		try {
			URL cp = new URL(at_page.toString() + "/index.html");
			System.out.println("attemping to use " + cp);
			addSite(new URL(cp, linkto));
			return;
		} catch (Exception e3) {
		}
		errors.insert("while parsing " + at_page.toString() + ", bad frame:"
				+ linkto + ", formed from: " + s);
	}

	/*
	 * given a link at a URL, will parse it and add it to the list ofsites to do
	 */
	private void parseLink(URL at_page, String s) throws Exception {
		// System.out.println("parsing link "+s);
		int beg = s.indexOf("href");
		if (beg == -1)
			beg = s.indexOf("HREF");
		if (beg == -1)
			return;// doesn't have a href, must be an anchor
		beg = s.indexOf("=", beg);
		if (beg == -1)
			throw new Exception("while parsing" + at_page.toString()
					+ ", bad link, missing \'=\' after href:" + s);
		int start = beg;
		for (; beg < s.length(); beg++) {
			if (s.charAt(beg) == '\'')
				break;
			if (s.charAt(beg) == '\"')
				break;
		}
		int end = beg + 1;
		for (; end < s.length(); end++) {
			if (s.charAt(beg) == s.charAt(end))
				break;
		}
		beg++;
		if (beg >= end) {// missing quotes... just take the first token
							// after"href="
			for (beg = start + 1; beg < s.length() && (s.charAt(beg) == ' '); beg++) {
			}
			for (end = beg + 1; end < s.length() && (s.charAt(beg) != ' ')
					&& (s.charAt(beg) != '>'); end++) {
			}
		}

		if (beg >= end) {
			errors.insert("while parsing" + at_page.toString() + ", bad href: "
					+ s);
			return;
		}

		String linkto = s.substring(beg, end);
		if (linkto.startsWith("mailto:") || linkto.startsWith("Mailto:"))
			return;
		if (linkto.startsWith("javascript:")
				|| linkto.startsWith("Javascript:"))
			return;
		if (linkto.startsWith("news:") || linkto.startsWith("Javascript:"))
			return;

		try {
			addSite(new URL(at_page, linkto));
			return;
		} catch (Exception e1) {
		}
		try {
			addSite(new URL(linkto));
			return;
		} catch (Exception e2) {
		}
		try {
			addSite(new URL(new URL(at_page.toString() + "/index.html"), linkto));
			return;
		} catch (Exception e3) {
		}
		errors.insert("while parsing " + at_page.toString() + ", bad link:"
				+ linkto + ", formed from: " + s);
	}

	/*
	 * gets the title of a web page with content s
	 */
	private String getTitle(String s) {
		try {
			int beg = s.indexOf("<title>");
			if (beg == -1)
				beg = s.indexOf("<TITLE>");
			int end = s.indexOf("</title>");
			if (end == -1)
				end = s.indexOf("</TITLE>");
			return s.substring(beg, end);
		} catch (Exception e) {
			return "";
		}
	}

	/*
	 * gets the text of a web page, times out after 10s
	 */
	private String getText(URL site) throws Exception {
		urlReader u = new urlReader(site);
		Thread t = new Thread(u);
		t.setDaemon(true);
		t.start();
		t.join(TIMEOUT);
		String ret = u.poll();
		if (ret == null) {
			throw new Exception("connection timed out");
		} else if (ret.equals("Not html")) {
			throw new Exception("Not an HTML document");
		}
		return ret;
	}

	/*
	 * returns how many sites have been visited so far
	 */
	public int Visited() {
		return visitedsites;
	}
}

class urlReader implements Runnable {
	URL site;
	String s;

	public urlReader(URL u) {
		site = u;
		s = null;
	}

	public void run() {
		try {
			String ret = new String();
			URLConnection u = site.openConnection();
			String type = u.getContentType();
			if (type.indexOf("text") == -1 && type.indexOf("txt") == -1
					&& type.indexOf("HTM") == -1 && type.indexOf("htm") == -1) {
				// System.err.println("bad content type "+type+" at site"+site);
				System.out.println("bad content type " + type + " at site"
						+ site);
				ret = "Not html";
				return;
			}
			InputStream in = u.getInputStream();
			BufferedInputStream bufIn = new BufferedInputStream(in);
			int data;
			while (true) {
				data = bufIn.read();
				// Check for EOF
				if (data == -1)
					break;
				else
					ret += ((char) data);
			}
			s = ret;
		} catch (Exception e) {
			s = null;
		}
	}

	public String poll() {
		return s;
	}
}

public class spidergui extends Frame {

	private spider s;
	private Color txtColor;
	private Color errColor;
	private Color topColor;
	private Color numColor;
	private Color curColor;

	public spidergui(spider spi, String title) {
		super(title);
		curColor = new Color(40, 40, 200);
		txtColor = new Color(0, 0, 0);
		errColor = new Color(255, 0, 0);
		topColor = new Color(40, 40, 100);
		numColor = new Color(50, 150, 50);
		s = spi;
		setBounds(0, 0, 800, 600);
		show();
		toFront();
		repaint();
	}

	public void endShow() {
		System.out.println(s);
		hide();
		dispose();
	}

	public void paint(Graphics g) {
		super.paint(g);
		s.todo.reset();
		s.done.reset();
		s.errors.reset();
		s.omittions.reset();
		String txt;
		Object o;
		g.setColor(curColor);
		g.setFont(new Font("arial", Font.PLAIN, 18));
		String cur = s.getCurrent();
		if (cur.length() > 80)
			g.drawString(cur.substring(0, 40) + " . . . "
					+ cur.substring(cur.length() - 30, cur.length()), 50, 50);
		else
			g.drawString(cur, 50, 50);

		g.setColor(numColor);
		g.setFont(new Font("arial", Font.BOLD, 24));
		g.drawString(Integer.toString(s.Visited()), 350, 80);

		g.setFont(new Font("arial", Font.PLAIN, 14));
		g.setColor(topColor);
		g.drawString("To Do:", 100, 80);
		g.drawString("Completed:", 500, 80);
		g.drawString("Ignored:", 500, 250);
		g.drawString("Errors:", 100, 420);

		g.setColor(txtColor);
		g.setFont(new Font("arial", Font.PLAIN, 12));
		for (int i = 0; i < 23 && (o = s.todo.get()) != null; i++) {
			txt = Integer.toString(i + 1) + ": " + o.toString();
			if (txt.length() > 65)
				g.drawString(txt.substring(0, 38) + " . . . "
						+ txt.substring(txt.length() - 18, txt.length()), 20,
						100 + 13 * i);
			else
				g.drawString(txt, 20, 100 + 13 * i);
		}
		for (int i = 0; i < 10 && (o = s.done.get()) != null; i++) {
			txt = Integer.toString(i + 1) + ": " + o.toString();
			if (txt.length() > 60)
				g.drawString(txt.substring(0, 57) + "...", 400, 100 + 13 * i);
			else
				g.drawString(txt, 400, 100 + 13 * i);
		}
		for (int i = 0; i < 10 && (o = s.omittions.get()) != null; i++) {
			txt = Integer.toString(i + 1) + ": " + o.toString();
			if (txt.length() > 60)
				g.drawString(txt.substring(0, 57) + "...", 400, 270 + 13 * i);
			else
				g.drawString(txt, 400, 270 + 13 * i);
		}
		g.setColor(errColor);
		for (int i = 0; i < 10 && (o = s.errors.get()) != null; i++) {
			txt = Integer.toString(i + 1) + ": " + o.toString();
			g.drawString(txt, 20, 440 + 13 * i);
		}

	}

	public void run() {
		repaint();
		while (s.hasMore()) {
			repaint();
			s.doNextSite();
		}
		repaint();
	}

	public static void main(String[] args) {
		int max = 5;
		String site = "";
		String base = "";
		int time = 0;
		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("-max=")) {
				max = Integer.parseInt(args[i].substring(5, args[i].length()));
			} else if (args[i].startsWith("-time=")) {
				time = Integer.parseInt(args[i].substring(6, args[i].length()));
			} else if (args[i].startsWith("-init=")) {
				site = args[i].substring(6, args[i].length());
			} else if (args[i].startsWith("-base=")) {
				base = args[i].substring(6, args[i].length());
			} else if (args[i].startsWith("-help") || args[i].startsWith("-?")) {
				System.out.println("additional command line switches:");
				System.out
						.println("-max=N     : to limit to N sites,default 5");
				System.out
						.println("-init=URL   : to set the initial site,REQUIRED");
				System.out
						.println("-base=URL   : only follow url's that startwith this");
				System.out.println("        default \"\" (matches all URLs)");
				System.out
						.println("-time=N   : how many millisec to wait foreach page");
				System.out.println("        default 5000 (5 seconds)");
				System.exit(0);
			} else
				System.err.println("unrecognized switch:" + args[i]
						+ ", continuing");
		}
		if (site == "") {
			System.err.println("No initial site parameter!");
			System.err
					.println("Use -init=<site> switch to set, or-help for more info.");
			System.exit(1);
		}

		spider spi = new spider(site, max, base);

		if (time > 0)
			spi.setTimer(time);

		spidergui s = new spidergui(spi, "Spider: " + site);
		s.run();
		System.out.println(spi);
	}
}
