/*******************************************************************************
 * PropertyListWriteStreamException.java                                       *
 * propertylistserialization Copyright (c) 2018; Electric Bolt Limited.        *
 ******************************************************************************/

package nz.co.electricbolt.propertylistserialization;

/**
 * Analogous to NSPropertyListWriteStreamError - an stream error was encountered while writing
 * the property list.
 */

public class PropertyListWriteStreamException extends PropertyListException {

    public PropertyListWriteStreamException(Exception e) {
        super(e);
    }

}
