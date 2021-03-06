package main;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

import api.AISheet;
import api.OthelloAI;

public class Start {

	public static final String version = "1.0";
	public static final int panelSize = 604;

	protected static Map<String, Class<? extends AISheet>> AIMap = new HashMap<String, Class<? extends AISheet>>();
	private static Map<Class<?>, File> FileMap = new HashMap<Class<?>, File>();

	public static void main(String[] args) throws IOException {

		loadAIs();

		AIMap.entrySet().stream().forEach(entry -> System.out.println(entry.getKey() + " : " + entry.getValue()));
		FileMap.entrySet().stream().forEach(entry -> System.out.println(entry.getKey() + " : " + entry.getValue()));

		System.gc();

		EventQueue.invokeLater(() -> {
			InitGui gui = null;
			try{
				gui = new InitGui();
				gui.setVisible(true);
			}catch (Exception e){
				if(gui != null){
					gui.dispose();
				}
				System.out.println("Exception occered on preparing Initial Gui.");
				e.printStackTrace();
			}
		});
	}

	@SuppressWarnings("unchecked")
	private static void loadAIs() throws MalformedURLException {
		File dirAI = new File(System.getProperty("user.dir"), "bin" + File.separator + "ai");
		if(dirAI.exists() && dirAI.isDirectory()){
			for (File fileAI : dirAI.listFiles()){
				if(fileAI.isFile()){
					URL[] fileUrls = { fileAI.toURI().toURL() };
					ClassLoader fileLoader = URLClassLoader.newInstance(fileUrls);

					//class file
					if(fileAI.getName().endsWith(".class")){
						try{
							String className = fileAI.getName().replaceFirst(".class", "");
							className = className.replace("/", ".");
							Class<?> clazz = fileLoader.loadClass(className);
							String aiName = canLoadClass(clazz);
							if(aiName != null){
								System.out.println("Detect ai class file: " + aiName);
								if(aiName == null || aiName.isEmpty()){
									System.out.println(fileAI.getName() + " does not have ai name.");
									System.out.println("Failed to load ai class: " + aiName);
								}else if(!AIMap.containsKey(aiName)){
									AIMap.put(aiName, (Class<? extends AISheet>) clazz);
									FileMap.put(clazz, new File(System.getProperty("user.dir"), "bin" + File.separator + "ai" + File.separator + "metadata" + File.separator + aiName.replace(" ", "_")));
									System.out.println("Successfully load ai class: " + aiName);
								}else{
									System.out.println("The same ai name is already loaded: " + aiName);
								}
							}
						}catch (Exception e){
							System.out.println("Cannot load class: " + fileAI.getName());
							e.printStackTrace();
						}
					}

					//jar file
					else if(fileAI.getName().endsWith(".jar")){
						try (JarFile jarAI = new JarFile(fileAI)){
							System.out.println("Start to load file: " + fileAI.getName());
							jarAI.stream().forEach(entry -> {
								if(entry.getName().endsWith(".class")){
									try{
										String className = entry.getName().replaceFirst(".class", "");
										className = className.replace("/", ".");
										Class<?> clazz = fileLoader.loadClass(className);
										String aiName = canLoadClass(clazz);
										if(aiName != null){
											System.out.println("Detect ai class: " + aiName);
											if(aiName == null || aiName.isEmpty()){
												System.out.println(entry.getName() + " does not have name.");
												System.out.println("Failed to load ai class: " + aiName);
											}else if(!AIMap.containsKey(aiName)){
												AIMap.put(aiName, (Class<? extends AISheet>) clazz);
												FileMap.put(clazz, new File(System.getProperty("user.dir"), "bin" + File.separator + "ai" + File.separator + "metadata" + File.separator + fileAI.getName().replace(".jar", "") + File.separator + aiName.replace(" ", "_")));
												System.out.println("Successfully load ai class: " + aiName);
											}else{
												System.out.println("The same ai name is already loaded: " + aiName);
											}
										}
									}catch (Exception e){
										System.out.println("Cannot load ai class file: " + entry.getName());
										e.printStackTrace();
									}
								}
							});
						}catch (IOException e){
							System.out.println("Cannot open file: " + fileAI.getName());
							e.printStackTrace();
						}
					}
				}
			}
		}else{
			System.out.println("Cannot detect the directory \"bin/ai\".");
			if(dirAI.mkdir()){
				System.out.println("Creeated the directory \"bin/ai\".");
			}
			if(new File(System.getProperty("user.dir"), "bin" + File.separator + "ai" + File.separator + "metadata").mkdir()){
				System.out.println("Creeated the directory \"bin/ai/metadata\".");
			}
			System.out.println("Put jar file that contains ai class in the directiory to load custom AIs.");
			System.out.println("Class file will also be loaded, but deprecated.");
		}
	}

	private static String canLoadClass(Class<?> clazz) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, InstantiationException {
		if(clazz != null){
			if(AISheet.class.isAssignableFrom(clazz) && clazz != AISheet.class){
				Annotation[] annotations = clazz.getAnnotations();
				if(annotations != null && annotations.length > 0){
					for (Annotation a : annotations){
						if(a.annotationType() == OthelloAI.class){
							String depend = ((OthelloAI) a).depend();
							if(depend.equals(version)){
								String name = (String) clazz.getMethod("getAIName").invoke(clazz.newInstance());
								String version = ((OthelloAI) a).version();
								String author = ((OthelloAI) a).author();
								if(!name.isEmpty() && !version.isEmpty() && !author.isEmpty()){
									return name + " " + version + " by " + author;
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	protected static File getSaveFileFromClass(Class<?> aiClass) {
		return FileMap.get(aiClass);
	}

}
