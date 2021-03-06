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

package org.metawidget.inspector.impl;

import java.lang.annotation.Annotation;

/**
 * Convenience implementation for Traits.
 * <p>
 * Handles construction, and returning names.
 *
 * @author Richard Kennard
 */

public abstract class BaseTrait
	implements Trait {

	//
	// Private methods
	//

	private String	mName;

	//
	// Constructor
	//

	public BaseTrait( String name ) {

		mName = name;
	}

	//
	// Public methods
	//

	public String getName() {

		return mName;
	}

	public boolean isAnnotationPresent( Class<? extends Annotation> annotation ) {

		return ( getAnnotation( annotation ) != null );
	}

	@Override
	public String toString() {

		return mName;
	}
}
