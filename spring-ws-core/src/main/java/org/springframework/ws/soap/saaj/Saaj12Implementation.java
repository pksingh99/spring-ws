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

package org.springframework.ws.soap.saaj;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.springframework.util.ObjectUtils;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.saaj.support.SaajUtils;
import org.springframework.ws.transport.TransportConstants;
import org.springframework.ws.transport.TransportOutputStream;

/**
 * SAAJ 1.2 specific implementation of the <code>SaajImplementation</code> interface.
 *
 * @author Arjen Poutsma
 * @since 1.0.0
 */
class Saaj12Implementation extends SaajImplementation {

    private static final Saaj12Implementation INSTANCE = new Saaj12Implementation();

    private Saaj12Implementation() {
    }

    public static Saaj12Implementation getInstance() {
        return INSTANCE;
    }

    @Override
    public QName getName(SOAPElement element) {
        return SaajUtils.toQName(element.getElementName());
    }

    @Override
    public QName getFaultCode(SOAPFault fault) {
        return SaajUtils.toQName(fault.getFaultCodeAsName());
    }

    public boolean isSoap11(SOAPElement element) {
        return true;
    }

    @Override
    public DetailEntry addDetailEntry(Detail detail, QName name) throws SOAPException {
        Name detailEntryName = SaajUtils.toName(name, detail);
        return detail.addDetailEntry(detailEntryName);
    }

    @Override
    public SOAPHeaderElement addHeaderElement(SOAPHeader header, QName name) throws SOAPException {
        Name saajName = SaajUtils.toName(name, header);
        return header.addHeaderElement(saajName);
    }

    @Override
    public SOAPFault addFault(SOAPBody body, QName faultCode, String faultString, Locale locale) throws SOAPException {
        Name name = SaajUtils.toName(faultCode, body);
        if (locale == null) {
            return body.addFault(name, faultString);
        }
        else {
            return body.addFault(name, faultString, locale);
        }
    }

    @Override
    public Source getSource(SOAPElement element) {
        return new DOMSource(element);
    }

    @Override
    public Result getResult(SOAPElement element) {
        return new DOMResult(element);
    }

    @Override
    public void addAttribute(SOAPElement element, QName name, String value) throws SOAPException {
        Name attributeName = SaajUtils.toName(name, element);
        element.addAttribute(attributeName, value);
    }

    @Override
    public void removeAttribute(SOAPElement element, QName name) throws SOAPException {
        Name attributeName = SaajUtils.toName(name, element);
        element.removeAttribute(attributeName);
    }

    @Override
    public String getAttributeValue(SOAPElement element, QName name) throws SOAPException {
        Name attributeName = SaajUtils.toName(name, element);
        return element.getAttributeValue(attributeName);
    }

    @Override
    public Iterator<QName> getAllAttributes(SOAPElement element) {
        List<QName> results = new ArrayList<QName>();
        for (Iterator<?> iterator = element.getAllAttributes(); iterator.hasNext();) {
            Name attributeName = (Name) iterator.next();
            results.add(SaajUtils.toQName(attributeName));
        }
        return results.iterator();
    }

    @Override
    public String getText(SOAPElement element) {
        return element.getValue();
    }

    @Override
    public void setText(SOAPElement element, String content) {
        element.setValue(content);
    }

    @Override
    public SOAPEnvelope getEnvelope(SOAPMessage message) throws SOAPException {
        return message.getSOAPPart().getEnvelope();
    }

    @Override
    public SOAPHeader getHeader(SOAPEnvelope envelope) throws SOAPException {
        return envelope.getHeader();
    }

    @Override
    public SOAPBody getBody(SOAPEnvelope envelope) throws SOAPException {
        return envelope.getBody();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterator<SOAPHeaderElement> examineAllHeaderElements(SOAPHeader header) {
        return header.examineAllHeaderElements();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterator<SOAPHeaderElement> examineMustUnderstandHeaderElements(SOAPHeader header, String actorOrRole) {
        return header.examineMustUnderstandHeaderElements(actorOrRole);
    }

    @Override
    public String getActorOrRole(SOAPHeaderElement headerElement) {
        return headerElement.getActor();
    }

    @Override
    public void setActorOrRole(SOAPHeaderElement headerElement, String actorOrRole) {
        headerElement.setActor(actorOrRole);
    }

    @Override
    public boolean getMustUnderstand(SOAPHeaderElement headerElement) {
        return headerElement.getMustUnderstand();
    }

    @Override
    public void setMustUnderstand(SOAPHeaderElement headerElement, boolean mustUnderstand) {
        headerElement.setMustUnderstand(mustUnderstand);
    }

    @Override
    public boolean hasFault(SOAPBody body) {
        return body.hasFault();
    }

    @Override
    public SOAPFault getFault(SOAPBody body) {
        return body.getFault();
    }

    @Override
    public String getFaultActor(SOAPFault fault) {
        return fault.getFaultActor();
    }

    @Override
    public void setFaultActor(SOAPFault fault, String actorOrRole) throws SOAPException {
        fault.setFaultActor(actorOrRole);
    }

    @Override
    public String getFaultString(SOAPFault fault) {
        return fault.getFaultString();
    }

    @Override
    public Locale getFaultStringLocale(SOAPFault fault) {
        return fault.getFaultStringLocale();
    }

    @Override
    public Detail getFaultDetail(SOAPFault fault) {
        return fault.getDetail();
    }

    @Override
    public Detail addFaultDetail(SOAPFault fault) throws SOAPException {
        return fault.addDetail();
    }

    @Override
    public void addTextNode(DetailEntry detailEntry, String text) throws SOAPException {
        detailEntry.addTextNode(text);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterator<DetailEntry> getDetailEntries(Detail detail) {
        return detail.getDetailEntries();
    }

    @Override
    public SOAPElement getFirstBodyElement(SOAPBody body) {
        for (Iterator<?> iterator = body.getChildElements(); iterator.hasNext();) {
            Object child = iterator.next();
            if (child instanceof SOAPElement) {
                return (SOAPElement) child;
            }
        }
        return null;
    }

    @Override
    public void removeContents(SOAPElement element) {
        element.removeContents();
    }

    @Override
    @SuppressWarnings("unchecked")
    Iterator<SOAPElement> getChildElements(SOAPElement element, QName name) throws SOAPException {
        Name elementName = SaajUtils.toName(name, element);
        return element.getChildElements(elementName);
    }

    @Override
    void addNamespaceDeclaration(SOAPElement element, String prefix, String namespaceUri) throws SOAPException {
        element.addNamespaceDeclaration(prefix, namespaceUri);
    }

    @Override
    public void writeTo(SOAPMessage message, OutputStream outputStream) throws SOAPException, IOException {
        if (message.saveRequired()) {
            message.saveChanges();
        }
        if (outputStream instanceof TransportOutputStream) {
            TransportOutputStream transportOutputStream = (TransportOutputStream) outputStream;
            // some SAAJ implementations (Axis 1) do not have a Content-Type header by default
            MimeHeaders headers = message.getMimeHeaders();
            if (ObjectUtils.isEmpty(headers.getHeader(TransportConstants.HEADER_CONTENT_TYPE))) {
                headers.addHeader(TransportConstants.HEADER_CONTENT_TYPE, SoapVersion.SOAP_11.getContentType());
                if (message.saveRequired()) {
                    message.saveChanges();
                }
            }
            for (Iterator<?> iterator = headers.getAllHeaders(); iterator.hasNext();) {
                MimeHeader mimeHeader = (MimeHeader) iterator.next();
                transportOutputStream.addHeader(mimeHeader.getName(), mimeHeader.getValue());
            }
        }
        message.writeTo(outputStream);

    }

    @Override
    public MimeHeaders getMimeHeaders(SOAPMessage message) {
        return message.getMimeHeaders();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterator<AttachmentPart> getAttachments(SOAPMessage message) {
        return message.getAttachments();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterator<AttachmentPart> getAttachment(SOAPMessage message, MimeHeaders mimeHeaders) {
        return message.getAttachments(mimeHeaders);
    }

    @Override
    public AttachmentPart addAttachmentPart(SOAPMessage message, DataHandler dataHandler) {
        AttachmentPart attachmentPart = message.createAttachmentPart(dataHandler);
        message.addAttachmentPart(attachmentPart);
        return attachmentPart;
    }

    //
    // Unsupported
    //

    @Override
    public String getFaultRole(SOAPFault fault) {
        throw new UnsupportedOperationException("SAAJ 1.2 does not support SOAP 1.2");
    }

    @Override
    public void setFaultRole(SOAPFault fault, String role) {
        throw new UnsupportedOperationException("SAAJ 1.2 does not support SOAP 1.2");
    }

    @Override
    public SOAPHeaderElement addNotUnderstoodHeaderElement(SOAPHeader header, QName name) {
        throw new UnsupportedOperationException("SAAJ 1.2 does not support SOAP 1.2");
    }

    @Override
    public SOAPHeaderElement addUpgradeHeaderElement(SOAPHeader header, String[] supportedSoapUris) {
        throw new UnsupportedOperationException("SAAJ 1.2 does not support SOAP 1.2");
    }

    @Override
    public Iterator<QName> getFaultSubcodes(SOAPFault fault) {
        throw new UnsupportedOperationException("SAAJ 1.2 does not support SOAP 1.2");
    }

    @Override
    public void appendFaultSubcode(SOAPFault fault, QName subcode) {
        throw new UnsupportedOperationException("SAAJ 1.2 does not support SOAP 1.2");
    }

    @Override
    public String getFaultNode(SOAPFault fault) {
        throw new UnsupportedOperationException("SAAJ 1.2 does not support SOAP 1.2");
    }

    @Override
    public void setFaultNode(SOAPFault fault, String uri) {
        throw new UnsupportedOperationException("SAAJ 1.2 does not support SOAP 1.2");
    }

    @Override
    public String getFaultReasonText(SOAPFault fault, Locale locale) {
        throw new UnsupportedOperationException("SAAJ 1.2 does not support SOAP 1.2");
    }

    @Override
    public void setFaultReasonText(SOAPFault fault, Locale locale, String text) {
        throw new UnsupportedOperationException("SAAJ 1.2 does not support SOAP 1.2");
    }

}
