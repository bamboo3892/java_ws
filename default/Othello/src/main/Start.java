package main;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

import api.AISheet;

public class Start {

	public static final int panelSize = 604;
	protected static Map<String, Class<? extends AISheet>> AIMap = new HashMap<String, Class<? extends AISheet>>();
	private static int reduplicateIndex = 0;

	public static void main(String[] args) throws IOException {

		//		AIMap.put(new RandomAI().getAIName(), RandomAI.class);
		//		AIMap.put(new EstimateAI().getAIName(), EstimateAI.class);
		//		AIMap.put(new MonteCarloAI().getAIName(), MonteCarloAI.class);
		//		AIMap.put(new AdvancedAI().getAIName(), AdvancedAI.class);
		//		AIMap.put(new AncientAi().getAIName(), AncientAi.class);
		//		AIMap.put(new EstimateAIMk2().getAIName(), EstimateAIMk2.class);
		//		AIMap.put(new OldAI().getAIName(), OldAI.class);

		loadAIs();

		System.gc();

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try{
					InitGui frame = new InitGui();
					frame.setVisible(true);
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	private static void loadAIs() throws MalformedURLException {
		File dirAI = new File(System.getProperty("user.dir"), "bin" + File.separator + "ai");
		if(dirAI.exists() && dirAI.isDirectory()){
			URL[] dirUrls = { dirAI.toURI().toURL() };
			ClassLoader dirLoader = URLClassLoader.newInstance(dirUrls);
			for (File fileAI : dirAI.listFiles()){
				if(fileAI.isFile()){
					try{
						URL[] urls = { fileAI.toURI().toURL() };
						ClassLoader loader = URLClassLoader.newInstance(urls);

						//class file
						if(fileAI.getName().endsWith(".class")){
							String className = fileAI.getName().replaceFirst(".class", "");
							className = className.replace("/", ".");
							className = "ai." + className;
							try{
								Class<?> clazz = dirLoader.loadClass(className);
								if(AISheet.class.isAssignableFrom(clazz) && clazz != AISheet.class){
									String aiName = (String) clazz.getMethod("getAIName").invoke(clazz.newInstance());
									System.out.println("Detect ai class file: " + aiName);
									if(aiName == null){
										System.out.println(fileAI.getName() + " does not have name.");
										System.out.println("Failed to load ai class: " + aiName);
									}else if(!AIMap.containsKey(aiName)){
										AIMap.put(aiName, (Class<? extends AISheet>) clazz);
										System.out.println("Successfully load ai class: " + aiName);
									}else{
										System.out.println("The same name ai is already loaded.");
										AIMap.put(aiName + " " + reduplicateIndex, (Class<? extends AISheet>) clazz);
										System.out.println("Named " + fileAI.getName() + " as " + aiName + " " + reduplicateIndex);
										System.out.println("Successfully load ai class: " + aiName + " " + reduplicateIndex);
										reduplicateIndex++;
									}
								}
							}catch (Exception e){
								System.out.println("Cannot load class: " + className);
								e.printStackTrace();
							}
							continue;
						}

						//jar file
						try (JarFile jarAI = new JarFile(fileAI)){
							System.out.println("Start to load file: " + fileAI.getName());
							jarAI.stream().forEach(entry -> {
								if(entry.getName().endsWith(".class")){
									String className = entry.getName().replaceFirst(".class", "");
									className = className.replace("/", ".");
									try{
										Class<?> clazz = loader.loadClass(className);
										if(AISheet.class.isAssignableFrom(clazz) && clazz != AISheet.class){
											String aiName = (String) clazz.getMethod("getAIName").invoke(clazz.newInstance());
											System.out.println("Detect ai class file: " + aiName);
											if(aiName == null){
												System.out.println(entry.getName() + " does not have name.");
												System.out.println("Failed to load ai class: " + aiName);
											}else if(!AIMap.containsKey(aiName)){
												AIMap.put(aiName, (Class<? extends AISheet>) clazz);
												System.out.println("Successfully load ai class: " + aiName);
											}else{
												System.out.println("The same name ai is already loaded.");
												AIMap.put(aiName + " " + reduplicateIndex, (Class<? extends AISheet>) clazz);
												System.out.println("Named " + entry.getName() + " as " + aiName + " " + reduplicateIndex);
												System.out.println("Successfully load ai class: " + aiName + " " + reduplicateIndex);
												reduplicateIndex++;
											}
										}
									}catch (Exception e){
										System.out.println("Cannot load class: " + className);
										e.printStackTrace();
									}
								}
							});
						}catch (IOException e){
							System.out.println("Cannot open file: " + fileAI.getName());
							e.printStackTrace();
						}
					}catch (MalformedURLException e){
						System.out.println("Unknown errer factor");
						e.printStackTrace();
					}
				}
			}
		}else{
			System.out.println("Cannot detect the directory \"bin/ai\".");
			dirAI.mkdir();
			System.out.println("Creeated the directory \"bin/ai\".");
			System.out.println("Put jar file that contains ai class in the directiory to load custom AIs.");
		}
	}

}
