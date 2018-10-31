package wills.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Copyright (c) 2018, Bongmi
 * All rights reserved
 * Author: shenwei@bongmi.com
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface CustomFieldAnnotation {
  String value();
}
