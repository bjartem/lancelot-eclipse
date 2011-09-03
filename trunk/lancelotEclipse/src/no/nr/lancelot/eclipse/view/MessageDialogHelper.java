package no.nr.lancelot.eclipse.view;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public final class MessageDialogHelper {
    private MessageDialogHelper() {}
    
    public static void openInformation(final String title, final String message) {
        MessageDialog.openInformation(getWorkbenchShellOrNull(), title, message);
    }
    
    private static Shell getWorkbenchShellOrNull() {
        final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        return window != null ? window.getShell() : null;
    }
}
