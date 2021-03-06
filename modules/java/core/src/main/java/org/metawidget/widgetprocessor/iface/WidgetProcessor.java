// Metawidget (licensed under LGPL)
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

package org.metawidget.widgetprocessor.iface;

import java.util.Map;

import org.metawidget.iface.Immutable;

/**
 * Common interface implemented by all WidgetProcessors. WidgetProcessors allow arbitrary processing
 * of a widget following its building by the WidgetBuilder and before adding to the Layout.
 * <p>
 * WidgetProcessors must be immutable (or, at least, appear that way to clients. They can have
 * caches or configuration settings internally, as long as they are threadsafe). If they need to
 * store state, they should use the Metawidget passed to each method.
 * <p>
 * Note: WidgetProcessors are an example of the Strategy design pattern.
 *
 * @param <W>
 *            base class of widgets that this WidgetProcessor processes
 * @param <M>
 *            Metawidget that supports this WidgetProcessor
 * @author Richard Kennard
 */

public interface WidgetProcessor<W, M extends W>
	extends Immutable {

	//
	// Methods
	//

	/**
	 * Process the given widget. Called after a widget has been built by the
	 * <code>WidgetBuilder</code>, and before it is added to the <code>Layout</code>.
	 *
	 * @param widget
	 *            the widget to process. Never null
	 * @param elementName
	 *            XML node name of the business field. Typically 'entity', 'property' or 'action'.
	 *            Never null
	 * @param attributes
	 *            attributes of the widget to process. Never null. This Map is modifiable - changes
	 *            will be passed to subsequent WidgetProcessors and Layouts
	 * @param metawidget
	 *            the parent Metawidget. Never null
	 * @return generally the original widget (as passed in to the first argument). Can be a
	 *         different widget if the WidgetProcessor wishes to substitute the original widget for
	 *         another. Can be null if the WidgetProcessor wishes to cancel all further processing
	 *         of this widget (including laying out)
	 */

	W processWidget( W widget, String elementName, Map<String, String> attributes, M metawidget );
}
