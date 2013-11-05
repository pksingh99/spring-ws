/*
 * Copyright 2005-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.ws.soap.axiom;

import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;

import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.SoapFaultDetailElement;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFaultDetail;

/**
 * Axiom-specific version of <code>org.springframework.ws.soap.SoapFaultDetail</code>.
 *
 * @author Arjen Poutsma
 * @since 1.0.0
 */
class AxiomSoapFaultDetail extends AxiomSoapElement implements SoapFaultDetail {

    public AxiomSoapFaultDetail(SOAPFaultDetail axiomFaultDetail, SOAPFactory axiomFactory) {
        super(axiomFaultDetail, axiomFactory);
    }

    public SoapFaultDetailElement addFaultDetailElement(QName name) {
        try {
            OMElement element = getAxiomFactory().createOMElement(name, getAxiomFaultDetail());
            return new AxiomSoapFaultDetailElement(element, getAxiomFactory());
        }
        catch (OMException ex) {
            throw new AxiomSoapFaultException(ex);
        }

    }

    public Iterator<SoapFaultDetailElement> getDetailEntries() {
        return new AxiomSoapFaultDetailElementIterator(getAxiomFaultDetail().getChildElements());
    }

    public Result getResult() {
        return new AxiomResult(getAxiomFaultDetail(), getAxiomFactory());
    }

    protected SOAPFaultDetail getAxiomFaultDetail() {
        return (SOAPFaultDetail) getAxiomElement();
    }

    private class AxiomSoapFaultDetailElementIterator implements Iterator<SoapFaultDetailElement> {

        private final Iterator<OMElement> axiomIterator;

        private AxiomSoapFaultDetailElementIterator(Iterator<OMElement> axiomIterator) {
            this.axiomIterator = axiomIterator;
        }

        public boolean hasNext() {
            return axiomIterator.hasNext();
        }

        public SoapFaultDetailElement next() {
            try {
                OMElement axiomElement = axiomIterator.next();
                return new AxiomSoapFaultDetailElement(axiomElement, getAxiomFactory());
            }
            catch (OMException ex) {
                throw new AxiomSoapFaultException(ex);
            }

        }

        public void remove() {
            axiomIterator.remove();
        }
    }

}
