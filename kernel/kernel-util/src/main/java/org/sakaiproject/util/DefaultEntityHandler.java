/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright 2006 Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 *
 *       http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.entity.api.Entity;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class is required by the SAX parser to parse into. Rather than use the ContentHandler interface
 * this class and the underlying DefautlHandler implements all the required methods of ContentHandler
 * and others, removing most of the implementation load from those produicing extendsion.
 * 
 * DefaultEntityHandlers are create by SAXReaders and consume SAXEvents building an Entity, which is theen
 * retrieved once parsing is complete with the getEntity() method.
 * 
 * @author ieb
 */
public class DefaultEntityHandler extends DefaultHandler implements SAXEntityHandler
{

	private static final Log log = LogFactory.getLog(DefaultEntityHandler.class);

	protected ContentHandler ch;

	protected int remove = -1;

	protected int nesting = 0;

	protected Entity entity;
	
	protected Entity container;



	/**
	 * 
	 */
	public DefaultEntityHandler()
	{
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException
	{
		if (ch != null)
		{
			ch.endElement(uri, localName, qName);
		}
		if (remove == nesting)
		{
			ch = null;
		}
		nesting--;
	}

	public boolean doStartElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException
	{

		nesting++;
		if (ch != null)
		{
			ch.startElement(uri, localName, qName, attributes);
			return false;
		}
		else
		{
			if ("properties".equals(qName))
			{
				ch = entity.getProperties().getContentHander();
				ch.startElement(uri, localName, qName, attributes);
				remove = nesting;
				return false;
			}
			return true;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
	 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException
	{
		if (!doStartElement(uri, localName, qName, attributes))
		{
			super.startElement(uri, localName, qName, attributes);
		}
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.util.DefaultEntityHandler#getEntity()
	 */
	public Entity getEntity()
	{
		return entity;
	}

	/**
	 * @param contentHandler
	 * @throws SAXException 
	 */
	protected void setContentHandler(ContentHandler contentHandler, String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
		if ( ch == null ) {
			ch = contentHandler;
			remove = nesting;
			ch.startElement(uri,localName,qName,attributes);
		} else {
			log.warn("Attempted to reset child content handler ");
		}
		
	}

	/**
	 * @param container2
	 */
	public void setContainer(Entity container)
	{
		this.container = container;
	}


}
