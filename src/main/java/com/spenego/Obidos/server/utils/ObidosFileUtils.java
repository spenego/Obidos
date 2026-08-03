/*
 * Copyright (C) 2016 - 2026 Spenego Software LLC. All rights reserved.
 *
 * This file is part of Obidos from Spenego Software LLC
 *
 * Obidos is dual-licensed under a commercial license and the GNU
 * Affero General Public License (AGPL) v3.0. For commercial licensing,
 * contact Spenego Software LLC at https://spenego.com/contacts.html.
 *
 * For AGPL licensing terms, see the LICENSE file in the project root
 * or <https://www.gnu.org/licenses/>.
 */

package com.spenego.Obidos.server.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.spenego.Obidos.shared.exceptions.ServerSideException;

/**
 * Copy content of directory in classpath to another directory Example: File
 * tempDir = Files.createTempDirectory("audit_report_template").toFile();
 * ObidosFileUtils.copyDirectoryContents("auditor_report_template", tempDir);
 * 
 * @author spgdev@spenego.com - Jul 28, 2024
 */
public class ObidosFileUtils
{
	private static final Logger logger = LoggerFactory.getLogger(ObidosFileUtils.class);

	protected ObidosFileUtils()
	{
	}

   /**
    * Gets the full path of a resource file from the classpath.
    * Note: it is very important the specify the filename correctly.
    * For example if filename is specified as "logo.png", it must
    * be in src/main/resources. If the logo.png is in 
    * src/main/resources/images/logo.png, the filename must be "images/logo.png"
    * 
    * @param filename The name of the file to locate
    * @return The full path of the file, or null if not found
	* <p>
	* @author spgdev@spenego.com - Aug 3, 2024
    */
	public static File getFileFromClassPath(final String fileName) throws IOException
	{
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL resourceUrl = classLoader.getResource(fileName);

		if (resourceUrl == null)
		{
//			throw new IOException("Resource not found: " + fileName);
		    throw new FileNotFoundException("Could not find resource: " + fileName);

		}
		if ("file".equals(resourceUrl.getProtocol())) {
			try
			{
				File file =  new File(resourceUrl.toURI());
				logger.info(() -> "MMM file found: " + file.getAbsolutePath());
				return file;
			} catch (URISyntaxException e) {
				throw new IOException("Invalid file URI", e);
			}
		}

		// For resources inside JARs or other non-file URLs
		try (InputStream inputStream = resourceUrl.openStream()) {
			Path tempFile = Files.createTempFile("resource-", fileName);
			Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
			return tempFile.toFile();
		}
	}
	
    /**
     * Gets a resource as an InputStream.
     * 
     * @param filename The name of the file to locate
     * @return An InputStream for the resource, or null if not found
     */
    public static InputStream getResourceAsStream(final String filename)
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return classLoader.getResourceAsStream(filename);
    }

	public static void copyDirectoryContents(String sourceDir, File destDir) throws ServerSideException
	{
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		Enumeration<URL> resources;
		try
		{
			resources = classLoader.getResources(sourceDir);
		} catch (IOException e)
		{
			throw new ServerSideException("Could not find directory in class path " + sourceDir + " :" + e);
		}

		logger.info(() -> "MMM source dir:" + sourceDir);
		while (resources.hasMoreElements())
		{
			URL resource = resources.nextElement();
			logger.info(() -> "MMM resources: " + resource);
			logger.info(() -> "MMM resource protocol: " + resource.getProtocol());

			if (resource.getProtocol().equals("file"))
			{
				File sourceFile = new File(resource.getFile());
				logger.info(() -> "MMM resource source file: " + sourceFile);
				copyDirectory(sourceFile, destDir);
			} else if (resource.getProtocol().equals("jar"))
			{
				String jarPath = resource.getPath().substring(5, resource.getPath().indexOf("!"));
				try(final JarFile jarfile = new JarFile(jarPath)) {
					copyDirectoryFromJar(jarfile, sourceDir, destDir);
				} catch (ServerSideException | IOException e) {
					throw new ServerSideException("Invalid jar file " + jarPath + " :" + e);
				}
			}
		}
	}

	private static void copyDirectory(File sourceDir, File destDir) throws ServerSideException
	{
		if (!destDir.exists())
		{
			destDir.mkdirs();
		}

		File[] files = sourceDir.listFiles();

		if (files != null)
		{
			for (File file : files)
			{
				File destFile = new File(destDir, file.getName());

				if (file.isDirectory())
				{
					copyDirectory(file, destFile);
				} else
				{
					try
					{
						Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
					} catch (IOException e)
					{
						throw new ServerSideException(
								"Could not copy " + file.toPath() + " to " + destFile.toPath() + " :" + e);
					}
				}
			}
		}
	}

	private static void copyDirectoryFromJar(JarFile jarFile, String sourceDir, File destDir) throws ServerSideException
	{
		Enumeration<JarEntry> entries = jarFile.entries();

		while (entries.hasMoreElements())
		{
			JarEntry entry = entries.nextElement();
			String name = entry.getName();

			if (name.startsWith(sourceDir) && !entry.isDirectory())
			{
				String relativePath = name.substring(sourceDir.length());
				File destFile = new File(destDir, relativePath);

				if (!destFile.getParentFile().exists())
				{
					destFile.getParentFile().mkdirs();
				}

				try (InputStream is = jarFile.getInputStream(entry))
				{
					Files.copy(is, destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e)
				{
					throw new ServerSideException("Could not copy to " + destFile.toPath() + " :" + e);
				}
			}
		}
	}

	// remove temporary directory and its contents
	public static void removeTempDirectory(File tempDir) throws IOException
	{
		if (tempDir.exists())
		{
			Files.walkFileTree(tempDir.toPath(), new SimpleFileVisitor<Path>()
			{
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
				{
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
				{
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				}
			});
		}
	}

	public static void appendToFile(File tempDir, String filename, String content) throws IOException
	{
		File file = new File(tempDir, filename);
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true)))
		{
			writer.write(content);
			writer.newLine();
		}
	}

	public static void createTextFile(File tempDir, String filename) throws IOException
	{
		File file = new File(tempDir, filename);
		if (!file.createNewFile())
		{
			throw new IOException("File already exists: " + filename);
		}
	}
}
