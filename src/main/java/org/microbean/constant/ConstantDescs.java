/* -*- mode: Java; c-basic-offset: 2; indent-tabs-mode: nil; coding: utf-8-unix -*-
 *
 * Copyright © 2022 microBean™.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package org.microbean.constant;

import java.lang.constant.ClassDesc;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

final class ConstantDescs {

  static final ClassDesc CD_Collections = Collections.class.describeConstable().orElseThrow();

  static final ClassDesc CD_Comparator = Comparator.class.describeConstable().orElseThrow();

  static final ClassDesc CD_Entry = Entry.class.describeConstable().orElseThrow();
  
  static final ClassDesc CD_SortedMap = SortedMap.class.describeConstable().orElseThrow();

  static final ClassDesc CD_SortedSet = SortedSet.class.describeConstable().orElseThrow();

  static final ClassDesc CD_TreeMap = TreeMap.class.describeConstable().orElseThrow();

  static final ClassDesc CD_TreeSet = TreeSet.class.describeConstable().orElseThrow();
  
  private ConstantDescs() {
    super();
  }
  
}
