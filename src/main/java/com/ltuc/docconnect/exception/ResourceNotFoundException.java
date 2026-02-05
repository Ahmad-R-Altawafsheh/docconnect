package com.ltuc.docconnect.exception;

import com.ltuc.docconnect.util.Messages;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format(Messages.RESOURCE_NOT_FOUND_TEMPLATE, resourceName, fieldName, fieldValue));
    }
}