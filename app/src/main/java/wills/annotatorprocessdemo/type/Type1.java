package wills.annotatorprocessdemo.type;

import wills.annotations.CustomFieldAnnotation;
import wills.annotations.CustomTypeAnnotation;

/**
 * Copyright (c) 2017, Bongmi
 * All rights reserved
 * Author: shenwei@bongmi.com
 */
@CustomTypeAnnotation(property = 5)
public class Type1 {
  @CustomFieldAnnotation(value = "hello1 from type1")
  int hello1;

  @CustomFieldAnnotation(value = "hello2 from type1")
  int hello2;
}
