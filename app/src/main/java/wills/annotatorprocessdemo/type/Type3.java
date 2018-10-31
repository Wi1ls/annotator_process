package wills.annotatorprocessdemo.type;

import wills.annotations.CustomFieldAnnotation;
import wills.annotations.CustomTypeAnnotation;

/**
 * Copyright (c) 2017, Bongmi
 * All rights reserved
 * Author: shenwei@bongmi.com
 */
@CustomTypeAnnotation(property = 6)
public class Type3 {

  @CustomFieldAnnotation(value = "hello1 from type3")
  int hello3;
}
