package de.unima.ki.pmmc.evaluator.model.parser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import de.unima.ki.pmmc.evaluator.model.Activity;
import de.unima.ki.pmmc.evaluator.model.Model;



public class EPMLParser implements Parser {
	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		
		EPMLParser parser = new EPMLParser();
		Model model = parser.parse("src/main/resources/data/dataset3/models/model2.epml");
		for(Activity a : model.getActivities()) {
			System.out.println(a);
		}
		System.out.println(model.getActivities().size());
	}
	
	
	
	
	public Model parse(String filepath) throws ParserConfigurationException, SAXException, IOException {
		
		
		Model pm = new Model();
		
		//Get the DOM Builder Factory
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		//Get the DOM Builder
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputStream in = new FileInputStream(filepath);
		
		Document document = builder.parse(in);

		
		NodeList eventList = document.getDocumentElement().getElementsByTagName("event");
		NodeList functionList = document.getDocumentElement().getElementsByTagName("function");
		
		for (int i = 0; i < eventList.getLength(); i++) {
			Node task = eventList.item(i);
			NodeList nameList = task.getChildNodes();
			for (int j = 0; j < nameList.getLength(); j++) {
				Node nameNode = nameList.item(j);
				String id = task.getAttributes().getNamedItem("id").getNodeValue();
				String name = nameNode.getTextContent();
				if(!name.trim().isEmpty()) {
					Activity activity = new Activity(id, name);
					pm.addActivity(activity);
				}
			}
		}
		for (int i = 0; i < functionList.getLength(); i++) {
			Node task = functionList.item(i);
			NodeList nameList = task.getChildNodes();
			for (int j = 0; j < nameList.getLength(); j++) {
				Node nameNode = nameList.item(j);
				String id = task.getAttributes().getNamedItem("id").getNodeValue();
				String name = nameNode.getTextContent();
				if(!name.trim().isEmpty()) {
					Activity activity = new Activity(id, name);
					pm.addActivity(activity);
				}
			}
		}
		return pm;
		
	}
	
	
	
	/*
	public ProcessModel parse(String filepath, String repository) throws Exception {		
		//Get the DOM Builder Factory
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		//Get the DOM Builder
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputStream in = new FileInputStream(filepath);
		
		Document document = builder.parse(in);
		NodeList nodeList = document.getDocumentElement().getChildNodes();

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeName().equals("process")) {
				onProcess(node);
			} else if (node.getNodeName().equals("collaboration")) {
				onCollaboration(node);
			}
		}
		
		ProcessModel pm = new ProcessModel();
		String processName = FilenameUtils.removeExtension(new File(filepath).getName()).replaceAll(" ", "-");
		String namespace = "http://" + repository + ".process/" + processName + "#";
		pm.setNsBase(namespace);
		pm.setId(namespace+processName);
		pm.setName(processName);
		
		for(GenericNode node:nodes.values()){
			if(node.getType()==null) {
				System.out.println("Unhandled node type in bpmn file for id " + node.getId() + ".");
			}
			else if(node.getType().equals("activity")) {
				Activity activity = new Activity(pm);
				activity.setId(namespace+node.getId());
				activity.setName(node.getLabel());
				activity.setAttributes(node.getAttributes());
				pm.getNodes().add(activity);
			} else if(node.getType().endsWith("event")) {
				Event event = null;
				System.out.println(node.getType());
				if(node.getType().startsWith("start")) {
					event = new Event(pm, EventType.Start);
				} else if(node.getType().startsWith("end")) {
					event = new Event(pm, EventType.End);
				} else {
					event = new Event(pm, EventType.Intermediate);
				} 
				event.setId(namespace+node.getId());
				event.setName(node.getLabel());
				event.setAttributes(node.getAttributes());
				pm.getNodes().add(event);
			} else if(node.getType().endsWith("gateway")) {
				Gateway gateway = null;
				if(node.getType().startsWith("AND")) {
					gateway = new Gateway(pm, GatewayType.AND);
				} else if(node.getType().startsWith("XOR")) {
					gateway = new Gateway(pm, GatewayType.XOR);
				} else {
					gateway = new Gateway(pm, GatewayType.OR);
				}
				gateway.setId(namespace+node.getId());
				gateway.setAttributes(node.getAttributes());
				pm.getNodes().add(gateway);
			}
		}
		
		for(GenericFlow genericFlow:flows.values()) {
			api.process.buildingblock.Node from = pm.searchNodeById(namespace+genericFlow.getFrom().getId());
			api.process.buildingblock.Node to = pm.searchNodeById(namespace+genericFlow.getTo().getId());
			if(from==null || to==null) {
				System.out.println("Inconsistent BPMN File." + from + ", " +  to.getId());
				return null;
			}
			
			Flow flow = new Flow(pm, from, to);
			flow.setId(namespace+genericFlow.getId());

		}
		
		//flows
		
		return pm;
	}

	private void onCollaboration(Node parentNode) {
		for(int i=0; i<parentNode.getChildNodes().getLength(); ++i) {
			Node childNode = parentNode.getChildNodes().item(i);
			if(childNode.getNodeName().equals("messageFlow")) {
				NamedNodeMap childNodeAttributes = childNode.getAttributes();
				String id = childNodeAttributes.getNamedItem("id").getNodeValue();
				GenericFlow flow = getFlow(id);
				
				String sourceId = childNodeAttributes.getNamedItem("sourceRef").getNodeValue();
				GenericNode sourceNode = getNode(sourceId);
				flow.setFrom(sourceNode);
				String targetId = childNodeAttributes.getNamedItem("targetRef").getNodeValue();
				GenericNode targetNode = getNode(targetId);
				flow.setTo(targetNode);
			}
		}
	}
	
	private void onProcess(Node parentNode) {
		NamedNodeMap attributes = parentNode.getAttributes();
		String organization = "";
		if(attributes.getNamedItem("name")!=null) {
			organization = attributes.getNamedItem("name").getNodeValue();
		}
		
		NodeList nodeList = parentNode.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeName().equals("laneSet")) {
				onLaneSet(node, organization);
			} else if (node.getNodeName().equals("task")) {
				onTask(node);
			} else if (node.getNodeName().endsWith("Event")) {
				onEvent(node);
			} else if (node.getNodeName().endsWith("Gateway")) {
				onGateway(node);
			} else if (node.getNodeName().equals("sequenceFlow")) {
				onSequenceFlow(node);
			}
		}
	}
	
	private void onLaneSet(Node parentNode, String organization) {
		NodeList nodeList = parentNode.getChildNodes();
		for(int i=0; i<nodeList.getLength(); ++i) {
			Node childNode = nodeList.item(i);
			if(childNode.getNodeName().equals("lane")) {
				onLane(childNode, organization);
			}
		}
	}
	
	private void onLane(Node parentNode, String organization) {
		NamedNodeMap attributes = parentNode.getAttributes();
		String department = "";
		if(attributes.getNamedItem("name")!=null) {
			department = attributes.getNamedItem("name").getNodeValue();
		}
				
		for(int i=0; i<parentNode.getChildNodes().getLength(); ++i) {
			Node childNode = parentNode.getChildNodes().item(i);
			if(childNode.getNodeName().equals("flowNodeRef")) {
				GenericNode node = getNode(childNode.getTextContent());
				if(!organization.isEmpty()) {
					node.addAttribute("organization", organization);
				}
				if(!department.isEmpty()) {
					node.addAttribute("department", department);
				}
			}
		}
	}
	
	private void onTask(Node parentNode) {
		NamedNodeMap attributes = parentNode.getAttributes();
		String id = attributes.getNamedItem("id").getNodeValue();
		GenericNode node = getNode(id);
		
		node.setLabel(normalizeLabel(attributes.getNamedItem("name").getNodeValue()));
		node.setType("activity");
		
		for(int i=0; i<parentNode.getChildNodes().getLength(); ++i) {
			Node childNode = parentNode.getChildNodes().item(i);
			if(childNode.getNodeName().equals("incoming")) {
				GenericFlow flow = getFlow(childNode.getTextContent());
				flow.setTo(node);
			} else if(childNode.getNodeName().equals("outgoing")) {
				GenericFlow flow = getFlow(childNode.getTextContent());
				flow.setFrom(node);
				node.getToNextNodeFlows().add(flow);
			}
		}
	}
	
	private void onEvent(Node parentNode) {
		NamedNodeMap attributes = parentNode.getAttributes();
		String id = attributes.getNamedItem("id").getNodeValue();
		GenericNode node = getNode(id);
		
		node.setLabel(normalizeLabel(attributes.getNamedItem("name").getNodeValue()));
		if(parentNode.getNodeName().equals("startEvent") && attributes.getNamedItem("isInterrupting")==null) {
			node.setType("startevent");
		} else if(parentNode.getNodeName().equals("intermediateCatchEvent") || parentNode.getNodeName().equals("startEvent") && attributes.getNamedItem("isInterrupting").getNodeValue().equals("true")) {
			node.setType("intermediateevent");
		} else if(parentNode.getNodeName().equals("endEvent")) {
			node.setType("endevent");
		}
		
		
		for(int i=0; i<parentNode.getChildNodes().getLength(); ++i) {
			Node childNode = parentNode.getChildNodes().item(i);
			if(childNode.getNodeName().equals("incoming")) {
				GenericFlow flow = getFlow(childNode.getTextContent());
				flow.setTo(node);
			} else if(childNode.getNodeName().equals("outgoing")) {
				GenericFlow flow = getFlow(childNode.getTextContent());
				flow.setFrom(node);
				node.getToNextNodeFlows().add(flow);
			}
		}
	}
	
	private void onGateway(Node parentNode) {
		NamedNodeMap attributes = parentNode.getAttributes();
		String id = attributes.getNamedItem("id").getNodeValue();
		GenericNode node = getNode(id);
		if(parentNode.getNodeName().equals("parallelGateway")) {
			node.setType("ANDgateway");
		} else if(parentNode.getNodeName().equals("exclusiveGateway") || attributes.getNamedItem("eventGatewayType").getNodeValue().equals("Exclusive")) {
			node.setType("XORgateway");
		}
		
		for(int i=0; i<parentNode.getChildNodes().getLength(); ++i) {
			Node childNode = parentNode.getChildNodes().item(i);
			if(childNode.getNodeName().equals("incoming")) {
				GenericFlow flow = getFlow(childNode.getTextContent());
				flow.setTo(node);
			} else if(childNode.getNodeName().equals("outgoing")) {
				GenericFlow flow = getFlow(childNode.getTextContent());
				flow.setFrom(node);
				node.getToNextNodeFlows().add(flow);
			}
		}
	}
	
	private void onSequenceFlow(Node parentNode) {
		NamedNodeMap attributes = parentNode.getAttributes();
		String id = attributes.getNamedItem("id").getNodeValue();
		GenericFlow flow = getFlow(id);
		
		String sourceId = attributes.getNamedItem("sourceRef").getNodeValue();
		GenericNode sourceNode = getNode(sourceId);
		flow.setFrom(sourceNode);
		String targetId = attributes.getNamedItem("targetRef").getNodeValue();
		GenericNode targetNode = getNode(targetId);
		flow.setTo(targetNode);
	}
	
	public static String normalizeLabel(String label){
		return label.replaceAll("[\n\t]", " ");
	}
	
	*/
	
}
