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

/**
 * A utiliy class containing useful {@link
 * java.lang.constant.ConstantDesc}s (mostly {@link ClassDesc}s).
 *
 * @author <a href="https://about.me/lairdnelson"
 * target="_parent">Laird Nelson</a>
 */
public final class ConstantDescs {


  /*
   * Static fields.
   */


  /**
   * A {@link ClassDesc} describing {@link java.util.Collections
   * java.util.Collections}.
   *
   * @nullability This field is never {@code null}.
   */
  public static final ClassDesc CD_Collections = ClassDesc.of("java.util.Collections");

  /**
   * A {@link ClassDesc} describing {@link java.util.Comparator
   * java.util.Comparator}.
   *
   * @nullability This field is never {@code null}.
   */
  public static final ClassDesc CD_Comparator = ClassDesc.of("java.util.Comparator");

  /**
   * A {@link ClassDesc} describing {@link
   * java.lang.constant.Constable java.lang.constant.Constable}.
   *
   * @nullability This field is never {@code null}.
   */
  public static final ClassDesc CD_Constable = ClassDesc.of("java.lang.constant.Constable");

  /**
   * A {@link ClassDesc} describing {@link java.util.Map.Entry
   * java.util.Map.Entry}.
   *
   * @nullability This field is never {@code null}.
   */
  public static final ClassDesc CD_Entry = ClassDesc.of("java.util.Map$Entry");

  /**
   * A {@link ClassDesc} describing {@link java.util.SortedMap
   * java.util.SortedMap}.
   *
   * @nullability This field is never {@code null}.
   */
  public static final ClassDesc CD_SortedMap = ClassDesc.of("java.util.SortedMap");

  /**
   * A {@link ClassDesc} describing {@link java.util.SortedSet
   * java.util.SortedSet}.
   *
   * @nullability This field is never {@code null}.
   */
  public static final ClassDesc CD_SortedSet = ClassDesc.of("java.util.SortedSet");

  /**
   * A {@link ClassDesc} describing {@link java.util.TreeMap
   * java.util.TreeMap}.
   *
   * @nullability This field is never {@code null}.
   */
  public static final ClassDesc CD_TreeMap = ClassDesc.of("java.util.TreeMap");

  /**
   * A {@link ClassDesc} describing {@link java.util.TreeSet
   * java.util.TreeSet}.
   *
   * @nullability This field is never {@code null}.
   */
  public static final ClassDesc CD_TreeSet = ClassDesc.of("java.util.TreeSet");


  /*
   * Constructors.
   */


  private ConstantDescs() {
    super();
  }


}
