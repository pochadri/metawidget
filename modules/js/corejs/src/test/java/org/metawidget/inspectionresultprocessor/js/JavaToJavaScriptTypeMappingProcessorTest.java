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

package org.metawidget.inspectionresultprocessor.js;

import junit.framework.TestCase;

/**
 * @author Richard Kennard
 */

public class JavaToJavaScriptTypeMappingProcessorTest
	extends TestCase {

	//
	// Public methods
	//

	public void testMapping()
		throws Exception {

		// Set up

		String inputXml = "<?xml version=\"1.0\"?>";
		inputXml += "<inspection-result xmlns=\"http://metawidget.org/inspection-result\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://metawidget.org/inspection-result http://metawidget.org/xsd/inspection-result-1.0.xsd\" version=\"1.0\">";
		inputXml += "<entity type=\"Foo\">";
		inputXml += "<property name=\"foo\" type=\"java.lang.String\"/>";
		inputXml += "<property name=\"bar\" type=\"java.lang.Double\"/>";
		inputXml += "<property name=\"baz\" type=\"java.util.HashSet\"/>";
		inputXml += "<property name=\"abc\" type=\"[B\"/>";
		inputXml += "<property name=\"def\" type=\"java.util.LinkedList\"/>";
		inputXml += "<property name=\"ghi\" type=\"java.lang.Boolean\"/>";
		inputXml += "<property name=\"jkl\" type=\"java.lang.Character\"/>";
		inputXml += "</entity></inspection-result>";

		// Run processor

		JavaToJavaScriptTypeMappingProcessor<Object> processor = new JavaToJavaScriptTypeMappingProcessor<Object>();
		String outputXml = processor.processInspectionResult( inputXml, null, null, null );

		// Test result

		String validateXml = "<inspection-result xmlns=\"http://metawidget.org/inspection-result\" version=\"1.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://metawidget.org/inspection-result http://metawidget.org/xsd/inspection-result-1.0.xsd\">";
		validateXml += "<entity type=\"Foo\">";
		validateXml += "<property name=\"foo\" type=\"string\"/>";
		validateXml += "<property name=\"bar\" type=\"number\"/>";
		validateXml += "<property name=\"baz\" type=\"array\"/>";
		validateXml += "<property name=\"abc\" type=\"array\"/>";
		validateXml += "<property name=\"def\" type=\"array\"/>";
		validateXml += "<property name=\"ghi\" type=\"boolean\"/>";
		validateXml += "<property name=\"jkl\" type=\"string\"/>";
		validateXml += "</entity>";
		validateXml += "</inspection-result>";

		assertEquals( validateXml, outputXml );
	}
}
