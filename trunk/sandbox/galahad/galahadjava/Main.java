package galahadjava;

import java.io.FileInputStream;
import java.lang.System;
import java.util.Properties;

import org.python.core.Py;
import org.python.core.PyException;
import org.python.core.PyFile;
import org.python.core.PySystemState;
import org.python.util.JLineConsole;
import org.python.util.InteractiveConsole;
import org.python.util.InteractiveInterpreter;

public class Main {
    private static InteractiveConsole newInterpreter(
        boolean isInteractiveStdin
    ){
        if (!isInteractiveStdin) {
            return new InteractiveConsole();
        }

        final String interpClass = 
                        PySystemState.registry.getProperty("python.console", "");
        if (interpClass.length() > 0) {
            try {
                return (InteractiveConsole)Class.forName(interpClass)
                       .newInstance();
            } catch (Throwable t) {
                // Ignore.
            }
        }
        return new JLineConsole();
    }

    public static void main(String[] args) throws PyException {
        PySystemState.initialize(
            PySystemState.getBaseProperties(), 
            new Properties(), 
            args
        );

        PySystemState systemState = Py.getSystemState();
        boolean isInteractive = ((PyFile)Py.defaultSystemState.stdin).isatty();
        if (!isInteractive) {
            systemState.ps1 = systemState.ps2 = Py.EmptyString;
        }

        InteractiveConsole interp = newInterpreter(isInteractive);
        systemState.__setattr__("_jy_interpreter", Py.java2py(interp));
        interp.exec("try:\n    import entrypoint\n    entrypoint.run()\n" + 
                    "except SystemExit:\n    pass");
    }
}
