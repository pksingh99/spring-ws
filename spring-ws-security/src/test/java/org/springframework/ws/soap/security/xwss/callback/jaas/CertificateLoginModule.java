/*
 * Copyright 2006 the original author or authors.
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

package org.springframework.ws.soap.security.xwss.callback.jaas;

import java.security.Principal;
import java.util.Iterator;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import javax.security.auth.x500.X500Principal;

public class CertificateLoginModule implements LoginModule {

    private Subject subject;

    private boolean loginSuccessful = false;

    public boolean abort() {
        return true;
    }

    public boolean commit() {
        if (!loginSuccessful) {
            subject.getPrincipals().clear();
            subject.getPrivateCredentials().clear();
            return false;
        }
        return true;
    }

    public void initialize(Subject subject,
                           CallbackHandler callbackHandler,
                           java.util.Map sharedState,
                           java.util.Map options) {
        this.subject = subject;
    }

    public boolean login() throws LoginException {
        if (subject == null) {
            return false;
        }

        String name = getName(subject);

        loginSuccessful = "CN=Arjen Poutsma,OU=Spring-WS,O=Interface21,L=Amsterdam,ST=Unknown,C=NL".equals(name);
        return loginSuccessful;
    }

    public boolean logout() {
        subject.getPrincipals().clear();
        subject.getPrivateCredentials().clear();
        return true;
    }

    private String getName(Subject subject) {
        for (Iterator iterator = subject.getPrincipals().iterator(); iterator.hasNext();) {
            Principal principal = (Principal) iterator.next();
            if (principal instanceof X500Principal) {
                return principal.getName();
            }
        }
        return null;
    }
}
