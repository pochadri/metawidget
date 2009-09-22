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

package org.metawidget.util;

import java.util.List;

import junit.framework.TestCase;

import org.metawidget.util.XmlUtils.CachingContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Richard Kennard
 */

public class XmlUtilsTest
	extends TestCase
{
	//
	// Public methods
	//

	public void testCachingContentHandler()
		throws Exception
	{
		SimpleContentHandler simpleContentHandler = new SimpleContentHandler();
		CachingContentHandler cachingContentHandler = new CachingContentHandler( simpleContentHandler );

		try
		{
			cachingContentHandler.replay( simpleContentHandler );
			assertTrue( false );
		}
		catch ( SAXException e )
		{
			assertTrue( "Nothing to replay. Not cached any SAX events".equals( e.getMessage() ) );
		}

		// Check delegate ready

		assertTrue( simpleContentHandler == cachingContentHandler.getDelegate() );

		// Fire events

		cachingContentHandler.startDocument();
		cachingContentHandler.processingInstruction( "pi-target", "pi-data" );
		cachingContentHandler.skippedEntity( "se-name" );
		cachingContentHandler.startPrefixMapping( "spm-prefix", "spm-uri" );
		cachingContentHandler.endPrefixMapping( "epm-prefix" );
		AttributesImpl attributes = new AttributesImpl();
		attributes.addAttribute( "a-uri", "a-localName", "a-qName", "a-type", "a-value" );
		cachingContentHandler.startElement( "se-uri", "se-localName", "se-name", attributes );
		char[] characters = "c-characters".toCharArray();
		cachingContentHandler.characters( characters, 0, 12 );
		char[] ignorableWhitespaceCharacters = "iw-characters".toCharArray();
		cachingContentHandler.ignorableWhitespace( ignorableWhitespaceCharacters, 0, 13 );
		cachingContentHandler.endElement( "ee-uri", "ee-localName", "ee-name" );
		cachingContentHandler.endDocument();

		// Check delegate field was released

		assertTrue( null == cachingContentHandler.getDelegate() );

		try
		{
			cachingContentHandler.startDocument();
			assertTrue( false );
		}
		catch ( SAXException e )
		{
			assertTrue( "Already cached SAX events. CachingContentHandler can only cache SAX events once".equals( e.getMessage() ) );
		}

		// Check they got delegated

		assertSimpleContentHandler( simpleContentHandler );

		// Check defensive copy

		attributes.clear();

		for( int loop = 0, length = characters.length; loop < length; loop++ )
		{
			characters[loop] = '_';
		}

		for( int loop = 0, length = ignorableWhitespaceCharacters.length; loop < length; loop++ )
		{
			ignorableWhitespaceCharacters[loop] = '_';
		}

		// Replay

		SimpleContentHandler newSimpleContentHandler = new SimpleContentHandler();
		cachingContentHandler.replay( newSimpleContentHandler );

		// Check delegate field was not used

		assertTrue( null == cachingContentHandler.getDelegate() );

		// Check they got replayed

		assertSimpleContentHandler( newSimpleContentHandler );
	}

	public void testCachingContentHandlerFragment()
		throws Exception
	{
		SimpleContentHandler simpleContentHandler = new SimpleContentHandler();
		CachingContentHandler cachingContentHandler = new CachingContentHandler( simpleContentHandler );
		cachingContentHandler.startElement( "se-uri", "se-localName", "se-name", new AttributesImpl() );
		cachingContentHandler.endElement( "ee-uri", "ee-localName", "ee-name" );

		SimpleContentHandler newSimpleContentHandler = new SimpleContentHandler();
		cachingContentHandler.replay( newSimpleContentHandler );

		assertTrue( newSimpleContentHandler.mEvents.size() == 2 );
		assertTrue( "startElement".equals( simpleContentHandler.mEvents.get( 0 )[0] ) );
		assertTrue( "se-uri".equals( simpleContentHandler.mEvents.get( 0 )[1] ) );
		assertTrue( "se-localName".equals( simpleContentHandler.mEvents.get( 0 )[2] ) );
		assertTrue( "se-name".equals( simpleContentHandler.mEvents.get( 0 )[3] ) );
		assertTrue( 0 == ( (Attributes) simpleContentHandler.mEvents.get( 0 )[4] ).getLength() );
		assertTrue( "endElement".equals( simpleContentHandler.mEvents.get( 1 )[0] ) );
		assertTrue( "ee-uri".equals( simpleContentHandler.mEvents.get( 1 )[1] ) );
		assertTrue( "ee-localName".equals( simpleContentHandler.mEvents.get( 1 )[2] ) );
		assertTrue( "ee-name".equals( simpleContentHandler.mEvents.get( 1 )[3] ) );
	}

	//
	// Private members
	//

	private void assertSimpleContentHandler( SimpleContentHandler simpleContentHandler )
	{
		assertTrue( simpleContentHandler.mEvents.size() == 10 );
		assertTrue( "startDocument".equals( simpleContentHandler.mEvents.get( 0 )[0] ) );
		assertTrue( "processingInstruction".equals( simpleContentHandler.mEvents.get( 1 )[0] ) );
		assertTrue( "pi-target".equals( simpleContentHandler.mEvents.get( 1 )[1] ) );
		assertTrue( "pi-data".equals( simpleContentHandler.mEvents.get( 1 )[2] ) );
		assertTrue( "skippedEntity".equals( simpleContentHandler.mEvents.get( 2 )[0] ) );
		assertTrue( "se-name".equals( simpleContentHandler.mEvents.get( 2 )[1] ) );
		assertTrue( "startPrefixMapping".equals( simpleContentHandler.mEvents.get( 3 )[0] ) );
		assertTrue( "spm-prefix".equals( simpleContentHandler.mEvents.get( 3 )[1] ) );
		assertTrue( "spm-uri".equals( simpleContentHandler.mEvents.get( 3 )[2] ) );
		assertTrue( "endPrefixMapping".equals( simpleContentHandler.mEvents.get( 4 )[0] ) );
		assertTrue( "epm-prefix".equals( simpleContentHandler.mEvents.get( 4 )[1] ) );
		assertTrue( "startElement".equals( simpleContentHandler.mEvents.get( 5 )[0] ) );
		assertTrue( "se-uri".equals( simpleContentHandler.mEvents.get( 5 )[1] ) );
		assertTrue( "se-localName".equals( simpleContentHandler.mEvents.get( 5 )[2] ) );
		assertTrue( "se-name".equals( simpleContentHandler.mEvents.get( 5 )[3] ) );
		assertTrue( "a-value".equals( ( (Attributes) simpleContentHandler.mEvents.get( 5 )[4] ).getValue( "a-uri", "a-localName" ) ) );
		assertTrue( "a-type".equals( ( (Attributes) simpleContentHandler.mEvents.get( 5 )[4] ).getType( "a-uri", "a-localName" ) ) );
		assertTrue( 1 == ( (Attributes) simpleContentHandler.mEvents.get( 5 )[4] ).getLength() );
		assertTrue( "characters".equals( simpleContentHandler.mEvents.get( 6 )[0] ) );
		assertTrue( "c-characters".equals( String.valueOf( (char[]) simpleContentHandler.mEvents.get( 6 )[1] ) ) );
		assertTrue( 0 == (Integer) simpleContentHandler.mEvents.get( 6 )[2] );
		assertTrue( 12 == (Integer) simpleContentHandler.mEvents.get( 6 )[3] );
		assertTrue( "ignorableWhitespace".equals( simpleContentHandler.mEvents.get( 7 )[0] ) );
		assertTrue( "iw-characters".equals( String.valueOf( (char[]) simpleContentHandler.mEvents.get( 7 )[1] ) ) );
		assertTrue( 0 == (Integer) simpleContentHandler.mEvents.get( 7 )[2] );
		assertTrue( 13 == (Integer) simpleContentHandler.mEvents.get( 7 )[3] );
		assertTrue( "endElement".equals( simpleContentHandler.mEvents.get( 8 )[0] ) );
		assertTrue( "ee-uri".equals( simpleContentHandler.mEvents.get( 8 )[1] ) );
		assertTrue( "ee-localName".equals( simpleContentHandler.mEvents.get( 8 )[2] ) );
		assertTrue( "ee-name".equals( simpleContentHandler.mEvents.get( 8 )[3] ) );
		assertTrue( "endDocument".equals( simpleContentHandler.mEvents.get( 9 )[0] ) );
	}

	//
	// Inner class
	//

	/* package private */static class SimpleContentHandler
		extends DefaultHandler
	{
		//
		// Public members
		//

		public List<Object[]>	mEvents	= CollectionUtils.newArrayList();

		//
		// Public methods
		//

		@Override
		public void startDocument()
		{
			mEvents.add( new Object[] { "startDocument" } );
		}

		@Override
		public void processingInstruction( String target, String data )
		{
			mEvents.add( new Object[] { "processingInstruction", target, data } );
		}

		@Override
		public void skippedEntity( String name )
		{
			mEvents.add( new Object[] { "skippedEntity", name } );
		}

		@Override
		public void startPrefixMapping( String prefix, String uri )
		{
			mEvents.add( new Object[] { "startPrefixMapping", prefix, uri } );
		}

		@Override
		public void endPrefixMapping( String prefix )
		{
			mEvents.add( new Object[] { "endPrefixMapping", prefix } );
		}

		@Override
		public void startElement( String uri, String localName, String name, Attributes attributes )
		{
			mEvents.add( new Object[] { "startElement", uri, localName, name, new AttributesImpl( attributes ) } );
		}

		@Override
		public void characters( char[] characters, int start, int length )
		{
			mEvents.add( new Object[] { "characters", characters, start, length } );
		}

		@Override
		public void ignorableWhitespace( char[] characters, int start, int length )
		{
			mEvents.add( new Object[] { "ignorableWhitespace", characters, start, length } );
		}

		@Override
		public void endElement( String uri, String localName, String name )
		{
			mEvents.add( new Object[] { "endElement", uri, localName, name } );
		}

		@Override
		public void endDocument()
		{
			mEvents.add( new Object[] { "endDocument" } );
		}
	}
}
