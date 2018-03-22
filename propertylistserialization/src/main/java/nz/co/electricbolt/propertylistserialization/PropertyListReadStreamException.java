package nz.co.electricbolt.propertylistserialization;

/**
 * Analogous to NSPropertyListReadStreamError - an stream error was encountered while reading
 * the property list.
 */

public class PropertyListReadStreamException extends PropertyListException {

    public PropertyListReadStreamException(Exception e) {
        super(e);
    }

}
