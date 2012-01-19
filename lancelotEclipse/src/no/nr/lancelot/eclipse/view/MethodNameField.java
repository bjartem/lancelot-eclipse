package no.nr.lancelot.eclipse.view;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.ui.views.markers.MarkerField;
import org.eclipse.ui.views.markers.MarkerItem;

public class MethodNameField extends MarkerField {
    private static final String ERROR_RESPONSE = "$$ERROR$$";
    
    @Override
    public String getValue(final MarkerItem item) {
        final IMethod method = LancelotMarkerUtil.findMethod(item.getMarker());
        return method != null ? method.getElementName() : ERROR_RESPONSE;
    }
}
