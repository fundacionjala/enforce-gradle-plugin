/*
 * Copyright (c) Fundacion Jala. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package org.fundacionjala.gradle.plugins.enforce.utils.salesforce

/**
 * Represents all metadata types on salesforce
 */
public enum MetadataComponent {

    ACCOUNT_OBJECT("Account.object"),
    OPPORTUNITY_OBJECT("Opportunity.object"),
    CONTACT_OBJECT("Contact.object"),
    ADMIN_PROFILE("Admin.profile"),
    CMC_APP("Contact.object"),
    OPPORTUNITY_LINE_ITEM_OBJECT("OpportunityLineItem.object"),
    QUOTE_OBJECT("Quote.object"),
    QUOTE_LINE_ITEM_OBJECT("QuoteLineItem.object"),
    PRODUCT2_OBJECT("Product2.object")

    public final static Map<String, String> COMPONENTS

    static {
        COMPONENTS = new HashMap<String, String>()

        for (MetadataComponent component : values()) {
            COMPONENTS.put(component.name(), component.componentName);
        }
    }

    private final String componentName

    MetadataComponent(String componentName) {
        this.componentName = componentName
    }

    String getComponentName() {
        return componentName
    }
}