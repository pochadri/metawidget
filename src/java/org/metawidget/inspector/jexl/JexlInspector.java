// Metawidget
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

package org.metawidget.inspector.jexl;

import java.util.Map;

import org.apache.commons.jexl.ExpressionFactory;
import org.apache.commons.jexl.JexlContext;
import org.apache.commons.jexl.JexlHelper;
import org.metawidget.faces.FacesUtils;
import org.metawidget.inspector.iface.InspectorException;
import org.metawidget.inspector.impl.BaseObjectInspector;
import org.metawidget.inspector.impl.BaseObjectInspectorConfig;
import org.metawidget.inspector.impl.actionstyle.Action;
import org.metawidget.inspector.impl.propertystyle.Property;
import org.metawidget.util.CollectionUtils;
import org.metawidget.util.simple.StringUtils;

/**
 * Inspects annotations defined by Metawidget's JEXL support (declared in this same package).
 * <p>
 * Many Web environments, such as Java Server Faces and Java Server Pages, supply their own
 * Expression Language (EL) - but most desktop and mobile environments do not. JEXL is a
 * lightweight, standalone Expression Language for use in Java applications. Using JEXL, non-Web
 * environments can 'wire together' properties using ELs in much the same way as Web environments
 * that use, say, <code>UiFacesAttribute</code>.
 *
 * @author Richard Kennard
 */

public class JexlInspector
	extends BaseObjectInspector
{
	//
	//
	// Constructor
	//
	//

	public JexlInspector()
	{
		this( new BaseObjectInspectorConfig() );
	}

	public JexlInspector( BaseObjectInspectorConfig config )
	{
		super( config );
	}

	//
	//
	// Protected methods
	//
	//

	@Override
	protected Map<String, String> inspectProperty( Property property, Object toInspect )
		throws Exception
	{
		Map<String, String> attributes = CollectionUtils.newHashMap();
		JexlContext context = null;

		// Note: this is all annotation based. We could imagine an XML version, but we'd have
		// to figure out a nice format for 'name="value" with condition'

		// UiJexlAttribute

		UiJexlAttribute jexlAttribute = property.getAnnotation( UiJexlAttribute.class );

		if ( jexlAttribute != null )
		{
			context = createContext( toInspect );
			putJexlAttribute( context, attributes, jexlAttribute );
		}

		// UiJexlAttributes

		UiJexlAttributes JexlAttributes = property.getAnnotation( UiJexlAttributes.class );

		if ( JexlAttributes != null )
		{
			if ( context == null )
				context = createContext( toInspect );

			for ( UiJexlAttribute nestedJexlAttribute : JexlAttributes.value() )
			{
				putJexlAttribute( context, attributes, nestedJexlAttribute );
			}
		}

		return attributes;
	}

	@Override
	protected Map<String, String> inspectAction( Action action, Object toInspect )
		throws Exception
	{
		Map<String, String> attributes = CollectionUtils.newHashMap();
		JexlContext context = null;

		// Note: this is all annotation based. We could imagine an XML version, but we'd have
		// to figure out a nice format for 'name="value" with condition'

		// UiJexlAttribute

		UiJexlAttribute jexlAttribute = action.getAnnotation( UiJexlAttribute.class );

		if ( jexlAttribute != null )
		{
			context = createContext( toInspect );
			putJexlAttribute( context, attributes, jexlAttribute );
		}

		// UiJexlAttributes

		UiJexlAttributes JexlAttributes = action.getAnnotation( UiJexlAttributes.class );

		if ( JexlAttributes != null )
		{
			if ( context == null )
				context = createContext( toInspect );

			for ( UiJexlAttribute nestedJexlAttribute : JexlAttributes.value() )
			{
				putJexlAttribute( context, attributes, nestedJexlAttribute );
			}
		}

		return attributes;
	}

	protected void putJexlAttribute( JexlContext context, Map<String, String> attributes, UiJexlAttribute jexlAttribute )
		throws Exception
	{
		// Optional condition

		String condition = jexlAttribute.condition();

		if ( !"".equals( condition ) )
		{
			if ( !FacesUtils.isValueReference( condition ) )
				throw InspectorException.newException( "Condition '" + condition + "' is not of the form ${...}" );

			Object conditionResult = ExpressionFactory.createExpression( JexlUtils.unwrapValueReference( condition ) ).evaluate( context );

			if ( !Boolean.TRUE.equals( conditionResult ) )
				return;
		}

		// Optionally expression-based

		String value = jexlAttribute.value();

		if ( JexlUtils.isValueReference( value ) )
			value = StringUtils.quietValueOf( ExpressionFactory.createExpression( JexlUtils.unwrapValueReference( value ) ).evaluate( context ) );

		// Set the value

		attributes.put( jexlAttribute.name(), StringUtils.quietValueOf( value ) );
	}

	/**
	 * Prepare the JexlContext.
	 * <p>
	 * Subclasses can override this method to control what is available in the context.
	 */

	protected JexlContext createContext( Object toInspect )
	{
		JexlContext context = JexlHelper.createContext();
		@SuppressWarnings( "unchecked" )
		Map<String, Object> contextMap = context.getVars();

		// Put the toInspect in under its simple Class name

		if ( toInspect != null )
			contextMap.put( StringUtils.lowercaseFirstLetter( toInspect.getClass().getSimpleName() ), toInspect );

		return context;
	}
}
