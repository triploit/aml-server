package io.github.triploit.amlserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.triploit.AML;
import io.github.triploit.amlserver.settings.Settings;
import io.github.triploit.parser.Tokenizer;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Main
{
	private static String responseBody = "";
	public static final String ERROR = "<html>"
			+"<body><h1>ERROR</h1>There was an error. Sorry :(.</body>"
			+"</html>";
	public static Settings main_settings;
	public static List<String> to_delete = new ArrayList<>();

	public static void main(String[] args)
	{
		String data = "";

		if (args.length < 1)
		{
			System.out.println("Error: Give me the settings file! ("+args.length+")");
			System.exit(0);
		}

		try (BufferedReader br = new BufferedReader(new FileReader(args[0])))
		{
			String line;
			while ((line = br.readLine()) != null)
			{
				data += line;
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		Gson gson = new GsonBuilder().create();
		main_settings = gson.fromJson(data, Settings.class);

		Thread t1 = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				server();
			}
		});

		Thread t2 = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				Scanner s = new Scanner(System.in);
				String input;

				while (true)
				{
					System.out.print(">> ");
					input = s.nextLine();

					if (input.equalsIgnoreCase("stop"))
					{
						for (String f : to_delete)
						{
							File file = new File(f);
							if (file.exists()) file.delete();
						}

						System.exit(0);
					}
					else if (input.equalsIgnoreCase("settings.port"))
					{
						System.out.println("SETTINGS.PORT: "+main_settings.port);
					}
					else if (input.equalsIgnoreCase("settings.source_dir"))
					{
						System.out.println("SETTINGS.SOURCE_DIR: "+main_settings.source_dir);
					}
					else if (input.equalsIgnoreCase("version"))
					{
						System.out.println("Version: 0.1.1");
					}
					else if (input.equalsIgnoreCase("settings.reload"))
					{
						String d = "";

						try (BufferedReader br = new BufferedReader(new FileReader(args[0])))
						{
							String line;
							while ((line = br.readLine()) != null)
							{
								d += line;
							}
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}

						Gson gson = new GsonBuilder().create();
						main_settings = gson.fromJson(d, Settings.class);
					}
				}
			}
		});

		t1.start();
		t2.start();
	}

	public static String generateCode(String file) throws IOException
	{
		System.out.println("Interpreting... " + file);

		if (!(new File(file)).exists() || !(new File(file)).isFile())
		{
			System.out.println("Cancelled: File not found or is directory: "+file);
			throw new IOException("File not found!");
		}

		String code = AML.readFile(file);

		if (AML.parser.parse(Tokenizer.tokenize(code)) <= 0 || AML.errors <= 0 && AML.warnings <= 0)
		{
			code = AML.parser.code;
			code = code.replace("~\\n~", "\n");
			code = code.replace("~<br>~", "\n");
			code = code.replace("~\\t~", "\t");
			String _of;

			if (AML.errors <= 0 && AML.warnings <= 0)
			{
				System.out.println("Finished! No errors!");
				return code;
			}
			else
			{
				System.out.println("Interpreting of " + file + " cancelled with " + AML.errors + " errors and " + AML.warnings + " warnings.");
				return ERROR+"<br>Error in File: " + file;
			}
		}
		else
		{
			System.out.println("Interpreting of " + file + " cancelled with " + AML.errors + " errors and " + AML.warnings + " warnings.");
			return ERROR+"<br>Error in File: " + file;
		}
	}

	public static void server()
	{
		Settings settings = main_settings;
		int port = settings.port;

		try(ServerSocket serverSocket = new ServerSocket(port))
		{
			while(true)
			{
				try (Socket socket = serverSocket.accept();
					 InputStream input = socket.getInputStream();
					 BufferedReader reader = new BufferedReader(new InputStreamReader(input));
					 OutputStream output = socket.getOutputStream();
					 PrintWriter writer = new PrintWriter(new OutputStreamWriter(output)))
				{
					boolean error = false;

					// Request-Header empfangen und ignorieren
					for (String line = reader.readLine(); !line.isEmpty(); line = reader.readLine())
					{
						try
						{
							if (line.startsWith("GET"))
							{
								String path = line.substring(4, line.length()-8);
								path = settings.source_dir + path;
								path = path.replace("//", "/").trim();
								System.out.println("!!!!!! PATH: "+path);

								if ((!path.toLowerCase().contains(".aml")
										|| !path.toLowerCase().endsWith(".aml"))
										&& !path.endsWith("/")
										&& !path.endsWith(".html")
										&& !path.endsWith(".xhtml")
										&& !path.endsWith(".htm"))
								{
									responseBody = "<code>"+AML.readFile(path)+"</code>";
									continue;
								}
								else if (path.endsWith(".html") || path.endsWith(".htm") || path.endsWith(".xhtml"))
								{
									System.out.println(2);
									responseBody = AML.readFile(path);
									continue;
								}

								try
								{
									responseBody = generateCode(path);
								}
								catch (IOException e)
								{
									if (path.equals(settings.source_dir))
									{
										String _data = "";
										path = path + "index.aml";

										try
										{
											_data = generateCode(path);
										}
										catch (IOException ex)
										{
											_data = ERROR+"<br>File index.aml not found!";
										}

										responseBody = _data;
									}
									else responseBody = ERROR;
								}

								break;
							}
						}
						catch(NullPointerException ex)
						{
							System.out.println("request from " + socket.getRemoteSocketAddress());

							writer.println("HTTP/1.0 200 OK");
							writer.println("Content-Type: text/html; charset=ISO-8859-1");
							writer.println("Server: NanoHTTPServer");
							writer.println();

							writer.println(ERROR);
							System.out.print(">> ");

							error = true;
							break;
						}
					}

					if (error) continue;

					System.out.println("request from " + socket.getRemoteSocketAddress());

					// Response-Header senden
					writer.println("HTTP/1.0 200 OK");
					writer.println("Content-Type: text/html; charset=ISO-8859-1");
					writer.println("Server: NanoHTTPServer");
					writer.println();
					// Response-Body senden
					writer.println(responseBody);
					System.out.print(">> ");
				}
				catch (IOException iox)
				{
					// Fehler
				}
			}
		}
		catch (IOException e)
		{
			System.out.println(">> Error: IOException");
			e.printStackTrace();
		}
	}
}
