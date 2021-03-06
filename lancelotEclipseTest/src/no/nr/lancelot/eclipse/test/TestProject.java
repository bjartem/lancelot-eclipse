/*******************************************************************************
 * Copyright (c) 2011 Norwegian Computing Center.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Norwegian Computing Center - initial API and implementation
 ******************************************************************************/
package no.nr.lancelot.eclipse.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.osgi.framework.Bundle;

public final class TestProject {
	public static final String TEST_PROJECTS_PKG_PREFIX = "no.nr.lancelot.eclipse.testdata";
	
    public interface TestProjectThunk {
        public void run(TestProject testProject) throws Exception;
    }
    
    private static final IPath RT_JAR_PATH;
    
    static {
    	final String sep = File.separator;
    	RT_JAR_PATH = Path.fromOSString(
    	    System.getProperty("java.home") + sep + "lib" + sep + "rt.jar"
        );
    	if (!RT_JAR_PATH.toFile().exists()) {
    		throw new RuntimeException("Test error. rt.jar lookup failed.");
    	}
    }
    
    private final IProject project;
    private final IJavaProject javaProject;
    private final IPackageFragmentRoot rootPackage;
    private final IFolder sourceFolder;
    private final IFolder outputFolder;

    public TestProject(final String name) throws CoreException {
        final IWorkspace workspace = ResourcesPlugin.getWorkspace();
        final IWorkspaceRoot root = workspace.getRoot();
        
        project = root.getProject(name);
        project.create(null);
        project.open(null);    
        addJavaNature();
        
        javaProject = JavaCore.create(project);
        sourceFolder = createSourceFolder();
        rootPackage = createRootPackage();
        outputFolder = createOutputFolder();
        setClassPath();
    }
    
    private void addJavaNature() throws CoreException {
        final IProjectDescription desc = project.getDescription();
        desc.setNatureIds(new String[]{ JavaCore.NATURE_ID });
        project.setDescription(desc, new NullProgressMonitor());
    }
    
    private IFolder createSourceFolder() throws CoreException {
        final IFolder folder = project.getFolder("src");
        folder.create(false, true, new NullProgressMonitor());
        return folder;
    }
    
    private IPackageFragmentRoot createRootPackage() {
        return javaProject.getPackageFragmentRoot(sourceFolder);
    }
    
    private IFolder createOutputFolder() throws CoreException {
       final IFolder folder = project.getFolder("bin");
       folder.create(false, true, new NullProgressMonitor());
       final IPath binPath = folder.getFullPath();
       javaProject.setOutputLocation(binPath, null);
       return folder;
    }
    
    private void setClassPath() throws JavaModelException {
        javaProject.setRawClasspath(new IClasspathEntry[]{ 
            JavaCore.newSourceEntry(rootPackage.getPath()),    
            JavaCore.newLibraryEntry(RT_JAR_PATH, null, null)
        } , new NullProgressMonitor());
    }
    
    
    public IProject getProject() {
        return project;
    }

    public IJavaProject getJavaProject() {
        return javaProject;
    }
    
    public IPackageFragment createPackage(final String name) throws CoreException {
        return rootPackage.createPackageFragment(name, true, new NullProgressMonitor());
    }
    
    public IType createType(
        final IPackageFragment pack, 
        final String compilationUnitName, 
        final String source
    ) throws JavaModelException {
        final String fullSource = "package " + pack.getElementName() + ";\n" + source;
        final ICompilationUnit compilationUnit = pack.createCompilationUnit(
            compilationUnitName, 
            fullSource, 
            true, 
            new NullProgressMonitor()
        );
        return compilationUnit.findPrimaryType();
    }    
    
    public void fullBuild() throws CoreException {
        project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
    }
    
    public void checkFullBuild() throws CoreException {
        fullBuild();
        if (!javaProject.hasBuildState()) 
            throw new RuntimeException();
    }
    
    public void delete() throws CoreException {
        project.delete(true, true, null);
    }

    public IFile findOutputFile(final String relativePath) {
        final IFile file = outputFolder.getFile(relativePath);
        if (!file.exists())
            throw new RuntimeException("findOutputFile failed. File does not exist. relativePath: " 
                                       + relativePath + ".");
        return file;
    }
    
    public List<IClassFile> getAllClassFiles() throws CoreException {
        final List<IClassFile> res = new LinkedList<IClassFile>();
        outputFolder.accept(new IResourceVisitor() {
            @Override
            public boolean visit(final IResource resource) throws CoreException {
                if (!(resource instanceof IFile)) 
                    return true;
                
                IFile fileResource = (IFile) resource;
                if (!fileResource.getName().endsWith(".class"))
                    return false;
                
                res.add(JavaCore.createClassFileFrom(fileResource));
                return false;
            }
        });
        return res;
    }
    
    public IClassFile findClassFile(final String relativePath) {
        return JavaCore.createClassFileFrom(findOutputFile(relativePath));
    }

    public IFile findSourceFile(final String relativePath) {
        final IFile file = sourceFolder.getFile(relativePath);
        if (!file.exists())
            throw new RuntimeException("findSourceFile failed. File does not exist. relativePath: '" 
                                       + relativePath + "'.");
        return file;
    }
    
    public IType findSourceType(final String relativePath) {
        try {
            return (IType) JavaCore.create(findSourceFile(relativePath));
        } catch (ClassCastException e) {
            throw new RuntimeException("IJavaElement found for relativePath \"" + relativePath + 
                                       "\" is not an IType instance.");
        }
    }
    
    public static TestProject createTestProjectFromScenario(final String name) throws Exception {
        final TestProject testProject = new TestProject("Test project for " + name);
        
        final Bundle bundle = Platform.getBundle("no.nr.lancelot.eclipseTest");
        final IPath path = new Path("test-data-src/no/nr/lancelot/eclipse/testdata/" + name); 
        final URL sourceUrl = FileLocator.find(bundle, path, null);
        if (sourceUrl == null) 
        	throw new Exception("Test error, could not locate test project '" + name + "'.");
        
        final IFileStore rootStore = EFS.getStore(FileLocator.resolve(sourceUrl).toURI());
        new RecursiveSourceFileAdder(testProject).addAll(
            rootStore, 
            TEST_PROJECTS_PKG_PREFIX
        );
        
        return testProject;
    }
    
    private static final class RecursiveSourceFileAdder {
        private final TestProject testProject;

        RecursiveSourceFileAdder(final TestProject testProject) {
            this.testProject = testProject;
        }

        void addAll(final IFileStore store, final String packageName) 
        throws CoreException, IOException {
            final IFileInfo info = store.fetchInfo();
            final String name = info.getName();
            
            if (name.endsWith(".java")) {
                final IPackageFragment packageFragment = testProject.createPackage(packageName);
                packageFragment.createCompilationUnit(
                    name, 
                    readSource(store), 
                    true, 
                    new NullProgressMonitor()
                );
            } else if (info.isDirectory()) {
                final String subPackageName = packageName + "." + name;
                for (final IFileStore childStore : store.childStores(EFS.NONE, null)) 
                    addAll(childStore, subPackageName);
            }
        }
        
        private String readSource(final IFileStore store) throws CoreException, IOException {
            final StringBuilder sb = new StringBuilder();
            final BufferedReader reader = new BufferedReader(
                                              new InputStreamReader(
                                                  store.openInputStream(EFS.NONE, null),
                                                  Charset.forName("UTF-8")
                                              )
                                          );
            for (String line; (line = reader.readLine()) != null; )
                sb.append(line + "\n");
            return sb.toString();
        }
    }
}