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

package org.metawidget.config.impl;

import java.io.InputStream;
import java.net.URL;

import javax.servlet.ServletContext;

import org.metawidget.inspector.iface.InspectorException;

/**
 * Specialized ResourceResolver for Servlets.
 * <p>
 * Resolves references by looking in <code>/WEB-INF/</code> first. Defined here, rather than in
 * <code>org.metawidget.jsp</code>, because needs to be shared by various Web-based frameworks
 * including GWT.
 *
 * @author Richard Kennard
 */

public class ServletResourceResolver
	extends SimpleResourceResolver {

	//
	// Private members
	//

	private ServletContext	mContext;

	//
	// Constructor
	//

	public ServletResourceResolver( ServletContext context ) {

		mContext = context;
	}

	//
	// Protected methods
	//

	/**
	 * Overridden to try <code>/WEB-INF/</code> first.
	 */

	@Override
	public InputStream openResource( String resource ) {

		try {
			URL url = mContext.getResource( "/WEB-INF/" + resource );

			if ( url != null ) {
				return url.openStream();
			}
		} catch ( Exception e ) {
			throw InspectorException.newException( e );
		}

		return super.openResource( resource );
	}
}
