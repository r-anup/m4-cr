package org.consumerreports.pagespeed.converters;

import org.apache.commons.lang.WordUtils;

import java.beans.PropertyEditorSupport;

public class EnumConverter<T extends Enum<T>> extends PropertyEditorSupport {

    private final Class<T> typeParameterClass;

    public EnumConverter(Class<T> typeParameterClass) {
        super();
        this.typeParameterClass = typeParameterClass;
    }

    @Override
    public void setAsText(final String text) throws IllegalArgumentException {
       setValue(T.valueOf(typeParameterClass, WordUtils.capitalizeFully(text.trim())));
    }
}