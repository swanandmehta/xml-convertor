/**
 * 
 */
package com.liberty.main;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

/**
 * @author swanandm
 *
 */
public class Application {
	
	public static void main(String[] args) throws JsonSyntaxException, JsonIOException, SAXException, IOException, ParserConfigurationException, TransformerException {
		File input = new File("D:\\Dignostic\\kpit-mdx-parser\\xml-parser\\src\\main\\resources\\input.xml");
		File inputSchema = new File("D:\\Dignostic\\kpit-mdx-parser\\xml-parser\\src\\main\\resources\\input.xsd");
		
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		
		Validator validator = factory.newSchema(inputSchema).newValidator();
		validator.validate(new StreamSource(input));
		
		File prop = new File("D:\\Dignostic\\kpit-mdx-parser\\xml-parser\\src\\main\\resources\\mapping.json");
		Gson gson = new Gson();
		Map<String, String> props = gson.fromJson(new FileReader(prop), HashMap.class);
		
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document outputDoc = builder.newDocument();
		Document inputDoc = builder.parse(input);  
		
		for(Map.Entry<String, String> entry : props.entrySet()) {
			String outputPath = entry.getKey();
			String inputPath = entry.getValue();
			
			updateOutput(outputDoc, inputDoc, outputPath, inputPath);
		}
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        DOMSource domSource = new DOMSource(outputDoc);
        StreamResult streamResult = new StreamResult(new File("D:\\Dignostic\\kpit-mdx-parser\\xml-parser\\src\\main\\resources\\ouput.xml"));
        transformer.transform(domSource, streamResult);
		
	}

	private static void updateOutput(Document outputDoc, Document inputDoc, String outputPath, String inputPath) {
		
		String[] inPathSegments = inputPath.split("->");
		Element inRoot = inputDoc.getDocumentElement();
		for(String path: inPathSegments) {
			if(!path.equals(inPathSegments[0])) {
				inRoot = (Element) inRoot.getElementsByTagName(path).item(0);
			}
		}
		
		String[] outPathSegments = outputPath.split("->");
		Element outRoot = outputDoc.getDocumentElement();
		for(String path: outPathSegments) {
			Element element ;
			
			if(outRoot == null) {
				element = outputDoc.createElement(path);
				outputDoc.appendChild(element);
				outRoot = element;
			} else if (!path.equals(outPathSegments[0])){
				if(outRoot.getElementsByTagName(path).getLength() == 0) {
					element = outputDoc.createElement(path);
					
					if(path.contentEquals(outPathSegments[outPathSegments.length - 1])) {
						element.setTextContent(inRoot.getTextContent());
					}
					
					outRoot.appendChild(element);
					outRoot = element;
				}
			}
		}
		
	}

}
