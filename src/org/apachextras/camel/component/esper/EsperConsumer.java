/**************************************************************************************
 * Copyright (C) 2008 Camel Extra Team. All rights reserved.                          *
 * http://code.google.com/p/camel-extra/                                              *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package org.apachextras.camel.component.esper;

import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.RuntimeExchangeException;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.log4j.Logger;

/**
 * @version $Revision: 1.1 $
 */
public class EsperConsumer extends DefaultConsumer implements UpdateListener {
    private EsperEndpoint endpoint;
    private EPStatement statement;

    static Logger LOG = Logger.getLogger(EsperProducer.class.getName());
    public EsperConsumer(EsperEndpoint endpoint, EPStatement statement, Processor processor) {
        super(endpoint, processor);
        LOG.debug("Create esper consumer: " + endpoint.getName());
        this.endpoint = endpoint;
        this.statement = statement;
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        statement.addListener(this);
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        statement.removeListener(this);
        endpoint.removeConsumer();
    }

    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
      if (newEvents != null) {
	        for (EventBean eventBean : newEvents) {
//	            Object underlying = eventBean.getUnderlying();
	            Exchange exchange = endpoint.createExchange(eventBean, statement);
	            try {
	                getProcessor().process(exchange);
	            } catch (Exception e) {
	                throw new RuntimeExchangeException("Cannot update event", exchange, e);
	            }
	        }
		  }
    }

}
