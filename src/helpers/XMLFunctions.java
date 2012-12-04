package helpers;

	import org.w3c.dom.Document;
	import org.w3c.dom.Element;
	import org.w3c.dom.Node;
	import org.w3c.dom.NodeList;


	/**
	 * @author Aleksander Pedersen
	 * @description This class is used as a helper class to handle some of the xml parsing and elementvalues
	 */
	public class XMLFunctions {

		/** Returns element value
		 * @param elem element (it is XML tag)
		 * @return Element value otherwise empty String
		 */
		public final static String getElementValue( Node elem ) {
			Node kid;
			if( elem != null){
				if (elem.hasChildNodes()){
					for( kid = elem.getFirstChild(); kid != null; kid = kid.getNextSibling() ){
						if( kid.getNodeType() == Node.TEXT_NODE  ){
							return kid.getNodeValue();
						}
					}
				}
			}
			return "";
		}

		/**
		 * Get how many results there are in a given document
		 * @param doc
		 * @return Number of elements
		 */
		public static int numResults(Document doc){		
			Node results = doc.getDocumentElement();
			int res = -1;

			try{
				res = Integer.valueOf(results.getAttributes().getNamedItem("count").getNodeValue());
			}catch(Exception e ){
				res = -1;
			}

			return res;
		}

		/**
		 * Get given value for an element
		 * @param item
		 * @param str
		 * @return The value for a given element
		 */
		public static String getValue(Element item, String str) {		
			NodeList n = item.getElementsByTagName(str);		
			return getElementValue(n.item(0));
		}
}
