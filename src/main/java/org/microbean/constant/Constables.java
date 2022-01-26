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
import java.lang.constant.Constable;
import java.lang.constant.ConstantDesc;
import java.lang.constant.ConstantDescs;
import java.lang.constant.DirectMethodHandleDesc;
import java.lang.constant.DynamicConstantDesc;
import java.lang.constant.MethodHandleDesc;
import java.lang.constant.MethodTypeDesc;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import java.util.function.Function;

import static java.lang.constant.ConstantDescs.BSM_INVOKE;
import static java.lang.constant.ConstantDescs.CD_List;
import static java.lang.constant.ConstantDescs.CD_Map;
import static java.lang.constant.ConstantDescs.CD_Object;
import static java.lang.constant.ConstantDescs.CD_Set;
import static java.lang.constant.ConstantDescs.NULL;

public final class Constables {

  private Constables() {
    super();
  }

  public static final Optional<? extends ConstantDesc> describeConstable(final ConstantDesc cd) {
    if (cd == null) {
      return Optional.of(NULL);
    } else if (cd instanceof Constable c) {
      return c.describeConstable();
    } else {
      return Optional.of(cd);
    }
  }

  public static final Optional<? extends ConstantDesc> describeConstable(final Constable c) {
    return c == null ? Optional.of(NULL) : c.describeConstable();
  }

  public static final <E extends Constable> Optional<? extends ConstantDesc> describeConstable(final List<? extends E> elements) {
    return describeConstable(elements, Constable::describeConstable);
  }

  public static final <E> Optional<? extends ConstantDesc> describeConstable(final List<? extends E> elements,
                                                                             final Function<? super E, ? extends Optional<? extends ConstantDesc>> f) {
    return describeConstable(elements, CD_List, f);
  }

  public static final <E extends Constable> Optional<? extends ConstantDesc> describeConstable(final Set<? extends E> elements) {
    return describeConstable(elements, Constable::describeConstable);
  }

  public static final <E> Optional<? extends ConstantDesc> describeConstable(final Set<? extends E> elements,
                                                                             final Function<? super E, ? extends Optional<? extends ConstantDesc>> f) {
    return describeConstable(elements, CD_Set, f);
  }

  public static final <K extends Constable, V extends Constable> Optional<? extends ConstantDesc> describeConstable(final Map<? extends K, ? extends V> map) {
    return describeConstable(map, Constable::describeConstable, Constable::describeConstable);
  }

  public static final <K, V> Optional<? extends ConstantDesc> describeConstable(final Map<? extends K, ? extends V> map,
                                                                                final Function<? super K, ? extends Optional<? extends ConstantDesc>> kf,
                                                                                final Function<? super V, ? extends Optional<? extends ConstantDesc>> vf) {
    if (map == null) {
      return Optional.of(NULL);
    } else if (map instanceof Constable c) {
      return c.describeConstable();
    } else if (map.isEmpty()) {
      return Optional.of(DynamicConstantDesc.of(BSM_INVOKE, MethodHandleDesc.ofMethod(DirectMethodHandleDesc.Kind.INTERFACE_STATIC,
                                                                                      CD_Map,
                                                                                      "of",
                                                                                      MethodTypeDesc.of(CD_Map))));
    } else {
      // No need for an instanceof ConstantDesc check; ConstantDesc is
      // sealed and none of its permitted subtypes is a Map
      final int mapSize = map.size();
      final ConstantDesc[] bsmInvokeArguments = new ConstantDesc[mapSize + 1];
      bsmInvokeArguments[0] =
        MethodHandleDesc.ofMethod(DirectMethodHandleDesc.Kind.INTERFACE_STATIC,
                                  CD_Map,
                                  "ofEntries",
                                  MethodTypeDesc.of(CD_Map,
                                                    Entry.class.describeConstable().orElseThrow().arrayType()));
      int i = 1;
      for (final Entry<? extends K, ? extends V> entry : map.entrySet()) {
        final Optional<? extends ConstantDesc> e = describeConstable(entry, kf, vf);
        if (e.isEmpty()) {
          return Optional.empty();
        }
        bsmInvokeArguments[i++] = e.orElseThrow();
      }
      return Optional.of(DynamicConstantDesc.of(BSM_INVOKE, bsmInvokeArguments));
    }
  }

  public static final <K, V> Optional<? extends ConstantDesc> describeConstable(final Entry<? extends K, ? extends V> entry,
                                                                                final Function<? super K, ? extends Optional<? extends ConstantDesc>> kf,
                                                                                final Function<? super V, ? extends Optional<? extends ConstantDesc>> vf) {
    if (entry == null) {
      return Optional.of(NULL);
    } else if (entry instanceof Constable c) {
      return c.describeConstable();
    } else {
      // No need for an instanceof ConstantDesc check; ConstantDesc is
      // sealed and none of its permitted subtypes is an Entry
      final K k = entry.getKey();
      final Optional<? extends ConstantDesc> key = k instanceof Constable c ? c.describeConstable() : kf.apply(k);
      if (key.isPresent()) {
        final V v = entry.getValue();
        final Optional<? extends ConstantDesc> value = v instanceof Constable c ? c.describeConstable() : vf.apply(v);
        if (value.isPresent()) {
          // Map#entry(K, V)
          return
            Optional.of(DynamicConstantDesc.of(BSM_INVOKE,
                                               MethodHandleDesc.ofMethod(DirectMethodHandleDesc.Kind.INTERFACE_STATIC,
                                                                         CD_Map,
                                                                         "entry",
                                                                         MethodTypeDesc.of(Entry.class.describeConstable().orElseThrow(),
                                                                                           CD_Object, // K erasure
                                                                                           CD_Object)), // V erasure
                                               key.orElseThrow(),
                                               value.orElseThrow()));
        }
      }
      return Optional.empty();
    }
  }

  private static final <E> Optional<? extends ConstantDesc> describeConstable(final Collection<? extends E> elements,
                                                                              final ClassDesc listOrSetClassDesc,
                                                                              final Function<? super E, ? extends Optional<? extends ConstantDesc>> f) {
    if (elements == null) {
      return Optional.of(NULL);
    } else if (elements instanceof Constable c) {
      return c.describeConstable();
    } else if (elements.isEmpty()) {
      return Optional.of(DynamicConstantDesc.of(BSM_INVOKE, MethodHandleDesc.ofMethod(DirectMethodHandleDesc.Kind.INTERFACE_STATIC,
                                                                                      listOrSetClassDesc,
                                                                                      "of",
                                                                                      MethodTypeDesc.of(listOrSetClassDesc))));
    } else {
      // No need for an instanceof ConstantDesc check; ConstantDesc is
      // sealed and none of its permitted subtypes is a Collection
      final int elementsSize = elements.size();
      final ConstantDesc[] bsmInvokeArguments = new ConstantDesc[elementsSize + 1];
      final MethodTypeDesc ofMethodTypeDesc;
      if (elementsSize < 11) {
        // List.of() and Set.of() have explicit polymorphic overrides
        // for parameter counts of up to 10.
        final ClassDesc[] paramDescs = new ClassDesc[elementsSize];
        Arrays.fill(paramDescs, CD_Object); // Object is the erasure of E
        ofMethodTypeDesc = MethodTypeDesc.of(listOrSetClassDesc, paramDescs);
      } else {
        // After 10 parameters, List.of() and Set.of() fall back on
        // varargs.
        ofMethodTypeDesc =
          MethodTypeDesc.of(listOrSetClassDesc, CD_Object.arrayType()); // Object is the erasure of E
      }
      bsmInvokeArguments[0] =
        MethodHandleDesc.ofMethod(DirectMethodHandleDesc.Kind.INTERFACE_STATIC, listOrSetClassDesc, "of", ofMethodTypeDesc);
      int i = 1;
      for (final E element : elements) {
        final Optional<? extends ConstantDesc> arg = element instanceof Constable c ? c.describeConstable() : f.apply(element);
        if (arg == null || arg.isEmpty()) {
          return Optional.empty();
        }
        bsmInvokeArguments[i++] = arg.orElseThrow();
      }
      return Optional.of(DynamicConstantDesc.of(BSM_INVOKE, bsmInvokeArguments));
    }
  }

}
