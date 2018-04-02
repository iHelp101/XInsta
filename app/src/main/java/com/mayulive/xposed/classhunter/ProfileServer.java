package com.mayulive.xposed.classhunter;


import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.mayulive.xposed.classhunter.packagetree.PackageTree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * Super duper simple web interface for profile parsing
 * Only supports GET requests, and the entire page is stored as a string (no loading files).
 */
public class ProfileServer implements Runnable
{


	private ClassLoader mClassLoader = null;
	private PackageTree mTree = null;
	private RequestParams mParams = new RequestParams();

	private static final String TAG = ClassHunter.getLogTag( ProfileServer.class );

	private final int mPort;

	private boolean mIsRunning;

	/**
	   The {@link ServerSocket} that we listen to.
	 */
	private ServerSocket mServerSocket;

	/**
	 *
	 * @param port	Port the server should run on
	 * @param loader The ClassLoader
	 * @param tree A populated PackageTree, necessary if package or classnames contain $
	 */
	public ProfileServer(int port, ClassLoader loader, @Nullable PackageTree tree)
	{
		mPort = port;
		mClassLoader = loader;
		mTree = tree;
	}

	public void start()
	{
		mIsRunning = true;
		new Thread(this).start();
	}

	public void stop()
	{
		try
		{
			mIsRunning = false;
			if (null != mServerSocket)
			{
				mServerSocket.close();
				mServerSocket = null;
			}
		}
		catch (IOException e)
		{
			Log.e(TAG, "Error closing the server socket.", e);
		}
	}

	public int getPort()
	{
		return mPort;
	}

	@Override
	public void run()
	{
		try
		{
			mServerSocket = new ServerSocket(mPort);
			while (mIsRunning)
			{
				Socket socket = mServerSocket.accept();
				handle(socket);
				socket.close();
			}
		}
		catch (SocketException e)
		{
			// The server was stopped; ignore.
		}
		catch (IOException e)
		{
			Log.e(TAG, "Web server error.", e);
		}
	}


	/**
	*	Stores the parameters passed in the GET request
	*/
	private class RequestParams
	{
		public  String fullPath = "";
		public  String knownPath = "";

		public int depth = 0;

		public String[] includePaths = new String[0];
		public Map<String,String> obfuscatedPaths = new HashMap<String,String>();

		public boolean includePublic = false;


		/**
		 * Clear all values
		 */
		public void reset()
		{
			fullPath = "";
			knownPath = "";

			includePaths = new String[0];
			obfuscatedPaths.clear();

			includePublic = false;
		}

		public void print()
		{
			Log.i(TAG, "Full path: "+fullPath);
			Log.i(TAG, "Known Path: "+knownPath);

			Log.i(TAG, "include path:		"+ Arrays.toString(includePaths));
			Log.i(TAG, "obfuscate path:		");
			for (Map.Entry<String,String> entry : obfuscatedPaths.entrySet())
			{
				Log.i(TAG, "Path: "+entry.getKey()+"	map: "+entry.getValue());
			}

			Log.i(TAG, "Include Public: "+includePublic);
		}
	}

	//Find [?&]key=value param pairs.
	private static final Pattern paramRegex = Pattern.compile("[?&](.[^=?&]+?)=([^&\\s]+)");

	/**
	 * Set a single key=value pair from the get request
	 * @param key	Param pair key
	 * @param value Param pair value
	 */
	private void setRequestParam(String key, String value)
	{
		if (key.equalsIgnoreCase("fullpath"))
		{
			mParams.fullPath = value;
		}
		else if (key.equalsIgnoreCase("knownpath"))
		{
			mParams.knownPath = value;
		}
		else if (key.equalsIgnoreCase("knownlist"))
		{
			mParams.includePaths = value.split(",");
		}
		else if (key.equalsIgnoreCase("obslist"))
		{

			String[] obfsucatedPathDepthPairs = value.split("\\|");
			for (int i = 0; i < obfsucatedPathDepthPairs.length; i+=2)
			{
				if (i+1 >= obfsucatedPathDepthPairs.length)
				{
					//That's not right ...
					break;
				}

				String obfusKey = obfsucatedPathDepthPairs[i];
				String unobfuscatedPath;
				int depth;

				try { depth = Integer.parseInt(obfsucatedPathDepthPairs[i+1]); }
				catch (Exception ex) { continue; }

				unobfuscatedPath = PackageTree.getSegmentedPath(obfusKey, depth);

				mParams.obfuscatedPaths.put(obfusKey, unobfuscatedPath);

			}
		}
		else if (key.equalsIgnoreCase("incpub"))
		{
			mParams.includePublic = value.equals("true");
		}
	}

	/**
	 * Parse a get request of key=value pairs
	 * @param line The get request, starting with GET /
	 */
	private void parseRequest(String line)
	{
		if (line.startsWith("GET /"))
		{
			Matcher matcher = paramRegex.matcher(line);

			while (matcher.find())
			{
				setRequestParam(matcher.group(1), matcher.group(2));
			}
		}
	}


	/**
	 * Respond to a request from a client.
	 *
	 * @param socket The client socket.
	 * @throws IOException
	 */
	private void handle(Socket socket) throws IOException
	{
		BufferedReader reader = null;
		PrintStream output = null;
		try
		{

			mParams.reset();
			// Read HTTP headers and parse out the route.
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String line;
			while (!TextUtils.isEmpty(line = reader.readLine()))
			{
				if (ClassHunter.DEBUG_SERVER)
					Log.i(TAG, "Server: "+line);

				parseRequest(line);
			}

			// Output stream that we send the response to
			output = new PrintStream(socket.getOutputStream());

			byte[] bytes = loadContent();
			if (null == bytes)
			{
				writeServerError(output);
				return;
			}

			if (ClassHunter.DEBUG_SERVER)
				mParams.print();

			// Send out the content.
			output.println("HTTP/1.0 200 OK");
			output.println("Content-Type: " + "text/html"); //detectMimeType(route));
			output.println("Content-Length: " + bytes.length);
			output.println();
			output.write(bytes);
			output.flush();
		}
		finally
		{
			if (null != output)
			{
				output.close();
			}
			if (null != reader)
			{
				reader.close();
			}
		}
	}

	/**
	 * Writes a server error response (HTTP/1.0 500) to the given output stream.
	 * @param output The output stream.
	 */
	private void writeServerError(PrintStream output)
	{
		output.println("HTTP/1.0 500 Internal Server Error");
		output.flush();
	}

	/**
	 * Uses the populated mParams to load and parse a class
	 * @return	The entire content of the web inteface, and the output profile.
	 * @throws IOException
	 */
	private byte[] loadContent() throws IOException
	{

		Class targetClass = ClassHunter.loadClass(mParams.fullPath, mClassLoader);
		String profileString = "";
		ProfileParser parser = new ProfileParser();
		if (targetClass != null)
		{
			parser.setPaths(mParams.includePaths, mParams.obfuscatedPaths);

			//The user may have input a path formatted differently (e.g. canonical name instead of name, period delimited instead of $)
			//from that used by the dexloader (name). The known path is a portion of the full path, so we figure out at what section it was
			//split by the user, and apply the same depth to the value we get from getName().
			int knownSimpleDepth = PackageTree.getSimpleSegmentCount( mParams.knownPath );
			String knownPath =  PackageTree.getSegmentedPath( targetClass.getName(), knownSimpleDepth );

			//Ideally we want to have a populated PackageTree so we can get the correct depth for classnames and packages
			//that have $ in their names. Usually won't be necessary though.
			if (mTree == null)
			{
				profileString = parser.getClassProfile( targetClass, "newProfile", knownPath,  mParams.includePublic );
			}
			else
			{
				profileString = parser.getClassProfile( targetClass, "newProfile", knownPath,  mTree.getDepth(knownPath, targetClass.getName()),  mParams.includePublic );
			}
		}
		else
		{
			profileString = "No such class";
		}

		//Interface is less pretty if
		StringBuilder builder = new StringBuilder();
		builder.append("<html>\n" +
				"\t<head>\n" +
				"\t\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/> \n" +
				"\n" +
				"\t</head>\n" +
				"\t<style>\n" +
				"\t\t.btn\n" +
				"\t\t{\n" +
				"\t\t    display: inline-block;\n" +
				"\t\t    padding: 6px 12px;\n" +
				"\t\t    margin-bottom: 0;\n" +
				"\t\t    font-size: 15px;\n" +
				"\t\t    font-weight: 400;\n" +
				"\t\t    line-height: 1.42857143;\n" +
				"\t\t    text-align: center;\n" +
				"\t\t    white-space: nowrap;\n" +
				"\t\t    vertical-align: middle;\n" +
				"\t\t    -ms-touch-action: manipulation;\n" +
				"\t\t    touch-action: manipulation;\n" +
				"\t\t    cursor: pointer;\n" +
				"\t\t    -webkit-user-select: none;\n" +
				"\t\t    -moz-user-select: none;\n" +
				"\t\t    -ms-user-select: none;\n" +
				"\t\t    user-select: none;\n" +
				"\t\t    background-image: none;\n" +
				"\t\t    border: 1px solid transparent;\n" +
				"\t\t    border-radius: 4px;\n" +
				"\t\t}\n" +
				"\t\t\n" +
				"\t\t.btn-primary\n" +
				"\t\t{\n" +
				"\t\t\t\n" +
				"\t\t\tcolor: #fff;\n" +
				"\t\t\tbackground-color: #337ab7;\n" +
				"\t\t\tborder-color: #2e6da4;\n" +
				"\t\t}\n" +
				"\t\t.btn-primary:hover\n" +
				"\t\t{\n" +
				"\t\t\tbackground-color: #246196;\n" +
				"\t\t\tborder-color: #163754;\n" +
				"\t\t}\n" +
				"\t\t\n" +
				"\t\t.btn-success\n" +
				"\t\t{\n" +
				"\t\t    color: black;\n" +
				"\t\t    background-color: #5cb85c;\n" +
				"\t\t    border-color: #4cae4c;\n" +
				"\t\t}\n" +
				"\t\t.btn-success:hover\n" +
				"\t\t{\t\t\n" +
				"\t\t\tcolor: white;\n" +
				"\t\t\tbackground-color: #489148;\n" +
				"\t\t\tborder-color: #387538;\n" +
				"\t\t}\n" +
				"\t\t\n" +
				"\t\t.btn-warning \n" +
				"\t\t{\n" +
				"\t\t    color: black;\n" +
				"\t\t    background-color: #f0ad4e;\n" +
				"\t\t    border-color: #eea236;\n" +
				"\t\t}\n" +
				"\t\t.btn-warning:hover\n" +
				"\t\t{\n" +
				"\t\t   color: white;\n" +
				"\t\t   background-color: #c98d38;\n" +
				"\t\t   border-color: #664311;\n" +
				"\t\t}\n" +
				"\t\n" +
				"\t\n" +
				"\t\n" +
				"\t\t.form-control\n" +
				"\t\t{\n" +
				"\t\t\tdisplay: block;\n" +
				"\t\t\twidth: 100%;\n" +
				"\t\t\theight: 34px;\n" +
				"\t\t\tpadding: 6px 12px;\n" +
				"\t\t\tfont-size: 14px;\n" +
				"\t\t\tline-height: 1.42857143;\n" +
				"\t\t\tcolor: #555;\n" +
				"\t\t\tbackground-color: #fff;\n" +
				"\t\t\tborder: 1px solid #ccc;\n" +
				"\t\t\tborder-radius: 4px;\n" +
				"\t\t\t-webkit-box-shadow: inset 0 1px 1px rgba(0,0,0,.075);\n" +
				"\t\t\tbox-shadow: inset 0 1px 1px rgba(0,0,0,.075);\n" +
				"\t\t}\n" +
				"\t\t\n" +
				"\t\t.body\n" +
				"\t\t{\n" +
				"\t\t\tfont-family: \"Helvetica Neue\",Helvetica,Arial,sans-serif;\n" +
				"\t\t\tfont-size: 14px;\n" +
				"\t\t\tline-height: 1.42857143;\n" +
				"\t\t\tcolor: #333;\n" +
				"\t\t\t\n" +
				"\t\t}\n" +
				"\t\n" +
				"\n" +
				"\t\t.horizontalWrapper\n" +
				"\t\t{\n" +
				"\t\t\tdisplay: flex;\n" +
				"\t\t}\n" +
				"\t\t\n" +
				"\t\t.pathListItem\n" +
				"\t\t{\n" +
				"\t\t\tdisplay:flex;\n" +
				"\t\t\tposition:relative;\n" +
				"\t\t\tmin-width:540px;\n" +
				"\t\t\t\n" +
				"\t\t}\n" +
				"\t\t\n" +
				"\t\t.pathBox\n" +
				"\t\t{\n" +
				"\t\t\tpadding-left:5px;\n" +
				"\t\t\tpadding-right:5px;\n" +
				"\t\t\tcolor:black;\n" +
				"\t\t}\n" +
				"\t\t\n" +
				"\t\n" +
				"\t\t.pathBoxContainer\n" +
				"\t\t{\n" +
				"\t\t\tdisplay:flex;\n" +
				"\t\t\tmargin-right:50px;\n" +
				"\t\t}\n" +
				"\t\t\n" +
				"\t\t.pathBoxMoveLeft\n" +
				"\t\t{\n" +
				"\t\t\tmargin-right:20px;\n" +
				"\t\t\tfloat:left;\n" +
				"\t\t}\n" +
				"\t\t\n" +
				"\t\t.pathBoxMoveRight\n" +
				"\t\t{\t\n" +
				"\t\t\tposition:absolute;\n" +
				"\t\t\tright:0;\n" +
				"\t\t}\n" +
				"\t\t\n" +
				"\t\t.genericWrapper\n" +
				"\t\t{\n" +
				"\t\t\tpadding:20px;\n" +
				"\t\t\tborder:20px;\n" +
				"\t\t\tborder-radius:5px;\n" +
				"\t\t\tbackground:#303030;\t\n" +
				"\t\t\tmargin:5px;\t\n" +
				"\t\t\tmin-width:500px;\n" +
				"\t\t}\n" +
				"\t\t.profileOutput\n" +
				"\t\t{\n" +
				"\t\t\twidth:100%;\n" +
				"\t\t\theight:75%;\n" +
				"\t\t}\n" +
				"\t\t.pathInputBox\n" +
				"\t\t{\n" +
				"\t\t\tmin-width:500px;\t\t\t\n" +
				"\t\t}\n" +
				"\t\tcodeBox\n" +
				"\t\t{\n" +
				"\t\t\tbackground:#303030;\n" +
				"\t\t\tborder-radius:5px;\n" +
				"\t\t\tpadding:2px;\n" +
				"\t\t}\n" +
				"\t\tdescriptionBox\n" +
				"\t\t{\t\t\n" +
				"\t\t\tfloat:left;\n" +
				"\t\t\tmin-width:250px;\n" +
				"\t\t\tmargin:20px;\n" +
				"\t\t\tpadding:10px;\n" +
				"\t\t\tborder:20px;\n" +
				"\t\t\tborder-radius:5px;\n" +
				"\t\t\tbackground:#101010;\t\n" +
				"\t\t}\n" +
				"\t\t\n" +
				"\t\t.listBoxWrapper\n" +
				"\t\t{\n" +
				"\t\t\tdisplay: flex;\n" +
				"\t\t\theight:100%;\n" +
				"\t\t\t\n" +
				"\t\t}\n" +
				"\t\t\n" +
				"\t\tinputTitle\n" +
				"\t\t{\n" +
				"\t\t\tgravity:center;\n" +
				"\t\t\tfont-size: 18px;\n" +
				"\t\t}\n" +
				"\t\t\n" +
				"\t\tsectionTitle\n" +
				"\t\t{\n" +
				"\t\t\tdisplay:block;\n" +
				"\t\t\twidth:100%;\n" +
				"\t\t\ttext-align: center;\n" +
				"\t\t\tfont-size: 18px;\n" +
				"\t\t\tmargin-bottom:10px;\n" +
				"\t\t}\n" +
				"\t\t\n" +
				"\t\tbuttonSpacer\n" +
				"\t\t{\t\n" +
				"\t\t\tdisplay:block;\n" +
				"\t\t\theight:150px;\n" +
				"\t\t\twidth:100%;\n" +
				"\t\t}\n" +
				"\t\t\n" +
				"\t</style>\n" +
				"\t<script>");


		//insert extra script stuff here
		builder.append("var profileKnownClasses = [");
		for (String clazzInProfile : parser.getKnownClassList())
		{
			builder.append("\""+clazzInProfile+"\"");
			builder.append(", ");
		}
			if (parser.getKnownClassList().size() > 0)
				builder.setLength( builder.length()-2);
		builder.append("];\n\n");

		builder.append("var profileObsClasses = [");
		for (String clazzInProfile : parser.getUnknownClassList())
		{
			builder.append("\""+clazzInProfile+"\"");
			builder.append(", ");
		}
		if (parser.getUnknownClassList().size() > 0)
			builder.setLength( builder.length()-2);
		builder.append("];\n\n");


		builder.append(
						"//Sets containing the above for easy checking\n" +
								"\t\tvar profiledKnownClassesSet = new Set();\n" +
								"\t\tvar profiledObsClassesSet = new Set();\n" +
								"\t\n" +
								"\t\tfunction formatParamPath(path)\n" +
								"\t\t{\n" +
								"\t\t\tvar path = path.replace('.*','');\n" +
								"\t\t\t//path = path.replace('$','.');\n" +
								"\t\t\tpath = path.replace(' ','');\n" +
								"\t\t\tif (path.slice(-1) == '.')\n" +
								"\t\t\t\treturn path.substring(0,path.length - 1);\n" +
								"\t\t\treturn path;\n" +
								"\t\t}\n" +
								"\t\t\n" +
								"\n" +
								"\t\tfunction removeNode(node)\n" +
								"\t\t{\n" +
								"\t\t\tnode.parentNode.removeChild(node);\n" +
								"\t\t}\n" +
								"\t\t\n" +
								"\t\t\n" +
								"\t\tfunction submit()\n" +
								"\t\t{\n" +
								"\t\t\tvar getString = \"\";\n" +
								"\t\t\n" +
								"\t\t\tvar fullpath = document.getElementById(\"fullpath\").value;\n" +
								"\t\t\tvar knownpath = getKnownPath();\n" +
								"\t\t\tvar incpub =  document.getElementById(\"incpub\").checked;\n" +
								"\t\t\n" +
								"\t\t\tfullpath = formatParamPath(fullpath);\n" +
								"\t\t\tknownpath = formatParamPath(knownpath);\n" +
								"\t\t\t\n" +
								"\t\t\tgetString += \"?fullpath=\"+fullpath;\n" +
								"\t\t\tgetString += \"&knownpath=\"+knownpath;\n" +
								"\t\t\tgetString += \"&incpub=\"+incpub;\n" +
								"\t\t\t\n" +
								"\t\t\t{\n" +
								"\t\t\t\tvar knownListArgs = getKnownList();\n" +
								"\t\t\t\tif (knownListArgs.length > 0)\n" +
								"\t\t\t\t\tgetString += \"&knownlist=\"+knownListArgs;\n" +
								"\t\t\t}\n" +
								"\t\t\t\n" +
								"\t\t\t{\n" +
								"\t\t\t\tvar obsListArgs = getObsList();\n" +
								"\t\t\t\tif (obsListArgs.length > 0)\n" +
								"\t\t\t\t\tgetString += \"&obsList=\"+obsListArgs;\n" +
								"\t\t\t}\n" +
								"\t\t\t\n" +
								"\t\t\n" +
								"\t\t\tlocation.href = location.protocol + '//' + location.host + location.pathname + getString;\n" +
								"\t\t}\n" +
								"\t\t\n" +
								"\t\tfunction getKnownList()\n" +
								"\t\t{\n" +
								"\t\t\tvar knownList = document.getElementById(\"knownList\");\n" +
								"\n" +
								"\t\t\tvar childNodes = document.getElementsByName(\"pathListItem\");\n" +
								"\t\t\t\t\n" +
								"\t\t\tvar returnString = \"\";\n" +
								"\t\t\t\n" +
								"\t\t\tfor (var i = 0; i < childNodes.length; i++)\n" +
								"\t\t\t{\n" +
								"\t\t\t\tvar child = childNodes[i];\n" +
								"\t\t\t\t\n" +
								"\t\t\t\tif (child.parentElement == knownList)\n" +
								"\t\t\t\t{\n" +
								"\t\t\t\t\tvar path = child.getAttribute(\"data-fullpath\");\n" +
								"\t\t\t\t\t\n" +
								"\t\t\t\t\t//No point if already already in server's list of knowns\n" +
								"\t\t\t\t\tif (!profiledKnownClassesSet.has(path))\n" +
								"\t\t\t\t\t{\t\t\t\t\t\t\n" +
								"\t\t\t\t\t\treturnString += path+\",\";\n" +
								"\t\t\t\t\t}\n" +
								"\t\t\t\t}\n" +
								"\t\t\t}\n" +
								"\t\t\t\n" +
								"\t\t\tif (returnString.length > 0)\n" +
								"\t\t\t{\n" +
								"\t\t\t\treturnString = returnString.slice(0,-1);\n" +
								"\t\t\t}\n" +
								"\n" +
								"\t\n" +
								"\t\t\treturn returnString;\n" +
								"\t\t}\n" +
								"\t\t\n" +
								"\t\tfunction getKnownPath()\n" +
								"\t\t{\n" +
								"\t\t\tvar knownContainer = document.getElementById(\"knownPath\");\n" +
								"\n" +
								"\t\t\tvar returnString = \"\";\n" +
								"\t\t\t\n" +
								"\t\t\tvar child = knownContainer.childNodes[0];\n" +
								"\t\t\t\n" +
								"\t\t\tvar path = child.getAttribute(\"data-fullpath\");\n" +
								"\t\t\tvar depth = child.getAttribute(\"data-obsindex\");\n" +
								"\t\t\t\n" +
								"\t\t\tvar splitPath = path.split(/\\.|\\$/);\n" +
								"\t\t\tvar maxDepth = splitPath.length;\n" +
								"\t\t\t\n" +
								"\t\t\tconsole.log(\"Depth: \"+depth+\", max: \"+maxDepth);\n" +
								"\t\t\t\n" +
								"\t\t\tif (depth >= maxDepth)\n" +
								"\t\t\t\treturn path;\n" +
								"\t\t\t\t\t\n" +
								"\t\t\tfor (var i = 0; i < depth; i++)\n" +
								"\t\t\t{\n" +
								"\t\t\t\treturnString = returnString + splitPath[i]+\".\";\n" +
								"\t\t\t}\n" +
								"\t\t\t\t\t\n" +
								"\t\t\treturn returnString;\n" +
								"\t\t}\n" +
								"\t\t\n" +
								"\t\tfunction getObsList()\n" +
								"\t\t{\n" +
								"\t\t\tvar obsList = document.getElementById(\"obsList\");\n" +
								"\n" +
								"\t\t\tvar childNodes = document.getElementsByName(\"pathListItem\");\n" +
								"\t\t\t\n" +
								"\t\t\tvar returnString = \"\";\n" +
								"\t\t\t\n" +
								"\t\t\tfor (var i = 0; i < childNodes.length; i++)\n" +
								"\t\t\t{\n" +
								"\t\t\t\tvar child = childNodes[i];\n" +
								"\t\t\t\t\n" +
								"\t\t\t\tif (child.parentElement == obsList)\n" +
								"\t\t\t\t{\n" +
								"\t\n" +
								"\t\t\t\t\tconsole.log(\"Maybe add\");\n" +
								"\t\t\t\t\tvar path = child.getAttribute(\"data-fullpath\");\n" +
								"\t\t\t\t\tvar depth = child.getAttribute(\"data-obsindex\");\n" +
								"\t\t\t\t\t\n" +
								"\t\t\t\t\tvar maxDepth = path.split(/\\.|\\$/).length;\n" +
								"\t\t\t\t\tif (depth >= maxDepth)\n" +
								"\t\t\t\t\t\tcontinue;\n" +
								"\t\t\t\t\t\t\t\t\n" +
								"\t\t\t\t\treturnString += path+\"|\"+depth+\"|\";\n" +
								"\t\t\t\t}\n" +
								"\t\t\t}\n" +
								"\t\t\t\n" +
								"\t\t\tif (returnString.length > 0)\n" +
								"\t\t\t{\n" +
								"\t\t\t\treturnString = returnString.slice(0,-1);\n" +
								"\t\t\t}\n" +
								"\n" +
								"\t\t\treturn returnString;\n" +
								"\t\t}\n" +
								"\t\t\n" +
								"\t\tdocument.addEventListener('DOMContentLoaded', function()\n" +
								"\t\t{\n" +
								"\t\t\tvar searchParams = new URLSearchParams(document.location.search);\n" +
								"\t\t\n" +
								"\t\t\tvar fullpath = searchParams.get('fullpath')\n" +
								"\t\t\tif (fullpath != null)\n" +
								"\t\t\t{\n" +
								"\t\t\t\tdocument.getElementById(\"fullpath\").value = fullpath;\n" +
								"\t\t\t}\n" +
								"\t\t\t\n" +
								"\t\t\tvar knownpath = searchParams.get('knownpath')\n" +
								"\t\t\tif (knownpath != null)\n" +
								"\t\t\t{\n" +
								"\t\t\t\tvar depth = 999;\n" +
								"\t\t\t\tconsole.log(\"length: \"+knownpath.length);\n" +
								"\t\t\t\tif (knownpath.length > 0)\n" +
								"\t\t\t\t{\n" +
								"\t\t\t\t\tdepth = knownpath.split(/\\.|\\$/).length ;\n" +
								"\t\t\t\t}\n" +
								"\t\t\t\telse\n" +
								"\t\t\t\t\tdepth = 0;\n" +
								"\t\t\t\t\n" +
								"\t\t\t\tconsole.log(\"Depth: \"+depth);\n" +
								"\t\t\t\t\n" +
								"\t\t\t\tsetKnownPath(fullpath, depth);\n" +
								"\t\t\t}\n" +
								"\t\t\telse\n" +
								"\t\t\t\tsetKnownPath(\"\");\n" +
								"\t\t\t\n" +
								"\t\t\tvar incpub = searchParams.get('incpub')\n" +
								"\t\t\tif (name != null)\n" +
								"\t\t\t{\n" +
								"\t\t\t\tdocument.getElementById(\"incpub\").checked = incpub==\"true\";\n" +
								"\t\t\t}\n" +
								"\n" +
								"\t\t\tpopulateSets(searchParams);\n" +
								"\t\t\tpopulatePaths(searchParams);\n" +
								"\n" +
								"\t\t}, false)\n" +
								"\t\t\n" +
								"\t\tfunction populateSets()\n" +
								"\t\t{\n" +
								"\t\t\tfor (var i = 0 ; i < profileKnownClasses.length; i++)\n" +
								"\t\t\t{\n" +
								"\t\t\t\t\n" +
								"\t\t\t\tprofiledKnownClassesSet.add(profileKnownClasses[i]);\n" +
								"\t\t\t}\n" +
								"\t\t\t\n" +
								"\t\t\tfor (var i = 0 ; i < profileObsClasses.length; i++)\n" +
								"\t\t\t{\n" +
								"\t\t\t\tprofiledObsClassesSet.add(profileObsClasses[i]);\n" +
								"\t\t\t}\n" +
								"\t\t}\n" +
								"\t\t\n" +
								"\t\tfunction populatePaths(searchParams)\n" +
								"\t\t{\n" +
								"\t\t\t\n" +
								"\t\t\t//Set of anything added anywhere\n" +
								"\t\t\tvar addedSet = new Set();\n" +
								"\t\t\t\n" +
								"\t\t\t\n" +
								"\t\t\t//Run through knownList first\n" +
								"\t\t\tvar knownArgs = searchParams.get('knownlist')\n" +
								"\t\t\tif (knownArgs != null)\n" +
								"\t\t\t{\n" +
								"\t\t\t\tvar splitKnownArgs = knownArgs.split(\",\");\n" +
								"\t\t\t\t//To be added, an item must exist in either of the\n" +
								"\t\t\t\t//sets passed by the server\n" +
								"\t\t\t\tfor (var i = 0 ; i < splitKnownArgs.length; i++)\n" +
								"\t\t\t\t{\n" +
								"\t\t\t\t\tvar currentPath = splitKnownArgs[i];\n" +
								"\t\t\t\t\tif (profiledKnownClassesSet.has(currentPath) || profiledObsClassesSet.has(currentPath))\n" +
								"\t\t\t\t\t{\n" +
								"\t\t\t\t\t\taddKnown(currentPath);\n" +
								"\t\t\t\t\t\taddedSet.add(currentPath);\n" +
								"\t\t\t\t\t}\n" +
								"\t\t\t\t}\t\n" +
								"\t\t\t}\n" +
								"\t\t\t\n" +
								"\t\t\t//Then obsList\n" +
								"\t\t\tvar obsArgs = searchParams.get('obsList')\n" +
								"\t\t\tif (obsArgs != null)\n" +
								"\t\t\t{\n" +
								"\t\t\t\tvar splitObsArgs = obsArgs.split(\"|\");\n" +
								"\t\t\t\t//To be added, an item must exist in either of the\n" +
								"\t\t\t\t//sets passed by the server\n" +
								"\t\t\t\tfor (var i = 0 ; i < splitObsArgs.length; i += 2)\n" +
								"\t\t\t\t{\n" +
								"\t\t\t\t\tif (i+1 >= splitObsArgs.length)\n" +
								"\t\t\t\t\t\tbreak;\n" +
								"\t\t\t\t\t\n" +
								"\t\t\t\t\tvar currentPath = splitObsArgs[i];\n" +
								"\t\t\t\t\tvar currentDepth = splitObsArgs[i+1];\n" +
								"\t\t\t\t\t\n" +
								"\t\t\t\t\t\n" +
								"\t\t\t\t\tif (profiledKnownClassesSet.has(currentPath) || profiledObsClassesSet.has(currentPath))\n" +
								"\t\t\t\t\t{\n" +
								"\t\t\t\t\t\t//Should only exist in either one list.\n" +
								"\t\t\t\t\t\t//Did the user muck with the arguments?\n" +
								"\t\t\t\t\t\tif (!addedSet.has(currentPath))\n" +
								"\t\t\t\t\t\t{\n" +
								"\t\t\t\t\t\t\tvar newItem = addObs(currentPath);\n" +
								"\t\t\t\t\t\t\tsetPathObfuscation(newItem, currentDepth);\n" +
								"\t\t\t\t\t\t\taddedSet.add(currentPath);\n" +
								"\t\t\t\t\t\t}\n" +
								"\t\t\t\t\t}\n" +
								"\t\t\t\t}\t\n" +
								"\t\t\t}\n" +
								"\t\t\t\n" +
								"\t\t\t//Finally add whatever was passed from the server that has not already been added.\n" +
								"\t\t\t\t\t\t\n" +
								"\t\t\tfor (var i = 0 ; i < profileKnownClasses.length; i++)\n" +
								"\t\t\t{\n" +
								"\t\t\t\tif (!addedSet.has(profileKnownClasses[i]))\n" +
								"\t\t\t\t{\n" +
								"\t\t\t\t\tvar newItem = addKnown(profileKnownClasses[i], true);\n" +
								"\t\t\t\t\tremoveSwitchButton(newItem);\n" +
								"\t\t\t\t}\n" +
								"\t\t\t\t\t\n" +
								"\t\t\t}\n" +
								"\t\t\t\n" +
								"\t\t\tfor (var i = 0 ; i < profileObsClasses.length; i++)\n" +
								"\t\t\t{\n" +
								"\t\t\t\tif (!addedSet.has(profileObsClasses[i]))\n" +
								"\t\t\t\t\taddObs(profileObsClasses[i], true);\n" +
								"\t\t\t}\n" +
								"\t\t\t\n" +
								"\t\t}\n" +
								"\t\t\n" +
								"\t\tfunction handlePathboxClick(pathBox, index)\n" +
								"\t\t{\n" +
								"\t\t\tsetPathObfuscation(pathBox.parentElement.parentElement,index);\n" +
								"\t\t}\n" +
								"\t\t\n" +
								"\t\tfunction removeSwitchButton(listItem)\n" +
								"\t\t{\n" +
								"\t\t\tlistItem.childNodes[0].style.visibility=\"hidden\";\n" +
								"\t\t}\n" +
								"\t\t\n" +
								"\t\tfunction moveListItem(listItem, toRight)\n" +
								"\t\t{\n" +
								"\t\t\tvar path = listItem.getAttribute(\"data-fullpath\");\n" +
								"\t\t\tremoveNode(listItem);\n" +
								"\t\t\t\n" +
								"\t\t\tif (toRight)\n" +
								"\t\t\t{\n" +
								"\t\t\t\taddObs(path);\n" +
								"\t\t\t}\n" +
								"\t\t\telse\n" +
								"\t\t\t{\n" +
								"\t\t\t\taddKnown(path);\n" +
								"\t\t\t}\n" +
								"\t\t}\n" +
								"\t\t\n" +
								"\t\t\n" +
								"\t\tfunction setPathObfuscation(listItem, index)\n" +
								"\t\t{\n" +
								"\t\t\n" +
								"\t\t\tvar boxList = listItem.lastChild;\n" +
								"\t\t\tboxList.parentElement.setAttribute(\"data-obsindex\",index);\n" +
								"\t\t\t\n" +
								"\t\t\tfor (var i = 0; i < boxList.childNodes.length; i++)\n" +
								"\t\t\t{\n" +
								"\t\t\t\tvar child = boxList.childNodes[i];\n" +
								"\t\t\t\t\n" +
								"\t\t\t\tif (i > index)\n" +
								"\t\t\t\t{\n" +
								"\t\t\t\t\tchild.className = \"btn btn-warning pathBox\";\n" +
								"\t\t\t\t}\n" +
								"\t\t\t\telse\n" +
								"\t\t\t\t{\n" +
								"\t\t\t\t\tchild.className = \"btn btn-success pathBox\";\n" +
								"\t\t\t\t}\t\n" +
								"\t\t\t}\t\n" +
								"\t\t}\n" +
								"\t\t\n" +
								"\t\tfunction addKnown(fullPath, appendToEnd)\n" +
								"\t\t{\n" +
								"\t\t\treturn addPath(fullPath, \"knownList\", false, appendToEnd,false);\n" +
								"\t\t}\n" +
								"\t\t\n" +
								"\t\tfunction addObs(fullPath, appendToEnd)\n" +
								"\t\t{\n" +
								"\t\t\treturn addPath(fullPath, \"obsList\", true, appendToEnd,true);\n" +
								"\t\t}\n" +
								"\t\t\n" +
								"\t\tfunction addPath(fullPath, listName, setClick, appendToEnd, indentLeft)\n" +
								"\t\t{\n" +
								"\t\t\tvar knownList = document.getElementById(listName);\n" +
								"\t\t\t\n" +
								"\t\t\tvar pathListItem = getPathListItem(indentLeft, fullPath);\n" +
								"\t\t\t\n" +
								"\t\t\tif(appendToEnd)\n" +
								"\t\t\t{\n" +
								"\t\t\t\tknownList.appendChild(pathListItem);\n" +
								"\t\t\t}\n" +
								"\t\t\telse\n" +
								"\t\t\t{\n" +
								"\t\t\t\tknownList.insertBefore(pathListItem, knownList.firstChild);\n" +
								"\t\t\t}\n" +
								"\n" +
								"\t\t\t\n" +
								"\t\t\tvar boxList = pathListItem.lastChild;\n" +
								"\n" +
								"\t\t\tvar strings = fullPath.split(/\\.|\\$/);\n" +
								"\t\t\tboxList.appendChild(getPathBox(\"#\",0, setClick));\n" +
								"\t\t\t\n" +
								"\t\t\tfor (var i = 0; i < strings.length; i++)\n" +
								"\t\t\t{\n" +
								"\t\t\t\tif (strings[i].length > 0)\n" +
								"\t\t\t\t\tboxList.appendChild(getPathBox(strings[i],i+1, setClick));\n" +
								"\t\t\t}\n" +
								"\t\t\t\n" +
								"\t\t\treturn pathListItem;\n" +
								"\t\t}\n" +
								"\t\t\n" +
								"\t\tfunction setKnownPath(path,depth)\n" +
								"\t\t{\n" +
								"\t\t\tif (depth == undefined)\n" +
								"\t\t\t\tdepth = 999;\n" +
								"\t\t\t\n" +
								"\t\t\t//Get rid of existing\n" +
								"\t\t\tpath = formatParamPath(path);\n" +
								"\t\t\tvar knownContainer = document.getElementById(\"knownPath\");\n" +
								"\t\t\twhile (knownContainer.firstChild)\n" +
								"\t\t\t{\n" +
								"\t\t\t\tknownContainer.removeChild(knownContainer.firstChild);\n" +
								"\t\t\t}\n" +
								"\t\t\t\n" +
								"\t\t\tvar newItem = addPath(path, \"knownPath\", true,true,false);\n" +
								"\t\t\tremoveSwitchButton(newItem);\n" +
								"\t\t\tsetPathObfuscation(newItem, depth);\n" +
								"\t\t}\n" +
								"\t\t\n" +
								"\t\tfunction getPathBox(content, index, addClick, appendToEnd)\n" +
								"\t\t{\n" +
								"\t\t\tvar pathBox = document.createElement(\"div\");\n" +
								"\t\t\tpathBox.className = \"btn btn-success pathBox\";\n" +
								"\t\t\tif (addClick)\n" +
								"\t\t\t\tpathBox.onclick = function() { handlePathboxClick(pathBox,index); };\n" +
								"\t\t\tpathBox.innerText = content;\n" +
								"\t\t\t\n" +
								"\t\t\t\n" +
								"\t\t\treturn pathBox;\n" +
								"\t\t}\n" +
								"\t\t\n" +
								"\t\tfunction getPathListItem(leftRight, fullPath)\n" +
								"\t\t{\n" +
								"\t\t\tvar pathListItem = document.createElement(\"div\");\n" +
								"\t\t\tpathListItem.className = \"pathListItem box\";\n" +
								"\t\t\tpathListItem.setAttribute(\"name\" ,\"pathListItem\");\n" +
								"\t\t\t\n" +
								"\t\t\t\n" +
								"\t\t\t\n" +
								"\t\t\tpathListItem.setAttribute(\"data-fullpath\", fullPath);\n" +
								"\t\t\tpathListItem.setAttribute(\"data-obsindex\", 999);\n" +
								"\t\t\t\n" +
								"\t\t\tvar moveButton = document.createElement(\"div\");\n" +
								"\t\t\tif (!leftRight)\n" +
								"\t\t\t{\n" +
								"\t\t\t\tmoveButton.innerText = \">\";\n" +
								"\t\t\t\tmoveButton.className = \"btn btn-primary pathBoxMoveRight\";\n" +
								"\t\t\t}\n" +
								"\t\t\t\t\n" +
								"\t\t\telse\n" +
								"\t\t\t{\n" +
								"\t\t\t\tmoveButton.innerText = \"<\";\n" +
								"\t\t\t\tmoveButton.className = \"btn btn-primary pathBoxMoveLeft\";\n" +
								"\t\t\t}\n" +
								"\t\t\t\t\n" +
								"\t\t\t\t\n" +
								"\t\t\tmoveButton.onclick = function() { moveListItem(pathListItem,!leftRight); };\n" +
								"\t\t\tpathListItem.appendChild(moveButton);\n" +
								"\t\t\t\n" +
								"\t\t\tvar boxList = document.createElement(\"div\");\n" +
								"\t\t\tboxList.className = \"pathBoxContainer\";\n" +
								"\t\t\tboxList.name = \"pathBoxList\";\n" +
								"\t\t\t\n" +
								"\t\t\tpathListItem.appendChild(boxList);\n" +
								"\t\t\t\n" +
								"\t\t\treturn pathListItem;\n" +
								"\t\t}\n" +
								"\t\t\n" +
								"\t</script>\n" +
								"\t<body class=\"body\" style=\"margin:50px; color:#FFFFFF; background:#252525\">\n" +
								"\t\t\t\t\t<div class=\"horizontalWrapper\">\n" +
								"\t\t\t\t\t\n" +
								"\t\t\t\t\t\t<div class=\"genericWrapper\">\n" +
								"\t\t\t\t\t\t\t<div class=\"listBoxWrapper\">\n" +
								"\t\t\t\t\t\t\t\t<div>\n" +
								"\t\t\t\t\t\t\t\t\t<inputTitle>Full path:</inputTitle><br>\n" +
								"\t\t\t\t\t\t\t\t\t<input class=\"form-control pathInputBox\" type=\"text\" id=\"fullpath\" oninput=\"setKnownPath(this.value)\"><br>\n" +
								"\t\t\t\t\t\t\t\t\t\n" +
								"\t\t\t\t\t\t\t\t\t<div id=\"knownPath\">\n" +
								"\t\t\t\t\t\t\t\t\t</div>\n" +
								"\t\t\t\t\t\t\t\t\t\n" +
								"\t\t\t\t\t\t\t\t\t<hr>\n" +
								"\t\t\t\t\t\t\t\t\t<inputTitle>Include public methods:</inputTitle> \n" +
								"\t\t\t\t\t\t\t\t\t<input type=\"checkbox\" id=\"incpub\"><br>\n" +
								"\t\t\t\t\t\t\t\t\t<hr>\n" +
								"\t\t\t\t\t\t\t\t\t<buttonSpacer> </buttonSpacer>\n" +
								"\t\t\t\t\t\t\t\t\t<input type=\"submit\" class=\"btn btn-primary\" value=\"Submit\" onclick=\"submit()\"><br>\n" +
								"\t\t\t\t\t\t\t\t</div>\n" +
								"\t\t\t\t\t\t\t</div>\n" +
								"\t\t\t\t\t\t</div>\n" +
								"\t\t\t\t\t\t<div class=\"genericWrapper\">\n" +
								"\t\t\t\t\t\t\n" +
								"\t\t\t\t\t\t\t<descriptionBox>\n" +
								"\t\t\t\t\t\t\t\t<div>\n" +
								"\t\t\t\t\t\t\t\t\tThe full path is the path of the current target class:<br><codeBox>com.mayulive.a.b.c</codeBox><br>\n" +
								"\t\t\t\t\t\t\t\t\t<br>\n" +
								"\t\t\t\t\t\t\t\t\tIf part of this path is obfuscated and expected to change,<br>\n" +
								"\t\t\t\t\t\t\t\t\tplease select the last known (unobfuscated) portion of the path:<br>\n" +
								"\t\t\t\t\t\t\t\t\t<codeBox>com.mayulive</codeBox><br>\n" +
								"\t\t\t\t\t\t\t\n" +
								"\t\t\t\t\t\t\t\t\t<br>\n" +
								"\t\t\t\t\t\t\t\t\tResolvable classes can be referenced with <codeBox>CLASSNAME.class</codeBox>:<br>\n" +
								"\t\t\t\t\t\t\t\t\t<codeBox>java.lang.String</codeBox>,  <codeBox>android.content.Context</codeBox><br>\n" +
								"\t\t\t\t\t\t\t\t\t<br>\n" +
								"\t\t\t\t\t\t\t\t\t\n" +
								"\t\t\t\t\t\t\t\t\tUnresolvable classes are either unknown to you, or on an obfuscated path:<br>\n" +
								"\t\t\t\t\t\t\t\t\t<codeBox>com.nordskog.youdontknowme</codeBox>, <codeBox>com.mayulive.somepackage.a.b.c</codeBox><br>\n" +
								"\t\t\t\t\t\t\t\t\tPlease select the last known (unobfuscated) portion of the path.<br>\n" +
								"\t\t\t\t\t\t\t\t\t<br>\n" +
								"\t\t\t\t\t\t\t\t\tFunctionally they are the same, but resolved classes are easier on the eyes.\n" +
								"\t\t\t\t\t\t\t\t\n" +
								"\t\t\t\t\t\t\t\t</div>\n" +
								"\t\t\t\t\t\t\t</descriptionBox> \n" +
								"\t\t\t\t\t\t\n" +
								"\t\t\t\t\t\t</div>\n" +
								"\t\t\t\t\t\n" +
								"\t\t\t\t\t</div>\n" +
								"\n" +
								"\t\t\t\t\t\n" +
								"\t\t\t\t\t<div class=\"horizontalWrapper\">\n" +
								"\t\t\t\t\t\n" +
								"\t\t\t\t\t\t<div class=\"genericWrapper\">\n" +
								"\t\t\t\t\t\t<sectionTitle>Resolvable</sectionTitle>\n" +
								"\t\t\t\t\n" +
								"\t\t\t\t\t\t\t<div id=\"knownList\">\n" +
								"\t\t\t\t\t\t\t\n" +
								"\t\t\t\t\t\t\t</div>\n" +
								"\t\t\t\t\t\t</div>\n" +
								"\t\t\t\t\t\t\n" +
								"\t\t\t\t\t\t<div class=\"genericWrapper\">\n" +
								"\t\t\t\t\t\t<sectionTitle>Unresolvable</sectionTitle>\n" +
								"\t\t\t\t\n" +
								"\t\t\t\t\t\t\t<div id=\"obsList\">\n" +
								"\t\t\t\t\t\t\t\n" +
								"\t\t\t\t\t\t\t</div>\n" +
								"\t\t\t\t\t\t</div>\n" +
								"\t\t\t\t\t\t\n" +
								"\t\t\t\t\t</div>\n" +
								"\t\t\t\t\t\n" +
								"\t\t\t\t</div>\n" +
								"\t\t\t\t\n" +
								"\t\t\t\n" +
								"\t\t\t\n" +
								"\t\t<div class=\"genericWrapper\" style=\"white-space: pre-wrap\">\n" +
								"<sectionTitle>Profile</sectionTitle>\n" +
								"<textarea readonly wrap=\"off\" class=\"profileOutput\">"
		);

		builder.append(profileString);

		builder.append(
						" </textarea>\n" +
								"\t\t</div>\n" +
								"\t</body>\n" +
								"</html> ");

		return builder.toString().getBytes();

	}
}
